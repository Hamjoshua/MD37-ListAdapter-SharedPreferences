package com.example.listadapter_sharedpreferences

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import java.net.HttpURLConnection
import java.net.URL

private var contacts : ArrayList<Contact> = ArrayList<Contact>()
private var editableContacts : ArrayList<Contact> = ArrayList<Contact>()

class MainActivity : AppCompatActivity() {
    lateinit var RView : RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Создаем акшн бар
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar()?.setDisplayShowTitleEnabled(false);
        val txtFromToolbar: EditText = findViewById<EditText>(R.id.et_search);

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
            editableContacts = contacts.clone() as ArrayList<Contact>

            withContext(Dispatchers.Main) {
                if(RView.context != null){
                    RView.adapter = CardAdapter(editableContacts);
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_toolbar, menu)
        return true
    }

    fun updateRView(line: String){
        if (line != ""){
            editableContacts = contacts.filter { it.name.contains(line) ||
                    it.type.contains(line) ||
                    it.phone.contains(line) } as ArrayList<Contact>;
        } else{
            editableContacts = contacts.clone() as ArrayList<Contact>;
        }
        RView.adapter = CardAdapter(editableContacts);
    }
}



data class Contact(
    val phone: String,
    val name: String,
    val type: String
)