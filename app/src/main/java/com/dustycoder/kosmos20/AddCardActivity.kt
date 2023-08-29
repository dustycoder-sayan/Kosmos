package com.dustycoder.kosmos20

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import java.util.*

class AddCardActivity : AppCompatActivity() {
    lateinit var cardName: EditText
    lateinit var cardDesc: MultiAutoCompleteTextView
    lateinit var startDate: Button
    lateinit var endDate: Button
    lateinit var save: Button
    lateinit var startDateString: TextView
    lateinit var endDateString: TextView

    var calendar: Calendar = Calendar.getInstance()
    var year: Int = calendar.get(Calendar.YEAR)
    var month: Int = calendar.get(Calendar.MONTH)
    var dayOfMonth: Int = calendar.get(Calendar.DAY_OF_MONTH)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_card)

        cardName = findViewById(R.id.cardNameInAddCard)
        cardDesc = findViewById(R.id.cardDescInAddCard)
        startDate = findViewById(R.id.startDateAddCard)
        endDate = findViewById(R.id.endDateAddCard)
        save = findViewById(R.id.saveCardBtn)
        startDateString = findViewById(R.id.startDateString)
        endDateString = findViewById(R.id.endDateString)

        val kosmosDao = (application as KosmosApp).db.kosmosDao()

        save.setOnClickListener { saveCard(kosmosDao) }
        startDate.setOnClickListener { startDatePicker() }
        endDate.setOnClickListener { endDatePicker() }
    }

    private fun saveCard(kosmosDao: KosmosDao) {
        val name = cardName.text.toString()
        val desc = cardDesc.text.toString()
        val startDateSelected = startDateString.text.toString()
        val endDateSelected = endDateString.text.toString()

        val boardId: Int = intent.getIntExtra("boardId", 1)
        val listId: Int = intent.getIntExtra("listId", 1)

        kosmosDao.addCard(CardEntity(cardName = name, cardDesc = desc, startDate = startDateSelected,
            endDate = endDateSelected, boardId = boardId, listId = listId))

        Toast.makeText(this, "Card Added Successfully", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, BoardListActivity::class.java)
        startActivity(intent)
    }

    private fun startDatePicker() {
        lateinit var dateSelected: String
        val datePickerDialog = DatePickerDialog(this,
            { view, year, month, day ->
                dateSelected = "$day/${month+1}/$year"
                startDateString.text = dateSelected
            },
            year, month, dayOfMonth)
        datePickerDialog.show()
    }

    private fun endDatePicker() {
        lateinit var dateSelected: String
        val datePickerDialog = DatePickerDialog(this,
            { view, year, month, day ->
                dateSelected = "$day/${month+1}/$year"
                endDateString.text = dateSelected
            },
            year, month, dayOfMonth)
        datePickerDialog.show()
    }
}