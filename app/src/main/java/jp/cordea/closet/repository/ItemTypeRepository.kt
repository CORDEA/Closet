package jp.cordea.closet.repository

import jp.cordea.closet.data.ItemType
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.enums.EnumEntries

@Singleton
class ItemTypeRepository @Inject constructor() {
    fun findAll(): EnumEntries<ItemType> = ItemType.entries
}
