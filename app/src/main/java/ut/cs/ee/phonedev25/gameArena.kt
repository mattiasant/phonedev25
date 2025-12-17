package ut.cs.ee.phonedev25

import android.content.Intent
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
import ut.cs.ee.phonedev25.data.StatsManager
import android.view.View
import androidx.activity.OnBackPressedCallback

class gameArena : AppCompatActivity() {
    private var activePowerUp: String = PowerUpManager.NONE
    private var powerUpUsed: Boolean = false
    private val playedCardsHistory: MutableList<Card> = mutableListOf() //This is for remembering what cards we have placed.
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
    private lateinit var gameInfoTextView: TextView // This is for game info
    private var attackingCard: Card? = null
    private var defendingCard: Card? = null
    private val PLAYER_CARD_ANIM_DURATION = 350L
    private val ENEMY_RESPONSE_DELAY = 300L

    private enum class GameTurn {
        PLAYER_ATTACK,  // Mängija valib kaardi ründeks
        ENEMY_DEFEND,   // AI valib kaardi kaitseks
        ENEMY_ATTACK,   // AI valib kaardi ründeks
        PLAYER_DEFEND   // Mängija valib kaardi kaitseks
    }
    private var currentTurn: GameTurn = GameTurn.PLAYER_ATTACK //By rule player starts for right now.
    private lateinit var cardsLeftTextView: TextView
    private lateinit var enemyCardsLayout: LinearLayout
    private lateinit var myCardsLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activePowerUp = PowerUpManager.getActivePowerUp(this)
        PowerUpManager.resetPowerUpUsage(this)
        powerUpUsed = false
        enableEdgeToEdge()
        setContentView(R.layout.activity_game_arena)

        //------------------------------DISABLE BACK BUTTON----------------------------

        /*
        //This disables the back button, so the player cannot cheat if bad cards. The only way to exit is play to the end or leave the app
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Toast.makeText(this@gameArena, "Cannot leave, sorry", Toast.LENGTH_SHORT)
                    .show()
            }
        }) */

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //------------------------------CARD SUFFLE----------------------------

        var kaardipakk: List<Card> = Deck.fullDeck
        drawableDeck = kaardipakk.shuffled().toMutableList()
        var kaartideArv = drawableDeck.size // Var can change.

        //------------------------------GAME------------------------------------

        // Views
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

        updateGameUI()

        // Give cards to enemy
        enemyCards = drawableDeck.take(5).toMutableList()
        drawableDeck.subList(0, 5).clear()

        myCards = drawableDeck.take(5).toMutableList()
        // remove cards from the deck.
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

            // save the mast
            trumpSuit = drawnTrumpCard.cardSuit
            revealedTrumpCard = drawnTrumpCard

