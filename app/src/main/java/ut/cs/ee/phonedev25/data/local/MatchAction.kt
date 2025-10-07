package ut.cs.ee.phonedev25.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "match_actions",
    foreignKeys = [
        ForeignKey(
            entity = MatchHistory::class,
            parentColumns = ["id"],
            childColumns = ["matchId"],
            onDelete = ForeignKey.CASCADE // delete actions if match is deleted
        )
    ]
)
data class MatchAction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val matchId: Int, // FK → MatchHistory.id

    val turn: Int, // Turn number within the match
    val player: String, // Player name or ID
    val cardName: String, // Card used
    val actionType: String, // e.g., "attack", "heal", "buff", "summon"
    val value: Int, // Amount of damage, healing, etc.
    val timestamp: Long = System.currentTimeMillis() //  when action occurred
)