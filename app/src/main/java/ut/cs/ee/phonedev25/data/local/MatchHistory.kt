package ut.cs.ee.phonedev25.data.local


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "match_history")
data class MatchHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val date: Long = System.currentTimeMillis(),  // Timestamp of when match occurred
    val player1: String,
    val player2: String,
    val winner: String,
    val duration: Long, // Duration in seconds or milliseconds
    val totalTurns: Int, // Number of turns in match
)