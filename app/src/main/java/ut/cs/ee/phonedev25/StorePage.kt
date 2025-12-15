package ut.cs.ee.phonedev25

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.widget.Button

class StorePage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_store_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Back button with animation
        findViewById<ImageView>(R.id.imageView19).setOnClickListener { btn ->
            AnimationManager.animateBackButton(btn as ImageView, this) {
                finish()
                AnimationManager.applyPageTransition(this, R.anim.slide_in_left, R.anim.slide_out_right)
            }
        }

        val powerUpsButton = findViewById<Button>(R.id.playNupp4)
        val animationButton = findViewById<Button>(R.id.playNupp3)

        // Apply entrance animations
        AnimationManager.animateEntrance(powerUpsButton)
        AnimationManager.animateEntrance(animationButton)

        powerUpsButton.setOnClickListener { btn ->
            AnimationManager.animateButtonClick(btn, this)
            startActivity(Intent(this, PowerUpsActivity::class.java))
            AnimationManager.applyPageTransition(this, R.anim.slide_in_right, R.anim.slide_out_left)
        }

        animationButton.setOnClickListener { btn ->
            AnimationManager.animateButtonClick(btn, this)
            startActivity(Intent(this, AnimationPAge::class.java))
            AnimationManager.applyPageTransition(this, R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }
}