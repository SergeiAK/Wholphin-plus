package com.github.damontecres.wholphin.services

import com.github.damontecres.wholphin.data.SeriesTrackChoiceDao
import com.github.damontecres.wholphin.data.model.ActivationFlag
import com.github.damontecres.wholphin.data.model.JellyfinUserPreferences
import com.github.damontecres.wholphin.data.model.RelativeMediaStream
import com.github.damontecres.wholphin.data.model.SeriesTrackChoice
import com.github.damontecres.wholphin.data.model.SeriesTrackChoiceType
import com.github.damontecres.wholphin.data.model.TrackChoiceParentType
import com.github.damontecres.wholphin.preferences.AppPreferences
import com.github.damontecres.wholphin.preferences.UserPreferences
import com.github.damontecres.wholphin.test.TestTracks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.jellyfin.sdk.model.UUID
import org.jellyfin.sdk.model.api.BaseItemDto
import org.jellyfin.sdk.model.api.BaseItemKind
import org.jellyfin.sdk.model.api.MediaStreamType
import org.junit.Assert
import org.junit.Test

/**
 * Season and series track choices: looked up by the right type and matched by position
 */
class StreamChoiceServiceStcTypeTest {
    private val seasonId = UUID.randomUUID()
    private val seriesId = UUID.randomUUID()

    private val episode =
        BaseItemDto(
            id = UUID.randomUUID(),
            type = BaseItemKind.EPISODE,
            seriesId = seriesId,
            parentId = seasonId,
        )

    private val source =
        TestTracks
            .Builder()
            .addVideo()
            .addAudio(2)
            .addSubtitle(2)
            .buildMediaSourceInfo()
            .let { source ->
                val langs = listOf(null, "eng", "rus", "eng", "rus")
                source.copy(
                    mediaStreams =
                        source.mediaStreams!!.map {
                            it.copy(language = langs[it.index], isDefault = it.index == 1 || it.index == 3)
                        },
                )
            }

    private fun stc(
        type: SeriesTrackChoiceType,
        language: String,
    ) = SeriesTrackChoice(
        userId = 1,
        parentId = seasonId,
        parentType = TrackChoiceParentType.SEASON,
        type = type,
        language = language,
    )

    private fun service(): StreamChoiceService {
        val serverRepo = mockServerRepo(null, null, null)
        every { serverRepo.currentUser } returns mockk { every { rowId } returns 1 }
        val dao = mockk<SeriesTrackChoiceDao>()
        coEvery { dao.get(any(), any(), any(), SeriesTrackChoiceType.AUDIO) } returns
            listOf(stc(SeriesTrackChoiceType.AUDIO, "rus"))
        coEvery { dao.get(any(), any(), any(), SeriesTrackChoiceType.SUBTITLE) } returns
            listOf(stc(SeriesTrackChoiceType.SUBTITLE, "eng"))
        return StreamChoiceService(serverRepo, dao)
    }

    private val prefs =
        UserPreferences(AppPreferences.getDefaultInstance(), JellyfinUserPreferences())

    @Test
    fun `audio uses the audio season choice`() =
        runTest {
            val service = service()
            val stc = service.getSeriesTrackChoices(episode, SeriesTrackChoiceType.AUDIO)
            val result = service.chooseAudioStream(source, episode, null, stc, prefs)
            Assert.assertEquals(2, result?.stream?.index)
        }

    @Test
    fun `audio without given choices looks up audio choices`() =
        runTest {
            val result = service().chooseAudioStream(source, episode, null, null, prefs)
            Assert.assertEquals(2, result?.stream?.index)
            Assert.assertEquals(MediaStreamType.AUDIO, result?.stream?.type)
        }

    @Test
    fun `subtitle uses the subtitle season choice`() =
        runTest {
            val service = service()
            val stc = service.getSeriesTrackChoices(episode, SeriesTrackChoiceType.SUBTITLE)
            val result = service.chooseSubtitleStream(episode, source, null, null, stc, prefs)
            Assert.assertEquals(3, result?.stream?.index)
        }

    @Test
    fun `second track of a language is found by its position among all audio tracks`() {
        // eng, rus (default), rus: the user picked the second Russian track, the third audio track
        val audio = listOf(audio(1, "eng"), audio(2, "rus", default = true), audio(3, "rus"))
        val chosen = RelativeMediaStream.from(3, audio)!!
        Assert.assertEquals(2, chosen.relativeIndex)
        val choice =
            SeriesTrackChoice(
                userId = 1,
                itemId = UUID.randomUUID(),
                parentId = seasonId,
                type = SeriesTrackChoiceType.AUDIO,
                parentType = TrackChoiceParentType.SEASON,
                stream = chosen,
                activation = ActivationFlag.ACTIVATED,
            )
        val result = service().chooseAudioStream(audio, null, listOf(choice), prefs)
        Assert.assertEquals(3, result.stream?.index)
    }
}
