package com.example.listadapter_sharedpreferences

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.net.HttpURLConnection
import java.net.URL


private var contacts : ArrayList<Contact> = ArrayList<Contact>()
private var editableContacts : ArrayList<Contact> = ArrayList<Contact>()

class MainActivity : AppCompatActivity() {
    lateinit var RView : RecyclerView
    lateinit var adapter : ListAdapter<Contact, CardViewHolder>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Привязываем акшн бар
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar()?.setDisplayShowTitleEnabled(true);

        Timber.plant(Timber.DebugTree())

        RView = findViewById<RecyclerView>(R.id.r_view);
        RView.layoutManager = LinearLayoutManager(this)

        CoroutineScope(Dispatchers.IO).launch {
            var text: String = "";
            val url: URL = URL("https://drive.google.com/u/0/uc?id=1-KO" +
                    "-9GA3NzSgIc1dkAsNm8Dqw0fuPxcR&export=download")
            val urlConn: HttpURLConnection = url.openConnection() as HttpURLConnection
            text = urlConn.inputStream.bufferedReader().readText()

            val gson = Gson();
            val itemType = object : TypeToken<ArrayList<Contact>>() {}.type
            contacts = gson.fromJson<ArrayList<Contact>>(text, itemType);

            withContext(Dispatchers.Main) {
                if(RView.context != null){
                    adapter = CardAdapter(editableContacts);
                    adapter.submitList(contacts.toMutableList());
                    RView.adapter = adapter;
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_toolbar, menu)
        val menuItem = menu.findItem(R.id.et_search)
        val search: SearchView = menuItem.actionView as SearchView

        search.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                updateRView(newText)
                return true
            }
        })

        return true
    }

    fun updateRView(line: String){
        if (line != ""){
            editableContacts = contacts.filter { it.name.contains(line) ||
                    it.type.contains(line) ||
                    it.phone.contains(line) } as ArrayList<Contact>
        } else{
            editableContacts = contacts.clone() as ArrayList<Contact>
        }
        adapter.submitList(editableContacts.toMutableList())
    }
}



data class Contact(
    val phone: String,
    val name: String,
    val type: String
)
class ContactDiffCallback : DiffUtil.ItemCallback<Contact>() {
    override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
        return oldItem.phone == newItem.phone;
    }

    override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
        return oldItem == newItem;
    }
}