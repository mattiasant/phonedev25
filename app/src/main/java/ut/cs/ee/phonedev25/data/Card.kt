package ut.cs.ee.phonedev25.data

//Loome klassi kaartidele, sest nii on andmete mõttes kergem ning see on staatiline.
data class Card(
    val cardSuit: String,       // Mast: "Ärtu", "Poti", "Ruutu", "Risti"
    val cardName: String,       // Väärtus/Nimi: "2", "3", ..., "10", "Soldat", "Kuninganna", "Kuningas", "Äss"
    val cardStrength: Int,         // Kaardi tugevus
    val drawableID: Int  // Viide pildile
)
