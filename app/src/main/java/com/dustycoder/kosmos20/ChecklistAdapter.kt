package com.dustycoder.kosmos20

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ChecklistAdapter(val list:ArrayList<ChecklistEntity>, val dao: KosmosDao) :
    RecyclerView.Adapter<ChecklistAdapter.ChecklistViewHolder>()
{
    inner class ChecklistViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        var checklist: CheckBox = view.findViewById(R.id.checkBox2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChecklistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.checklist, parent, false)
        return ChecklistViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChecklistViewHolder, position: Int) {
        val task = list[position]

        holder.checklist.text =task.task
        holder.checklist.isChecked = task.isChecked

        holder.checklist.setOnCheckedChangeListener { _, isSelected ->
            if(!task.isChecked) {
                task.isChecked = true
                GlobalScope.launch {
                    dao.updateChecklist(task)
                }
            } else {
                task.isChecked = false
                GlobalScope.launch {
                    dao.updateChecklist(task)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}