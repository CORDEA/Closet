package jp.cordea.closet.repository

import jp.cordea.closet.data.Item
import jp.cordea.closet.data.ItemType
import jp.cordea.closet.data.local.ItemDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItemRepository @Inject constructor(
    private val dao: ItemDao
) {
    suspend fun findAll(): List<Item> = withContext(Dispatchers.IO) { dao.findAll() }

    suspend fun findBy(title: String, types: Set<ItemType>, tags: Set<String>): List<Item> =
        withContext(Dispatchers.IO) {
            val items = dao.findBy(title, types)
            items.filter { it.tags.containsAll(tags) }
        }

    suspend fun find(type: ItemType): List<Item> = dao.findByType(type)

    suspend fun find(id: String): Item = dao.find(id)

    suspend fun insert(item: Item) = withContext(Dispatchers.IO) { dao.insert(item) }

    suspend fun update(item: Item) = withContext(Dispatchers.IO) { dao.update(item) }

    suspend fun delete(id: String) = dao.delete(id)
}
