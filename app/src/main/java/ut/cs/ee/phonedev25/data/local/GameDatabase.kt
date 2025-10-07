package ut.cs.ee.phonedev25.data.local

import androidx.room.*
import android.content.Context



@Database(entities = [GameItem::class, MatchHistory::class, MatchAction::class], version = 1, exportSchema = false)
abstract class GameDatabase : RoomDatabase() {

    abstract fun gameItemDao(): GameItemDao
    abstract fun matchHistoryDao(): MatchHistoryDao
    abstract fun matchActionDao(): MatchActionDao

    companion object {
        @Volatile
        private var INSTANCE: GameDatabase? = null

        fun getDatabase(context: Context): GameDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GameDatabase::class.java,
                    "game_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}