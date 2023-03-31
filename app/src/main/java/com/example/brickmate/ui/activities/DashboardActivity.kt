package com.example.brickmate.ui.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintSet.Constraint
import com.example.brickmate.R
import com.example.brickmate.util.Constants
import com.google.firebase.auth.FirebaseAuth

class DashboardActivity : BaseActivity(), OnClickListener {

    private lateinit var sharedPreferences: SharedPreferences

    private var toolBarDashboardActivity : androidx.appcompat.widget.Toolbar? = null
    private var clCustomers : androidx.constraintlayout.widget.ConstraintLayout? = null
    private var clProducts : androidx.constraintlayout.widget.ConstraintLayout? = null
    private var clOrder : androidx.constraintlayout.widget.ConstraintLayout? = null
    private var clOrderList : androidx.constraintlayout.widget.ConstraintLayout? = null
    private var clPayments : androidx.constraintlayout.widget.ConstraintLayout? = null
    private var clEnquiry : androidx.constraintlayout.widget.ConstraintLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        initializeVars()
        setSupportActionBar(toolBarDashboardActivity)

        clCustomers?.setOnClickListener(this)
        clProducts?.setOnClickListener(this)
        clOrder?.setOnClickListener(this)
        clOrderList?.setOnClickListener(this)
        clPayments?.setOnClickListener(this)
        clEnquiry?.setOnClickListener(this)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.dashboard_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.action_user -> {
                Toast.makeText(this, "Contact Developer", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        val builder = AlertDialog.Builder(this@DashboardActivity)
        builder.setTitle("Alert")
        builder.setMessage("Are you sure you want to logout?")
        builder.setPositiveButton("Yes"){ _, _ ->
            FirebaseAuth.getInstance().signOut()
            sharedPreferences = getSharedPreferences(Constants.BRICKMATE_PREFERENCES, Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putBoolean(Constants.LOG_IN_STATUS, false)
            editor.apply()
            val intent = Intent(this@DashboardActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        builder.setNegativeButton("No"){ _, _ ->
            Toast.makeText(applicationContext,"Clicked No",Toast.LENGTH_LONG).show()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(true)
        alertDialog.show()
    }

    private fun initializeVars() {
        toolBarDashboardActivity = findViewById(R.id.toolbar_dashboard_activity)
        clCustomers = findViewById(R.id.cl_customers)
        clProducts = findViewById(R.id.cl_products)
        clOrder = findViewById(R.id.cl_order)
        clOrderList = findViewById(R.id.cl_order_list)
        clPayments = findViewById(R.id.cl_payments)
        clEnquiry = findViewById(R.id.cl_enquiry)
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.cl_customers -> {
                val intent = Intent(this@DashboardActivity, CustomerActivity::class.java)
                startActivity(intent)
            }
            R.id.cl_products -> {
                //Toast.makeText(this, "Contact Developer", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@DashboardActivity, ProductActivity::class.java)
                startActivity(intent)
            }
            R.id.cl_order -> {
                Toast.makeText(this, "Contact Developer", Toast.LENGTH_SHORT).show()
            }
            R.id.cl_order_list -> {
                Toast.makeText(this, "Contact Developer", Toast.LENGTH_SHORT).show()
            }
            R.id.cl_payments -> {
                Toast.makeText(this, "Contact Developer", Toast.LENGTH_SHORT).show()
            }
            R.id.cl_enquiry -> {
                Toast.makeText(this, "Contact Developer", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        doubleBackToExit()
    }

}