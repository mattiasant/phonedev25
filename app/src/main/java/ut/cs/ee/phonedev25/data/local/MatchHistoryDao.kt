package ut.cs.ee.phonedev25.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchHistoryDao {

    // Get all matches sorted by most recent
    @Query("SELECT * FROM match_history ORDER BY date DESC")
    fun getAllMatches(): Flow<List<MatchHistory>>

    // Get a specific match by ID
    @Query("SELECT * FROM match_history WHERE id = :matchId LIMIT 1")
    suspend fun getMatchById(matchId: Int): MatchHistory?

    // Insert a new match
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: MatchHistory): Long

    // Update existing match info (e.g., winner, duration)
    @Update
    suspend fun updateMatch(match: MatchHistory)

    // Delete a single match
    @Delete
    suspend fun deleteMatch(match: MatchHistory)

    // Delete all match records
    @Query("DELETE FROM match_history")
    suspend fun deleteAllMatches()

    // Count total matches
    @Query("SELECT COUNT(*) FROM match_history")
    fun getMatchCount(): Flow<Int>
}
