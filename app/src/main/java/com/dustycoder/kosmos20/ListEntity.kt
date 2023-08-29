package com.dustycoder.kosmos20

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "list-table")
data class ListEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(name = "board-id")
    var boardId: Int = 0,
    @ColumnInfo(name = "list-name")
    var listName: String = ""
)
