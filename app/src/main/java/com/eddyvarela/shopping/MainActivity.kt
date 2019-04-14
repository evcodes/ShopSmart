package com.eddyvarela.shopping

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import com.eddyvarela.shopping.adapter.ItemAdapter
import com.eddyvarela.shopping.data.AppDatabase
import com.eddyvarela.shopping.data.ShoppingItem
import com.eddyvarela.shopping.touch.ItemRecyclerTouchCallback
import kotlinx.android.synthetic.main.activity_main.*

@Suppress("MayBeConstant")
class MainActivity : AppCompatActivity(), ItemDialog.ItemHandler {

    companion object{

        val KEY_ITEM_TO_EDIT = "KEY_ITEM_TO_EDIT"
    }

    private lateinit var itemAdapter : ItemAdapter

    private var editIndex: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        newShoppingItem.setOnClickListener{
               showAddItemDialog()
        }
        initRecyclerViewFromDb()
    }

    private fun initRecyclerViewFromDb() {
        Thread {
            val itemList = AppDatabase.getInstance(this@MainActivity).shoppingItemDao().getAllItems()

            runOnUiThread {
                // Update UI

                itemAdapter = ItemAdapter(this, itemList)

                recyclerView.layoutManager = LinearLayoutManager(this)

                recyclerView.adapter = itemAdapter

                val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
                recyclerView.addItemDecoration(itemDecoration)

                val callback = ItemRecyclerTouchCallback(itemAdapter)
                val touchHelper = ItemTouchHelper(callback)
                touchHelper.attachToRecyclerView(recyclerView)
            }

        }.start()
    }

    private fun showAddItemDialog() {
        ItemDialog().show(supportFragmentManager, "TAG_ITEM_DIALOG")
    }

    fun showEditItemDialog(itemToEdit: ShoppingItem, idx: Int) {
        editIndex = idx
        val editItemDialog = ItemDialog()

        val bundle = Bundle()
        bundle.putSerializable(KEY_ITEM_TO_EDIT, itemToEdit)
        editItemDialog.arguments = bundle

        editItemDialog.show(supportFragmentManager,
            "EDITITEMDIALOG")
    }

    override fun itemCreated(item: ShoppingItem) {
        Thread{
            val newId = AppDatabase.getInstance(this).shoppingItemDao().insertItem(item)

            item.itemID = newId

            runOnUiThread{
                itemAdapter.addItem(item)
            }
        }.start()
    }

    override fun itemUpdated(item: ShoppingItem) {
        Thread {
            AppDatabase.getInstance(this).shoppingItemDao().updateItem(item)

            runOnUiThread {
                itemAdapter.updateItem(item, editIndex)
            }
        }.start()
    }
}
