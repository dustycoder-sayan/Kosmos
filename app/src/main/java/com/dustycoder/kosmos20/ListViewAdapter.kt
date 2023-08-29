package com.dustycoder.kosmos20

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dustycoder.kosmos20.databinding.AddCardBinding
import com.dustycoder.kosmos20.databinding.CreateListBinding
import com.dustycoder.kosmos20.databinding.EditListBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListViewAdapter(private val context: Context, private val listItems:ArrayList<ListEntity>, private val dao: KosmosDao) :
    RecyclerView.Adapter<ListViewAdapter.ListViewHolder>() {

    inner class ListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val listNameInListView : TextView = view.findViewById(R.id.listNameInListView)
        val addNewCardBtn : Button = view.findViewById(R.id.addNewCardBtn)
        val noCardsText: TextView = view.findViewById(R.id.noCardsText)
        val cards : RecyclerView = view.findViewById(R.id.cards)
        val deleteListBtn: ImageView = view.findViewById(R.id.deleteListBtn)
        val editListBtn: ImageView = view.findViewById(R.id.editListBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_view, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val list = listItems[position]

        holder.listNameInListView.text = list.listName
        holder.deleteListBtn.setOnClickListener {
            deleteList(dao, list.id, list.boardId)
        }
        holder.editListBtn.setOnClickListener {
            updateList(dao, list.id, list.boardId)
        }

        holder.addNewCardBtn.setOnClickListener { addNewCard(dao, list.boardId, list.id) }

        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                loadCards(list, holder)
            }
        }
    }

    private suspend fun loadCards(list:ListEntity, holder: ListViewHolder) {
        dao.getAllCardsForList(list.boardId, list.id).collect {
            val list = ArrayList<CardEntity>()
            list.addAll(it)
            setUpToRecyclerView(list, dao, holder)
        }
    }

    private fun setUpToRecyclerView(list: ArrayList<CardEntity>, boardDao: KosmosDao, holder: ListViewHolder) {
        if(list.isNotEmpty()) {
            holder.noCardsText.visibility = View.GONE
            holder.cards.visibility = View.VISIBLE
            holder.cards.layoutManager = LinearLayoutManager(context)
            holder.cards.adapter = CardViewAdapter(context, list, dao)
        } else {
            holder.noCardsText.visibility = View.VISIBLE
            holder.cards.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    private fun deleteList(kosmosDao: KosmosDao, listId:Int?, boardId:Int?) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Delete List")
        builder.setMessage("Are you sure you want to delete the List?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton("Yes") { dialogInterface, _ ->
            kosmosDao.deleteList(ListEntity(listId!!, boardId!!))
            Toast.makeText(context, "List Deleted Successfully", Toast.LENGTH_SHORT).show()
            dialogInterface.dismiss()
        }
        builder.setNegativeButton("No") { dialogInterface, which ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun updateList(kosmosDao: KosmosDao, listId: Int, boardId: Int?) {
        val createDialog = Dialog(context, androidx.appcompat.R.style.Base_ThemeOverlay_AppCompat_Dialog)
        val binding = EditListBinding.inflate(createDialog.layoutInflater)
        createDialog.setContentView(binding.root)

        binding.editListBtn2.setOnClickListener {
            val name = binding.listNameEditText.text.toString()

            if(name.isNotEmpty()) {
                GlobalScope.launch {
                    withContext(Dispatchers.Main) {
                        kosmosDao.updateList(ListEntity(id = listId,boardId = boardId!!, listName = name))
                        Toast.makeText(context, "List Updated Successfully!",Toast.LENGTH_SHORT).show()
                        createDialog.dismiss()
                    }
                }
            } else
                Toast.makeText(context, "List could not be updated",Toast.LENGTH_SHORT).show()
        }

        createDialog.show()
    }

    private fun addNewCard(kosmosDao: KosmosDao, boardId: Int, listId: Int) {
        val createDialog = Dialog(context, androidx.appcompat.R.style.Theme_AppCompat_Dialog)
        val binding = AddCardBinding.inflate(createDialog.layoutInflater)
        createDialog.setContentView(binding.root)

        binding.addCard.setOnClickListener {
            val cardName = binding.cardNameNew.text.toString()
            val cardDesc = binding.cardDescNew.text.toString()
            val day = binding.datePicker.dayOfMonth.toString()
            val month = binding.datePicker.month.toString()
            val year = binding.datePicker.year.toString()
            val datePicked = "$day/$month/$year"

            if(cardName.isNotEmpty()) {
                GlobalScope.launch {
                    withContext(Dispatchers.Main) {
                        kosmosDao.addCard(CardEntity(cardName = cardName, cardDesc = cardDesc, boardId = boardId, listId = listId,
                        endDate = datePicked))
                        Toast.makeText(context, "Card Added Successfully!", Toast.LENGTH_SHORT).show()
                        createDialog.dismiss()
                    }
                }
            } else
                Toast.makeText(context, "Card could not be added", Toast.LENGTH_SHORT).show()
        }

        createDialog.show()
    }
}