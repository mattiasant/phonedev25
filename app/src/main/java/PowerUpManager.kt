package ut.cs.ee.phonedev25

import android.content.Context
import android.content.SharedPreferences

object PowerUpManager {
    private const val PREFS_NAME = "PowerUpPrefs"
    private const val KEY_ACTIVE_POWERUP = "active_powerup"
    private const val KEY_POWERUP_USED = "powerup_used"

    // Power-up types
    const val INSTANT_TRIUMPH = "Instant Triumph"
    const val GENESIS_FORGE = "Genesis Forge"
    const val HYPER_THINKER = "Hyper-Thinker"
    const val CLUELESS_CHAOS = "Clueless Chaos"
    const val NONE = "None"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // Get the currently active power-up
    fun getActivePowerUp(context: Context): String {
        return getPrefs(context).getString(KEY_ACTIVE_POWERUP, NONE) ?: NONE
    }

    // Set the active power-up (called from PowerUpsActivity)
    fun setActivePowerUp(context: Context, powerUp: String) {
        getPrefs(context).edit().putString(KEY_ACTIVE_POWERUP, powerUp).apply()
    }

    // Check if power-up has been used this game
    fun isPowerUpUsed(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_POWERUP_USED, false)
    }

    // Mark power-up as used
    fun markPowerUpUsed(context: Context) {
        getPrefs(context).edit().putBoolean(KEY_POWERUP_USED, true).apply()
    }

    // Reset power-up usage at the start of a new game
    fun resetPowerUpUsage(context: Context) {
        getPrefs(context).edit().putBoolean(KEY_POWERUP_USED, false).apply()
    }
}