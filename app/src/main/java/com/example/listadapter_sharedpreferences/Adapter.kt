package com.example.listadapter_sharedpreferences

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView


class CardViewHolder(view: View) : RecyclerView.ViewHolder(view){
    val nameView : TextView = view.findViewById<TextView>(R.id.r_name);
    val phoneView : TextView = view.findViewById<TextView>(R.id.r_phone);
    val typeView : TextView = view.findViewById<TextView>(R.id.r_type);
}

class CardAdapter() :
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
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:<${contact.phone}>")
            startActivity(holder.phoneView.context, intent, null)
        }
        holder.nameView.setText(contact.name)
        holder.phoneView.setText(contact.phone)
        holder.typeView.setText(contact.type)
    }
}