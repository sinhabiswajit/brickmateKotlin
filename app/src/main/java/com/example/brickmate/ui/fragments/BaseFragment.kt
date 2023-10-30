package com.example.brickmate.ui.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.brickmate.R
import java.text.SimpleDateFormat
import java.util.*

open class BaseFragment : Fragment() {

    private lateinit var mProgressDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_base, container, false)
    }

    fun generateEnquiryDate(): String {
        val date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        return "$date - $time"
    }

    fun generateQuoteValidDate(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 7) // Add 7 days to the current date

        val date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(calendar.time)
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(calendar.time)

        return "$date - $time"
    }

    fun showProgressDialog(text: String){
        mProgressDialog = Dialog(requireActivity())

        /* Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level view to the screen */
        mProgressDialog.setContentView(R.layout.dialog_progress)
        mProgressDialog.findViewById<TextView>(R.id.tv_progress_text).text = text
        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)

        //Start the dialog and display it on screen
        mProgressDialog.show()
    }
    fun hideProgressDialog(){
        mProgressDialog.dismiss()
    }

}