package ut.cs.ee.phonedev25

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.delay
import ut.cs.ee.phonedev25.data.Card
import ut.cs.ee.phonedev25.data.Deck

class gameArena : AppCompatActivity() {

    private lateinit var enemyCards: List<Card>
    private lateinit var myCards: List<Card>

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

        val kaardipakk: List<Card> = Deck.fullDeck //võtame need 36 kaarti ja paneme ühte listi.
        val segatud: List<Card> = kaardipakk.shuffled() //segame ära
        val kaartideArv = kaardipakk.size
        println(kaartideArv)

        //------------------------------GAME------------------------------------

        // vaated
        val enemyCardsLayout: LinearLayout = findViewById(R.id.enemyCardsLayout)
        val myCardsLayout: LinearLayout = findViewById(R.id.myCardsLayout)
        val cardsLeft: TextView = findViewById(R.id.cardsLeft) //Määrame arvude asukoha


        enemyCards = segatud.take(5) // vastane võtab esimesed 5 kaarti
        myCards = segatud.subList(5,10) // ma võtan 5 kaarti nüüd pärast seda mida tema võttis


        for (i in 1..5) {
            addCardImageToLayout(enemyCardsLayout, R.drawable.cardback)
        }

        for (card in myCards) {
            addCardImageToLayout(myCardsLayout, card.drawableID)
        }
        kaartideArv - 10 // arvutame maha võetud 10 kaarti.
        println(kaartideArv)
        cardsLeft.text = "$kaartideArv" //Paneme tekstiks numbri

    }



    //Loome funktsiooni, mis lisab kaardid layouti.
    private fun addCardImageToLayout(layout: LinearLayout, drawableId: Int) {
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

        // 2. Kasuta kaalumist (weight) laiuse automaatseks jaotamiseks

        // Kui kaartide arv on N, siis iga kaardi kaal on 1,
        // mis jagab kogu layouti laiuse N võrdseks osaks.
        val kaal = 1f

        // Määra kaardile marginaalid
        val marginEndPx = (4 * density).toInt() // 4dp vahet kaartide vahel

        // Loome LayoutParams-id
        // Laius: 0dp (sest weight hakkab seda ise arvutama)
        // Kõrgus: wrap_content (et hoida pildi proportsioone koos adjustViewBounds-iga)
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
}