package com.example.listadapter_sharedpreferences

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlin.reflect.KFunction1


class CardViewHolder(view: View) : RecyclerView.ViewHolder(view){
    val nameView : TextView = view.findViewById<TextView>(R.id.r_name);
    val phoneView : TextView = view.findViewById<TextView>(R.id.r_phone);
    val typeView : TextView = view.findViewById<TextView>(R.id.r_type);
}

class CardAdapter(val phoneFunction : (input: String) -> Unit) :
    androidx.recyclerview.widget.ListAdapter<Contact, CardViewHolder>(ContactDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.r_item,
            parent, false);
        return CardViewHolder(view);
    }

    override fun getItemCount() = currentList.size

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val contact = currentList[position]
        holder.phoneView.setOnClickListener{
            phoneFunction(contact.phone)
        }
        holder.nameView.setText(contact.name)
        holder.phoneView.setText(contact.phone)
        holder.typeView.setText(contact.type)
    }
}

class ContactDiffCallback : DiffUtil.ItemCallback<Contact>() {
    override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
        return oldItem.phone == newItem.phone;
    }

    override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
        return oldItem == newItem;
    }
}