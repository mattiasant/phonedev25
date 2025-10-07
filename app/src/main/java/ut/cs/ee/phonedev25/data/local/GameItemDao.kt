package ut.cs.ee.phonedev25.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GameItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: GameItem): Long

    @Update
    suspend fun updateItem(item: GameItem)

    @Delete
    suspend fun deleteItem(item: GameItem)

    @Query("SELECT * FROM game_items ORDER BY created_at DESC")
    fun getAllItems(): Flow<List<GameItem>>

    @Query("SELECT * FROM game_items WHERE id = :itemId")
    suspend fun getItemById(itemId: Int): GameItem?

    @Query("SELECT * FROM game_items WHERE item_type = :type ORDER BY power_level DESC")
    fun getItemsByType(type: String): Flow<List<GameItem>>

    @Query("SELECT * FROM game_items WHERE rarity = :rarity")
    fun getItemsByRarity(rarity: String): Flow<List<GameItem>>

    @Query("DELETE FROM game_items")
    suspend fun deleteAllItems()

    @Query("SELECT COUNT(*) FROM game_items")
    fun getItemCount(): Flow<Int>
}