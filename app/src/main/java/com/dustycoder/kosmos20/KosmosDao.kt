package com.dustycoder.kosmos20

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface KosmosDao {

    @Insert
    fun addBoard(board: BoardEntity)

    @Insert
    fun addList(list: ListEntity)

    @Insert
    fun addCard(card: CardEntity)

    @Insert
    fun addNote(note: NotesEntity)

    @Insert
    fun addChecklist(checklist: ChecklistEntity)

    @Update
    fun updateBoard(board: BoardEntity)

    @Update
    fun updateList(list: ListEntity)

    @Update
    fun updateCard(card: CardEntity)

    @Update
    fun updateNote(note: NotesEntity)

    @Update
    fun updateChecklist(checklist: ChecklistEntity)

    @Delete
    fun deleteBoard(board: BoardEntity)

    @Delete
    fun deleteList(list: ListEntity)

    @Delete
    fun deleteCard(card: CardEntity)

    @Delete
    fun deleteNote(note: NotesEntity)

    @Delete
    fun deleteChecklist(checklist: ChecklistEntity)

    @Query("SELECT * FROM `board-table`")
    fun getAllBoards(): Flow<List<BoardEntity>>

    @Query("SELECT * FROM `board-table` WHERE `id`= :id")
    fun getBoardById(id:Int): Flow<BoardEntity>

    @Query("SELECT * FROM `list-table` WHERE `board-id`= :boardId")
    fun getListsByBoard(boardId:Int): Flow<List<ListEntity>>

    @Query("SELECT * FROM `card-table` WHERE `board-id`= :boardId AND `list-id`= :listId")
    fun getAllCardsForList(boardId: Int, listId: Int): Flow<List<CardEntity>>

    @Query("SELECT * FROM `card-table` WHERE `board-id`= :boardId AND `list-id`= :listId AND cardId= :cardId")
    fun getCardByCard(boardId: Int, listId: Int, cardId: Int): Flow<CardEntity>

    @Query("SELECT `list-name` FROM `list-table` WHERE `id`= :listId")
    fun getListIdByName(listId: Int): String

    @Query("SELECT * FROM `notes-table` WHERE `board-id`=:boardId AND `list-id`=:listId AND `card-id`=:cardId")
    fun getAllNotesForCard(boardId: Int, listId: Int, cardId: Int): Flow<List<NotesEntity>>

    @Query("SELECT * from `checklist-table` WHERE `board-id`=:boardId AND `list-id`=:listId AND `card-id`=:cardId")
    fun getAllChecklistForCard(boardId: Int, listId: Int, cardId: Int) : Flow<List<ChecklistEntity>>

    @Query("SELECT COUNT(*) FROM `checklist-table` WHERE `is-checked`=1 AND `board-id`=:boardId AND `list-id`=:listId AND `card-id`=:cardId")
    fun getCountOfSelectedChecklist(boardId: Int, listId: Int, cardId: Int) : Int

//    @Query("SELECT `board-name` as title, `list-name` as , `card-name` FROM `board-table`, `list-table`, `card-table`" +
//            " WHERE `card-table`.`board-id` = `board-table`.id AND `card-table`.`list-id` = `list-table`.id" +
//            " AND `card-table`.`card-endDate`=:endDate")
//    fun getEndDateCards(endDate : String) : List<>
}