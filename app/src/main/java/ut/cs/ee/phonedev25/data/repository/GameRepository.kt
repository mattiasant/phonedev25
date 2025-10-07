package ut.cs.ee.phonedev25.data.repository


import kotlinx.coroutines.flow.Flow
import ut.cs.ee.phonedev25.data.local.GameItem
import ut.cs.ee.phonedev25.data.local.GameItemDao
import ut.cs.ee.phonedev25.data.local.MatchAction
import ut.cs.ee.phonedev25.data.local.MatchActionDao
import ut.cs.ee.phonedev25.data.local.MatchHistory
import ut.cs.ee.phonedev25.data.local.MatchHistoryDao

class GameRepository(
    private val gameItemDao: GameItemDao,
    private val matchHistoryDao: MatchHistoryDao,
    private val matchActionDao: MatchActionDao
) {
    // --- GameItem functions ---
    val allItems: Flow<List<GameItem>> = gameItemDao.getAllItems()
    suspend fun insertItem(item: GameItem) = gameItemDao.insertItem(item)

    // --- MatchHistory functions ---
    val allMatches: Flow<List<MatchHistory>> = matchHistoryDao.getAllMatches()

    suspend fun insertMatch(match: MatchHistory): Long =
        matchHistoryDao.insertMatch(match)

    suspend fun deleteMatch(match: MatchHistory) =
        matchHistoryDao.deleteMatch(match)

    // --- MatchAction functions ---
    fun getActionsForMatch(matchId: Int): Flow<List<MatchAction>> =
        matchActionDao.getActionsForMatch(matchId)

    suspend fun insertAction(action: MatchAction) =
        matchActionDao.insertAction(action)

    suspend fun deleteActionsForMatch(matchId: Int) =
        matchActionDao.deleteActionsForMatch(matchId)
}