            trumpCardImage.setImageResource(drawnTrumpCard.drawableID)
            Toast.makeText(this, "The trump is: $trumpSuit", Toast.LENGTH_LONG).show()
        }

        if (activePowerUp != PowerUpManager.NONE) {
            Toast.makeText(this, "Power-up ready: $activePowerUp (click to activate)", Toast.LENGTH_LONG).show()
        }
        //------------------------------GAME LISTENER------------------------------------

        setupPlaceButtonListener(myCardsLayout)
        setupRandomCardListener(myCardsLayout)
        setupTrumpCardTakeListener(myCardsLayout)
        setupCardPlacementListener(myCardsLayout)
        setupPowerUpButtonListener()
    }

    //------------------------------PICK UP CARDS WHEN CANNOT ATTACK------------------------------------

    private fun recordPlayedCard(card: Card) { //This function records the placed cards and order
        playedCardsHistory.add(card)
    }

    private fun getCardsToPickUp(): List<Card> { // Pick up cards logic
        val count = playedCardsHistory.size

        if (count <= 1) return emptyList()

        return if (count > 5) { //If there is more than 5 cards then take 5
            playedCardsHistory.takeLast(5)
        } else { //if there 5 or less than take every card except the first card.
            playedCardsHistory.drop(1)
        }
    }

    private fun removeCardsFromHistory(cards: List<Card>) { //Will remove the
        playedCardsHistory.removeAll(cards)
    }

    // Add this new function to handle power-up button clicks:
    private fun setupPowerUpButtonListener() {
        // Assuming you have a button with id "powerUpButton" in your layout
        val powerUpButton = findViewById<Button>(R.id.powersButton)

        // Show/hide button based on power-up availability
        if (activePowerUp == PowerUpManager.NONE || powerUpUsed) {
            powerUpButton.visibility = View.GONE
        } else {
            powerUpButton.visibility = View.VISIBLE
            powerUpButton.text = "Use Power Up"
        }

        powerUpButton.setOnClickListener {
            if (!powerUpUsed && activePowerUp != PowerUpManager.NONE) {
                applyPowerUpEffect()
                powerUpButton.visibility = View.GONE
            } else {
                Toast.makeText(this, "Power-up already used!", Toast.LENGTH_SHORT).show()
            }
        }
    }
    //Game disables buttons when AI is thinking and doing its part.
    private fun updateGameUI() {
        when (currentTurn) {
            GameTurn.PLAYER_ATTACK -> {
                gameInfoTextView.text = "Your time to attack!"
                placeButton.isEnabled = true
            }
            GameTurn.ENEMY_DEFEND -> {
                gameInfoTextView.text = "Enemy is defending"
                placeButton.isEnabled = false
            }
            GameTurn.ENEMY_ATTACK -> {
                gameInfoTextView.text = "Enemy attacked"
                placeButton.isEnabled = false
            }
            GameTurn.PLAYER_DEFEND -> {
                gameInfoTextView.text = "Your turn to defend"
                placeButton.isEnabled = true
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
                    AnimationManager.animateCardDeselect(clickedView, this)
                    clickedView.elevation =0f
                    selectedImageView = null
                } else {
                    //Otherwise bring back/keep down
                    selectedImageView?.translationY = 0f
                    selectedImageView?.elevation = 0f
                    AnimationManager.animateCardSelect(clickedView, this)
                    clickedView.elevation = 20f
                    selectedImageView = clickedView as ImageView
                }
            }
        }

        // Put marginals to the card layout
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
        imageView.post {
            AnimationManager.animateCardDraw(imageView, this)
        }

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
            checkForWinOrLoss()
            addCardImageToLayout(myCardsLayout, newCard.drawableID, newCard)
            StatsManager.addCardPickedUp(this)


            // Update card deck number.
            cardsLeftTextView.text = "${drawableDeck.size}"

            Toast.makeText(this, "You took a card: ${newCard.cardSuit} ${newCard.cardName}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupTrumpCardTakeListener(myCardsLayout: LinearLayout) {
        trumpCardImage.setOnClickListener {
            // Check if there exists a trump card.
            if (revealedTrumpCard == null) {
                Toast.makeText(this, "The trump card has been taken", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // IF the deck is empty then the last trump card will be taken by the player or enemy.
            if (drawableDeck.isEmpty()) {
                //We take the card
                val cardToTake = revealedTrumpCard!!

                myCards.add(cardToTake)
                checkForWinOrLoss()
                // add it to our cards
                addCardImageToLayout(myCardsLayout, cardToTake.drawableID, cardToTake)
                StatsManager.addCardPickedUp(this)


                Toast.makeText(this, "You took the trump card: ${cardToTake.cardSuit} ${cardToTake.cardName}", Toast.LENGTH_SHORT).show()

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

                recordPlayedCard(selectedCard) //We remember the placed card

                val placedView = selectedImageView!!
                val sourceView = selectedImageView!!
                val card = sourceView.tag as Card

                AnimationManager.animateCardFlyToTable(
                    activity = this,
                    sourceView = sourceView,
                    targetView = cardPlacement,
                    drawableRes = card.drawableID
                ) {
                    myCardsLayout.removeView(sourceView)
                    cardPlacement.setImageResource(card.drawableID)
                }


                myCards.remove(selectedCard) //removes it from the list
                checkForWinOrLoss()
                selectedImageView = null //deltes the image
                StatsManager.addCardPlaced(this) //stat card placed +1

                attackingCard = selectedCard //put it to the table and show it
                rundekaartTextView.text = "Attacking: ${selectedCard.cardSuit} ${selectedCard.cardName}"

                kaitsekaartTextView.text = "Defending: ..."

                currentTurn = GameTurn.ENEMY_DEFEND //Its now enemy's turn
                updateGameUI() // update the UI and text

                lifecycleScope.launch {
                    kotlinx.coroutines.delay(PLAYER_CARD_ANIM_DURATION + ENEMY_RESPONSE_DELAY)
                    runEnemyLogic()
                }
                //Call out the enemy's turn
            }

            //DEFENCE LOGIC
            else if (currentTurn == GameTurn.PLAYER_DEFEND) { //Its my turn
                if (defendingCard != null) {
                    if (selectedImageView != null) {
                        // Clean the table or just remove the old card picture from the table
                        cardPlacement.setImageResource(0)
                        attackingCard = null
                        defendingCard = null
                        // Give cards
                        drawCards(myCards)
                        drawCards(enemyCards)
                        // Do attack
                        val selectedCard = selectedImageView?.tag as Card
                        // Remove the attacked card from the hand
                        val placedView = selectedImageView!!
                        val sourceView = selectedImageView!!
                        val card = sourceView.tag as Card

                        AnimationManager.animateCardFlyToTable(
                            activity = this,
                            sourceView = sourceView,
                            targetView = cardPlacement,
                            drawableRes = card.drawableID
                        ) {
                            myCardsLayout.removeView(sourceView)
                            cardPlacement.setImageResource(card.drawableID)
                        }

                        myCards.remove(selectedCard)
                        checkForWinOrLoss()
                        selectedImageView = null
                        StatsManager.addCardPlaced(this)

                        attackingCard = selectedCard
                        cardPlacement.setImageResource(selectedCard.drawableID)
                        rundekaartTextView.text = "Attacking: ${selectedCard.cardSuit} ${selectedCard.cardName}"
                        kaitsekaartTextView.text = "Defending: ..."

                        currentTurn = GameTurn.ENEMY_DEFEND
                        updateGameUI()

                        lifecycleScope.launch {
                            kotlinx.coroutines.delay(PLAYER_CARD_ANIM_DURATION + ENEMY_RESPONSE_DELAY)
                            runEnemyLogic()
                        }

                    } else {
                        clearTableAfterTurnAndContinue(
                            nextTurn = GameTurn.PLAYER_ATTACK,
                            delayAfterClear = 0L,
                            drawForPlayer = true,
                            drawForEnemy = true
                        )
                    }
                    return@setOnClickListener
                }
                if (selectedImageView == null) {
                    Toast.makeText(this, "You need to choose the card before pressing the button", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val selectedCard = selectedImageView?.tag as Card //Select the card imgae

                if (attackingCard == null) {
                    Toast.makeText(this, "There is not card to attack with!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val canDefend = kasKaartLobA(selectedCard, attackingCard!!)

                if (canDefend) { //if the card can defend
                    val placedView = selectedImageView!!
                    val sourceView = selectedImageView!!
                    val card = sourceView.tag as Card

                    AnimationManager.animateCardFlyToTable(
                        activity = this,
                        sourceView = sourceView,
                        targetView = cardPlacement,
                        drawableRes = card.drawableID
                    ) {
                        myCardsLayout.removeView(sourceView)
                        cardPlacement.setImageResource(card.drawableID)
                    }

                    myCards.remove(selectedCard)
                    checkForWinOrLoss()
                    selectedImageView = null

                    defendingCard = selectedCard

                    recordPlayedCard(selectedCard) //We remember

                    //Update text
                    kaitsekaartTextView.text = "Defending: ${selectedCard.cardSuit} ${selectedCard.cardName}"
                    cardPlacement.setImageResource(selectedCard.drawableID)
                    placeButton.isEnabled = false
                    gameInfoTextView.text = "Preparing next attack…"

                    lifecycleScope.launch {
                        kotlinx.coroutines.delay(PLAYER_CARD_ANIM_DURATION + 200L)

                        currentTurn = GameTurn.PLAYER_ATTACK
                        updateGameUI()
                        gameInfoTextView.text = "Your time to attack!"
                    }

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

            //playedCardsHistory.clear() //Cleans the tabel after the new attack/defence, unless we took up 5 cards

            //cardPlacement.setImageResource(0) //Delete the old text to replace with the new.
            rundekaartTextView.text = "Attacking:"
            kaitsekaartTextView.text = "Defending:"
            attackingCard = null
            defendingCard = null

            if (drawForPlayer) {
                drawCards(myCards)
            }
            if (drawForEnemy) {
                drawCards(enemyCards)
            }
            currentTurn = nextTurn
            updateGameUI()
            if (nextTurn == GameTurn.ENEMY_ATTACK || nextTurn == GameTurn.ENEMY_DEFEND) {
                kotlinx.coroutines.delay(1000L)
                runEnemyLogic()
            }
        }
    }

    private fun runEnemyLogic() {
        if (currentTurn == GameTurn.ENEMY_DEFEND) { //Its enemy's turn and its locic.
            val cardToBeat = attackingCard!! // Find card to beat players card
            // 1. Find cards if the enemy can defend it.
            val validDefendingCards = enemyCards.filter { cardInHand ->
                kasKaartLobA(cardInHand, cardToBeat)
            }

            val chosenCard = validDefendingCards.minByOrNull { it.cardStrength } //if can defend choose the weakest to keep the stronger

            if (chosenCard != null) { //Make a desision and update UI and cards in the layout and list

                enemyCards.remove(chosenCard)
                checkForWinOrLoss()
                (enemyCardsLayout.getChildAt(0) as? ImageView)?.let { enemyCardView ->

                    AnimationManager.animateCardFlyToTable(
                        activity = this,
                        sourceView = enemyCardView,
                        targetView = cardPlacement,
                        drawableRes = R.drawable.cardback
                    ) {
                        enemyCardsLayout.removeView(enemyCardView)
                        cardPlacement.setImageResource(chosenCard.drawableID)
                    }
                    cardPlacement.rotationY = 90f
                    cardPlacement.animate()
                        .rotationY(0f)
                        .setDuration(200)
                        .start()

                }



                defendingCard = chosenCard

                recordPlayedCard(chosenCard) //We remember the placed card

                kaitsekaartTextView.text = "Defending: ${chosenCard.cardSuit} ${chosenCard.cardName}"
                cardPlacement.setImageResource(chosenCard.drawableID)
                cardPlacement.scaleX = 0.7f
                cardPlacement.scaleY = 0.7f
                cardPlacement.alpha = 0f

                cardPlacement.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .alpha(1f)
                    .setDuration(250)
                    .start()

                updateGameUI()

                Toast.makeText(this, "Enemy Defended", Toast.LENGTH_SHORT).show()

                clearTableAfterTurnAndContinue(
                    nextTurn = GameTurn.ENEMY_ATTACK,
                    delayAfterClear = 2000L,
                    drawForPlayer = true,
                    drawForEnemy = true
                )
                return
            } else {
                val cardsToTake = getCardsToPickUp()

                if (cardsToTake.isNotEmpty()) {
                    enemyCards.addAll(cardsToTake)
                    removeCardsFromHistory(cardsToTake) // Eemaldame ajaloost!

                    for (i in cardsToTake.indices) {
                        addCardImageToLayout(enemyCardsLayout, R.drawable.cardback)
                    }
                    Toast.makeText(this, "Vastane võttis ${cardsToTake.size} kaarti", Toast.LENGTH_SHORT).show()
                }

                checkForWinOrLoss() //Added check for win or loss

                clearTableAfterTurnAndContinue(
                    nextTurn = GameTurn.PLAYER_ATTACK,
                    delayAfterClear = 2000L,
                    drawForPlayer = true,
                    drawForEnemy = false
                )
                return // End the enemy's round
            }
        } else if (currentTurn == GameTurn.ENEMY_ATTACK) {
            val cardToAttackWith = enemyCards
                .filter { it.cardSuit != trumpSuit } // checks for the trump suit
                .minByOrNull { it.cardStrength } ?: enemyCards.minByOrNull { it.cardStrength } // or the weakest trump card

            if (cardToAttackWith != null) {
                enemyCards.remove(cardToAttackWith) //remove the card from the enemy's deck list
                checkForWinOrLoss()
                //Update the enemy cards UI layout
                (enemyCardsLayout.getChildAt(0) as? ImageView)?.let { enemyCardView ->

                    AnimationManager.animateCardFlyToTable(
                        activity = this,
                        sourceView = enemyCardView,
                        targetView = cardPlacement,
                        drawableRes = R.drawable.cardback
                    ) {
                        enemyCardsLayout.removeView(enemyCardView)
                        cardPlacement.setImageResource(cardToAttackWith.drawableID)
                    }
                }

                attackingCard = cardToAttackWith

                recordPlayedCard(cardToAttackWith)

                cardPlacement.setImageResource(cardToAttackWith.drawableID)
                rundekaartTextView.text = "Attacking: ${cardToAttackWith.cardSuit} ${cardToAttackWith.cardName}"
                kaitsekaartTextView.text = "Defending: ..."

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
        checkForWinOrLoss()
        val targetSize = 5 //checks the size

        while (cardsList.size < targetSize && drawableDeck.isNotEmpty()) {
            val newCard = drawableDeck.removeAt(0)
            cardsList.add(newCard)
        }

        cardsLeftTextView.text = "${drawableDeck.size}"

        if (cardsList === myCards) {
            myCardsLayout.removeAllViews()
            for (card in myCards) {
                addCardImageToLayout(myCardsLayout, card.drawableID, card)
            }
        } else if (cardsList === enemyCards) {
            enemyCardsLayout.removeAllViews()
            for (i in 0 until enemyCards.size) {
                addCardImageToLayout(enemyCardsLayout, R.drawable.cardback)
            }
        }
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
            if (currentTurn != GameTurn.PLAYER_DEFEND) {
                return@setOnClickListener
            }

            val cardsToTake = getCardsToPickUp()

            if (cardsToTake.isEmpty()) {
                return@setOnClickListener
            }

            myCards.addAll(cardsToTake)

            removeCardsFromHistory(cardsToTake)

            checkForWinOrLoss()

            for (card in cardsToTake) { //We will update the card pictures.
                addCardImageToLayout(myCardsLayout, card.drawableID, card)
            }
            StatsManager.addCardPickedUp(this)

            Toast.makeText(this, "You took ${cardsToTake.size} cards up, its now enemy's turn to attack.", Toast.LENGTH_LONG).show()

            clearTableAfterTurnAndContinue(
                nextTurn = GameTurn.ENEMY_ATTACK,
                delayAfterClear = 2000L,
                drawForPlayer = false,
                drawForEnemy = true
            )
        }
    }

    private fun goToResultPage(isWin: Boolean) {
        val intent = Intent(this, GameResultPage::class.java)
        intent.putExtra(GameResultPage.EXTRA_IS_WIN, isWin)
        startActivity(intent)
        finish()
    }

    private fun checkForWinOrLoss() {

        // Win condition: player has no cards left and deck is empty
        if (myCards.isEmpty() && drawableDeck.isEmpty()) {
            StatsManager.addWin(this)
            goToResultPage(isWin = true)
            return
        }

        // Loss condition: enemy has no cards left and deck is empty
        if (enemyCards.isEmpty() && drawableDeck.isEmpty()) {
            StatsManager.addLoss(this)
            goToResultPage(isWin = false)
            return
        }
    }

    private fun applyPowerUpEffect() {
        if (powerUpUsed || activePowerUp == PowerUpManager.NONE) {
            return // Already used or no power-up active
        }

        when (activePowerUp) {
            PowerUpManager.INSTANT_TRIUMPH -> {
                // Win the game instantly
                Toast.makeText(this, "Instant Triumph activated! You win!", Toast.LENGTH_LONG).show()
                StatsManager.addWin(this)
                PowerUpManager.markPowerUpUsed(this)
                powerUpUsed = true
                StatsManager.addWin(this)
                goToResultPage(isWin = true)
            }

            PowerUpManager.GENESIS_FORGE -> {
                // Create a random trump card in hand
                if (!powerUpUsed) {
                    // Find trump cards in the deck
                    val availableTrumps = drawableDeck.filter { it.cardSuit == trumpSuit }

                    if (availableTrumps.isNotEmpty()) {
                        val randomTrump = availableTrumps.random() // Take a random trump card
                        drawableDeck.remove(randomTrump) //Remove the trump card duplicate from the deck.
                        myCards.add(randomTrump) // Add it to player's hand
                        myCardsLayout.removeAllViews() // Update UI
                        for (card in myCards) {
                            addCardImageToLayout(myCardsLayout, card.drawableID, card)
                        }

                        cardsLeftTextView.text = "${drawableDeck.size}"

                        Toast.makeText(this, "Genesis Forge: Created ${randomTrump.cardName} of $trumpSuit!", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Genesis Forge: No trump cards available!", Toast.LENGTH_SHORT).show()
                    }
                    PowerUpManager.markPowerUpUsed(this)
                    powerUpUsed = true
                }
            }
            PowerUpManager.HYPER_THINKER -> {
                if (!powerUpUsed) { // Player draws a card
                    if (drawableDeck.isNotEmpty()) {
                        val newCard = drawableDeck.removeAt(0)
                        myCards.add(newCard)

                        // Update UI
                        myCardsLayout.removeAllViews()
                        for (card in myCards) {
                            addCardImageToLayout(myCardsLayout, card.drawableID, card)
                        }

                        cardsLeftTextView.text = "${drawableDeck.size}"

                        Toast.makeText(this, "Hyper-Thinker: Drew ${newCard.cardName} of ${newCard.cardSuit}!", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Hyper-Thinker: Deck is empty!", Toast.LENGTH_SHORT).show()
                    }

                    PowerUpManager.markPowerUpUsed(this)
                    powerUpUsed = true
                }
            }

            PowerUpManager.CLUELESS_CHAOS -> {
                // Enemy draws a card
                if (!powerUpUsed) {
                    if (drawableDeck.isNotEmpty()) {
                        val newCard = drawableDeck.removeAt(0)
                        enemyCards.add(newCard)

                        // Update enemy UI
                        enemyCardsLayout.removeAllViews()
                        for (i in 0 until enemyCards.size) {
                            addCardImageToLayout(enemyCardsLayout, R.drawable.cardback)
                        }

                        cardsLeftTextView.text = "${drawableDeck.size}"

                        Toast.makeText(this, "Clueless Chaos: Enemy drew a card!", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Clueless Chaos: Deck is empty!", Toast.LENGTH_SHORT).show()
                    }

                    PowerUpManager.markPowerUpUsed(this)
                    powerUpUsed = true
                }
            }
        }
    }
}