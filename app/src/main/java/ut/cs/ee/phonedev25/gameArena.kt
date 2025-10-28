package ut.cs.ee.phonedev25

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isEmpty
import kotlinx.coroutines.delay
import ut.cs.ee.phonedev25.data.Card
import ut.cs.ee.phonedev25.data.Deck
import android.widget.Button
import android.widget.Toast

class gameArena : AppCompatActivity() {

    private lateinit var enemyCards: MutableList<Card>
    private lateinit var myCards: MutableList<Card>
    private lateinit var drawableDeck: MutableList<Card>

    private var selectedImageView: ImageView? = null
    private var myCurrentCard: Card? = null
    private lateinit var cardPlacement: ImageView
    private lateinit var rundekaartTextView: TextView
    private lateinit var kaitsekaartTextView: TextView
    private lateinit var placeButton: Button
    private lateinit var randomCardImage: ImageView
    private lateinit var trumpCardImage: ImageView
    private var trumpSuit: String? = ""
    private var revealedTrumpCard: Card? = null

    private lateinit var gameInfoTextView: TextView // Viide 'gameInfo' tekstile
    private lateinit var killButton: Button // Viide 'killButton' nupule

    // Muutuja, mis hoiab meeles, mis kaart on ründav kaart
    private var attackingCard: Card? = null
    // Muutuja, mis hoiab meeles, mis kaart on kaitsev kaart
    private var defendingCard: Card? = null
    private enum class GameTurn {
        PLAYER_ATTACK,  // Mängija valib kaardi ründeks
        ENEMY_DEFEND,   // AI valib kaardi kaitseks
        ENEMY_ATTACK,   // AI valib kaardi ründeks
        PLAYER_DEFEND   // Mängija valib kaardi kaitseks
    }

    // Määra algne olek: Mängija alustab rünnakut
    private var currentTurn: GameTurn = GameTurn.PLAYER_ATTACK

    private lateinit var cardsLeftTextView: TextView

