package com.example.brickmate.ui.activities

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.*
import com.example.brickmate.R
import com.example.brickmate.firestore.FireStoreClass
import com.example.brickmate.model.User
import com.example.brickmate.util.Constants
import com.example.brickmate.util.Constants.RC_SIGN_IN
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

open class LoginActivity : BaseActivity(), OnClickListener {

    private var etLoginEmail: EditText? = null
    private var etLoginPassword: EditText? = null
    private var tvForgotPassword: TextView? = null
    private var btnLogin: Button? = null
    private var tvSignUp: TextView? = null
    private var llGoogleSignIn: LinearLayout? = null

    private lateinit var googleSignInClient: GoogleSignInClient
    private var mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initializeVars()
        configureGoogleSignIn()

        tvForgotPassword?.setOnClickListener(this)
        btnLogin?.setOnClickListener(this)
        tvSignUp?.setOnClickListener(this)
        llGoogleSignIn?.setOnClickListener(this)

    }

    private fun initializeVars() {
        etLoginEmail = findViewById(R.id.et_login_email)
        etLoginPassword = findViewById(R.id.et_login_password)
        tvForgotPassword = findViewById(R.id.tv_forgot_password)
        btnLogin = findViewById(R.id.btn_login)
        tvSignUp = findViewById(R.id.tv_sign_up)
        llGoogleSignIn = findViewById(R.id.ll_google_sign_in)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_login -> {
                logInRegisteredUser()
            }
            R.id.tv_forgot_password -> {
                val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
                startActivity(intent)
            }
            R.id.tv_sign_up -> {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
            }
            R.id.ll_google_sign_in -> {
//                val intent = Intent(this@LoginActivity, GoogleSignInActivity::class.java)
//                startActivity(intent)
                googleSignIn()
            }
        }
    }

    private fun logInRegisteredUser() {
        if (validateLoginDetails()) {
            showProgressDialog(resources.getString(R.string.please_wait))

            val email = etLoginEmail?.text.toString().trim { it <= ' ' }
            val password = etLoginPassword?.text.toString().trim { it <= ' ' }

            // Log-In using Firebase Authentication
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        FireStoreClass().getUserDetails(this@LoginActivity)
                    } else {
                        hideProgressDialog()
                        Toast.makeText(this, task.exception!!.message.toString(), Toast.LENGTH_LONG)
                            .show()
                    }
                }
        }
    }

    private fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(etLoginEmail?.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(
                    this,
                    resources.getString(R.string.err_msg_enter_email),
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            TextUtils.isEmpty(etLoginPassword?.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(
                    this,
                    resources.getString(R.string.err_msg_enter_password),
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            else -> {
                true
            }
        }
    }

    fun userLoggedInSuccess(user: User) {
        hideProgressDialog()
        val welcomeText = "Welcome, ${user.firstName}!"
        Toast.makeText(this, welcomeText, Toast.LENGTH_SHORT).show()
        Log.e("USER_ID",FireStoreClass().getCurrentUserID())
        val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
        intent.putExtra(Constants.EXTRA_USER_DETAILS, user)
        startActivity(intent)
        finish()
    }

    private fun configureGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(getString(R.string.default_web_client_id))
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun googleSignIn() {
        showProgressDialog(resources.getString(R.string.please_wait))
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign-In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign-In failed
                hideProgressDialog()
                Log.w(TAG, "Google sign in failed", e)
                Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    hideProgressDialog()
                    // Sign in success, update UI with the signed-in user's information
                    val user = mAuth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    hideProgressDialog()
                    Toast.makeText(
                        this, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val sharedPref = getSharedPreferences(
                Constants.BRICKMATE_PREFERENCES,
                Context.MODE_PRIVATE
            )

            if (user.providerData[1].providerId == GoogleAuthProvider.PROVIDER_ID) {
                // User is signed in with Google
                sharedPref.edit().putString(Constants.LOGGED_IN_WITH, Constants.GOOGLE).apply()

            } else {
                // User is signed in with email
                sharedPref.edit().putString(Constants.LOGGED_IN_WITH, Constants.EMAIL).apply()

            }
            // Navigate to the dashboard activity
            val intent = Intent(this, DashboardActivity::class.java)
            val welcomeText = "Welcome, ${user.displayName}!"
            Toast.makeText(this, welcomeText, Toast.LENGTH_SHORT).show()
            Log.e("USER_ID",FireStoreClass().getCurrentUserID())
            startActivity(intent)
            finish()
        } else {
            // User is signed out, update UI with sign-in button
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}