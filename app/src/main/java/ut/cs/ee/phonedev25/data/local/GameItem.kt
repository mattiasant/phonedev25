package ut.cs.ee.phonedev25.data.local



import androidx.room.*


@Entity(tableName = "game_items")
data class GameItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "item_name")
    val itemName: String,

    @ColumnInfo(name = "item_type")
    val itemType: String, // e.g., "weapon", "armor", "potion"

    @ColumnInfo(name = "power_level")
    val powerLevel: Int,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "rarity")
    val rarity: String, // e.g., "common", "rare", "legendary"

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)