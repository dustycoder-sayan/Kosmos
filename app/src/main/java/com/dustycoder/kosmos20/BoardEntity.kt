package com.dustycoder.kosmos20

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "board-table")
data class BoardEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(name = "board-name")
    var boardName: String = "",
    @ColumnInfo(name = "board-desc")
    var boardDesc: String = "",
    @ColumnInfo(name = "deadline-date")
    var deadlineDate: String = ""
)
