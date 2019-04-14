package com.eddyvarela.shopping.data

import android.arch.persistence.room.*

@Dao
interface ShoppingItemDao {
    @Query("SELECT * FROM shoppingItem")
    fun getAllItems(): List<ShoppingItem>

    @Insert
    fun insertItem(item: ShoppingItem):Long

    @Delete
    fun deleteItem(item: ShoppingItem)

    @Update
    fun updateItem(item:ShoppingItem)
}