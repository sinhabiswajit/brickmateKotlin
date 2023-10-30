package com.example.brickmate.firestore

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.brickmate.model.*
import com.example.brickmate.ui.activities.*
import com.example.brickmate.ui.fragments.*
import com.example.brickmate.util.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.Serializable
import kotlin.collections.HashMap

class FireStoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun getUserDetails(activity: LoginActivity) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.toString())

                //Here we have received the document snapshot which is converted into the User Data model object.
                val user = document.toObject(User::class.java)!!

                val sharedPreferences = activity.getSharedPreferences(
                    Constants.BRICKMATE_PREFERENCES,
                    Context.MODE_PRIVATE
                )
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString(Constants.LOGGED_IN_USERNAME, "${user.firstName} ${user.lastName}")
                editor.putBoolean(Constants.LOG_IN_STATUS, true)
                editor.apply()
                activity.userLoggedInSuccess(user)

            }.addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting the details", e
                )

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
            .addOnSuccessListener {
                activity.successAddCustomerListFromFireStore()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while entering new customer.", e)
            }
    }

    fun getCustomerListFromFireStore(activity: Activity) {
        mFireStore.collection(Constants.CUSTOMERS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())

                val customerList = ArrayList<Customer>()
                for (i in document.documents) {
                    val customer = i.toObject(Customer::class.java)
                    customer!!.customer_id = i.id
                    customerList.add(customer)
                }
                when (activity) {
                    is CustomerActivity -> {
                        activity.successGetCustomerListFromFireStore(customerList)
                        Log.e("CustomerList", customerList.toString())
                    }

                    is OrderActivity -> {
                        activity.successGetCustomerListFromFireStore(customerList)
                    }
//                    is PaymentActivity ->{
//                        activity.successGetCustomerListFromFireStore(customerList)
//                    }
                }

            }
            .addOnFailureListener { e ->
                when (activity) {
                    is CustomerActivity -> {
                        activity.hideProgressDialog()
                    }

                    is OrderActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, "Error getting customer list.", e)
            }
    }

    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?, imageType: String) {
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            imageType + System.currentTimeMillis() + "."
                    + Constants.getFileExtension(activity, imageFileURI)
        )

        sRef.putFile(imageFileURI!!).addOnSuccessListener { taskSnapShot ->
            // The image upload is a success
            Log.e("Firebase Image URL", taskSnapShot.metadata!!.reference!!.downloadUrl.toString())

            // Get the downloadable URL from the task snapshot
            taskSnapShot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                Log.e("Downloadable Image URL", uri.toString())
                when (activity) {
                    is AddProductActivity -> {
                        activity.imageUploadSuccess(uri.toString())
                    }

                    is AddCompanyActivity -> {
                        activity.imageUploadSuccess(uri.toString())
                    }


                }
            }
        }.addOnFailureListener { exception ->
            when (activity) {
                is AddProductActivity -> {
                    activity.hideProgressDialog()
                }

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
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while uploading product details", e
                )
            }

    }

    fun getProductListFromFireStore(activity: Activity) {
        mFireStore.collection(Constants.PRODUCTS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e("Products List", document.documents.toString())
                val productsList: ArrayList<Product> = ArrayList()
                for (i in document.documents) {
                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id
                    productsList.add(product)
                }
                when (activity) {
                    is ProductActivity -> {
                        activity.successGetProductListFromFireStore(productsList)
                    }

                    is OrderDetailsUpdateActivity -> {
                        activity.successGetProductListFromFireStore(productsList)
                    }
                }

            }
            .addOnFailureListener { e ->
                when (activity) {
                    is ProductActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting products", e
                )
            }
    }

    fun uploadCustomerAddressToFireStore(
        activity: AddAddressActivity,
        address: Address,
        name: String
    ) {
        mFireStore.collection(Constants.CUSTOMERS)
            .whereEqualTo("name", name).limit(1)
            .get()
            .addOnSuccessListener { documents ->
                for (doc in documents) {
                    val customerId = doc.id
                    val newAddress = Address(address.address, address.zipCode, address.landmark)
                    mFireStore.collection(Constants.CUSTOMERS).document(customerId)
                        .update("addresses", FieldValue.arrayUnion(newAddress))
                        .addOnSuccessListener {
                            // Address added successfully
                            activity.customerAddressUploadSuccess()
                        }
                        .addOnFailureListener { e ->
                            // Handle any errors here
                        }
                }

            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while uploading customer address details", e
                )
            }
    }

    fun getProductDetails(activity: ProductDetailsActivity, productId: String) {
        mFireStore.collection(Constants.PRODUCTS)
            .document(productId)
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.toString())

                val product = document.toObject(Product::class.java)
                if (product != null) {
                    activity.productDetailsSuccess(product)
                }
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting the product details.", e
                )
            }
    }

    fun saveCartListToFireStore(activity: ProductDetailsActivity, cartItem: CartItem) {
        mFireStore.collection(Constants.CART_ITEMS)
            .document()
            .set(cartItem, SetOptions.merge())
            .addOnSuccessListener {
                activity.addToCartSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating the document for cart item", e
                )
            }
    }

    fun checkIfItemExistsInCart(activity: ProductDetailsActivity, productId: String) {
        mFireStore.collection(Constants.CART_ITEMS)
            .whereEqualTo("product_id", productId)
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())
                if (document.documents.size > 0) {
                    activity.productExistsInCart()
                } else {
                    activity.hideProgressDialog()
                }
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while checking the existing cart list", e
                )
            }
    }

    fun getCustomerAddresses(activity: Activity, customerName: String) {
        mFireStore.collection(Constants.CUSTOMERS)
            .whereEqualTo("name", customerName)
            .get()
            .addOnSuccessListener { documents ->
                val addresses = ArrayList<Address>()
                for (document in documents) {
                    val customer = document.toObject(Customer::class.java)
                    addresses.addAll(customer.addresses)
                    Log.e("FireStore addresses", addresses.toString())
                }
                when (activity) {
                    is SelectCustomerActivity -> {
                        activity.successAddressForSelectedCustomer(addresses)
                    }

                    is OrderActivity -> {
                        activity.successAddressForSelectedCustomer(addresses)
                    }

                    is AddressListActivity -> {
                        activity.successGetCustomerAddressListFromFireStore(addresses)
                    }
                }

            }
            .addOnFailureListener {

            }
    }

    fun saveOrderToFireStore(activity: OrderSummaryActivity, order: Order) {
        mFireStore.collection(Constants.ORDERS)
            .document()
            .set(order, SetOptions.merge())
            .addOnSuccessListener {
                activity.successSaveOrderToFireStore()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while placing order.", e)
            }
    }

    fun saveEnquiry(fragment: QuotationFormFragment, quotation: Quotation) {
        mFireStore.collection(Constants.QUOTATION)
            .add(quotation)
            .addOnSuccessListener {
                fragment.successSaveEnquiry()
            }
            .addOnFailureListener { e ->
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName, "Error while adding enquiry.", e)
            }
    }

    fun getEnquiryList(fragment: QuotationListFragment) {
        mFireStore.collection(Constants.QUOTATION)
            .get()
            .addOnSuccessListener { document ->
                Log.e("Enquiry List", document.documents.toString())
                val quotationList = ArrayList<Quotation>()
                for (i in document.documents) {
                    val quotation = i.toObject(Quotation::class.java)
                    quotation?.quotation_id = i.id
                    quotationList.add(quotation!!)
                }
                fragment.successGetEnquiryList(quotationList)
            }
            .addOnFailureListener { e ->
                fragment.hideProgressDialog()
                Log.e(TAG, "Error getting enquiry list", e)
            }
    }

    fun getProductListFromFireStore(fragment: QuotationFormFragment) {
        mFireStore.collection(Constants.PRODUCTS)
            .get()
            .addOnSuccessListener { document ->
                Log.e("Products List", document.documents.toString())
                val productsList: ArrayList<Product> = ArrayList()
                for (i in document.documents) {
                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id
                    productsList.add(product)
                }
                fragment.successGetProductList(productsList)

            }
            .addOnFailureListener { e ->

                Log.e(
                    fragment.javaClass.simpleName,
                    "Error while getting products", e
                )
            }
    }

    fun deleteEnquiryFromFireStore(fragment: QuotationListFragment, enquiryId: String) {
        mFireStore.collection(Constants.QUOTATION)
            .document(enquiryId)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "Enquiry deleted from FireStore.")
                fragment.successDeleteEnquiry()
            }
            .addOnFailureListener { e ->
                fragment.hideProgressDialog()
                Log.e(TAG, "Enquiry failed to delete from FireStore.", e)
            }
    }

    fun deleteCustomerFromFireStore(activity: CustomerDetailActivity, customerId: String) {
        mFireStore.collection(Constants.CUSTOMERS)
            .document(customerId)
            .delete()
            .addOnSuccessListener {
                activity.successDeleteCustomer()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(TAG, "Failed to delete customer.", e)
            }
    }

    fun updateProduct(
        activity: AddProductActivity,
        productId: String?,
        updatedProduct: HashMap<String, String>
    ) {
        mFireStore.collection(Constants.PRODUCTS)
            .document(productId!!)
            .update(updatedProduct as Map<String, Any>)
            .addOnSuccessListener {
                activity.successUpdateProduct()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(TAG, "Failed to update product.", e)
            }
    }

    fun deleteProduct(activity: ProductDetailsActivity, mProductId: String) {
        mFireStore.collection(Constants.PRODUCTS)
            .document(mProductId)
            .delete()
            .addOnSuccessListener {
                activity.successDeleteProduct()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(TAG, "Failed to delete product.", e)
            }
    }

    fun deleteOrderFromFireStore(activity: OrderListDetailActivity, orderId: String) {
        mFireStore.collection(Constants.ORDERS)
            .whereEqualTo("orderId", orderId)
            .get()
            .addOnSuccessListener {
                if (!it.isEmpty) {
                    for (documentSnapshot in it.documents) {
                        documentSnapshot.reference.delete()
                            .addOnSuccessListener { activity.successDeleteOrder() }
                            .addOnFailureListener { e -> Log.w(TAG, "Error deleting order", e) }
                    }
                }

            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(TAG, "Failed to delete order.", e)
            }
    }

    fun updateOrder(
        activity: OrderDetailsUpdateActivity,
        updatedOrder: Map<String, Any>,
        orderId: String
    ) {
        mFireStore.collection(Constants.ORDERS)
            .whereEqualTo("orderId", orderId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    mFireStore.collection(Constants.ORDERS)
                        .document(document.id)
                        .update(updatedOrder)
                        .addOnSuccessListener {
                            Log.d(TAG, "Order successfully updated!")
                            activity.successOrderUpdate()
                        }
                        .addOnFailureListener { e ->
                            activity.hideProgressDialog()
                            Log.e(TAG, "Error updating order", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(TAG, "Error getting document ID", e)
            }
    }

    fun updateCustomerDetail(
        activity: CustomerDetailUpdateActivity,
        updatedCustomer: java.util.HashMap<String, Serializable>,
        customerId: String
    ) {
        mFireStore.collection(Constants.CUSTOMERS)
            .document(customerId)
            .update(updatedCustomer as Map<String, Any>)
            .addOnSuccessListener {
                activity.successUpdateCustomer(updatedCustomer)
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(TAG, "Failed to update customer.", e)
            }
    }


    fun getCustomerListWithUnpaidOrders(fragment: NewOrdersFragment) {
        val unpaidCustomers = mutableListOf<Customer>()

        mFireStore.collection(Constants.ORDERS)
            .whereEqualTo("user_id", getCurrentUserID())
            .whereEqualTo("paid", false)
            .get()
            .addOnSuccessListener { documents ->
                // Group orders by customer ID
                val customerOrders = documents.groupBy { it.getString("customerId") }

                // Create a list of customers from the grouped orders
                for ((customerId, orders) in customerOrders) {
                    val customerName = orders.first().getString("customerName") ?: ""
                    val customerPhone = orders.first().getString("phone") ?: ""

                    val unpaidOrders = orders.filter {
                        it.getBoolean("paid") != null && !it.getBoolean("paid")!!
                    }
                        .map { order ->
                            val orderProducts =
                                order.get("productList") as? List<HashMap<String, Any>>
                                    ?: emptyList()
                            val orderPayments =
                                order.get("paymentList") as? List<HashMap<String, Any>>
                                    ?: emptyList()

                            // Map the list of HashMaps to a list of Product objects
                            val productList = orderProducts.map { product ->
                                Product(
                                    user_id = product["user_id"] as? String ?: "",
                                    product_id = product["productId"] as? String ?: "",
                                    name = product["name"] as? String ?: "",
                                    uom = product["uom"] as? String ?: "",
                                    sell_price = product["sell_price"] as? String ?: "",
                                    gst_rate = product["gst_rate"] as? String ?: "",
                                    quantity = (product["quantity"] as? Long)?.toInt() ?: 0,
                                    gstIncluded = (product["gstIncluded"] as? Boolean ?: false)
                                )
                            }

                            val paymentList = orderPayments.map { payment ->
                                Payment(
                                    date = payment["date"] as? String ?: "",
                                    remarks = payment["payment"] as? String ?: "",
                                    amount = payment["amount"] as? Double ?: 0.0
                                )
                            }

                            Order(
                                user_id = order.getString("user_id") ?: "",
                                orderId = order.getString("orderId") ?: "",
                                customerId = order.getString("customerId") ?: "",
                                customerName = order.getString("customerName") ?: "",
                                phone = order.getString("phone") ?: "",
                                address = order.getString("address") ?: "",
                                productList = productList,
                                total = order.getDouble("total") ?: 0.0,
                                paid = order.getBoolean("paid") ?: false,
                                orderDate = order.getString("orderDate") ?: "",
                                delivered = order.getBoolean("delivered") ?: false,
                                deliveryDate = order.getString("deliveryDate") ?: "",
                                paymentList = paymentList as ArrayList<Payment>
                            )
                        }

                    unpaidCustomers.add(
                        Customer(
                            customer_id = customerId ?: "",
                            name = customerName,
                            phone = customerPhone,
                            orders = unpaidOrders
                        )
                    )
                }

                // Update your RecyclerView with the list of unpaid customers
                fragment.updateUnpaidCustomersList(unpaidCustomers)
            }
            .addOnFailureListener { e ->
                fragment.hideProgressDialog()
                Log.w(TAG, "Error getting unpaid orders", e)
            }
    }

    fun getCustomerListWithPaidOrders(fragment: Fragment, boolean: Boolean) {
        val paidCustomers = mutableListOf<Customer>()

        mFireStore.collection(Constants.ORDERS)
            .whereEqualTo("user_id", getCurrentUserID())
            .whereEqualTo("paid", boolean)
            .get()
            .addOnSuccessListener { documents ->
                // Group orders by customer ID
                val customerOrders = documents.groupBy { it.getString("customerId") }

                // Create a list of customers from the grouped orders
                for ((customerId, orders) in customerOrders) {
                    val customerName = orders.first().getString("customerName") ?: ""
                    val customerPhone = orders.first().getString("phone") ?: ""

                    val unpaidOrders = orders.filter {
                        it.getBoolean("paid") != null && it.getBoolean("paid")!!
                    }
                        .map { order ->
                            val orderProducts =
                                order.get("productList") as? List<HashMap<String, Any>>
                                    ?: emptyList()
                            val orderPayments =
                                order.get("paymentList") as? List<HashMap<String, Any>>
                                    ?: emptyList()

                            // Map the list of HashMaps to a list of Product objects
                            val productList = orderProducts.map { product ->
                                Product(
                                    user_id = product["user_id"] as? String ?: "",
                                    product_id = product["productId"] as? String ?: "",
                                    name = product["name"] as? String ?: "",
                                    uom = product["uom"] as? String ?: "",
                                    sell_price = product["sell_price"] as? String ?: "",
                                    gst_rate = product["gst_rate"] as? String ?: "",
                                    quantity = (product["quantity"] as? Long)?.toInt() ?: 0,
                                    gstIncluded = (product["gstIncluded"] as? Boolean ?: false)
                                )
                            }
                            val paymentList = orderPayments.map { payment ->
                                Payment(
                                    date = payment["date"] as? String ?: "",
                                    remarks = payment["payment"] as? String ?: "",
                                    amount = payment["amount"] as? Double ?: 0.0
                                )
                            }

                            Order(
                                user_id = order.getString("user_id") ?: "",
                                orderId = order.getString("orderId") ?: "",
                                customerId = order.getString("customerId") ?: "",
                                customerName = order.getString("customerName") ?: "",
                                phone = order.getString("phone") ?: "",
                                address = order.getString("address") ?: "",
                                productList = productList,
                                total = order.getDouble("total") ?: 0.0,
                                paid = order.getBoolean("paid") ?: false,
                                orderDate = order.getString("orderDate") ?: "",
                                delivered = order.getBoolean("delivered") ?: false,
                                deliveryDate = order.getString("deliveryDate") ?: "",
                                paymentList = paymentList as ArrayList<Payment>
                            )
                        }

                    paidCustomers.add(
                        Customer(
                            customer_id = customerId ?: "",
                            name = customerName,
                            phone = customerPhone,
                            orders = unpaidOrders
                        )
                    )
                }
                when (fragment) {
                    is PaidOrderFragment -> {
                        // Update your RecyclerView with the list of unpaid customers
                        fragment.updatePaidCustomersList(paidCustomers)
                    }
                }
            }
            .addOnFailureListener { e ->
                when (fragment) {
                    is PaidOrderFragment -> {
                        fragment.hideProgressDialog()
                    }
                }
                Log.w(TAG, "Error getting paid orders", e)
            }
    }

    fun getCustomerListWithAllOrders(fragment: AllOrdersFragment, b: Boolean) {
        val allCustomers = mutableListOf<Customer>()

        mFireStore.collection(Constants.ORDERS)
            .whereEqualTo("user_id", getCurrentUserID())
            .get()
            .addOnSuccessListener { documents ->
                // Group orders by customer ID
                val customerOrders = documents.groupBy { it.getString("customerId") }

                // Create a list of customers from the grouped orders
                for ((customerId, orders) in customerOrders) {
                    val customerName = orders.first().getString("customerName") ?: ""
                    val customerPhone = orders.first().getString("phone") ?: ""

                    val orderList = orders.map { order ->
                        val orderProducts =
                            order.get("productList") as? List<HashMap<String, Any>> ?: emptyList()
                        val orderPayments =
                            order.get("paymentList") as? List<HashMap<String, Any>> ?: emptyList()
                        // Map the list of HashMaps to a list of Product objects
                        val productList = orderProducts.map { product ->
                            Product(
                                user_id = product["user_id"] as? String ?: "",
                                product_id = product["productId"] as? String ?: "",
                                name = product["name"] as? String ?: "",
                                uom = product["uom"] as? String ?: "",
                                sell_price = product["sell_price"] as? String ?: "",
                                gst_rate = product["gst_rate"] as? String ?: "",
                                quantity = (product["quantity"] as? Long)?.toInt() ?: 0,
                                gstIncluded = (product["gstIncluded"] as? Boolean ?: false)
                            )
                        }

                        val paymentList = orderPayments.map { payment ->
                            Payment(
                                date = payment["date"] as? String ?: "",
                                remarks = payment["payment"] as? String ?: "",
                                amount = payment["amount"] as? Double ?: 0.0
                            )
                        }

                        Order(
                            user_id = order.getString("user_id") ?: "",
                            orderId = order.getString("orderId") ?: "",
                            customerId = order.getString("customerId") ?: "",
                            customerName = order.getString("customerName") ?: "",
                            phone = order.getString("phone") ?: "",
                            address = order.getString("address") ?: "",
                            productList = productList,
                            total = order.getDouble("total") ?: 0.0,
                            paid = order.getBoolean("paid") ?: false,
                            orderDate = order.getString("orderDate") ?: "",
                            delivered = order.getBoolean("delivered") ?: false,
                            deliveryDate = order.getString("deliveryDate") ?: "",
                            paymentList = paymentList as ArrayList<Payment>
                        )
                    }

                    allCustomers.add(
                        Customer(
                            customer_id = customerId ?: "",
                            name = customerName,
                            phone = customerPhone,
                            orders = orderList
                        )
                    )
                }
                when (fragment) {
                    is AllOrdersFragment -> {
                        // Update your RecyclerView with the list of unpaid customers
                        fragment.updateAllCustomersList(allCustomers)
                    }
                }
            }
            .addOnFailureListener { e ->
                when (fragment) {
                    is AllOrdersFragment -> {
                        fragment.hideProgressDialog()
                    }
                }
                Log.w(TAG, "Error getting paid orders", e)
            }
    }

    fun getCustomerListWithUnpaidOrdersActivity(activity: PaymentActivity) {
        val unpaidCustomers = mutableListOf<Customer>()

        mFireStore.collection(Constants.ORDERS)
            .whereEqualTo("user_id", getCurrentUserID())
            .get()
            .addOnSuccessListener { documents ->
                // Group orders by customer ID
                val customerOrders = documents.groupBy { it.getString("customerId") }

                // Create a list of customers from the grouped orders
                for ((customerId, orders) in customerOrders) {
                    val customerName = orders.first().getString("customerName") ?: ""
                    val customerPhone = orders.first().getString("phone") ?: ""

                    val unpaidOrders = orders.map { order ->
                        val orderProducts =
                            order.get("productList") as? List<HashMap<String, Any>> ?: emptyList()
                        val orderPayments =
                            order.get("paymentList") as? List<HashMap<String, Any>> ?: emptyList()
                        // Map the list of HashMaps to a list of Product objects
                        val productList = orderProducts.map { product ->
                            Product(
                                user_id = product["user_id"] as? String ?: "",
                                product_id = product["productId"] as? String ?: "",
                                name = product["name"] as? String ?: "",
                                uom = product["uom"] as? String ?: "",
                                sell_price = product["sell_price"] as? String ?: "",
                                gst_rate = product["gst_rate"] as? String ?: "",
                                quantity = (product["quantity"] as? Long)?.toInt() ?: 0,
                                gstIncluded = (product["gstIncluded"] as? Boolean ?: false)
                            )
                        }

                        val paymentList = orderPayments.map { payment ->
                            Payment(
                                date = payment["date"] as? String ?: "",
                                remarks = payment["remarks"] as? String ?: "",
                                amount = payment["amount"] as? Double ?: 0.0
                            )
                        }

                        Order(
                            user_id = order.getString("user_id") ?: "",
                            orderId = order.getString("orderId") ?: "",
                            customerId = order.getString("customerId") ?: "",
                            customerName = order.getString("customerName") ?: "",
                            phone = order.getString("phone") ?: "",
                            address = order.getString("address") ?: "",
                            productList = productList,
                            total = order.getDouble("total") ?: 0.0,
                            paid = order.getBoolean("paid") ?: false,
                            orderDate = order.getString("orderDate") ?: "",
                            delivered = order.getBoolean("delivered") ?: false,
                            deliveryDate = order.getString("deliveryDate") ?: "",
                            paymentList = paymentList as ArrayList<Payment>
                        )
                    }

                    unpaidCustomers.add(
                        Customer(
                            customer_id = customerId ?: "",
                            name = customerName,
                            phone = customerPhone,
                            orders = unpaidOrders
                        )
                    )
                }

                // Update your RecyclerView with the list of unpaid customers
                activity.successGetUnpaidCustomersList(unpaidCustomers)
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.w(TAG, "Error getting unpaid orders", e)
            }
    }

    fun updatePaymentListForCurrentOrder(
        activity: PaymentActionActivity,
        paymentList: MutableList<Payment>,
        orderId: String
    ) {
        // Create a map of the updated data
        val updatedData = mapOf("paymentList" to paymentList)
        mFireStore.collection(Constants.ORDERS)
            .whereEqualTo("orderId", orderId)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // No order found with the given orderId
                    activity.hideProgressDialog()
                    return@addOnSuccessListener
                }

                val documentSnapshot = documents.documents[0]
                documentSnapshot.reference.update(updatedData)
                    .addOnSuccessListener {
                        // Payment list updated successfully
                        activity.successUpdatePaymentList()
                        // Show a success message or perform any other actions
                    }
                    .addOnFailureListener { e ->
                        // Error occurred while updating the payment list
                        activity.hideProgressDialog()
                        Log.w(TAG, "Error updating payment list", e)
                    }
            }
            .addOnFailureListener { e ->
                // Error occurred while fetching the order
                activity.hideProgressDialog()
                Log.w(TAG, "Error fetching order", e)
            }
    }

    fun getTotalOrdersCount(activity: Activity) {
        mFireStore.collection(Constants.ORDERS)
            .whereEqualTo("user_id", getCurrentUserID())
            .get()
            .addOnSuccessListener {
                val totalOrderCount = it.size()
                when (activity) {
                    is DashboardActivity -> {
                        activity.successTotalOrderCount(totalOrderCount)
                    }
                }
            }
            .addOnFailureListener {
                println("Failed to fetch total order count: $it")
            }
    }

    fun getTotalUnpaidOrdersCount(activity: Activity) {
        mFireStore.collection(Constants.ORDERS)
            .whereEqualTo("user_id", getCurrentUserID())
            .whereEqualTo("paid", false)
            .get()
            .addOnSuccessListener {
                val totalUnpaidOrderCount = it.size()
                when (activity) {
                    is DashboardActivity -> {
                        activity.successTotalUnpaidOrderCount(totalUnpaidOrderCount)
                    }
                }
            }
            .addOnFailureListener {
                println("Failed to fetch total unpaid order count: $it")
            }
    }

    fun getTotalUndeliveredOrdersCount(activity: Activity) {
        mFireStore.collection(Constants.ORDERS)
            .whereEqualTo("user_id", getCurrentUserID())
            .whereEqualTo("delivered", false)
            .get()
            .addOnSuccessListener {
                val totalUndeliveredOrderCount = it.size()
                when (activity) {
                    is DashboardActivity -> {
                        activity.successTotalUndeliveredOrderCount(totalUndeliveredOrderCount)
                    }
                }
            }
            .addOnFailureListener {
                println("Failed to fetch total undeliverd order count: $it")
            }
    }

    fun uploadCompanyDetails(activity: AddCompanyActivity, company: Company) {
        mFireStore.collection(Constants.COMPANY)
            .document()
            .set(company, SetOptions.merge())
            .addOnSuccessListener {
                activity.companyUploadSuccessToCloud()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while uploading company details", e
                )
            }
    }

    fun getCompanyDetailsFromFireStore(activity: Activity) {
        mFireStore.collection(Constants.COMPANY)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e("Company", document.documents.toString())
                var company = Company()
                if (!document.isEmpty) {
                    company = document.documents[0].toObject(Company::class.java)!!
                }
                when (activity) {
                    is UserProfileActivity -> {
                        activity.successGetCompanyDetailsFromFireStore(company)
                    }
                    is ViewQuotationActivity -> {
                        activity.successGetCompanyDetailsFromFireStore(company)
                    }
                }

            }
            .addOnFailureListener { e ->
                when (activity) {
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting products", e
                )
            }
    }

    fun updateCompany(
        activity: Activity,
        updatedCompany: java.util.HashMap<String, String>
    ) {
        mFireStore.collection(Constants.COMPANY)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val documentSnapshot = documents.documents[0]
                    documentSnapshot.reference.update(updatedCompany as Map<String, Any>)
                        .addOnSuccessListener {
                            if (activity is AddCompanyActivity) {
                                activity.companyUpdateSuccess()
                            }
                        }
                        .addOnFailureListener { e ->
                            if (activity is AddCompanyActivity) {
                                activity.hideProgressDialog()
                            }
                            Log.e(activity.javaClass.simpleName, "Error updating company", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                when(activity) {
                    is AddCompanyActivity -> {
                        activity.hideProgressDialog()
                        Log.e(activity.javaClass.simpleName, "Error fetching company", e)
                    }
                }
            }
    }
}