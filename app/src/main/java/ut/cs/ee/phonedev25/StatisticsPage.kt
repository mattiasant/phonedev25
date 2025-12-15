package ut.cs.ee.phonedev25

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ut.cs.ee.phonedev25.data.StatsManager

class StatisticsPage : AppCompatActivity() {

    private lateinit var cardsPlacedText: TextView
    private lateinit var cardsPickedUpText: TextView
    private lateinit var winrateText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_statistics_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Back button with animation
        findViewById<ImageView>(R.id.imageView).setOnClickListener { btn ->
            AnimationManager.animateBackButton(btn as ImageView, this) {
                finish()
                AnimationManager.applyPageTransition(this, R.anim.slide_in_left, R.anim.slide_out_right)
            }
        }

        cardsPlacedText   = findViewById(R.id.textView10)
        cardsPickedUpText = findViewById(R.id.textView6)
        winrateText       = findViewById(R.id.textView12)

        // Apply entrance animations to text views
        AnimationManager.animateEntrance(cardsPlacedText)
        AnimationManager.animateEntrance(cardsPickedUpText)
        AnimationManager.animateEntrance(winrateText)

        // Load stats from StatsManager
        updateStatistics()
    }

    private fun updateStatistics() {
        val context = this

        val cardsPlaced   = StatsManager.getStats(context).cardsPlaced
        val cardsPickedUp = StatsManager.getStats(context).cardsPickedUp
        val wins          = StatsManager.getStats(context).gamesWon
        val losses        = StatsManager.getStats(context).gamesLost
        val totalGames    = wins + losses

        cardsPlacedText.text = cardsPlaced.toString()
        cardsPickedUpText.text = cardsPickedUp.toString()

        winrateText.text = if (totalGames == 0) {
            "0%"
        } else {
            val rate = (wins.toDouble() / totalGames * 100).toInt()
            "$rate%"
        }
    }
}