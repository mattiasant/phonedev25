package ut.cs.ee.phonedev25

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ut.cs.ee.phonedev25.data.Card
import ut.cs.ee.phonedev25.data.Deck
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class gameArena : AppCompatActivity() {

    private lateinit var enemyCards: MutableList<Card>
    private lateinit var myCards: MutableList<Card>
    private lateinit var drawableDeck: MutableList<Card>

    private var selectedImageView: ImageView? = null
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
        enemyCardsLayout = findViewById(R.id.enemyCardsLayout)
        myCardsLayout = findViewById(R.id.myCardsLayout)
        cardPlacement = findViewById(R.id.card_placement)
        rundekaartTextView = findViewById(R.id.rundekaart)
        kaitsekaartTextView = findViewById(R.id.kaitsekaart)
        placeButton = findViewById(R.id.placeButton)
        randomCardImage = findViewById(R.id.randomCard)
        cardsLeftTextView = findViewById(R.id.cardsLeft)

        //------------------------------Trump part------------------------------------

        trumpCardImage = findViewById(R.id.trumpCard)
        gameInfoTextView = findViewById(R.id.gameInfo)

        setupTrumpCardTakeListener(myCardsLayout)

        //------------------------------UI update------------------------------------

        // Uuenda UI-d vastavalt algsele olekule
        updateGameUI()

        // Jagame vastase kaardid ja eemaldame need pakist
        enemyCards = drawableDeck.take(5).toMutableList()
        drawableDeck.subList(0, 5).clear()

        // ** VEA PARANDUS 2: INISTIALISEERIME MÄNGIJA KAARDID **
        myCards = drawableDeck.take(5).toMutableList()
        // Eemaldame ka mängija kaardid pakist
        drawableDeck.subList(0, 5).clear()

        //------------------------------GIVE CARDS------------------------------------

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

        //------------------------------GAME LISTENER------------------------------------

        // * MUUDATUS NR. 4: Kutsume välja uue kaardi võtmise funktsiooni
        setupPlaceButtonListener(myCardsLayout)
        setupRandomCardListener(myCardsLayout)
        setupTrumpCardTakeListener(myCardsLayout)
        setupCardPlacementListener(myCardsLayout)
    }

    //Game disables buttons when AI is thinking and doing its part.
    private fun updateGameUI() {
        when (currentTurn) {
            GameTurn.PLAYER_ATTACK -> {
                gameInfoTextView.text = "Sinu kord rünnata!"
                placeButton.isEnabled = true
            }
            GameTurn.ENEMY_DEFEND -> {
                gameInfoTextView.text = "Vastane kaitseb..."
                placeButton.isEnabled = false // Keela nupud, kuni AI mõtleb
            }
            GameTurn.ENEMY_ATTACK -> {
                gameInfoTextView.text = "Vastane ründab!"
                placeButton.isEnabled = false // AI ründab, sina ei saa "Place" panna
            }
            GameTurn.PLAYER_DEFEND -> {
                gameInfoTextView.text = "Sinu kord kaitsta!"
                placeButton.isEnabled = true // "Place" nupp on nüüd kaitseks
            }
        }
    }

    //Inserts cards to layout panel.
    private fun addCardImageToLayout(layout: LinearLayout, drawableId: Int, card: Card? = null) {
        //Checks layout orientation
        if (layout.orientation != LinearLayout.HORIZONTAL) {
            layout.orientation = LinearLayout.HORIZONTAL
        }

        val imageView = ImageView(this)
        imageView.setImageResource(drawableId)

        val density = resources.displayMetrics.density

        // Checks if the card is not streched.
        imageView.adjustViewBounds = true
        imageView.scaleType = ImageView.ScaleType.FIT_CENTER // Või FIT_XY olenevalt soovist

        if (card != null) {
            imageView.tag = card
        }

        // Click listener for that the user knows that a card has been selected
        if (layout.id == R.id.myCardsLayout) {

            imageView.setOnClickListener { clickedView ->
                // Moves card upward about 30 pixels
                val moveUpDistancePx = (-30 * density)

                // Checks if the card is selected
                if (clickedView == selectedImageView) {
                    // If its been selected then it ignores
                    clickedView.translationY = 0f
                    selectedImageView = null
                } else {
                    //Otherwise bring back/keep down
                    selectedImageView?.translationY = 0f
                    clickedView.translationY = moveUpDistancePx
                    selectedImageView = clickedView as ImageView
                }
            }
        }

        // Määra kaardile marginaalid
        val marginEndPx = (4 * density).toInt() // 4dp vahet kaartide vahel

        // Create LayoutParams-id
        val params = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1f
        )

        // Add margin so the placement looks smooth or better. Not together pushed.
        params.marginEnd = marginEndPx
        imageView.layoutParams = params
        layout.addView(imageView) //Adds the card picture
    }
    private fun setupRandomCardListener(myCardsLayout: LinearLayout) {
        randomCardImage.setOnClickListener {
            // Check if I have 5 cards on hand
            if (myCards.size >= 5) {
                Toast.makeText(this, "You already have 5 cards on hand", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Checks if there are any cards left at the playing deck
            if (drawableDeck.isEmpty()) {
                Toast.makeText(this, "The card deck is empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //When we take a new card we take from the top, so we remove the first card on the array list.
            val newCard = drawableDeck.removeAt(0)

            // Give it to the player
            myCards.add(newCard)
            addCardImageToLayout(myCardsLayout, newCard.drawableID, newCard)

            // Update card deck number.
            cardsLeftTextView.text = "${drawableDeck.size}"

            Toast.makeText(this, "Võtsid kaardi: ${newCard.cardSuit} ${newCard.cardName}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupTrumpCardTakeListener(myCardsLayout: LinearLayout) {
        trumpCardImage.setOnClickListener {
            // Check if there exists a trump card.
            if (revealedTrumpCard == null) {
                Toast.makeText(this, "Trumbikaart on juba võetud!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // IF the deck is empty then the last trump card will be taken by the player or enemy.
            if (drawableDeck.isEmpty()) {
                //We take the card
                val cardToTake = revealedTrumpCard!! // !! sest me kontrollisime null'i esimeses if-is

                myCards.add(cardToTake)
                // add it to our cards
                addCardImageToLayout(myCardsLayout, cardToTake.drawableID, cardToTake)

                Toast.makeText(this, "Võtsid trumbikaardi: ${cardToTake.cardSuit} ${cardToTake.cardName}", Toast.LENGTH_SHORT).show()

                revealedTrumpCard = null //empty the card thingy
                trumpCardImage.setImageResource(0) // removes picture
                trumpCardImage.isClickable = false // disables the clicking

            } else {
                // If the deck is not empty then the player cannot take it
                Toast.makeText(this, "The Deck is not empty yet!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupPlaceButtonListener(myCardsLayout: LinearLayout) {
        placeButton.setOnClickListener {

            // Check if the card has been seleted.
            if (selectedImageView == null) {
                Toast.makeText(this, "Chose a card before placing it", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ATTACKING LOGIC
            if (currentTurn == GameTurn.PLAYER_ATTACK) { //if its my turn

                val selectedCard = selectedImageView?.tag as Card //use the selected card
                myCardsLayout.removeView(selectedImageView) //removes the card from layout
                myCards.remove(selectedCard) //removes it from the list
                selectedImageView = null //deltes the image

                attackingCard = selectedCard //put it to the table and show it
                cardPlacement.setImageResource(selectedCard.drawableID)
                rundekaartTextView.text = "Attacking: ${selectedCard.cardSuit} ${selectedCard.cardName}"
                kaitsekaartTextView.text = "Defending: ..." //

                currentTurn = GameTurn.ENEMY_DEFEND //Its now enemy's turn
                updateGameUI() // update the UI and text

                runEnemyLogic() //Call out the enemy's turn
            }

            //DEFENCE LOGIC
            else if (currentTurn == GameTurn.PLAYER_DEFEND) { //Its my turn

                //You need to defend.
                if (defendingCard != null) {
                    Toast.makeText(this, "You need to kill or attack again if there is no cards on the table", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                val selectedCard = selectedImageView?.tag as Card //Select the card imgae

                // **Siia pead lisama reegli, kas sinu kaart lööb AI kaardi (attackingCard) ära!**
                val canDefend = kasKaartLobA(selectedCard, attackingCard!!) // Pead ise looma selle funktsiooni!

                if (canDefend) { //if the card can defend
                    myCardsLayout.removeView(selectedImageView)
                    myCards.remove(selectedCard)
                    selectedImageView = null

                    defendingCard = selectedCard

                    //Update text
                    kaitsekaartTextView.text = "Defence: ${selectedCard.cardSuit} ${selectedCard.cardName}"
                    cardPlacement.setImageResource(selectedCard.drawableID) // Kuva kaitsva kaardi pilt laua

                    Toast.makeText(this, "Defence sucsessful!", Toast.LENGTH_SHORT).show()

                    //Wait and change turns
                    clearTableAfterTurnAndContinue(
                        nextTurn = GameTurn.PLAYER_ATTACK,
                        delayAfterClear = 5000L,
                        drawForPlayer = false, // Sina (kaitsja) tõmbad
                        drawForEnemy = true   // AI (ründaja) tõmbab
                    )

                } else {
                    // If the selected card cannot defend or kill the enemy's card.
                    Toast.makeText(this, "This card cannot defend, try another!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //This is waiting mechanism, so the enemy cards just don't teleport around in 1ms
    private fun clearTableAfterTurnAndContinue(nextTurn: GameTurn, delayAfterClear: Long, drawForPlayer: Boolean, drawForEnemy: Boolean) {
        lifecycleScope.launch { //Waiting and updates the mechanics and UI in the game
            kotlinx.coroutines.delay(delayAfterClear)

            cardPlacement.setImageResource(0) //Delete the old text to replace with the new.
            rundekaartTextView.text = "Rünne:"
            kaitsekaartTextView.text = "Kaitse:"
            attackingCard = null
            defendingCard = null

            // 3. JÄTKA MÄNGUGA
            if (drawForPlayer) {
                drawCards(myCards)
            }
            if (drawForEnemy) {
                drawCards(enemyCards)
            }
            currentTurn = nextTurn
            updateGameUI()
            if (nextTurn == GameTurn.ENEMY_ATTACK || nextTurn == GameTurn.ENEMY_DEFEND) {
                runEnemyLogic()
            }
        }
    }

    private fun runEnemyLogic() {

        // --- AI KAITSE LOOGIKA ---
        if (currentTurn == GameTurn.ENEMY_DEFEND) { //Its enemy's turn

            val cardToBeat = attackingCard!! // Find card to beat players card

            // 1. Find cards if the enemy can defend it.
            val validDefendingCards = enemyCards.filter { cardInHand ->
                kasKaartLobA(cardInHand, cardToBeat) // Kasutame sama reeglit!
            }

            val chosenCard = validDefendingCards.minByOrNull { it.cardStrength } //if can defend choose the weakest to keep the stronger

            if (chosenCard != null) { //Make a desision and update UI and cards in the layout and list

                enemyCards.remove(chosenCard)
                (enemyCardsLayout.getChildAt(0) as? ImageView)?.let {
                    enemyCardsLayout.removeView(it)
                }

                defendingCard = chosenCard
                kaitsekaartTextView.text = "Defence: ${chosenCard.cardSuit} ${chosenCard.cardName}"
                Toast.makeText(this, "Enemy Defended", Toast.LENGTH_SHORT).show()

                clearTableAfterTurnAndContinue(
                    nextTurn = GameTurn.ENEMY_ATTACK,
                    delayAfterClear = 2000L,
                    drawForPlayer = true, // Sina (ründaja) tõmbad
                    drawForEnemy = true   // AI (kaitsja) tõmbab
                )
                return
            } else {
                // AI cannot defend and takes a new card.
                val cardToBeat = attackingCard!! // takes the attacking card
                Toast.makeText(this, "Enemy took a card", Toast.LENGTH_SHORT).show()

                enemyCards.add(cardToBeat)
                addCardImageToLayout(enemyCardsLayout, R.drawable.cardback)

                clearTableAfterTurnAndContinue(
                    nextTurn = GameTurn.PLAYER_ATTACK,
                    delayAfterClear = 1000L,
                    drawForPlayer = true, // Sina (ründaja) tõmbad
                    drawForEnemy = false  // AI (kes korjas) EI TÕMBA
                )
                return // End the enemy's round
            }
        } else if (currentTurn == GameTurn.ENEMY_ATTACK) {
            val cardToAttackWith = enemyCards
                .filter { it.cardSuit != trumpSuit } // checks for the trump suit
                .minByOrNull { it.cardStrength } ?: enemyCards.minByOrNull { it.cardStrength } // or the weakest trump card

            if (cardToAttackWith != null) {
                enemyCards.remove(cardToAttackWith) //remove the card from the enemy's deck list
                //Update the enemy cards UI layout
                (enemyCardsLayout.getChildAt(0) as? ImageView)?.let {
                    enemyCardsLayout.removeView(it)
                }

                // 3. Pane kaart lauale ründeks
                attackingCard = cardToAttackWith
                cardPlacement.setImageResource(cardToAttackWith.drawableID)
                rundekaartTextView.text = "Attacking: ${cardToAttackWith.cardSuit} ${cardToAttackWith.cardName}"
                kaitsekaartTextView.text = "Defence: ..."

                currentTurn = GameTurn.PLAYER_DEFEND //our time to defend
                updateGameUI()

                Toast.makeText(this, "Enemy attacked with: ${cardToAttackWith.cardName}", Toast.LENGTH_SHORT).show()
            } else {
                currentTurn = GameTurn.PLAYER_ATTACK //if enemy cannot attack then take a new card
                updateGameUI()
            }
        }
    }

    private fun drawCards(cardsList: MutableList<Card>) {

        val targetSize = 5 //checks the size

        while (cardsList.size < targetSize && drawableDeck.isNotEmpty()) {
            val newCard = drawableDeck.removeAt(0)
            cardsList.add(newCard)

            // ----- PARANDUS ALGAB SIIT -----
            if (cardsList == myCards) {
                // Lisa uus kaart MÄNGIJA layouti
                addCardImageToLayout(myCardsLayout, newCard.drawableID, newCard)
            } else if (cardsList == enemyCards) {
                // Lisa kaarditagus VASTASE layouti
                addCardImageToLayout(enemyCardsLayout, R.drawable.cardback)
            }
        }
        cardsLeftTextView.text = "${drawableDeck.size}"
    }
    private fun kasKaartLobA(defendingCard: Card, cardToBeat: Card): Boolean {
        if (defendingCard.cardSuit == trumpSuit) {
            if (cardToBeat.cardSuit != trumpSuit) {
                return true
            }
            else { // cardToBeat.cardSuit == trumpSuit
                return defendingCard.cardStrength > cardToBeat.cardStrength //Checks if the card kill a stronger trump
            }
        } else { // defendingCard.cardSuit != trumpSuit basically if the trump card cannot kill a stronger trump card
            if (cardToBeat.cardSuit == trumpSuit) {
                return false
            }

            if (defendingCard.cardSuit == cardToBeat.cardSuit) { //Checks the suit
                return defendingCard.cardStrength > cardToBeat.cardStrength
            }
            return false //if its not trump suit then you cannot kill or do nothing
        }
    }

    private fun setupCardPlacementListener(myCardsLayout: LinearLayout) {
        cardPlacement.setOnClickListener {

            // 1. Kontrolli, kas on üldse mängija kaitse kord
            if (currentTurn != GameTurn.PLAYER_DEFEND) {
                // Kui ei ole sinu kaitse kord, siis see klikk ei tee midagi
                // (Võid lisada Tosti, nt: "Saad kaarte võtta vaid enda kaitse korra ajal")
                return@setOnClickListener
            }

            // 2. Kontrolli, kas laual on kaart, mida võtta
            // (See peaks alati tõsi olema PLAYER_DEFEND olukorras, aga on hea kontroll)
            if (attackingCard == null) {
                return@setOnClickListener // Laual pole ründavat kaarti
            }

            // 3. Mängija võtab ründava kaardi endale
            val cardToTake = attackingCard!! // Teame, et see pole null

            myCards.add(cardToTake) // Lisa kaart mängija andmetesse
            addCardImageToLayout(myCardsLayout, cardToTake.drawableID, cardToTake) // Lisa kaart mängija UI-sse

            // (Märkus: Sinu praegune loogika ei toeta mitme kaardi laual olemist,
            // seega võtame ainult 'attackingCard'. Kui 'defendingCard' oleks olemas,
            // tähendaks see, et kaitse oli edukas, aga me oleme siin ebaõnnestumise haru.)

            Toast.makeText(this, "Võtsid kaardi. Vastane ründab uuesti.", Toast.LENGTH_LONG).show()

            // 4. Kasuta olemasolevat laua tühjendamise loogikat
            // See on sarnane AI ebaõnnestunud kaitsele, aga vastupidi.
            // Ründaja (AI) võitis, mängija kaotas korra.
            // Järgmine kord on JÄLLE vastase rünnak.
            clearTableAfterTurnAndContinue(
                nextTurn = GameTurn.ENEMY_ATTACK,
                delayAfterClear = 5000L,
                drawForPlayer = false, // Sina (kes korjas) EI TÕMBA
                drawForEnemy = true    // AI (ründaja) tõmbab
            )
        }
    }
}