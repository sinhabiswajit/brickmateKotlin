package com.example.brickmate.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import com.example.brickmate.model.Customer
import com.example.brickmate.model.Product
import com.example.brickmate.model.User
import com.example.brickmate.ui.activities.*
import com.example.brickmate.util.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FireStoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun getUserDetails(activity: LoginActivity) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.toString())

                //Here we have received the document snapshot which is converted into the User Date model object.
                val user = document.toObject(User::class.java)!!

                val sharedPreferences = activity.getSharedPreferences(
                    Constants.BRICKMATE_PREFERENCES,
                    Context.MODE_PRIVATE
                )
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString(Constants.LOGGED_IN_USERNAME, "${user.firstName} ${user.lastName}")
                editor.putBoolean(Constants.LOG_IN_STATUS, true)
                editor.apply()

                when (activity) {
                    is LoginActivity -> {
                        activity.userLoggedInSuccess(user)
                    }
                }
            }.addOnFailureListener { e ->
                when(activity){
                    is LoginActivity ->{
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName,
                    "Error while getting the details", e)

            }
    }

    fun registerUser(activity: RegisterActivity, userInfo: User) {
        mFireStore.collection(Constants.USERS)
            .document(userInfo.id)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while registering the user", e
                )
            }

    }

    fun getCurrentUserID(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

    fun addCustomer(activity: AddCustomerActivity, model: Customer) {
        mFireStore.collection(Constants.CUSTOMERS)
            .document()
            .set(model, SetOptions.merge())
            .addOnSuccessListener{
                activity.successAddCustomerListFromFireStore()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while entering new customer.", e)
            }
    }

    fun getCustomerListFromFireStore(activity: Activity) {
        mFireStore.collection(Constants.CUSTOMERS)
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())

                val customerList : ArrayList<Customer> = ArrayList()
                for (i in document.documents){
                    val customer = i.toObject(Customer::class.java)
                    customer!!.customer_id = i.id
                    customerList.add(customer)
                }
                when(activity){
                    is CustomerActivity ->{
                        activity.successGetCustomerListFromFireStore(customerList)
                    }
                    is SelectCustomerActivity -> {
                        activity.successGetCustomerListFromFireStore(customerList)
                    }
                }

            }
            .addOnFailureListener { e ->
                when(activity){
                    is CustomerActivity ->{
                        activity.hideProgressDialog()
                    }
                    is SelectCustomerActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, "Error getting customer list.", e)
            }
    }

    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?, imageType : String){
        val sRef : StorageReference = FirebaseStorage.getInstance().reference.child(
            imageType + System.currentTimeMillis() + "."
                    + Constants.getFileExtension(activity, imageFileURI)
        )

        sRef.putFile(imageFileURI!!).addOnSuccessListener { taskSnapShot ->
            // The image upload is a success
            Log.e("Firebase Image URL", taskSnapShot.metadata!!.reference!!.downloadUrl.toString())

            // Get the downloadable URL from the task snapshot
            taskSnapShot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                Log.e("Downloadable Image URL", uri.toString())
                when(activity){
                    is AddProductActivity -> {
                        activity.imageUploadSuccess(uri.toString())
                    }
                }
            }
        }.addOnFailureListener { exception ->
            when (activity) {
                is AddProductActivity -> {
                    activity.hideProgressDialog()
                }
            }
            Log.e(activity.javaClass.simpleName, exception.message, exception)
        }
    }

    fun uploadProductDetails(activity: AddProductActivity, product: Product) {
        mFireStore.collection(Constants.PRODUCTS)
            .document()
            .set(product, SetOptions.merge())
            .addOnSuccessListener {
                activity.productUploadSuccessToCloud()
            }
            .addOnFailureListener { e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,
                    "Error while uploading product details", e)
            }

    }

    fun getProductListFromFireStore(activity: ProductActivity) {
            mFireStore.collection(Constants.PRODUCTS)
                .get()
                .addOnSuccessListener { document ->
                    Log.e("Products List", document.documents.toString())
                    val productsList: ArrayList<Product> = ArrayList()
                    for (i in document.documents){
                        val product = i.toObject(Product::class.java)
                        product!!.product_id = i.id
                        productsList.add(product)
                    }
                    activity.successGetProductListFromFireStore(productsList)
                }
                .addOnFailureListener {e->
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName,
                        "Error while getting products", e)
                }
    }


}