package com.github.damontecres.wholphin.ui.detail.discover

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.focusable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.damontecres.wholphin.api.seerr.model.PersonDetails
import com.github.damontecres.wholphin.data.model.DiscoverItem
import com.github.damontecres.wholphin.preferences.UserPreferences
import com.github.damontecres.wholphin.services.BackdropService
import com.github.damontecres.wholphin.services.NavigationManager
import com.github.damontecres.wholphin.services.SeerrService
import com.github.damontecres.wholphin.ui.components.ErrorMessage
import com.github.damontecres.wholphin.ui.components.LoadingPage
import com.github.damontecres.wholphin.ui.data.ItemDetailsDialog
import com.github.damontecres.wholphin.ui.data.ItemDetailsDialogInfo
import com.github.damontecres.wholphin.ui.detail.PersonPage
import com.github.damontecres.wholphin.ui.detail.PersonPageContent
import com.github.damontecres.wholphin.ui.launchIO
import com.github.damontecres.wholphin.ui.nav.Destination
import com.github.damontecres.wholphin.ui.toLocalDate
import com.github.damontecres.wholphin.util.DataLoadingState
import com.github.damontecres.wholphin.util.RowLoadingState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.jellyfin.sdk.api.client.ApiClient
import org.jellyfin.sdk.api.client.extensions.itemsApi
import org.jellyfin.sdk.model.api.BaseItemKind
import org.jellyfin.sdk.model.api.ImageType
import org.jellyfin.sdk.model.api.ItemFields
import org.jellyfin.sdk.model.api.request.GetItemsRequest
import timber.log.Timber
import java.util.Locale
import java.util.UUID

/**
 * What to show for a person opened from Seerr
 */
sealed interface DiscoverPersonState {
    /**
     * The person is in the library, so the regular person page is shown
     */
    data class InLibrary(
        val itemId: UUID,
    ) : DiscoverPersonState

    /**
     * The person is not in the library, so the person page is filled from Seerr
     */
    data class FromSeerr(
        val details: PersonDetails,
        val imageUrl: String?,
        val credits: List<DiscoverItem>,
    ) : DiscoverPersonState
}

@HiltViewModel(assistedFactory = DiscoverPersonDetailsViewModel.Factory::class)
class DiscoverPersonDetailsViewModel
    @AssistedInject
    constructor(
        private val api: ApiClient,
        val navigationManager: NavigationManager,
        private val seerrService: SeerrService,
        private val backdropService: BackdropService,
        @Assisted val person: DiscoverItem,
    ) : ViewModel() {
        @AssistedFactory
        interface Factory {
            fun create(person: DiscoverItem): DiscoverPersonDetailsViewModel
        }

        val state = MutableStateFlow<DataLoadingState<DiscoverPersonState>>(DataLoadingState.Pending)

        init {
            viewModelScope.launchIO {
                backdropService.clearBackdrop()
                try {
                    val libraryId = findInLibrary()
                    val result =
                        if (libraryId != null) {
                            DiscoverPersonState.InLibrary(libraryId)
                        } else {
                            val language = Locale.getDefault().language
                            val details =
                                seerrService.personDetails(person.id, language).let { local ->
                                    // TMDB often has no biography in the user's language; fall back to English
                                    if (local.biography.isNullOrBlank() && language != "en") {
                                        local.copy(biography = seerrService.personDetails(person.id, "en").biography)
                                    } else {
                                        local
                                    }
                                }
                            DiscoverPersonState.FromSeerr(
                                details = details,
                                imageUrl = seerrService.createImageUrl(ImageType.PRIMARY, details.profilePath, null),
                                credits = seerrService.personCredits(person.id),
                            )
                        }
                    state.update { DataLoadingState.Success(result) }
                } catch (ex: Exception) {
                    Timber.e(ex, "Error fetching person %s", person.id)
                    state.update { DataLoadingState.Error(ex) }
                }
            }
        }

        /**
         * Jellyfin can't filter by provider id, so search by name and confirm by the TMDB id.
         * `/Persons?searchTerm=` misses names with non-ASCII characters, `/Items` finds them.
         */
        private suspend fun findInLibrary(): UUID? {
            val name = person.title?.takeIf { it.isNotBlank() } ?: return null
            return try {
                api.itemsApi
                    .getItems(
                        GetItemsRequest(
                            searchTerm = name,
                            includeItemTypes = listOf(BaseItemKind.PERSON),
                            recursive = true,
                            fields = listOf(ItemFields.PROVIDER_IDS),
                        ),
                    ).content.items
                    .firstOrNull { it.providerIds?.get("Tmdb") == person.id.toString() }
                    ?.id
            } catch (ex: Exception) {
                Timber.w(ex, "Error looking up person %s in the library", person.id)
                null
            }
        }
    }

