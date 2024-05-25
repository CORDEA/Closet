package jp.cordea.closet.repository

import jp.cordea.closet.data.Item
import jp.cordea.closet.data.ItemType
import jp.cordea.closet.data.local.ItemDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItemRepository @Inject constructor(
    private val dao: ItemDao
) {
    suspend fun findAll(): List<Item> = dao.findAll()

    suspend fun find(type: ItemType): List<Item> = dao.findByType(type)

    suspend fun find(id: String): Item = dao.find(id)

    suspend fun insert(item: Item) = dao.insert(item)

    suspend fun update(item: Item) = dao.update(item)

    suspend fun delete(id: String) = dao.delete(id)
}
