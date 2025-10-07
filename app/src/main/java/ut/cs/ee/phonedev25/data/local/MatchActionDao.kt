package ut.cs.ee.phonedev25.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchActionDao {

    // Get all actions for a specific match in order
    @Query("SELECT * FROM match_actions WHERE matchId = :matchId ORDER BY turn ASC")
    fun getActionsForMatch(matchId: Int): Flow<List<MatchAction>>

    // Get all actions (useful for debugging or analytics)
    @Query("SELECT * FROM match_actions ORDER BY id ASC")
    fun getAllActions(): Flow<List<MatchAction>>

    // Insert one or more actions
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAction(action: MatchAction)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActions(actions: List<MatchAction>)

    // Delete a specific action
    @Delete
    suspend fun deleteAction(action: MatchAction)

    // Delete all actions for a specific match (when match is removed)
    @Query("DELETE FROM match_actions WHERE matchId = :matchId")
    suspend fun deleteActionsForMatch(matchId: Int)

    // Count actions per match
    @Query("SELECT COUNT(*) FROM match_actions WHERE matchId = :matchId")
    fun getActionCountForMatch(matchId: Int): Flow<Int>
}