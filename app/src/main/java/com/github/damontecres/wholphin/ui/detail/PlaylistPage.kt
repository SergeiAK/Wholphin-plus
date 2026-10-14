package com.github.damontecres.wholphin.ui.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.damontecres.wholphin.data.ServerRepository
import com.github.damontecres.wholphin.preferences.UserPreferences
import com.github.damontecres.wholphin.services.KeyValueService
import com.github.damontecres.wholphin.ui.components.ErrorMessage
import com.github.damontecres.wholphin.ui.components.LoadingPage
import com.github.damontecres.wholphin.ui.detail.collection.CollectionDetails
import com.github.damontecres.wholphin.ui.launchIO
import com.github.damontecres.wholphin.ui.nav.Destination
import com.github.damontecres.wholphin.ui.toServerString
import com.github.damontecres.wholphin.util.LoadingState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import org.jellyfin.sdk.api.client.ApiClient
import org.jellyfin.sdk.api.client.extensions.userLibraryApi
import org.jellyfin.sdk.model.api.MediaType
import timber.log.Timber
import java.util.UUID

/**
 * How a playlist is displayed: as a list (the classic playlist page) or as a grid like a collection
 */
data class PlaylistPageState(
    val loadingState: LoadingState = LoadingState.Pending,
    val showAsGrid: Boolean = false,
)

@HiltViewModel(assistedFactory = PlaylistPageViewModel.Factory::class)
class PlaylistPageViewModel
    @AssistedInject
    constructor(
        private val api: ApiClient,
        private val serverRepository: ServerRepository,
        private val keyValueService: KeyValueService,
        @Assisted private val itemId: UUID,
    ) : ViewModel() {
        @AssistedFactory
        interface Factory {
            fun create(itemId: UUID): PlaylistPageViewModel
        }

        private val _state = MutableStateFlow(PlaylistPageState())
        val state: StateFlow<PlaylistPageState> = _state

        init {
            viewModelScope.launchIO {
                try {
                    val saved =
                        serverRepository.currentUser?.id?.let { userId ->
                            keyValueService
                                .get(userId, VIEW_MODE_KEY, emptyMap<String, Boolean>())
                                .first()[itemId.toServerString()]
                        }
                    val showAsGrid =
                        saved ?: run {
                            // Video playlists default to the grid, everything else to the list
                            val playlist by api.userLibraryApi.getItem(itemId)
                            playlist.mediaType == MediaType.VIDEO
                        }
                    _state.update {
                        it.copy(loadingState = LoadingState.Success, showAsGrid = showAsGrid)
                    }
                } catch (ex: CancellationException) {
                    throw ex
                } catch (ex: Exception) {
                    Timber.e(ex, "Error getting view mode for playlist %s", itemId)
                    _state.update { it.copy(loadingState = LoadingState.Success) }
                }
            }
        }

        fun setShowAsGrid(showAsGrid: Boolean) {
            _state.update { it.copy(showAsGrid = showAsGrid) }
            viewModelScope.launchIO {
                serverRepository.currentUser?.id?.let { userId ->
                    val current =
                        keyValueService
                            .get(userId, VIEW_MODE_KEY, emptyMap<String, Boolean>())
                            .first()
                    keyValueService.save(
                        userId,
                        VIEW_MODE_KEY,
                        current + (itemId.toServerString() to showAsGrid),
                    )
                }
            }
        }

        companion object {
            const val VIEW_MODE_KEY = "PlaylistShowAsGrid"
        }
    }

/**
 * Shows a playlist either as a list ([PlaylistDetails]) or as a grid ([CollectionDetails]),
 * remembering the choice per playlist
 */
@Composable
fun PlaylistPage(
    preferences: UserPreferences,
    destination: Destination.MediaItem,
    modifier: Modifier = Modifier,
    viewModel: PlaylistPageViewModel =
        hiltViewModel<PlaylistPageViewModel, PlaylistPageViewModel.Factory>(
            creationCallback = { it.create(destination.itemId) },
        ),
) {
    val state by viewModel.state.collectAsState()
    when (val s = state.loadingState) {
        is LoadingState.Error -> {
            ErrorMessage(s, modifier)
        }

        LoadingState.Pending,
        LoadingState.Loading,
        -> {
            LoadingPage(modifier)
        }

        LoadingState.Success -> {
            if (state.showAsGrid) {
                CollectionDetails(
                    preferences = preferences,
                    itemId = destination.itemId,
                    onClickShowAsList = { viewModel.setShowAsGrid(false) },
                    modifier = modifier,
                )
            } else {
                PlaylistDetails(
                    preferences = preferences,
                    destination = destination,
                    onClickShowAsGrid = { viewModel.setShowAsGrid(true) },
                    modifier = modifier,
                )
            }
        }
    }
}
