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
    private var total = 0.00


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getTotal()
        setContentView(R.layout.activity_main)


        initRecyclerViewFromDb()


        newShoppingItem.setOnClickListener {
            showAddItemDialog()
        }

        btnDeleteAll.setOnClickListener {

            Thread {
                AppDatabase.getInstance(this@MainActivity).shoppingItemDao().deleteAll()
                initRecyclerViewFromDb()
                getTotal()
            }.start()
        }

    }

    private fun initRecyclerViewFromDb() {
        Thread {
            val itemList = AppDatabase.getInstance(this@MainActivity).shoppingItemDao().getAllItems()

            runOnUiThread {

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


    private fun getTotal() {
        Thread {
            total = 0.0
            var shoppingItems = AppDatabase.getInstance(this@MainActivity).shoppingItemDao().getAllItems()
            for (i in shoppingItems) {
                total += i.price.toFloat()
            }
            runOnUiThread {
                tvEstimatedTotal.text = "Estimated Total: $total"
            }
        }.start()
    }

    override fun itemCreated(item: ShoppingItem) {
        Thread{
            var newId = AppDatabase.getInstance(this).shoppingItemDao().insertItem(item)
            item.itemID = newId
            getTotal()

            runOnUiThread{
                itemAdapter.addItem(item)
            }
        }.start()
    }

    override fun itemUpdated(item: ShoppingItem) {
        Thread {
            AppDatabase.getInstance(this).shoppingItemDao().updateItem(item)
            getTotal()
            runOnUiThread {
                itemAdapter.updateItem(item, editIndex)
            }
        }.start()
    }
}

