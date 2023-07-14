package com.example.brickmate.ui.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.brickmate.R
import com.example.brickmate.databinding.ActivityDashboardBinding
import com.example.brickmate.databinding.ActivityUserProfileBinding
import com.example.brickmate.model.User
import com.example.brickmate.util.Constants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class UserProfileActivity : AppCompatActivity() {

    private var binding: ActivityUserProfileBinding? = null
    private lateinit var sharedPreferences: SharedPreferences
    private var toolBarUserProfileActivity: androidx.appcompat.widget.Toolbar? = null
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        initializeVars()
        setUpActionBar()

        getUserSignInInformation()

        binding?.cardLogout?.setOnClickListener {
            logout()
        }

        binding?.btnAddCompanyDetails?.setOnClickListener {

        }

    }

    private fun getUserSignInInformation() {
        // Get the user's signed-in account information and display on the card views
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) {
            binding?.tvAccountUserName?.text = account.displayName
            binding?.tvAccountUserEmail?.text = account.email
            val profilePicUri = account.photoUrl
            if (profilePicUri != null) {
                Glide.with(this)
                    .load(profilePicUri)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(binding!!.ivAccountUserPicture)
            }
        } else {
            binding?.tvAccountUserName?.text = "${user?.firstName + " " + user?.lastName}"
            binding?.tvAccountUserEmail?.text = user?.email
            Glide.with(this)
                .load(R.drawable.iv_user_placeholder)
                .placeholder(R.drawable.ic_user_placeholder)
                .into(binding!!.ivAccountUserPicture)
        }
    }

    private fun logout() {
        val builder = AlertDialog.Builder(this@UserProfileActivity)
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
                val intent = Intent(this@UserProfileActivity, LoginActivity::class.java)
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
        toolBarUserProfileActivity = binding?.toolbarUserProfileActivity
    }
    private fun setUpActionBar() {
        setSupportActionBar(toolBarUserProfileActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_vector_back_arrow_light)
        }
        toolBarUserProfileActivity?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}