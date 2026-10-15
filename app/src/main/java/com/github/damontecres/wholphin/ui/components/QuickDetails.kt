package com.github.damontecres.wholphin.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.github.damontecres.wholphin.R
import com.github.damontecres.wholphin.data.model.QuickDetailsData
import com.github.damontecres.wholphin.preferences.AppThemeColors
import com.github.damontecres.wholphin.preferences.DisplayToggle
import com.github.damontecres.wholphin.ui.dot
import com.github.damontecres.wholphin.ui.formatTime
import com.github.damontecres.wholphin.ui.theme.LocalTheme
import com.github.damontecres.wholphin.ui.util.LocalClock
import com.github.damontecres.wholphin.ui.util.LocalInterfaceCustomization
import org.jellyfin.sdk.model.DateTime
import kotlin.time.Duration

/**
 * Year, runtime, time left and end time on the first line; age and ratings on the second.
 *
 * Upstream puts everything on one line, which on long (e.g. Russian) lines pushes the end time
 * or the ratings past the edge, where they are cut off without a trace. The second line is
 * only shown when there is a rating to show.
 *
 * @param showYearAndRuntime false where the year and runtime are already shown next to this,
 * as in the library list, so only the time left and the end time remain on the first line
 */
@Composable
fun QuickDetails(
    details: QuickDetailsData?,
    timeRemaining: Duration?,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.titleSmall,
    endsAt: DateTime? = null,
    showYearAndRuntime: Boolean = true,
) {
    val enabled = LocalInterfaceCustomization.current.enabledDisplayToggles
    val inlineContentMap = rememberQuickDetailsContentMap(textStyle)
    val signal = LocalTheme.current == AppThemeColors.SIGNAL
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier,
    ) {
        // Time
        Row(verticalAlignment = Alignment.CenterVertically) {
            val basic =
                when {
                    details == null -> null
                    showYearAndRuntime -> details.basic
                    else -> details.timeLeft?.let { AnnotatedString(it) }
                }
            val yearSplit = remember(basic, signal) { if (signal) basic?.let(::splitLeadingYear) else null }
            if (yearSplit != null) {
                PlateText(yearSplit.first, textStyle, Modifier.padding(end = 8.dp))
                QuickDetailsText(yearSplit.second, Modifier, textStyle, inlineContentMap)
            } else {
                QuickDetailsText(basic, Modifier, textStyle, inlineContentMap)
            }
            // After a lone year plate the plate itself separates
            val leadingDot = !basic.isNullOrEmpty() && yearSplit?.second?.isEmpty() != true
            if (timeRemaining != null) {
                TimeRemaining(timeRemaining, textStyle = textStyle, leadingDot = leadingDot)
            } else if (endsAt != null) {
                EndsAt(endsAt, textStyle = textStyle, leadingDot = leadingDot)
            }
        }
        // Age and ratings
        if (details != null) {
            val official = details.officialRating.takeIf { DisplayToggle.OFFICIAL_RATING in enabled }
            val ratings =
                listOfNotNull(
                    details.communityRating.takeIf { DisplayToggle.COMMUNITY_RATING in enabled },
                    details.criticRating.takeIf { DisplayToggle.CRITIC_RATING in enabled },
                )
            if (official != null || ratings.isNotEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // The first item and whatever follows a plate go without a dot
                    var dropDot = true
                    if (official != null) {
                        if (signal) {
                            official.text
                                .trim(' ', '\u2022')
                                .takeIf { it.isNotBlank() }
                                ?.let { PlateText(it, textStyle, Modifier.padding(end = 8.dp)) }
                        } else {
                            QuickDetailsText(official.withoutLeadingDot(), Modifier, textStyle, inlineContentMap)
                            dropDot = false
                        }
                    }
                    ratings.forEach {
                        QuickDetailsText(if (dropDot) it.withoutLeadingDot() else it, Modifier, textStyle, inlineContentMap)
                        dropDot = false
                    }
                }
            }
        }
    }
}

private const val DOT_SEPARATOR = "  \u2022  "

