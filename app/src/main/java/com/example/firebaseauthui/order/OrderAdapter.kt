package com.example.firebaseauthui.order

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.firebaseauthui.R
import com.example.firebaseauthui.databinding.ItemOrderBinding
import com.example.firebaseauthui.entities.Order

class OrderAdapter (private val orderList : MutableList<Order>,
                    private val listener : OnOrderListener) :
    RecyclerView.Adapter<OrderAdapter.ViewHolder>(){
    private lateinit var context : Context
    private val aValues : Array<String> by lazy {
        context.resources.getStringArray(R.array.status_value)
    }
    private val aKeys : Array<Int> by lazy {
        context.resources.getIntArray(R.array.status_key).toTypedArray()
    }
    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val binding = ItemOrderBinding.bind(view)
        fun setListener(order : Order) {
            binding.acStatus.setOnItemClickListener { adapterView, view, position, id ->
                order.status = aKeys[position]
                listener.onStatusChange(order)
            }
            binding.chipChat.setOnClickListener {
                listener.onStartChat(order)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = orderList.size
    fun add (order : Order) {
        orderList.add(order)
        notifyItemInserted(orderList.size-1)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orderList[position]
        holder.setListener(order)
        holder.binding.tvID.text = context.getString(R.string.order_id, order.id)
        var names =  ""
        order.products.forEach {
            names += "${it.value.name}, "
        }
        holder.binding.tvProductNames.text = names.dropLast(2)
        holder.binding.tvTotalPrice.text = context.getString(R.string.product_full_cart,
            order.totalPrice)
        val index = aKeys.indexOf(order.status)
        val statusAdapter = ArrayAdapter(context,
            android.R.layout.simple_dropdown_item_1line, aValues)
        holder.binding.acStatus.setAdapter(statusAdapter)
        if(index != -1){
            holder.binding.acStatus.setText(aValues[index], false)
        } else {
            holder.binding.acStatus
                .setText(context.getString(R.string.order_status_error), false)
        }



    }
}