package com.example.firebaseauthui.order

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebaseauthui.Constants
import com.example.firebaseauthui.R
import com.example.firebaseauthui.chat.ChatFragment
import com.example.firebaseauthui.databinding.ActivityOrderBinding
import com.example.firebaseauthui.entities.Order
import com.example.firebaseauthui.fcm.NotificationRS
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class OrderActivity : AppCompatActivity(), OnOrderListener, OrderAux {
    private lateinit var binding : ActivityOrderBinding
    private lateinit var adapter : OrderAdapter
    private lateinit var orderSelected : Order
    private val aValues : Array<String> by lazy {
        resources.getStringArray(R.array.status_value)
    }
    private val aKeys : Array<Int> by lazy {
        resources.getIntArray(R.array.status_key).toTypedArray()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRecyclerVIew()
        setupFirestore()
    }
    private fun notifyClient(order : Order) {
        val db = FirebaseFirestore.getInstance()
        db.collection(Constants.COLL_USERS)
            .document(order.clientId)
            .collection(Constants.COLL_TOKENS)
            .get()
            .addOnSuccessListener {
                var tokensStr = ""
                for (document in it) {
                    val tokenMap = document.data
                    tokensStr += "${tokenMap.getValue(Constants.PROP_TOKEN)},"
                }
                if (tokensStr.length > 0) {
                    tokensStr = tokensStr.dropLast(1)

                    var names = ""
                    order.products.forEach {
                        names += "${it.value.name}, "
                    }
                    names = names.dropLast(2)
                    val index = aKeys.indexOf(order.status)
                    val notificationRS = NotificationRS()
                    notificationRS
                        .sendNotification(
                            "Estado de tu pedido: ${aValues[index]}",
                            names, tokensStr
                        )
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, getString(R.string.toast_status_error),
                    Toast.LENGTH_SHORT).show()
            }
    }

    override fun onStatusChange(order: Order) {
        val db = FirebaseFirestore.getInstance()
        db.collection(Constants.COLL_REQUESTS)
            .document(order.id)
            .update(Constants.PROP_STATUS, order.status)
            .addOnSuccessListener {
                Toast.makeText(this, getString(R.string.toast_status_changed),
                    Toast.LENGTH_SHORT).show()
                notifyClient(order)
            }
            .addOnFailureListener {
                Toast.makeText(this, getString(R.string.toast_status_error),
                    Toast.LENGTH_SHORT).show()
            }
    }

    override fun onStartChat(order: Order) {
        orderSelected = order
        val fragment = ChatFragment()
        supportFragmentManager
            .beginTransaction()
            .add(R.id.containerMain, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun getOrderSelected(): Order = orderSelected
    private fun setupFirestore(){
        val db = FirebaseFirestore.getInstance()
        db.collection(Constants.COLL_REQUESTS)
            .orderBy(Constants.PROP_DATE, Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {
                for(document in it){
                    val order = document.toObject(Order::class.java)
                    order.id = document.id
                    adapter.add(order)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this,
                    "Ha fallado conexión con Firestore",
                    Toast.LENGTH_SHORT).show()
            }
    }
    private fun setupRecyclerVIew() {
        adapter = OrderAdapter(mutableListOf(), this)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@OrderActivity)
            adapter = this@OrderActivity.adapter
        }
    }
}