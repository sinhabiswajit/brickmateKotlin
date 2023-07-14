package com.example.brickmate.util

import com.example.brickmate.model.Customer

interface CustomerClickInterface {
    fun onItemClick(customer : Customer)
}