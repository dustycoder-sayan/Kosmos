package com.dustycoder.kosmos20

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dustycoder.kosmos20.databinding.CreateListBinding
import com.dustycoder.kosmos20.databinding.EditBoardBinding
import com.dustycoder.kosmos20.databinding.EditListBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

class BoardListActivity : AppCompatActivity() {

    private var boardId: Int? = null
    private lateinit var boardName: String
    private lateinit var boardNameInList:TextView
    private lateinit var deleteBoardBtn:ImageView
    private lateinit var editBoardBtn: ImageView
    private lateinit var createListBtn: Button
    private lateinit var lists: RecyclerView
    private lateinit var textView2: TextView
    private lateinit var boardListDesc: TextView
    private lateinit var kosmosDao: KosmosDao
    private lateinit var cards: RecyclerView
    private lateinit var newDeadline: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_list)

        boardId = intent.getIntExtra("boardId", 1)

        kosmosDao = (application as KosmosApp).db.kosmosDao()

        boardNameInList = findViewById(R.id.boardNameInList)
        deleteBoardBtn = findViewById(R.id.deleteBoardBtn)
        editBoardBtn = findViewById(R.id.editBoardBtn)
        createListBtn = findViewById(R.id.createListBtn)
        lists = findViewById(R.id.lists)
        textView2 = findViewById(R.id.textView2)
        boardListDesc = findViewById(R.id.boardListDesc)

        lifecycleScope.launch {
            try {
                kosmosDao.getBoardById(boardId!!).collect {
                    boardName = it.boardName
                    boardListDesc.text = it.boardDesc
                    boardNameInList.text = boardName
                    newDeadline = it.deadlineDate
                }
            } catch(e: Exception) {}
        }

        deleteBoardBtn.setOnClickListener { deleteBoard(kosmosDao) }
        editBoardBtn.setOnClickListener { editBoard(kosmosDao, boardId!!) }
        createListBtn.setOnClickListener { createList(kosmosDao) }

        lifecycleScope.launch {
            kosmosDao.getListsByBoard(boardId!!).collect {
                val list = ArrayList(it)
                setUpToRecyclerView(list)
            }
        }
    }

    private fun createList(kosmosDao: KosmosDao) {
        val createDialog = Dialog(this, androidx.appcompat.R.style.Theme_AppCompat_Dialog)
        val binding = CreateListBinding.inflate(layoutInflater)
        createDialog.setContentView(binding.root)

        binding.createListBtn2.setOnClickListener {
            val name = binding.listNameCreationText.text.toString()

            if(name.isNotEmpty()) {
                lifecycleScope.launch {
                    kosmosDao.addList(ListEntity(boardId = boardId!!, listName = name))
                    Toast.makeText(this@BoardListActivity, "List Added Successfully!",Toast.LENGTH_SHORT).show()
                    createDialog.dismiss()
                }
            } else
                Toast.makeText(this@BoardListActivity, "List could not be added",Toast.LENGTH_SHORT).show()
        }

        createDialog.show()
    }

    private fun setUpToRecyclerView(list: ArrayList<ListEntity>) {
        if(list.isNotEmpty()) {
            lists.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            lists.adapter = ListViewAdapter(this, list, kosmosDao)
            lists.visibility = View.VISIBLE
            textView2.visibility = View.GONE
        } else {
            lists.visibility = View.GONE
            textView2.visibility = View.VISIBLE
        }
    }

    private fun editBoard(kosmosDao: KosmosDao, boardId: Int) {
        val createDialog = Dialog(this, androidx.appcompat.R.style.Base_ThemeOverlay_AppCompat_Dialog)
        val binding = EditBoardBinding.inflate(createDialog.layoutInflater)
        createDialog.setContentView(binding.root)
        binding.boardNameInEditBoard.setText(boardNameInList.text)
        binding.boardDescInEditBoard.setText(boardListDesc.text)

        binding.updateButtonInUpdateBoard.setOnClickListener {
            val name = binding.boardNameInEditBoard.text.toString()
            val desc = binding.boardDescInEditBoard.text.toString()
            binding.deadlineBtnInUpdateBoard.setOnClickListener { deadline() }

            if(name.isNotEmpty()) {
                GlobalScope.launch {
                    lifecycleScope.launch {
                        kosmosDao.updateBoard(BoardEntity(id = boardId, boardName = name, boardDesc = desc, deadlineDate = newDeadline))
                        Toast.makeText(this@BoardListActivity, "Note Added Successfully!", Toast.LENGTH_SHORT).show()
                        createDialog.dismiss()
                    }
                }
            } else
                Toast.makeText(this@BoardListActivity, "List could not be updated",Toast.LENGTH_SHORT).show()
        }

        createDialog.show()
    }

    private fun deadline() {
        val calendar: Calendar = Calendar.getInstance()
        val year: Int = calendar.get(Calendar.YEAR)
        val month: Int = calendar.get(Calendar.MONTH)
        val dayOfMonth: Int = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this,
            { view, year1, month1, day ->
                newDeadline = "$day/${month1+1}/$year1"
            },
            year, month, dayOfMonth)
        datePickerDialog.show()
    }

    private fun deleteBoard(kosmosDao: KosmosDao) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Board")
        builder.setMessage("Are you sure you want to delete the Board?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton("Yes") { dialogInterface, _ ->
            lifecycleScope.launch {
                try {
                    dialogInterface.dismiss()
                    Toast.makeText(this@BoardListActivity, "Board Deleted Successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@BoardListActivity, MainActivity::class.java)
                    startActivity(intent)
                    kosmosDao.deleteBoard(BoardEntity(boardId!!))
                } catch(e: Exception) {}
            }
        }
        builder.setNegativeButton("No") { dialogInterface, which ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}