/**
 * Person opened from Seerr: the library person page if they are in the library, otherwise the
 * same page filled from Seerr
 */
@Composable
fun DiscoverPersonDetails(
    preferences: UserPreferences,
    person: DiscoverItem,
    modifier: Modifier = Modifier,
    viewModel: DiscoverPersonDetailsViewModel =
        hiltViewModel<DiscoverPersonDetailsViewModel, DiscoverPersonDetailsViewModel.Factory>(
            creationCallback = { it.create(person) },
        ),
) {
    val state by viewModel.state.collectAsState()
    when (val st = state) {
        is DataLoadingState.Error -> {
            ErrorMessage(st.message, st.exception, modifier.focusable())
        }

        DataLoadingState.Loading,
        DataLoadingState.Pending,
        -> {
            LoadingPage(modifier.focusable())
        }

        is DataLoadingState.Success -> {
            when (val data = st.data) {
                is DiscoverPersonState.InLibrary -> {
                    PersonPage(
                        preferences = preferences,
                        destination = Destination.MediaItem(itemId = data.itemId, type = BaseItemKind.PERSON),
                        modifier = modifier,
                    )
                }

                is DiscoverPersonState.FromSeerr -> {
                    val details = data.details
                    val name = details.name ?: person.title ?: ""
                    var showOverviewDialog by remember { mutableStateOf(false) }
                    val maxItems = preferences.appPreferences.homePagePreferences.maxItemsPerRow
                    val discovered = remember(data.credits, maxItems) { data.credits.take(maxItems) }
                    val noRow = RowLoadingState.Success(emptyList())
                    PersonPageContent(
                        preferences = preferences,
                        name = name,
                        overview = details.biography,
                        imageUrl = data.imageUrl,
                        birthdate = toLocalDate(details.birthday),
                        deathdate = toLocalDate(details.deathday),
                        birthPlace = details.placeOfBirth,
                        favorite = null,
                        movies = noRow,
                        series = noRow,
                        episodes = noRow,
                        onClickItem = { _, _ -> },
                        overviewOnClick = { showOverviewDialog = true },
                        favoriteOnClick = {},
                        discovered = discovered,
                        onClickDiscover = { _, item ->
                            viewModel.navigationManager.navigateTo(item.destination)
                        },
                        onClickViewMoreDiscover = {
                            viewModel.navigationManager.navigateTo(
                                Destination.DiscoveredItem(person, maxItems, creditsGrid = true),
                            )
                        },
                        enableViewMoreDiscover = data.credits.size > discovered.size,
                        modifier = modifier,
                    )
                    AnimatedVisibility(showOverviewDialog) {
                        ItemDetailsDialog(
                            info =
                                ItemDetailsDialogInfo(
                                    title = name,
                                    overview = details.biography,
                                    genres = listOf(),
                                    files = listOf(),
                                ),
                            showFilePath = false,
                            onDismissRequest = { showOverviewDialog = false },
                        )
                    }
                }
            }
        }
    }
}
