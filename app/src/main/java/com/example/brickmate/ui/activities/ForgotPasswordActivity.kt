package com.example.brickmate.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.brickmate.R
import com.example.brickmate.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : BaseActivity() {

    private lateinit var binding : ActivityForgotPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar()

        binding.btnSubmit.setOnClickListener {
            resetPassword()
        }
    }

    private fun resetPassword() {
        val email : String = binding.etResetEmail.text.toString().trim { it <= ' ' }
        if (email.isEmpty()){
            Toast.makeText(this, resources.getString(R.string.err_msg_enter_email), Toast.LENGTH_SHORT).show()
        }else{
            showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener{ task ->
                    hideProgressDialog()
                    if (task.isSuccessful){
                        Toast.makeText(this@ForgotPasswordActivity, resources.getString(R.string.email_sent_success),
                            Toast.LENGTH_LONG).show()

                        finish()

                    }else{
                        Toast.makeText(this, task.exception!!.message.toString(), Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun setUpActionBar() {
        val toolbar = binding.toolbarForgotPasswordActivity
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_vector_back_arrow_light)
        }
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}