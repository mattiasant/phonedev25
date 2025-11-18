package ut.cs.ee.phonedev25.data

import android.content.Context
import android.content.SharedPreferences

object StatsManager {

    private const val PREF_NAME = "game_stats"
    private const val KEY_CARDS_PLACED = "cards_placed"
    private const val KEY_CARDS_PICKED_UP = "cards_picked_up"
    private const val KEY_GAMES_WON = "games_won"
    private const val KEY_GAMES_LOST = "games_lost"

    private fun prefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun addCardPlaced(context: Context) {
        val p = prefs(context)
        p.edit().putInt(KEY_CARDS_PLACED, p.getInt(KEY_CARDS_PLACED, 0) + 1).apply()
    }

    fun addCardPickedUp(context: Context) {
        val p = prefs(context)
        p.edit().putInt(KEY_CARDS_PICKED_UP, p.getInt(KEY_CARDS_PICKED_UP, 0) + 1).apply()
    }

    fun addWin(context: Context) {
        val p = prefs(context)
        p.edit().putInt(KEY_GAMES_WON, p.getInt(KEY_GAMES_WON, 0) + 1).apply()
    }

    fun addLoss(context: Context) {
        val p = prefs(context)
        p.edit().putInt(KEY_GAMES_LOST, p.getInt(KEY_GAMES_LOST, 0) + 1).apply()
    }

    fun getStats(context: Context): StatsData {
        val p = prefs(context)
        return StatsData(
            cardsPlaced = p.getInt(KEY_CARDS_PLACED, 0),
            cardsPickedUp = p.getInt(KEY_CARDS_PICKED_UP, 0),
            gamesWon = p.getInt(KEY_GAMES_WON, 0),
            gamesLost = p.getInt(KEY_GAMES_LOST, 0)
        )
    }
}

data class StatsData(
    val cardsPlaced: Int,
    val cardsPickedUp: Int,
    val gamesWon: Int,
    val gamesLost: Int
) {
    val winrate: String
        get() {
            val total = gamesWon + gamesLost
            return if (total == 0) "0%" else "${(gamesWon * 100 / total)}%"
        }
}
