package com.eddyvarela.shopping

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.*
import com.eddyvarela.shopping.data.ShoppingItem
import kotlinx.android.synthetic.main.new_item_dialog.view.*
import java.lang.RuntimeException
import java.util.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ItemDialog : DialogFragment(), AdapterView.OnItemSelectedListener {


    interface ItemHandler {
        fun itemCreated(item: ShoppingItem)
        fun itemUpdated(item: ShoppingItem)
    }

    private lateinit var itemHandler: ItemHandler


    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is ItemHandler) {
            itemHandler = context
        } else {
            throw RuntimeException(
                "The activity does not implement the ItemHandlerInterface"
            )
        }
    }

    private lateinit var etItemCategory: Spinner
    private lateinit var etItemPrice: EditText
    private lateinit var etItemTitle: EditText
    private lateinit var etItemDescription: EditText


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {


        var categoryAdapter = ArrayAdapter.createFromResource(
            this.context,
            R.array.array_category, android.R.layout.simple_spinner_item
        )

        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


        var rootView = requireActivity().layoutInflater.inflate(
            R.layout.new_item_dialog, null
        )
        rootView.spinnerCategory.adapter = categoryAdapter

        var builder = AlertDialog.Builder(requireContext())

        builder.setTitle(getString(R.string.Make_new_item))

        rootView.spinnerCategory.onItemSelectedListener = this
        etItemTitle = rootView.etTitle
        etItemDescription = rootView.etDescription


        etItemPrice = rootView.etPrice
        etItemCategory = rootView.spinnerCategory

        builder.setView(rootView)

        var arguments = this.arguments

        // IF I AM IN EDIT MODE
        if (arguments != null && arguments.containsKey(
                MainActivity.KEY_ITEM_TO_EDIT
            )
        ) {

            var shoppingItem = arguments.getSerializable(
                MainActivity.KEY_ITEM_TO_EDIT
            ) as ShoppingItem

            builder.setTitle("Edit Item")

            etItemTitle.setText(shoppingItem.title)
            etItemDescription.setText(shoppingItem.description)
            etItemPrice.setText(shoppingItem.price)
            etItemCategory.setSelection(getIndex(shoppingItem.category))

        }

        builder.setPositiveButton("OK") { _, _ ->
            // empty
        }

        return builder.create()
    }

    private fun getIndex(s: String): Int {

        return when (s) {
            "Groceries" -> 0
            "Clothing" -> 1
            "Miscellaneous" -> 2
            else -> 2
        }
    }
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onResume() {
        super.onResume()

        val positiveButton = (dialog as AlertDialog).getButton(Dialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener {
            if (etItemTitle.text.isNotEmpty()) {
                val arguments = this.arguments
                // IF EDIT MODE
                if (arguments != null && arguments.containsKey(MainActivity.KEY_ITEM_TO_EDIT)) {
                    handleItemEdit()
                } else {
                    handleItemCreate()
                }

                dialog.dismiss()
            } else {
                etItemTitle.error = "This field can not be empty"
                etItemDescription.error = "This field can not be empty"
                etItemPrice.error = "This field can not be empty"
            }
        }
    }

    private fun handleItemCreate() {
        itemHandler.itemCreated(
            ShoppingItem(
                null,
                etItemTitle.text.toString(),
                etItemDescription.text.toString(),
                etItemCategory.selectedItem.toString(),
                etItemPrice.text.toString(),
                Date(System.currentTimeMillis()).toString(),
                false
            )
        )
    }


    private fun handleItemEdit() {
        val shoppingItemEdit = arguments?.getSerializable(
            MainActivity.KEY_ITEM_TO_EDIT
        ) as ShoppingItem
        shoppingItemEdit.title = etItemTitle.text.toString()
        shoppingItemEdit.description = etItemDescription.text.toString()
        shoppingItemEdit.category = etItemCategory.selectedItem.toString()
        shoppingItemEdit.price = etItemPrice.text.toString()
        itemHandler.itemUpdated(shoppingItemEdit)
    }

}
