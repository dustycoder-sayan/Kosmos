package com.dustycoder.kosmos20

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

// Take the context (MainActivity) and an ArrayList of all items as input to class
class BoardViewAdapter(private val context: Context, private val boardItems: ArrayList<BoardEntity>) :
    RecyclerView.Adapter<BoardViewAdapter.BoardViewHolder>() {

    // Used to extract the individual components of the layout resource file created for views
    inner class BoardViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val boardId: TextView = view.findViewById(R.id.projectId)
        val boardName: TextView = view.findViewById(R.id.projectName)
        val boardDesc: TextView = view.findViewById(R.id.projectDesc)
        val deadline: TextView = view.findViewById(R.id.deadline)
        val board: CardView = view.findViewById(R.id.boardCard)
    }

    // Create a binding for the resource file and return a ViewHolder for the same
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.board_row, parent, false)
        return BoardViewHolder(view)
    }

    // Bind data to each component in the ViewHolder
    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        val board:BoardEntity = boardItems[position]
        holder.boardId.text = (position+1).toString()
        holder.boardName.text = board.boardName
        holder.boardDesc.text = board.boardDesc
        holder.deadline.text = board.deadlineDate

        holder.board.setOnClickListener {
            if(boardItems.size != 0) {
                val intent = Intent(context, BoardListActivity::class.java)
                intent.putExtra("boardId", board.id)
                context.startActivity(intent)
            }
        }
    }

    // Return the number of items to display in the RecyclerView
    override fun getItemCount(): Int {
        return boardItems.size
    }
}