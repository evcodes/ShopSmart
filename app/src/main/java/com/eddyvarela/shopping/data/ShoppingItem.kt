package com.eddyvarela.shopping.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "shoppingItem")
data class ShoppingItem(
    @PrimaryKey(autoGenerate = true) var itemID : Long?,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "description") var description: String,
    @ColumnInfo(name = "category") var category: String,
    @ColumnInfo(name= "price") var price: String,
    @ColumnInfo(name= "dateCreated") var date: String,
    @ColumnInfo(name = "isPurchased") var isPurchased: Boolean
):Serializable
