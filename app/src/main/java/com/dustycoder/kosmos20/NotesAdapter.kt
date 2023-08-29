package com.dustycoder.kosmos20

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NotesAdapter(private val context: Context, private val notes:ArrayList<NotesEntity>, private val dao: KosmosDao) :
    RecyclerView.Adapter<NotesAdapter.NotesHolder>() {

        inner class NotesHolder(view: View) : RecyclerView.ViewHolder(view) {
            val notesView: TextView = view.findViewById(R.id.notesView)
            val deleteBtn: ImageView = view.findViewById(R.id.editNote)
            val editBtn: ImageView = view.findViewById(R.id.editNote)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.notes_view, parent, false)
        return NotesHolder(view)
    }

    override fun onBindViewHolder(holder: NotesHolder, position: Int) {
        val note = notes[position]

        holder.notesView.text = note.notes

        holder.deleteBtn.setOnClickListener { }
        holder.editBtn.setOnClickListener { }
    }

    override fun getItemCount(): Int {
        return notes.size
    }
}