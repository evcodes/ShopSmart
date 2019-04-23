package com.eddyvarela.shopping.touch


interface ItemTouchHelperCallback {
    fun onDismissed(position: Int)
    fun onItemMoved(fromPosition: Int, toPosition: Int)
}



