package jp.cordea.closet.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import jp.cordea.closet.data.Item

@Dao
interface ItemDao {
    @Query("SELECT * FROM item")
    fun findAll(): List<Item>

    @Insert
    fun insert(item: Item)

    @Query("DELETE FROM item WHERE id = :id")
    fun delete(id: String)
}
