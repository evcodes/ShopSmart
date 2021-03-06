package com.eddyvarela.shopping.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.eddyvarela.shopping.MainActivity
import com.eddyvarela.shopping.R
import com.eddyvarela.shopping.data.AppDatabase
import com.eddyvarela.shopping.data.ShoppingItem
import com.eddyvarela.shopping.touch.ItemTouchHelperCallback
import kotlinx.android.synthetic.main.item_row.view.*
import java.util.*

class ItemAdapter : RecyclerView.Adapter<ItemAdapter.ViewHolder>, ItemTouchHelperCallback {

    var shoppingItems = mutableListOf<ShoppingItem>()

    private val context: Context
    constructor(context: Context, listItems: List<ShoppingItem>) : super() {

        this.context = context
        shoppingItems.addAll(listItems)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val itemRowView = LayoutInflater.from(context).inflate(
            R.layout.item_row, viewGroup, false
        )
        return ViewHolder(itemRowView)
    }


    override fun getItemCount(): Int {
        return shoppingItems.size
    }


    fun addItem(item: ShoppingItem) {
        shoppingItems.add(0, item)
        //notifyDataSetChanged()
        notifyItemInserted(0)
    }

    override fun onDismissed(position: Int) {
        deleteItem(position)
    }

    private fun deleteItem(deletePosition:Int){
        Thread {
            AppDatabase.getInstance(context).shoppingItemDao().deleteItem(shoppingItems[deletePosition])
            (context as MainActivity).runOnUiThread{
                shoppingItems.removeAt(deletePosition)
                notifyItemRemoved(deletePosition)
            }
        }.start()
    }

    fun updateItem(item: ShoppingItem, editIndex: Int) {
        shoppingItems[editIndex] = item
        notifyItemChanged(editIndex)
    }

    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        Collections.swap(shoppingItems, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    private fun getImagesrc(category: String): Int {
        return when (category) {
            "Groceries" -> R.drawable.groceries
            "Miscellaneous" -> R.drawable.misc
            "Clothing" -> R.drawable.clothing
            else -> R.mipmap.ic_launcher
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var tvTitle = itemView.tvTitle!!
        var tvDate = itemView.tvDate!!
        var tvCategory = itemView.tvCategory!!
        var tvDescription = itemView.tvDescription!!
        var tvPrice = itemView.tvPrice!!
        var cbDone = itemView.cbDone!!
        var btnEdit = itemView.btnEdit!!
        var btnDelete= itemView.btnDelete!!
        var imageSrc = itemView.itemIcon!!
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = shoppingItems[viewHolder.adapterPosition]

        viewHolder.tvTitle.text = item.title
        viewHolder.tvDate.text = item.date
        viewHolder.tvCategory.text = item.category
        viewHolder.tvDescription.text = item.description
        viewHolder.tvPrice.text = item.price
        viewHolder.cbDone.isChecked = item.isPurchased
        viewHolder.imageSrc.setImageResource(getImagesrc(item.category))


        viewHolder.btnDelete.setOnClickListener {
            deleteItem(viewHolder.adapterPosition)
        }

        viewHolder.cbDone.setOnClickListener{
            item.isPurchased = viewHolder.cbDone.isChecked
            Thread {
                AppDatabase.getInstance(
                    context
                ).shoppingItemDao().updateItem(item)
            }.start()
        }

        viewHolder.btnEdit.setOnClickListener {
            (context as MainActivity).showEditItemDialog(item,
                viewHolder.adapterPosition)
        }
    }
}