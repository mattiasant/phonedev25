package ut.cs.ee.phonedev25.data

import androidx.compose.ui.node.Ref
import ut.cs.ee.phonedev25.R


object Deck {
    // Card(Mast,Nimi,Tugevus,Pildiasukoht)
    // Poti1.png on Poti Äss ja tugevusega 14, jne
    val fullDeck: List<Card> = listOf(

        //ARTU 6-14
        Card("Ärtu", "6", 6, R.drawable.artu6),
        Card("Ärtu", "7", 7, R.drawable.artu7),
        Card("Ärtu", "8", 8, R.drawable.artu8),
        Card("Ärtu", "9", 9, R.drawable.artu9),
        Card("Ärtu", "10", 10, R.drawable.artu10),
        Card("Ärtu", "Soldat", 11, R.drawable.artu11),
        Card("Ärtu", "Emand", 12, R.drawable.artu12),
        Card("Ärtu", "Kuningas", 13, R.drawable.artu13),
        Card("Ärtu", "Äss", 14, R.drawable.artu1),

        //POTI 6-14
        Card("Poti", "6", 6, R.drawable.poti6),
        Card("Poti", "7", 7, R.drawable.poti7),
        Card("Poti", "8", 8, R.drawable.poti8),
        Card("Poti", "9", 9, R.drawable.poti9),
        Card("Poti", "10", 10, R.drawable.poti10),
        Card("Poti", "Soldat", 11, R.drawable.poti11),
        Card("Poti", "Emand", 12, R.drawable.poti12),
        Card("Poti", "Kuningas", 13, R.drawable.poti13),
        Card("Poti", "Äss", 14, R.drawable.poti1),

        //RISTI 6-14
        Card("Risti", "6", 6, R.drawable.risti6),
        Card("Risti", "7", 7, R.drawable.risti7),
        Card("Risti", "8", 8, R.drawable.risti8),
        Card("Risti", "9", 9, R.drawable.risti9),
        Card("Risti", "10", 10, R.drawable.risti10),
        Card("Risti", "Soldat", 11, R.drawable.risti11),
        Card("Risti", "Emand", 12, R.drawable.risti12),
        Card("Risti", "Kuningas", 13, R.drawable.risti13),
        Card("Risti", "Äss", 14, R.drawable.risti1),

        //RUUTU 6-14
        Card("Ruutu", "6", 6, R.drawable.rutu6),
        Card("Ruutu", "7", 7, R.drawable.rutu7),
        Card("Ruutu", "8", 8, R.drawable.rutu8),
        Card("Ruutu", "9", 9, R.drawable.rutu9),
        Card("Ruutu", "10", 10, R.drawable.rutu10),
        Card("Ruutu", "Soldat", 11, R.drawable.rutu11),
        Card("Ruutu", "Emand", 12, R.drawable.rutu12),
        Card("Ruutu", "Kuningas", 13, R.drawable.rutu13),
        Card("Ruutu", "Äss", 14, R.drawable.rutu1),

    )
}