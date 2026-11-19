package com.huyvu.flashcard.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class EmblemTest {

    @Test
    fun `text emblem survives encode then decode`() {
        val emblem = Emblem.Text("あ")

        assertEquals("text:あ", emblem.encode())
        assertEquals(emblem, decodeEmblem(emblem.encode()))
    }

    @Test
    fun `icon emblem survives encode then decode`() {
        val emblem = Emblem.Icon(EmblemIcon.GLOBE)

        assertEquals("icon:globe", emblem.encode())
        assertEquals(emblem, decodeEmblem(emblem.encode()))
    }

    @Test
    fun `text emblem may contain a colon`() {
        assertEquals(Emblem.Text("a:b"), decodeEmblem("text:a:b"))
    }

    @Test
    fun `garbage decodes to question mark`() {
        assertEquals(Emblem.Text("?"), decodeEmblem("hello"))
        assertEquals(Emblem.Text("?"), decodeEmblem("icon:unknown"))
        assertEquals(Emblem.Text("?"), decodeEmblem(""))
    }
}
