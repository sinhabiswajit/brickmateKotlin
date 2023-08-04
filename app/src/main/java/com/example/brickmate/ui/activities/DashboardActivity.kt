package com.example.brickmate.ui.activities

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintSet.Constraint
import com.bumptech.glide.Glide
import com.example.brickmate.R
import com.example.brickmate.databinding.ActivityCustomerBinding
import com.example.brickmate.databinding.ActivityDashboardBinding
import com.example.brickmate.firestore.FireStoreClass
import com.example.brickmate.model.User
import com.example.brickmate.util.Constants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class DashboardActivity : BaseActivity(), OnClickListener {

    private lateinit var sharedPreferences: SharedPreferences

    private var toolBarDashboardActivity: androidx.appcompat.widget.Toolbar? = null
    private var clCustomers: androidx.constraintlayout.widget.ConstraintLayout? = null
    private var clProducts: androidx.constraintlayout.widget.ConstraintLayout? = null
    private var clOrder: androidx.constraintlayout.widget.ConstraintLayout? = null
    private var clOrderList: androidx.constraintlayout.widget.ConstraintLayout? = null
    private var clPayments: androidx.constraintlayout.widget.ConstraintLayout? = null
    private var clEnquiry: androidx.constraintlayout.widget.ConstraintLayout? = null
    private var binding: ActivityDashboardBinding? = null
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        initializeVars()
        setSupportActionBar(toolBarDashboardActivity)

        user = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)

        clCustomers?.setOnClickListener(this)
        clProducts?.setOnClickListener(this)
        clOrder?.setOnClickListener(this)
        clOrderList?.setOnClickListener(this)
        clPayments?.setOnClickListener(this)
        clEnquiry?.setOnClickListener(this)
        binding?.tvFooter?.setOnClickListener(this)

    }

    override fun onResume() {
        super.onResume()
        fetchOrderCounts()
    }

    private fun fetchOrderCounts() {
        FireStoreClass().getTotalOrdersCount(this)
        FireStoreClass().getTotalUnpaidOrdersCount(this)
        FireStoreClass().getTotalUndeliveredOrdersCount(this)
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
                // Not showing the User Info Dialog box anymore when user clicks on the user icon, instead sending the user to another activity called UserProfileActivity
                //showUserProfileDialog()
                val intent = Intent(this, UserProfileActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showUserProfileDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_profile)
        dialog.setCancelable(true)

        // Get the user's signed-in account information
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) {
            dialog.findViewById<TextView>(R.id.tv_dialog_user_name).text = account.displayName
            dialog.findViewById<TextView>(R.id.tv_dialog_user_email).text = account.email
            val profilePicUri = account.photoUrl
            if (profilePicUri != null) {
                Glide.with(this)
                    .load(profilePicUri)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(dialog.findViewById(R.id.iv_dialog_user_picture))
            }
        } else {
            dialog.findViewById<TextView>(R.id.tv_dialog_user_name).text =
                "${user?.firstName + " " + user?.lastName}"
            dialog.findViewById<TextView>(R.id.tv_dialog_user_email).text = user?.email
            Glide.with(this)
                .load(R.drawable.ic_user_placeholder)
                .placeholder(R.drawable.ic_user_placeholder)
                .into(dialog.findViewById(R.id.iv_dialog_user_picture))
        }

        dialog.findViewById<Button>(R.id.btn_dialog_dismiss).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun logout() {
        val builder = AlertDialog.Builder(this@DashboardActivity)
        builder.setTitle("Alert")
        builder.setMessage("Are you sure you want to logout?")
        builder.setPositiveButton("Yes") { _, _ ->
            // sign out of Firebase Authentication
            FirebaseAuth.getInstance().signOut()

            // sign out of Google Sign-In
            val googleSignInClient =
                GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN)
            googleSignInClient.signOut().addOnCompleteListener(this) {
                // clear login status in shared preferences
                sharedPreferences =
                    getSharedPreferences(Constants.BRICKMATE_PREFERENCES, Context.MODE_PRIVATE)
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putBoolean(Constants.LOG_IN_STATUS, false)
                editor.apply()
                // go to login activity
                val intent = Intent(this@DashboardActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
        builder.setNegativeButton("No") { _, _ ->
            Toast.makeText(applicationContext, "Clicked No", Toast.LENGTH_LONG).show()
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
        when (view?.id) {
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
                //Toast.makeText(this, "Contact Developer", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@DashboardActivity, OrderActivity::class.java)
                startActivity(intent)
            }
            R.id.cl_order_list -> {
                //Toast.makeText(this, "Contact Developer", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@DashboardActivity, OrderListActivity::class.java)
                startActivity(intent)
            }
            R.id.cl_payments -> {
                //Toast.makeText(this, "Contact Developer", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@DashboardActivity, PaymentActivity::class.java)
                startActivity(intent)
            }
            R.id.cl_enquiry -> {
                val intent = Intent(this@DashboardActivity, EnquiryActivity::class.java)
                startActivity(intent)
            }
            R.id.tv_footer ->  {
                val companyWebsiteUrl = "http://indigiconsulting.com/"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(companyWebsiteUrl))
                startActivity(intent)
            }
        }
    }

    @Deprecated("", ReplaceWith("doubleBackToExit()"))
    override fun onBackPressed() {
        doubleBackToExit()
    }

    fun successTotalOrderCount(totalOrderCount: Int) {
        binding?.dashboardTotalOrderCount?.text = totalOrderCount.toString()
    }

    fun successTotalUnpaidOrderCount(totalUnpaidOrderCount: Int) {
        binding?.dashboardUnpaidOrderCount?.text = totalUnpaidOrderCount.toString()
    }

    fun successTotalUndeliveredOrderCount(totalUndeliveredOrderCount: Int) {
        binding?.dashboardUndeliveredOrderCount?.text = totalUndeliveredOrderCount.toString()
    }

}