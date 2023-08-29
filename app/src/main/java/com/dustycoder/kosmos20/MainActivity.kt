package com.dustycoder.kosmos20

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dustycoder.kosmos20.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var addBtn : FloatingActionButton
    private lateinit var boardItems : RecyclerView
    private lateinit var zeroBoard: CardView
    private lateinit var createFirstBoard: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addBtn = findViewById(R.id.addBoardBtn)
        boardItems = findViewById(R.id.boards)
        zeroBoard = findViewById(R.id.zeroBoard)
        createFirstBoard = findViewById(R.id.createFirstBoard)

        val kosmosDao = (application as KosmosApp).db.kosmosDao()

        addBtn.setOnClickListener {
            val intent = Intent(this, AddBoardActivity::class.java)
            startActivity(intent)
        }

        createFirstBoard.setOnClickListener {
            val intent = Intent(this, AddBoardActivity::class.java)
            startActivity(intent)
        }

        // Make a background thread perform DB access
        lifecycleScope.launch {
            kosmosDao.getAllBoards().collect {
                val list = ArrayList<BoardEntity>()
                list.addAll(it)
                setUpToRecyclerView(list, kosmosDao)
            }
        }
    }

    // Pass ArrayList of Items as argument
    private fun setUpToRecyclerView(list: ArrayList<BoardEntity>, boardDao: KosmosDao) {
        // Bind data only if items available
        if(list.isNotEmpty()) {
            // If items available, change visibility of RecyclerView to "visible"
            zeroBoard.visibility = View.GONE
            boardItems.visibility = View.VISIBLE
            addBtn.visibility = View.VISIBLE
            boardItems.layoutManager = LinearLayoutManager(this)
            boardItems.adapter = BoardViewAdapter(this, list)
        } else {
            zeroBoard.visibility = View.VISIBLE
            boardItems.visibility = View.GONE
            addBtn.visibility = View.GONE
        }
    }
}