private val YearRegex = Regex("""^\d{4}(\s*[-\u2013]\s*\S*)?$""")

/**
 * Split a leading year (or year range) off the dotted line, so it can be shown on a plate
 */
private fun splitLeadingYear(str: AnnotatedString): Pair<String, AnnotatedString>? {
    val text = str.text
    val sep = text.indexOf(DOT_SEPARATOR)
    val lead = if (sep < 0) text else text.substring(0, sep)
    if (!YearRegex.matches(lead.trim())) return null
    val rest = if (sep < 0) AnnotatedString("") else str.subSequence(sep + DOT_SEPARATOR.length, text.length)
    return lead.trim() to rest
}

private fun AnnotatedString.withoutLeadingDot(): AnnotatedString =
    if (text.startsWith(DOT_SEPARATOR)) subSequence(DOT_SEPARATOR.length, text.length) else this

/**
 * A value on a plate of its own, as the Signal theme shows the year and the age rating.
 *
 * Upstream folds them into the dotted line; a year or a certificate is a label, not prose,
 * and reads better boxed.
 */
@Composable
private fun PlateText(
    text: String,
    textStyle: TextStyle,
    modifier: Modifier = Modifier,
) = Text(
    text = text,
    color = MaterialTheme.colorScheme.onSurface,
    style = textStyle,
    maxLines = 1,
    modifier =
        modifier
            .background(
                MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(4.dp),
            ).padding(horizontal = 6.dp, vertical = 1.dp),
)

@NonRestartableComposable
@Composable
fun QuickDetailsText(
    str: AnnotatedString?,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.titleSmall,
    inlineContentMap: Map<String, InlineTextContent> = rememberQuickDetailsContentMap(textStyle),
) = str?.let {
    Text(
        text = str,
        color = MaterialTheme.colorScheme.onSurface,
        style = textStyle,
        inlineContent = inlineContentMap,
        maxLines = 1,
        modifier = modifier,
    )
}

@Composable
fun rememberQuickDetailsContentMap(textStyle: TextStyle = MaterialTheme.typography.titleSmall) =
    remember(textStyle) {
        mapOf(
            "star" to
                InlineTextContent(
                    Placeholder(
                        textStyle.fontSize,
                        textStyle.fontSize,
                        PlaceholderVerticalAlign.TextCenter,
                    ),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        tint = FilledStarColor,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                    )
                },
            "rotten" to
                InlineTextContent(
                    Placeholder(
                        textStyle.fontSize,
                        textStyle.fontSize,
                        PlaceholderVerticalAlign.TextCenter,
                    ),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_rotten_tomatoes_rotten),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        tint = Color.Unspecified,
                    )
                },
            "fresh" to
                InlineTextContent(
                    Placeholder(
                        textStyle.fontSize,
                        textStyle.fontSize,
                        PlaceholderVerticalAlign.TextCenter,
                    ),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_rotten_tomatoes_fresh),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        tint = Color.Unspecified,
                    )
                },
        )
    }

@Composable
fun TimeRemaining(
    remaining: Duration,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.titleSmall,
    leadingDot: Boolean = true,
) {
    val now by LocalClock.current.now
    EndsAt(
        endsAt = now.plusSeconds(remaining.inWholeSeconds),
        modifier = modifier,
        textStyle = textStyle,
        leadingDot = leadingDot,
    )
}

@Composable
fun EndsAt(
    endsAt: DateTime,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.titleSmall,
    leadingDot: Boolean = true,
) {
    val resources = LocalResources.current
    val context = LocalContext.current
    val remainingStr =
        remember(endsAt, resources, context, leadingDot) {
            val endTimeStr = formatTime(context, endsAt)
            buildAnnotatedString {
                if (leadingDot) dot()
                // Never break "ends at 15:08", so the time can't be wrapped away on its own
                append(resources.getString(R.string.ends_at, endTimeStr).replace(' ', '\u00A0'))
            }
        }
    Text(
        text = remainingStr,
        style = textStyle,
        maxLines = 1,
        softWrap = false,
        modifier = modifier,
    )
}
