package ut.cs.ee.phonedev25

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PowerUpsActivity : AppCompatActivity() {

    private var activePowerUp: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_power_ups)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Back button
        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
        }

        // PowerUp containers
        val instantTriumph = findViewById<LinearLayout>(R.id.instantTriumphContainer)
        val genesisForge = findViewById<LinearLayout>(R.id.genesisForgeContainer)
        val hyperThinker = findViewById<LinearLayout>(R.id.hyperThinkerContainer)
        val cluelessChaos = findViewById<LinearLayout>(R.id.cluelessChaosContainer)

        // PowerUp status texts
        val instantTriumphStatus = findViewById<TextView>(R.id.instantTriumphStatus)
        val genesisForgeStatus = findViewById<TextView>(R.id.genesisForgeStatus)
        val hyperThinkerStatus = findViewById<TextView>(R.id.hyperThinkerStatus)
        val cluelessChaosStatus = findViewById<TextView>(R.id.cluelessChaosStatus)

        // Set click listeners
        instantTriumph.setOnClickListener {
            togglePowerUp("Instant Triumph", instantTriumphStatus,
                listOf(genesisForgeStatus, hyperThinkerStatus, cluelessChaosStatus))
        }

        genesisForge.setOnClickListener {
            togglePowerUp("Genesis Forge", genesisForgeStatus,
                listOf(instantTriumphStatus, hyperThinkerStatus, cluelessChaosStatus))
        }

        hyperThinker.setOnClickListener {
            togglePowerUp("Hyper-Thinker", hyperThinkerStatus,
                listOf(instantTriumphStatus, genesisForgeStatus, cluelessChaosStatus))
        }

        cluelessChaos.setOnClickListener {
            togglePowerUp("Clueless Chaos", cluelessChaosStatus,
                listOf(instantTriumphStatus, genesisForgeStatus, hyperThinkerStatus))
        }
    }

    private fun togglePowerUp(powerUpName: String, statusView: TextView, otherStatusViews: List<TextView>) {
        if (activePowerUp == powerUpName) {
            // Turn off the active power-up
            activePowerUp = null
            statusView.text = "OFF"
            statusView.setTextColor(getColor(android.R.color.darker_gray))
        } else {
            // Turn off all other power-ups
            otherStatusViews.forEach {
                it.text = "OFF"
                it.setTextColor(getColor(android.R.color.darker_gray))
            }
            // Turn on this power-up
            activePowerUp = powerUpName
            statusView.text = "ON"
            statusView.setTextColor(getColor(android.R.color.holo_green_dark))
        }
    }
}