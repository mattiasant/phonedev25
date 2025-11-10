package ut.cs.ee.phonedev25

import org.junit.Test

import org.junit.Assert.*
import ut.cs.ee.phonedev25.data.Card
import ut.cs.ee.phonedev25.data.Deck


class DeckTest {
    @Test
    fun testFullDeckIntegrity() {
        val deck = Deck.fullDeck

        // 1. Check total number of cards
        assertEquals(36, deck.size) // 4 suits * 9 cards each = 36

        // 2. Check each suit has 9 cards
        val suits = listOf("Ärtu", "Poti", "Risti", "Rutu")
        for (suit in suits) {
            val cardsOfSuit = deck.filter { it.cardSuit == suit }
            assertEquals(9, cardsOfSuit.size)

            // 3. Check cardStrengths are in ascending order for that suit
            val strengths = cardsOfSuit.map { it.cardStrength }
            assertEquals(listOf(6, 7, 8, 9, 10, 11, 12, 13, 14), strengths)
        }

        // 4. Check that cardName and cardStrength match correctly for a few examples
        val artoAce = deck.find { it.cardSuit == "Ärtu" && it.cardName == "Äss" }
        assertNotNull(artoAce)
        assertEquals(14, artoAce?.cardStrength)

        val potiSoldat = deck.find { it.cardSuit == "Poti" && it.cardName == "Soldat" }
        assertNotNull(potiSoldat)
        assertEquals(11, potiSoldat?.cardStrength)

    }

    @Test
    fun testCardEqualityAndHashCode() {
        val card1 = Card("Poti", "Äss", 14, R.drawable.poti1)
        val card2 = Card("Poti", "Äss", 14, R.drawable.poti1)
        val card3 = Card("Risti", "Kuningas", 13, R.drawable.risti13)

        // Equality check
        assertEquals(card1, card2)
        assertNotEquals(card1, card3)

        // Hash code check
        assertEquals(card1.hashCode(), card2.hashCode())
        assertNotEquals(card1.hashCode(), card3.hashCode())
    }
}