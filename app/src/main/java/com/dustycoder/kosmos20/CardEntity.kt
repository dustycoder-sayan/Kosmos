package com.dustycoder.kosmos20

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "card-table")
data class CardEntity(
    @PrimaryKey(autoGenerate = true)
    var cardId: Int = 0,
    @ColumnInfo(name = "list-id")
    var listId: Int = 0,
    @ColumnInfo(name = "board-id")
    var boardId: Int = 0,
    @ColumnInfo(name = "card-name")
    var cardName: String = "",
    @ColumnInfo(name = "card-desc")
    var cardDesc: String = "",
    @ColumnInfo(name = "card-startDate")
    var startDate: String? = null,
    @ColumnInfo(name = "card-endDate")
    var endDate: String? = null
)
