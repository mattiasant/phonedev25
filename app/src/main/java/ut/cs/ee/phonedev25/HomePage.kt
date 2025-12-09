package ut.cs.ee.phonedev25

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Animatable
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HomePage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_page)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ---------------------------
        // PLAY BUTTON
        // ---------------------------
        findViewById<Button>(R.id.playNupp).setOnClickListener { btn ->
            AnimationManager.animateButtonClick(btn, this)

            val intent = Intent(this, Join_Game::class.java)
            startActivity(intent)
            AnimationManager.applyPageTransition(this, R.anim.slide_in_right, R.anim.slide_out_left)
        }

        // ---------------------------
        // STATS BUTTON
        // ---------------------------
        findViewById<Button>(R.id.statsNupp).setOnClickListener { btn ->
            AnimationManager.animateButtonClick(btn, this)

            val intent = Intent(this, StatisticsPage::class.java)
            startActivity(intent)
            AnimationManager.applyPageTransition(this, R.anim.slide_in_right, R.anim.slide_out_left)
        }

        // ---------------------------
        // STORE BUTTON
        // ---------------------------
        findViewById<Button>(R.id.storeNupp).setOnClickListener { btn ->
            AnimationManager.animateButtonClick(btn, this)

            val intent = Intent(this, StorePage::class.java)
            startActivity(intent)
            AnimationManager.applyPageTransition(this, R.anim.slide_in_right, R.anim.slide_out_left)
        }

        // ---------------------------
        // SETTINGS BUTTON
        // ---------------------------
        findViewById<Button>(R.id.settingsNupp).setOnClickListener { btn ->
            AnimationManager.animateButtonClick(btn, this)

            val intent = Intent(this, GameSettingsPage::class.java)
            startActivity(intent)
            AnimationManager.applyPageTransition(this, R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }
}
