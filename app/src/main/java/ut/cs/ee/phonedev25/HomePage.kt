package ut.cs.ee.phonedev25

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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

        findViewById<Button>(R.id.playNupp).setOnClickListener {
            startActivity(Intent(this, Join_Game::class.java))
        }
        findViewById<Button>(R.id.statsNupp).setOnClickListener {
            startActivity(Intent(this, StatisticsPage::class.java))
        }
        findViewById<Button>(R.id.storeNupp).setOnClickListener {
            startActivity(Intent(this, StorePage::class.java))
        }
        findViewById<Button>(R.id.settingsNupp).setOnClickListener {
            startActivity(Intent(this, GameSettingsPage::class.java))
        }

    }
}