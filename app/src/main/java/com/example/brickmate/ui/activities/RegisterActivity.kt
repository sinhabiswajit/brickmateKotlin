package com.example.brickmate.ui.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.brickmate.R
import com.example.brickmate.firestore.FireStoreClass
import com.example.brickmate.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class RegisterActivity : BaseActivity() {

    private var toolBarRegisterActivity : androidx.appcompat.widget.Toolbar? = null
    var etFirstName : EditText? = null
    var etLastName : EditText? = null
    var etEmail : EditText? = null
    var etPassword : EditText? = null
    var etConfirmPassword : EditText? = null
    var btnRegister : Button? = null

    private var tvLogin : TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        initializeVars()

        setupActionBar()

        tvLogin?.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        btnRegister?.setOnClickListener {
            //Toast.makeText(this,"Clicked", Toast.LENGTH_SHORT).show()
            registerUser()
        }
    }

    private fun registerUser() {
        if (validateRegisterUserDetails()){
            showProgressDialog(resources.getString(R.string.please_wait))

            val email = etEmail?.text.toString().trim { it <= ' '}
            val password = etPassword?.text.toString().trim { it <= ' '}

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener{ task ->
                    if (task.isSuccessful){
                        val firebaseUser : FirebaseUser = task.result!!.user!!

                        val user = User(
                            firebaseUser.uid,
                            etFirstName?.text.toString().trim { it <= ' ' },
                            etLastName?.text.toString().trim { it <= ' ' },
                            etEmail?.text.toString().trim { it <= ' ' })
                        FireStoreClass().registerUser(this@RegisterActivity, user)

                        //FirebaseAuth.getInstance().signOut()

                    }
                    else{
                        hideProgressDialog()
                        Toast.makeText(this@RegisterActivity, task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    fun userRegistrationSuccess(){
        hideProgressDialog()
        Toast.makeText(this@RegisterActivity,
            resources.getString(R.string.success_msg_registration),
            Toast.LENGTH_LONG).show()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun setupActionBar() {
        setSupportActionBar(toolBarRegisterActivity)

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_vector_back_arrow_dark)

        toolBarRegisterActivity?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun initializeVars() {
        toolBarRegisterActivity = findViewById(R.id.toolbar_register_activity)
        tvLogin = findViewById(R.id.tv_login)
        etFirstName = findViewById(R.id.et_register_firstname)
        etLastName = findViewById(R.id.et_register_lastname)
        etEmail = findViewById(R.id.et_register_email)
        etPassword = findViewById(R.id.et_register_password)
        etConfirmPassword = findViewById(R.id.et_register_confirm_password)
        btnRegister = findViewById(R.id.btn_sign_up)
    }

    private fun validateRegisterUserDetails(): Boolean {
        return when {
            TextUtils.isEmpty(etFirstName?.text.toString().trim { it <= ' ' }) ->{
                Toast.makeText(this,resources.getString(R.string.err_msg_enter_first_name), Toast.LENGTH_SHORT).show()
                false
            }
            TextUtils.isEmpty(etLastName?.text.toString().trim { it <= ' ' }) ->{
                Toast.makeText(this,resources.getString(R.string.err_msg_enter_last_name), Toast.LENGTH_SHORT).show()
                false
            }
            TextUtils.isEmpty(etEmail?.text.toString().trim { it <= ' ' }) ->{
                Toast.makeText(this,resources.getString(R.string.err_msg_enter_email), Toast.LENGTH_SHORT).show()
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail?.text.toString().trim { it <= ' ' }).matches() -> {
                Toast.makeText(
                    this,
                    resources.getString(R.string.err_msg_enter_valid_email),
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            TextUtils.isEmpty(etPassword?.text.toString().trim { it <= ' ' }) ->{
                Toast.makeText(this,resources.getString(R.string.err_msg_enter_password), Toast.LENGTH_SHORT).show()
                false
            }
            TextUtils.isEmpty(etConfirmPassword?.text.toString().trim { it <= ' ' }) ->{
                Toast.makeText(this,resources.getString(R.string.err_msg_enter_confirm_password), Toast.LENGTH_SHORT).show()
                false
            }
            etPassword?.text.toString().trim { it <= ' ' } != etConfirmPassword?.text.toString().trim { it <= ' ' } ->{
                Toast.makeText(this,resources.getString(R.string.err_msg_password_and_confirm_password_mismatch), Toast.LENGTH_SHORT).show()
                false
            }
          else -> {
                true
            }
        }
    }
}