package com.dustycoder.kosmos20

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dustycoder.kosmos20.databinding.AddChecklistBinding
import com.dustycoder.kosmos20.databinding.AddNotesBinding
import com.dustycoder.kosmos20.databinding.CreateListBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.w3c.dom.Text

class CardViewActivity : AppCompatActivity() {

    lateinit var cardName: TextView
    lateinit var listName: TextView
    lateinit var cardDesc: TextView
    lateinit var addNotes: Button
    lateinit var addChecklist: Button
    lateinit var notes: RecyclerView
    lateinit var checklists: RecyclerView
    lateinit var noNotes: TextView
    lateinit var noChecklists: TextView
    lateinit var startDateString: TextView
    lateinit var endDateString: TextView
    lateinit var progressBar: ProgressBar

    lateinit var card : CardEntity

    var boardId: Int? = null
    var listId: Int? = null
    var cardId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_view)

        cardName = findViewById(R.id.cardNameInCardView)
        cardDesc = findViewById(R.id.cardDescInCardView)
        addNotes = findViewById(R.id.addNotesInCardView)
        addChecklist = findViewById(R.id.addChecklistsInCardView)
        notes = findViewById(R.id.cardNotesInCardView)
        checklists = findViewById(R.id.cardChecklistsInCardView)
        noNotes = findViewById(R.id.noNotesTextInCardView)
        noChecklists = findViewById(R.id.noListsTextInCardView)
        endDateString = findViewById(R.id.endDateTextInCardView)
        listName = findViewById(R.id.listNameinCardView)
        progressBar = findViewById(R.id.progressBar)

        val kosmosDao = (application as KosmosApp).db.kosmosDao()

        val i : Intent = intent
        boardId = i.getIntExtra("boardId", 1)
        listId = i.getIntExtra("listId", 1)
        cardId = i.getIntExtra("cardId", 1)
        val listNameString = kosmosDao.getListIdByName(listId!!)

        lifecycleScope.launch {
            kosmosDao.getCardByCard(boardId!!, listId!!, cardId!!).collect {
                cardName.text = it.cardName
                cardDesc.text = it.cardDesc
                listName.text = listNameString
                if(it.endDate == null)
                    endDateString.text = "Not Specified"
                else
                    endDateString.text = it.endDate
            }
        }


        lifecycleScope.launch {
            kosmosDao.getAllNotesForCard(boardId = boardId!!, listId = listId!!, cardId = cardId!!).collect {
                val list = ArrayList(it)
                setUpToRecyclerView(list, kosmosDao)
            }
        }

        lifecycleScope.launch {
            kosmosDao.getAllChecklistForCard(boardId!!, listId!!, cardId!!).collect {
                val checklists = ArrayList(it)
                setUpChecklistRecyclerView(checklists, kosmosDao)

                val numberChecked = kosmosDao.getCountOfSelectedChecklist(boardId!!, listId!!, cardId!!)
                if(numberChecked > 0) {
                    val progressStatus = (numberChecked.toFloat()/checklists.size.toFloat())*100
                    progressBar.progress = progressStatus.toInt()
                } else
                    progressBar.progress = 0
            }
        }

        addNotes.setOnClickListener { addNotesFunc(kosmosDao) }
        addChecklist.setOnClickListener { addChecklists(kosmosDao) }
    }

    private fun setUpToRecyclerView(list: ArrayList<NotesEntity>, kosmosDao: KosmosDao) {
        if(list.isNotEmpty()) {
            notes.visibility = View.VISIBLE
            noNotes.visibility = View.GONE
            notes.layoutManager = LinearLayoutManager(this)
            notes.adapter = NotesAdapter(this, list, kosmosDao)
        } else {
            notes.visibility = View.GONE
            noNotes.visibility = View.VISIBLE
        }
    }

    private fun setUpChecklistRecyclerView(list: ArrayList<ChecklistEntity>, kosmosDao: KosmosDao) {
        if(list.isNotEmpty()) {
            noChecklists.visibility = View.GONE
            checklists.visibility = View.VISIBLE
            checklists.layoutManager = LinearLayoutManager(this)
            checklists.adapter = ChecklistAdapter(list, kosmosDao)
        }
    }

    fun addNotesFunc(kosmosDao: KosmosDao) {
        val createDialog = Dialog(this, androidx.appcompat.R.style.Theme_AppCompat_Dialog)
        val binding = AddNotesBinding.inflate(layoutInflater)
        createDialog.setContentView(binding.root)

        binding.addNotesBtn.setOnClickListener {
            val note = binding.notesText.text.toString()

            if(note.isNotEmpty()) {
                lifecycleScope.launch {
                    kosmosDao.addNote(NotesEntity(boardId = boardId!!, listId = listId!!, cardId = cardId!!, notes = note))
                    Toast.makeText(this@CardViewActivity, "Note Added Successfully!", Toast.LENGTH_SHORT).show()
                    createDialog.dismiss()
                }
            } else
                Toast.makeText(this@CardViewActivity, "Note could not be added", Toast.LENGTH_SHORT).show()
        }

        createDialog.show()
    }

    private fun addChecklists(kosmosDao: KosmosDao) {
        val createDialog = Dialog(this, androidx.appcompat.R.style.Theme_AppCompat_Dialog)
        val binding = AddChecklistBinding.inflate(layoutInflater)
        createDialog.setContentView(binding.root)

        binding.addTask.setOnClickListener {
            val task = binding.newTask.text.toString()

            if(task.isNotEmpty()) {
                lifecycleScope.launch {
                    kosmosDao.addChecklist(ChecklistEntity(boardId = boardId!!, listId = listId!!, cardId = cardId!!, task = task, isChecked = false))
                    Toast.makeText(this@CardViewActivity, "Task Added Successfully!", Toast.LENGTH_SHORT).show()
                    createDialog.dismiss()
                }
            } else
                Toast.makeText(this@CardViewActivity, "Task could not be added", Toast.LENGTH_SHORT).show()
        }
        createDialog.show()
    }
}