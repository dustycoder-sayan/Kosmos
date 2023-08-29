package com.dustycoder.kosmos20

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.dustycoder.kosmos20.databinding.EditCardBinding
import com.dustycoder.kosmos20.databinding.EditListBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CardViewAdapter(val context: Context, val cards: ArrayList<CardEntity>, val kosmosDao: KosmosDao)
    : RecyclerView.Adapter<CardViewAdapter.CardViewHolder>() {

    inner class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardName: TextView = view.findViewById(R.id.cardName)
        val cardDesc: TextView = view.findViewById(R.id.cardDesc)
        val cardHolder: LinearLayout = view.findViewById(R.id.cardHolder)
        val cardEdit: ImageView = view.findViewById(R.id.cardEdit)
        val cardDel: ImageView = view.findViewById(R.id.cardDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_view, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = cards[position]

        holder.cardName.text = card.cardName
        holder.cardDesc.text = card.cardDesc

        holder.cardName.setOnClickListener {
            val intent = Intent(context, CardViewActivity::class.java)
            intent.putExtra("boardId", card.boardId)
            intent.putExtra("listId", card.listId)
            intent.putExtra("cardId", card.cardId)
            context.startActivity(intent)
        }

        holder.cardDesc.setOnClickListener {
            val intent = Intent(context, CardViewActivity::class.java)
            intent.putExtra("boardId", card.boardId)
            intent.putExtra("listId", card.listId)
            intent.putExtra("cardId", card.cardId)
            context.startActivity(intent)
        }

        holder.cardEdit.setOnClickListener { editCard(kosmosDao, card.cardId, card.boardId, card.listId, card.cardName, card.cardDesc) }
        holder.cardDel.setOnClickListener { delCard(kosmosDao, card.cardId, card.boardId, card.listId) }
    }

    override fun getItemCount(): Int {
        return cards.size
    }

    private fun editCard(kosmosDao: KosmosDao, cardId: Int, boardId: Int, listId: Int, cardName: String, cardDesc: String) {
        val createDialog = Dialog(context, androidx.appcompat.R.style.Base_ThemeOverlay_AppCompat_Dialog)
        val binding = EditCardBinding.inflate(createDialog.layoutInflater)
        createDialog.setContentView(binding.root)

        binding.cardNameInEditCard.setText(cardName)
        binding.cardDescInEditCard.setText(cardDesc)

        binding.updateCardBtn.setOnClickListener {
            val name = binding.cardNameInEditCard.text.toString()
            val desc = binding.cardDescInEditCard.text.toString()

            if(name.isNotEmpty()) {
                GlobalScope.launch {
                    withContext(Dispatchers.Main) {
                        kosmosDao.updateCard(CardEntity(cardId = cardId, boardId = boardId, listId = listId, cardName = name, cardDesc = desc))
                        Toast.makeText(context, "Card Updated Successfully!", Toast.LENGTH_SHORT).show()
                        createDialog.dismiss()
                    }
                }
            } else
                Toast.makeText(context, "Card could not be updated", Toast.LENGTH_SHORT).show()
        }

        createDialog.show()
    }

    private fun delCard(kosmosDao: KosmosDao, cardId: Int, boardId: Int, listId: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Delete Card")
        builder.setMessage("Are you sure you want to delete the Card?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton("Yes") { dialogInterface, _ ->
            kosmosDao.deleteCard(CardEntity(cardId = cardId, boardId = boardId, listId = listId))
            Toast.makeText(context, "Card Deleted Successfully", Toast.LENGTH_SHORT).show()
            dialogInterface.dismiss()
        }
        builder.setNegativeButton("No") { dialogInterface, which ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}