    private lateinit var enemyCardsLayout: LinearLayout
    private lateinit var myCardsLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game_arena)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //------------------------------CARD SUFFLE----------------------------

        var kaardipakk: List<Card> = Deck.fullDeck
        // * MUUDATUS NR. 2: Paki loome nüüd MutableList'ina
        drawableDeck = kaardipakk.shuffled().toMutableList()
        var kaartideArv = drawableDeck.size // Var on muutuv

        //------------------------------GAME------------------------------------

        // vaated
        cardPlacement = findViewById(R.id.card_placement)
        rundekaartTextView = findViewById(R.id.rundekaart)
        kaitsekaartTextView = findViewById(R.id.kaitsekaart)
        placeButton = findViewById(R.id.placeButton)
        randomCardImage = findViewById(R.id.randomCard)
        cardsLeftTextView = findViewById(R.id.cardsLeft)

        trumpCardImage = findViewById(R.id.trumpCard)
        gameInfoTextView = findViewById(R.id.gameInfo)
        killButton = findViewById(R.id.killButton)

        setupTrumpCardTakeListener(myCardsLayout)
        setupKillButtonListener(myCardsLayout) // Uus funktsioon "Kill" nupule

        // Uuenda UI-d vastavalt algsele olekule
        updateGameUI()


        // Jagame vastase kaardid ja eemaldame need pakist
        enemyCards = drawableDeck.take(5).toMutableList()
        drawableDeck.subList(0, 5).clear()

        // ** VEA PARANDUS 2: INISTIALISEERIME MÄNGIJA KAARDID **
        myCards = drawableDeck.take(5).toMutableList()
        // Eemaldame ka mängija kaardid pakist
        drawableDeck.subList(0, 5).clear()


        for (i in 1..5) {
            addCardImageToLayout(enemyCardsLayout, R.drawable.cardback)
        }

        for (card in myCards) {
            addCardImageToLayout(myCardsLayout, card.drawableID, card) // LISA , card
        }
        cardsLeftTextView.text = "${drawableDeck.size}" // Kasutame drawableDeck.size


        if (drawableDeck.isNotEmpty()) {
            val drawnTrumpCard = drawableDeck.removeAt(0)

            // Salvestame nii masti kui ka kaardi enda
            trumpSuit = drawnTrumpCard.cardSuit
            revealedTrumpCard = drawnTrumpCard // <--- LISA SEE RIDA

            trumpCardImage.setImageResource(drawnTrumpCard.drawableID)
            Toast.makeText(this, "Trumbimast on: $trumpSuit", Toast.LENGTH_LONG).show()
        }

        // * MUUDATUS NR. 4: Kutsume välja uue kaardi võtmise funktsiooni
        setupPlaceButtonListener(myCardsLayout)
        setupRandomCardListener(myCardsLayout)

    }

    private fun updateGameUI() {
        when (currentTurn) {
            GameTurn.PLAYER_ATTACK -> {
                gameInfoTextView.text = "Sinu kord rünnata!"
                placeButton.isEnabled = true
                killButton.isEnabled = false // Ei saa "võtta", kui ise ründad
            }
            GameTurn.ENEMY_DEFEND -> {
                gameInfoTextView.text = "Vastane kaitseb..."
                placeButton.isEnabled = false // Keela nupud, kuni AI mõtleb
                killButton.isEnabled = false
            }
            GameTurn.ENEMY_ATTACK -> {
                gameInfoTextView.text = "Vastane ründab!"
                placeButton.isEnabled = false // AI ründab, sina ei saa "Place" panna
                killButton.isEnabled = false
            }
            GameTurn.PLAYER_DEFEND -> {
                gameInfoTextView.text = "Sinu kord kaitsta!"
                placeButton.isEnabled = true // "Place" nupp on nüüd kaitseks
                killButton.isEnabled = true // "Kill" nupp on kaartide võtmiseks
            }
        }
    }

    //Loome funktsiooni, mis lisab kaardid layouti.
    // TÄHELEPANU: Lisa funktsioonile kolmas parameeter: Card objekt, mis võib olla ka null!
    private fun addCardImageToLayout(layout: LinearLayout, drawableId: Int, card: Card? = null) {
        // 1. Veendu, et layout on horisontaalne, et kaalud töötaksid laiuse jaotamisel
        if (layout.orientation != LinearLayout.HORIZONTAL) {
            layout.orientation = LinearLayout.HORIZONTAL
        }

        val imageView = ImageView(this)
        imageView.setImageResource(drawableId)

        val density = resources.displayMetrics.density

        // Määra kaardi proportsioon, et kaardi pilt ei veniks
        imageView.adjustViewBounds = true
        imageView.scaleType = ImageView.ScaleType.FIT_CENTER // Või FIT_XY olenevalt soovist

        // Kui funktsioon sai kaasa Card objekti (st kaart ei ole vastase oma), siis
        // lisame selle ImageView külge tag'iks.
        if (card != null) {
            imageView.tag = card
        }

        // Lisame klikikuulaja AINULT mängija kaartide layoutile
        if (layout.id == R.id.myCardsLayout) {

            imageView.setOnClickListener { clickedView ->
                // Kui palju kaarti üles liigutada (negatiivne väärtus liigub üles)
                // Kasutame 30dp väärtust, mis on teisendatud piksliteks
                val moveUpDistancePx = (-30 * density)

                // Kontrollime, kas klikitud kaart ON juba valitud kaart
                if (clickedView == selectedImageView) {
                    // JAH: See ON juba valitud. Tühistame valiku.
                    clickedView.translationY = 0f
                    selectedImageView = null
                } else {
                    // EI: See EI OLE valitud kaart. Valime selle.

                    // 1. Liiguta eelmine valitud kaart (kui see eksisteeris) tagasi alla
                    selectedImageView?.translationY = 0f

                    // 2. Liiguta ÄSJA klikitud kaart üles
                    clickedView.translationY = moveUpDistancePx

                    // 3. Salvesta see vaade kui uus valitud kaart
                    selectedImageView = clickedView as ImageView
                }
            }
        }

        // 2. Kasuta kaalumist (weight) laiuse automaatseks jaotamiseks
        val kaal = 1f

        // Määra kaardile marginaalid
        val marginEndPx = (4 * density).toInt() // 4dp vahet kaartide vahel

        // Loome LayoutParams-id
        val params = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            kaal
        )

        // Lisa marginaal
        params.marginEnd = marginEndPx

        imageView.layoutParams = params

        // 3. Lõpuks lisage pilt layouti
        layout.addView(imageView)
    }

    private fun setupRandomCardListener(myCardsLayout: LinearLayout) {
        randomCardImage.setOnClickListener {
            // 1. Kontrolli, kas käes on vähem kui 4 kaarti
            if (myCards.size >= 5) {
                Toast.makeText(this, "Sul on juba 5 või rohkem kaarti käes!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2. Kontrolli, kas kaarte on pakis
            if (drawableDeck.isEmpty()) {
                Toast.makeText(this, "Kaardipakk on tühi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 3. Võta kaardipaki esimene kaart
            val newCard = drawableDeck.removeAt(0) // Eemaldab esimese kaardi pakist

            // 4. Lisa kaart mängija kätte
            myCards.add(newCard)
            addCardImageToLayout(myCardsLayout, newCard.drawableID, newCard)

            // 5. Uuenda kaardipaki arvu
            cardsLeftTextView.text = "${drawableDeck.size}"

            Toast.makeText(this, "Võtsid kaardi: ${newCard.cardSuit} ${newCard.cardName}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupTrumpCardTakeListener(myCardsLayout: LinearLayout) {
        trumpCardImage.setOnClickListener {
            // Kontroll 1: Kas kaart on juba ära võetud?
            if (revealedTrumpCard == null) {
                Toast.makeText(this, "Trumbikaart on juba võetud!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Kontroll 2: Kas pakk ON tühi?
            if (drawableDeck.isEmpty()) {
                // Pakk on tühi, võtame kaardi endale
                val cardToTake = revealedTrumpCard!! // !! sest me kontrollisime null'i esimeses if-is

                // Lisa kaart andmetesse
                myCards.add(cardToTake)
                // Lisa kaart UI-sse
                addCardImageToLayout(myCardsLayout, cardToTake.drawableID, cardToTake)

                Toast.makeText(this, "Võtsid trumbikaardi: ${cardToTake.cardSuit} ${cardToTake.cardName}", Toast.LENGTH_SHORT).show()

                // Tühjenda trumbikaardi pesa
                revealedTrumpCard = null
                trumpCardImage.setImageResource(0) // 0 eemaldab pildi
                trumpCardImage.isClickable = false // Keela edasised klikid

            } else {
                // Pakk pole veel tühi
                Toast.makeText(this, "Pakk pole veel tühi!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupPlaceButtonListener(myCardsLayout: LinearLayout) {
        placeButton.setOnClickListener {

            // Kontrolli, kas kaart on valitud
            if (selectedImageView == null) {
                Toast.makeText(this, "Vali kõigepealt kaart!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // --- MINU RÜNDE LOOGIKA ---
            if (currentTurn == GameTurn.PLAYER_ATTACK) {

                val selectedCard = selectedImageView?.tag as Card

                // Eemalda kaart käest (UI ja andmed)
                myCardsLayout.removeView(selectedImageView)
                myCards.remove(selectedCard)
                selectedImageView = null

                // Pane kaart lauale
                attackingCard = selectedCard
                cardPlacement.setImageResource(selectedCard.drawableID)
                rundekaartTextView.text = "Rünne: ${selectedCard.cardSuit} ${selectedCard.cardName}"
                kaitsekaartTextView.text = "Kaitse: ..."

                // Muuda olekut: Vastane peab kaitsma
                currentTurn = GameTurn.ENEMY_DEFEND
                updateGameUI() // Uuenda nuppe ja teksti

                // Kutsu välja AI loogika
                runEnemyLogic()
            }

            // --- MINU KAITSE LOOGIKA ---
            else if (currentTurn == GameTurn.PLAYER_DEFEND) {

                val selectedCard = selectedImageView?.tag as Card

                // **Siia pead lisama reegli, kas sinu kaart lööb AI kaardi (attackingCard) ära!**
                val canDefend = kasKaartLobA(selectedCard, attackingCard!!) // Pead ise looma selle funktsiooni!

                if (canDefend) {
                    // Kaart sobib kaitseks
                    myCardsLayout.removeView(selectedImageView)
                    myCards.remove(selectedCard)
                    selectedImageView = null

                    defendingCard = selectedCard
                    // VÕI uuenda kaitsekaardi pilti, kui sul on eraldi pesa?
                    // Praegu uuendame lihtsalt teksti:
                    kaitsekaartTextView.text = "Kaitse: ${selectedCard.cardSuit} ${selectedCard.cardName}"

                    // Kaardid on laualt maas (biito)
                    Toast.makeText(this, "Kaitsesid edukalt!", Toast.LENGTH_SHORT).show()
                    clearTableAfterTurn(attackerWon = false) // false = kaitsja võitis

                    // Muuda olekut: Mängija ründab uuesti
                    currentTurn = GameTurn.PLAYER_ATTACK
                    updateGameUI()

                } else {
                    // Kaart ei sobi
                    Toast.makeText(this, "See kaart ei löö ründavat kaarti ära!", Toast.LENGTH_SHORT).show()
                    // Ära tee midagi, lase mängijal uus kaart valida
                }
            }
        }
    }

    private fun setupKillButtonListener(myCardsLayout: LinearLayout) {
        killButton.setOnClickListener {
            // "Kill" nupp töötab ainult siis, kui mängija peab kaitsma
            if (currentTurn != GameTurn.PLAYER_DEFEND) {
                return@setOnClickListener
            }

            Toast.makeText(this, "Võtsid kaardi endale!", Toast.LENGTH_SHORT).show()

            // Lisa ründav kaart (ja potentsiaalselt teised laual olevad kaardid) endale kätte
            myCards.add(attackingCard!!)
            addCardImageToLayout(myCardsLayout, attackingCard!!.drawableID, attackingCard!!)

            // Tühjenda laud
            clearTableAfterTurn(attackerWon = true) // true = ründaja (AI) võitis

            // Muuda olekut: Mängija ründab (kuna ta võttis kaardid)
            currentTurn = GameTurn.PLAYER_ATTACK
            updateGameUI()
        }
    }

    private fun runEnemyLogic() {

        // --- AI KAITSE LOOGIKA ---
        if (currentTurn == GameTurn.ENEMY_DEFEND) {

            // Leia kaart, millega rünnak (attackingCard) ära lüüa
            val cardToBeat = attackingCard!!

            // **AI LOOGIKA ALGUS (VÄGA LIHTNE NÄIDE):**
            // 1. Leia kõik kaardid AI käes, mis sobivad kaitseks
            val validDefendingCards = enemyCards.filter { cardInHand ->
                kasKaartLobA(cardInHand, cardToBeat) // Kasutame sama reeglit!
            }

            // 2. Vali neist nõrgim (et säästa tugevaid kaarte)
            val chosenCard = validDefendingCards.minByOrNull { it.cardStrength }

            // 3. Tee otsus
            if (chosenCard != null) {
                // **AI SUUTIS KAITSTA**
                enemyCards.remove(chosenCard)
                // Eemalda AI UI-st üks kaart (suvaline, sest näeme vaid tagakülgi)
                (enemyCardsLayout.getChildAt(0) as? ImageView)?.let {
                    enemyCardsLayout.removeView(it)
                }

                defendingCard = chosenCard
                kaitsekaartTextView.text = "Kaitse: ${chosenCard.cardSuit} ${chosenCard.cardName}"

                Toast.makeText(this, "Vastane kaitses!", Toast.LENGTH_SHORT).show()
                clearTableAfterTurn(attackerWon = false) // false = kaitsja (AI) võitis

                // Muuda olekut: AI ründab (sest tema kaitses edukalt)
                currentTurn = GameTurn.ENEMY_ATTACK
                updateGameUI()
                runEnemyLogic() // Kutsu AI loogika uuesti, et ta ründaks

            } else {
                // **AI EI SUUTNUD KAITSTA (Võtab kaardi)**
                Toast.makeText(this, "Vastane võttis kaardi!", Toast.LENGTH_SHORT).show()
                enemyCards.add(cardToBeat)

                addCardImageToLayout(enemyCardsLayout, R.drawable.cardback)

                clearTableAfterTurn(attackerWon = true) // true = ründaja (mängija) võitis

                // Muuda olekut: Mängija ründab uuesti
                currentTurn = GameTurn.PLAYER_ATTACK
                updateGameUI()
            }
        }

        // --- AI RÜNDE LOOGIKA ---
        else if (currentTurn == GameTurn.ENEMY_ATTACK) {

            // **AI LOOGIKA ALGUS (VÄGA LIHTNE NÄIDE):**
            // 1. Vali ründamiseks nõrgim kaart (mis pole trump)
            val attackCard = enemyCards.filter { it.cardSuit != trumpSuit }.minByOrNull { it.cardStrength }
                ?: enemyCards.minByOrNull { it.cardStrength } // Või suvaline nõrgim, kui trumbid on

            if (attackCard != null) {
                // AI ründab
                enemyCards.remove(attackCard)
                (enemyCardsLayout.getChildAt(0) as? ImageView)?.let {
                    enemyCardsLayout.removeView(it)
                }

                attackingCard = attackCard
                cardPlacement.setImageResource(attackCard.drawableID)
                rundekaartTextView.text = "Rünne: ${attackCard.cardSuit} ${attackCard.cardName}"
                kaitsekaartTextView.text = "Kaitse: ..."

                // Muuda olekut: Mängija peab kaitsma
                currentTurn = GameTurn.PLAYER_DEFEND
                updateGameUI()

            } else {
                // AI-l pole kaarte, mäng läbi?
                Toast.makeText(this, "Vastasel said kaardid otsa!", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Kontrollib, kas kaitsev kaart lööb ründava kaardi.
     * PEAD SELLE ISE TÄIENDAMA VASTAVALT OMA REEGLITELE!
     */
    private fun kasKaartLobA(defendingCard: Card, cardToBeat: Card): Boolean {
        // Reegel 1: Ründav kaart on trump
        if (cardToBeat.cardSuit == trumpSuit) {
            // Ainult tugevam trump lööb
            return (defendingCard.cardSuit == trumpSuit && defendingCard.cardStrength > cardToBeat.cardStrength)
        }

        // Reegel 2: Ründav kaart EI ole trump
        else {
            // Variant A: Sama mast, tugevam kaart
            if (defendingCard.cardSuit == cardToBeat.cardSuit && defendingCard.cardStrength > cardToBeat.cardStrength) {
                return true
            }
            // Variant B: Kaitsev kaart on trump
            if (defendingCard.cardSuit == trumpSuit) {
                return true
            }
        }

        // Muul juhul kaart ei sobi
        return false
    }

    /**
     * Tühjendab laua pärast käigu lõppu.
     */
    private fun clearTableAfterTurn(attackerWon: Boolean) {
        // TODO: Võiksid lisada siia viivituse (delay), et mängija näeks kaarte

        cardPlacement.setImageResource(0) // 0 = tühi pilt
        rundekaartTextView.text = "Rünne:"
        kaitsekaartTextView.text = "Kaitse:"

        // Kes saab kaardid? (Hetkel me ei tee "hunnikut", seega kaardid lihtsalt kaovad)

        attackingCard = null
        defendingCard = null
    }
}