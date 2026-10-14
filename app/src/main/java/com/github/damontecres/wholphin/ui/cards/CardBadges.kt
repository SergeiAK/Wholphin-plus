package com.github.damontecres.wholphin.ui.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.github.damontecres.wholphin.R
import com.github.damontecres.wholphin.data.model.BaseItem
import com.github.damontecres.wholphin.ui.FontAwesome
import com.github.damontecres.wholphin.ui.PreviewTvSpec
import com.github.damontecres.wholphin.ui.abbreviateNumber
import com.github.damontecres.wholphin.ui.theme.WholphinTheme
import org.jellyfin.sdk.model.api.BaseItemKind

/**
 * What a card shows in its top corner.
 *
 * The badges used to share one translucent plate, so an unwatched count, a number of versions
 * and an episode number looked the same and faded into light artwork. Each kind now has its
 * own look on an opaque plate:
 * - state (unwatched) is filled with the watched-check color of the theme, so the count and
 *   the check read as one indicator that turns into the check once everything is watched;
 * - versions are outlined with the theme's primary color;
 * - names (episode number) stay neutral.
 */
sealed interface CornerBadge {
    /** [season] is optional and drawn dimmed, e.g. `S2 E5` on the home page, `E5` in a season */
    data class Episode(
        val season: String?,
        val episode: String,
    ) : CornerBadge

    data class Unplayed(
        val count: String,
    ) : CornerBadge

    /** [unplayed] is null when nothing or everything in the collection is watched */
    data class Collection(
        val unplayed: String?,
        val total: String,
    ) : CornerBadge

    companion object {
        fun collection(
            unplayed: Int,
            total: Int,
        ) = Collection(
            unplayed = unplayed.takeIf { it in 1..<total }?.let(::abbreviateNumber),
            total = abbreviateNumber(total),
        )
    }
}

/** Number of items in a collection, 0 for anything else or when the server did not send it */
val BaseItem?.collectionSize: Int
    get() = if (this?.type == BaseItemKind.BOX_SET) data.childCount ?: 0 else 0

private val PlateColor = Color(0xFF121212)
private val PlateTextColor = Color(0xFFECECEC)
private val PlateMutedColor = Color(0xFF8A8A8A)
private val PlateShape = RoundedCornerShape(4.dp)
private val BadgePadding = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)

@Composable
fun CornerBadge(
    badge: CornerBadge,
    textStyle: TextStyle,
    modifier: Modifier = Modifier,
) {
    when (badge) {
        is CornerBadge.Episode -> EpisodeBadge(badge.season, badge.episode, textStyle, modifier)
        is CornerBadge.Unplayed -> UnplayedBadge(badge.count, textStyle, modifier)
        is CornerBadge.Collection -> CollectionBadge(badge.unplayed, badge.total, textStyle, modifier)
    }
}

/** The watched-check color with whichever of black or white reads better on it */
@Composable
private fun unplayedColors(): Pair<Color, Color> {
    val background = WatchedIconBackground()
    // Contrast against white beats contrast against black only below this luminance
    val content = if (background.luminance() > 0.179f) Color.Black else Color.White
    return background to content
}

@Composable
fun UnplayedBadge(
    count: String,
    textStyle: TextStyle,
    modifier: Modifier = Modifier,
) {
    val (background, content) = unplayedColors()
    Text(
        text = count,
        style = textStyle,
        color = content,
        modifier =
            modifier
                .background(background, CircleShape)
                .border(.5.dp, Color.Black, CircleShape)
                .padding(horizontal = 7.dp, vertical = 2.dp),
    )
}

@Composable
fun VersionsBadge(
    count: Int,
    textStyle: TextStyle,
    modifier: Modifier = Modifier,
) {
    val color = MaterialTheme.colorScheme.primary
    Row(
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .background(PlateColor, PlateShape)
                .border(1.dp, color, PlateShape)
                .then(BadgePadding),
    ) {
        Icon(
            painter = painterResource(R.drawable.tabler_files),
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(with(LocalDensity.current) { textStyle.fontSize.toDp() }),
        )
        Text(text = count.toString(), style = textStyle, color = color)
    }
}

@Composable
fun CollectionBadge(
    unplayed: String?,
    total: String,
    textStyle: TextStyle,
    modifier: Modifier = Modifier,
) {
    val (background, content) = unplayedColors()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .clip(PlateShape)
                .background(PlateColor)
                .border(1.dp, PlateMutedColor, PlateShape),
    ) {
        if (unplayed != null) {
            Text(
                text = unplayed,
                style = textStyle,
                color = content,
                modifier = Modifier.background(background).then(BadgePadding),
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = BadgePadding,
        ) {
            BadgeIcon(R.string.fa_layer_group, PlateTextColor, textStyle)
            Text(text = total, style = textStyle, color = PlateTextColor)
        }
    }
}

@Composable
fun EpisodeBadge(
    season: String?,
    episode: String,
    textStyle: TextStyle,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .background(PlateColor, PlateShape)
                .border(1.dp, PlateMutedColor, PlateShape)
                .then(BadgePadding),
    ) {
        if (season != null) {
            Text(text = season, style = textStyle, color = PlateMutedColor)
        }
        Text(text = episode, style = textStyle, color = PlateTextColor)
    }
}

@Composable
private fun BadgeIcon(
    glyph: Int,
    color: Color,
    textStyle: TextStyle,
) = Text(
    text = stringResource(glyph),
    color = color,
    fontFamily = FontAwesome,
    fontSize = if (textStyle.fontSize.isSp) textStyle.fontSize * .85f else 12.sp,
)

@PreviewTvSpec
@Composable
private fun CardBadgesPreview() {
    WholphinTheme {
        val style = MaterialTheme.typography.bodyMedium
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            UnplayedBadge("14", style)
            VersionsBadge(2, style)
            CollectionBadge("8", "26", style)
            CollectionBadge(null, "26", style)
            EpisodeBadge("S2", "E5", style)
        }
    }
}
