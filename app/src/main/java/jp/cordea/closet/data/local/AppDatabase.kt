package jp.cordea.closet.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import jp.cordea.closet.data.Item

@Database(entities = [Item::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
}
