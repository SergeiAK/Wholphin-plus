package com.github.damontecres.wholphin.data.model

import org.jellyfin.sdk.model.api.PersonKind
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.UUID

class CastAndCrewTest {
    private val ids = mutableMapOf<String, UUID>()

    private fun person(
        name: String,
        type: PersonKind,
        role: String? = null,
    ) = Person(
        id = ids.getOrPut(name) { UUID.randomUUID() },
        name = name,
        role = role,
        type = type,
        imageUrl = null,
        favorite = false,
    )

    private fun split(vararg people: Person) = people.toList().toCastAndCrew { it.name.lowercase() }

    @Test
    fun crewIsSeparatedFromCastAndDirectorComesFirst() {
        val result =
            split(
                person("A", PersonKind.ACTOR, "Hero"),
                person("B", PersonKind.ACTOR, "Villain"),
                person("P", PersonKind.PRODUCER),
                person("W", PersonKind.WRITER, "Screenplay"),
                person("D", PersonKind.DIRECTOR),
            )
        assertEquals(listOf("A", "B"), result.cast.map { it.name })
        assertEquals(listOf("Hero", "Villain"), result.cast.map { it.role })
        assertEquals(listOf("D", "W", "P"), result.crew.map { it.name })
        assertEquals(emptyList<Person>(), result.guestStars)
    }

    @Test
    fun crewCreditsOfOnePersonAreMergedByType() {
        val result =
            split(
                person("X", PersonKind.WRITER, "Director"),
                person("Y", PersonKind.PRODUCER),
                person("X", PersonKind.DIRECTOR, "Writer"),
            )
        assertEquals(listOf("X", "Y"), result.crew.map { it.name })
        assertEquals(PersonKind.DIRECTOR, result.crew.first().type)
        assertEquals("director, writer", result.crew.first().role)
    }

    @Test
    fun actorWhoDirectedIsInBothRows() {
        val result =
            split(
                person("X", PersonKind.ACTOR, "Himself"),
                person("X", PersonKind.DIRECTOR),
            )
        assertEquals(listOf("X"), result.cast.map { it.name })
        assertEquals(listOf("X"), result.crew.map { it.name })
    }

    @Test
    fun guestStarsKeepServerOrder() {
        val result =
            split(
                person("G2", PersonKind.GUEST_STAR),
                person("A", PersonKind.ACTOR),
                person("G1", PersonKind.GUEST_STAR),
            )
        assertEquals(listOf("G2", "G1"), result.guestStars.map { it.name })
        assertEquals(listOf("A"), result.cast.map { it.name })
    }

    @Test
    fun unlistedCrewKindsFollowInServerOrder() {
        val result =
            split(
                person("E", PersonKind.ENGINEER),
                person("M", PersonKind.MIXER),
                person("C", PersonKind.COMPOSER),
            )
        assertEquals(listOf("C", "E", "M"), result.crew.map { it.name })
    }
}
