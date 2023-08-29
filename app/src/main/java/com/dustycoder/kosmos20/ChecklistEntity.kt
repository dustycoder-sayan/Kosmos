package com.dustycoder.kosmos20

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "checklist-table")
data class ChecklistEntity(
    @PrimaryKey(autoGenerate = true)
    var id:Int = 0,
    @ColumnInfo(name = "board-id")
    var boardId:Int = 0,
    @ColumnInfo(name = "list-id")
    var listId:Int = 0,
    @ColumnInfo(name = "card-id")
    var cardId:Int = 0,
    var task: String = "",
    @ColumnInfo(name = "is-checked")
    var isChecked: Boolean = false
)
