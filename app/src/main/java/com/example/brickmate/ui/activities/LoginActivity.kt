package com.example.brickmate.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.View.OnClickListener
import android.widget.*
import com.example.brickmate.R
import com.example.brickmate.firestore.FireStoreClass
import com.example.brickmate.model.User
import com.example.brickmate.util.Constants
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : BaseActivity(), OnClickListener {

    private var etLoginEmail : EditText? = null
    private var etLoginPassword : EditText? = null
    private var cbShowPassword : CheckBox? = null
    private var tvForgotPassword : TextView? = null
    private var btnLogin : Button? = null
    private var tvSignUp : TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initializeVars()

        cbShowPassword?.setOnClickListener(this)
        tvForgotPassword?.setOnClickListener(this)
        btnLogin?.setOnClickListener(this)
        tvSignUp?.setOnClickListener(this)

    }

    private fun initializeVars() {
        etLoginEmail = findViewById(R.id.et_login_email)
        etLoginPassword = findViewById(R.id.et_login_password)
        cbShowPassword = findViewById(R.id.cb_show_password)
        tvForgotPassword = findViewById(R.id.tv_forgot_password)
        btnLogin = findViewById(R.id.btn_login)
        tvSignUp = findViewById(R.id.tv_sign_up)
    }

    override fun onClick(view: View) {
        when(view.id) {
            R.id.btn_login -> {
                logInRegisteredUser()
            }
            R.id.cb_show_password -> {
                showEnteredPassword()
            }
            R.id.tv_forgot_password -> {
                val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
                startActivity(intent)
            }
            R.id.tv_sign_up -> {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun showEnteredPassword() {
        Toast.makeText(this,"Implementation waiting", Toast.LENGTH_SHORT).show()
    }

    private fun logInRegisteredUser() {
        if (validateLoginDetails()){
            showProgressDialog(resources.getString(R.string.please_wait))

            val email = etLoginEmail?.text.toString().trim{ it <= ' '}
            val password = etLoginPassword?.text.toString().trim { it <= ' ' }

            // Log-In using Firebase Authentication
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        FireStoreClass().getUserDetails(this@LoginActivity)
                    }else{
                        hideProgressDialog()
                        Toast.makeText(this,task.exception!!.message.toString(), Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun validateLoginDetails() : Boolean {
        return when {
            TextUtils.isEmpty(etLoginEmail?.text.toString().trim { it <= ' ' }) ->{
                Toast.makeText(this, resources.getString(R.string.err_msg_enter_email), Toast.LENGTH_SHORT).show()
                false
            }
            TextUtils.isEmpty(etLoginPassword?.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(this, resources.getString(R.string.err_msg_enter_password), Toast.LENGTH_SHORT).show()
                false
            }
            else ->{
                true
            }
        }
    }

    fun userLoggedInSuccess(user: User) {
        hideProgressDialog()
        val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
        intent.putExtra(Constants.EXTRA_USER_DETAILS, user)
        startActivity(intent)
        finish()
    }
}