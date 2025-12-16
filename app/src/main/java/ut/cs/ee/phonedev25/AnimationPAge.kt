package ut.cs.ee.phonedev25

import android.os.Bundle
import android.widget.ImageView
import android.widget.Switch
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.SharedPreferences
import android.preference.PreferenceManager

class AnimationPAge : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private lateinit var switch1: Switch
    private lateinit var switch2: Switch
    private lateinit var switch3: Switch
    private lateinit var themeSwitch: Switch
    private lateinit var cardSwitch: Switch

    private lateinit var backArrow: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {

        // Initialize SharedPreferences
        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_animation_page)

        // Handle edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize views
        backArrow = findViewById(R.id.imageView4)
        switch1 = findViewById(R.id.switch1)
        switch2 = findViewById(R.id.switch2)
        switch3 = findViewById(R.id.switch3)
        cardSwitch = findViewById(R.id.cardSwitch)
        themeSwitch = findViewById(R.id.themeSwitch)

        backArrow.setOnClickListener { btn ->
            AnimationManager.animateBackButton(btn as ImageView, this) {
                finish()
                AnimationManager.applyPageTransition(this, R.anim.slide_in_left, R.anim.slide_out_right)
            }
        }

        // Load current preferences into switches
        switch1.isChecked = AnimationManager.getPreference(this, AnimationManager.getBackAnimationKey())
        switch2.isChecked = AnimationManager.getPreference(this, AnimationManager.getSwitchAnimationKey())
        switch3.isChecked = AnimationManager.getPreference(this, AnimationManager.getButtonAnimationKey())
        themeSwitch.isChecked = AnimationManager.getPreference(this, AnimationManager.getPageTransitionKey())
        cardSwitch.isChecked = AnimationManager.getPreference(this, AnimationManager.getCardAnimationKey())


        // Save preferences when switches are toggled
        switch1.setOnCheckedChangeListener { _, isChecked ->
            AnimationManager.setPreference(this, AnimationManager.getBackAnimationKey(), isChecked)
        }
        switch2.setOnCheckedChangeListener { _, isChecked ->
            AnimationManager.setPreference(this, AnimationManager.getSwitchAnimationKey(), isChecked)
        }
        switch3.setOnCheckedChangeListener { _, isChecked ->
            AnimationManager.setPreference(this, AnimationManager.getButtonAnimationKey(), isChecked)
        }
        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            AnimationManager.setPreference(this, AnimationManager.getPageTransitionKey(), isChecked)
        }

        cardSwitch.setOnCheckedChangeListener { _, isChecked ->
            AnimationManager.setPreference(this,AnimationManager.getCardAnimationKey(),isChecked)
        }


    }
}
