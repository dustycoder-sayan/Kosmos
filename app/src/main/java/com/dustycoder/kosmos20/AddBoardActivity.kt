package com.dustycoder.kosmos20

import android.app.*
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddBoardActivity : AppCompatActivity() {
    lateinit var boardName: EditText
    lateinit var boardDesc: EditText
    lateinit var deadlineBtn: Button
    lateinit var createBoardBtn: Button
    lateinit var selectedDate: TextView

    var calendar: Calendar = Calendar.getInstance()
    var year: Int = calendar.get(Calendar.YEAR)
    var month: Int = calendar.get(Calendar.MONTH)
    var dayOfMonth: Int = calendar.get(Calendar.DAY_OF_MONTH)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_board)

        boardName = findViewById(R.id.boardName)
        boardDesc = findViewById(R.id.boardDesc)
        deadlineBtn = findViewById(R.id.deadlineBtn)
        createBoardBtn = findViewById(R.id.createBtn)
        selectedDate = findViewById(R.id.dateSelected)

        val boardDao = (application as KosmosApp).db.kosmosDao()

        deadlineBtn.setOnClickListener { deadline() }

        createNotificationChannel()
        createBoardBtn.setOnClickListener { addBoard(boardDao) }
    }

    private fun createNotificationChannel() {
        val name = "Board Due"
        val desc = "Description of Channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(1.toString(), name, importance)
        channel.description = desc
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun addBoard(boardDao: KosmosDao) {
        val name = boardName.text.toString()
        val desc = boardDesc.text.toString()
        val deadlineDate = selectedDate.text.toString()

        if(name.isNotEmpty() && desc.isNotEmpty()) {
            scheduleNotification()
            lifecycleScope.launch{
                boardDao.addBoard(BoardEntity(boardName = name, boardDesc = desc, deadlineDate = deadlineDate))
                Toast.makeText(applicationContext, "Board Saved", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@AddBoardActivity, MainActivity::class.java)
                startActivity(intent)
            }
        } else
            Toast.makeText(applicationContext, "Entries Empty", Toast.LENGTH_SHORT).show()
    }

    private fun scheduleNotification()
    {
        val intent = Intent(applicationContext, Notification::class.java)
        val title = boardName.text.toString()
        val message = boardDesc.text.toString()
        intent.putExtra("titleExtra", title)
        intent.putExtra("msgExtra", message)

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            1,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time = getTime()
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
    }

    private fun getTime() : Long {
        val minute = 0
        val hour = 8

        val deadlineDate = selectedDate.text.toString()
        val sp = deadlineDate.split("/")
        val day = sp[0].toInt()-2
        val month = sp[1].toInt()
        val year = sp[2].toInt()

        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, hour, minute)
        return calendar.timeInMillis
    }

    private fun deadline() {
        lateinit var dateSelected: String
        val datePickerDialog = DatePickerDialog(this,
            { view, year, month, day ->
                dateSelected = "$day/${month+1}/$year"
                selectedDate.text = dateSelected
            },
            year, month, dayOfMonth)
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 3600000
        datePickerDialog.show()
    }
}
