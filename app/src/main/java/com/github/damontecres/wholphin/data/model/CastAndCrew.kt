package com.github.damontecres.wholphin.data.model

import android.content.Context
import org.jellyfin.sdk.model.api.PersonKind

/**
 * The people of an item split the way a details page shows them: who is on screen and who made it
 *
 * The server lists the crew after the whole cast, so on a single row the director ends up behind
 * dozens of actors.
 */
data class CastAndCrew(
    val cast: List<Person>,
    val guestStars: List<Person>,
    val crew: List<Person>,
)

/** Kinds that are shown as cast: their role is a character name, not a job */
private val castKinds = setOf(PersonKind.ACTOR, PersonKind.UNKNOWN)

/** Order of the crew row; kinds not listed follow in server order */
private val crewOrder =
    listOf(
        PersonKind.DIRECTOR,
        PersonKind.CREATOR,
        PersonKind.WRITER,
        PersonKind.PRODUCER,
        PersonKind.COMPOSER,
        PersonKind.CONDUCTOR,
    )

private fun crewRank(kind: PersonKind): Int = crewOrder.indexOf(kind).let { if (it < 0) crewOrder.size else it }

fun PersonKind.isCast(): Boolean = this in castKinds

fun PersonKind.isCrew(): Boolean = this != PersonKind.GUEST_STAR && !isCast()

/**
 * Splits [people] into cast, guest stars and crew.
 *
 * The cast and guest stars keep the server order. Crew credits of one person are merged into one
 * card, labelled with every job ("Director, Writer"), and sorted director first. The label is built
 * from the credit types rather than the role text, which on crew is often another job entirely
 * (a director credited with the role "Writer").
 *
 * A person who is both in the cast and in the crew appears in both rows.
 */
fun List<Person>.toCastAndCrew(label: (PersonKind) -> String?): CastAndCrew {
    val crew =
        filter { it.type.isCrew() }
            .groupBy { it.id }
            .values
            .map { credits ->
                val kinds = credits.map { it.type }.distinct().sortedBy(::crewRank)
                credits.first().copy(
                    type = kinds.first(),
                    role =
                        kinds
                            .mapNotNull(label)
                            .distinct()
                            .joinToString(", ")
                            .ifBlank { credits.first().role },
                )
            }.sortedBy { crewRank(it.type) }
    return CastAndCrew(
        cast = filter { it.type.isCast() },
        guestStars = filter { it.type == PersonKind.GUEST_STAR },
        crew = crew,
    )
}

fun List<Person>.toCastAndCrew(context: Context): CastAndCrew =
    toCastAndCrew { kind -> kind.stringRes?.let { context.getString(it) } }
