package ut.cs.ee.phonedev25

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ut.cs.ee.phonedev25.data.StatsManager

class GameResultPage : AppCompatActivity() {

    companion object {
        const val EXTRA_IS_WIN = "extra_is_win"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game_result_page)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        // Read win/loss value
        val isWin = intent.getBooleanExtra(EXTRA_IS_WIN, false)

        // Views
        val resultTitle = findViewById<TextView>(R.id.resultTitle)
        val resultMessage = findViewById<TextView>(R.id.resultMessage)
        val playAgainButton = findViewById<Button>(R.id.playAgainButton)
        val mainMenuButton = findViewById<Button>(R.id.mainMenuButton)
        val viewStatsButton = findViewById<Button>(R.id.viewStatsButton)

        // Set text based on result
        if (isWin) {
            resultTitle.setText(R.string.result_victory_title)
            resultMessage.setText(R.string.result_victory_message)
        } else {
            resultTitle.setText(R.string.result_defeat_title)
            resultMessage.setText(R.string.result_defeat_message)
        }

        // Button actions
        playAgainButton.setOnClickListener { btn ->
            AnimationManager.animateButtonClick(btn, this)
            val intent = Intent(this, Join_Game::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            AnimationManager.applyPageTransition(
                this,
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
            finish()
        }

        mainMenuButton.setOnClickListener { btn ->
            AnimationManager.animateButtonClick(btn, this)
            val intent = Intent(this, HomePage::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            AnimationManager.applyPageTransition(
                this,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
            finish()
        }

        viewStatsButton.setOnClickListener { btn ->
            AnimationManager.animateButtonClick(btn, this)
            startActivity(Intent(this, StatisticsPage::class.java))
            AnimationManager.applyPageTransition(
                this,
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
        }
    }
}
