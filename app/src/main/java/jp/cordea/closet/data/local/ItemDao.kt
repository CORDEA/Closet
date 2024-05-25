package jp.cordea.closet.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import jp.cordea.closet.data.Item
import jp.cordea.closet.data.ItemType

@Dao
interface ItemDao {
    @Query("SELECT * FROM item")
    suspend fun findAll(): List<Item>

    @Query("SELECT * FROM item WHERE type = :type")
    suspend fun findByType(type: ItemType): List<Item>

    @Query("SELECT * FROM item WHERE id = :id")
    suspend fun find(id: String): Item

    @Insert
    suspend fun insert(item: Item)

    @Update
    suspend fun update(item: Item)

    @Query("DELETE FROM item WHERE id = :id")
    suspend fun delete(id: String)
}
