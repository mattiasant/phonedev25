package ut.cs.ee.phonedev25

import android.os.Bundle
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.SharedPreferences
import android.preference.PreferenceManager

class GameSettingsPage : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {

        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        // Apply saved theme before setContentView
        val isDarkMode = prefs.getBoolean("dark_mode", false)
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game_settings_page)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Back button with animation
        findViewById<ImageView>(R.id.imageView4).setOnClickListener { btn ->
            AnimationManager.animateBackButton(btn as ImageView, this) {
                finish()
                AnimationManager.applyPageTransition(this, R.anim.slide_in_left, R.anim.slide_out_right)
            }
        }

        // Theme switch
        val themeSwitch = findViewById<Switch>(R.id.themeSwitch)
        themeSwitch.isChecked = isDarkMode

        // Apply entrance animation to the switch
        AnimationManager.animateEntrance(themeSwitch)

        themeSwitch.setOnCheckedChangeListener { switchView, isChecked ->
            AnimationManager.animateSwitch(switchView as Switch, this)
            prefs.edit().putBoolean("dark_mode", isChecked).apply()
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }
}