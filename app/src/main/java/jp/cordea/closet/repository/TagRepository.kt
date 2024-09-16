package jp.cordea.closet.repository

import jp.cordea.closet.data.local.ItemDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TagRepository @Inject constructor(
    private val dao: ItemDao
) {
    suspend fun findAll(): List<String> = withContext(Dispatchers.IO) {
        val items = dao.findAll()
        items.flatMap { it.tags }.distinct()
    }
}
