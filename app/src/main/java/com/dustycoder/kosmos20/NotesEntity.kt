package com.dustycoder.kosmos20

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes-table")
data class NotesEntity(
    @PrimaryKey(autoGenerate = true)
    var id:Int = 0,
    @ColumnInfo(name = "board-id")
    var boardId: Int = 0,
    @ColumnInfo(name = "list-id")
    var listId: Int = 0,
    @ColumnInfo(name = "card-id")
    var cardId: Int = 0,
    @ColumnInfo(name = "notes")
    var notes:String = ""
)
