package com.example.listadapter_sharedpreferences

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.recyclerview.widget.LinearLayoutManager
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


private var contacts: ArrayList<Contact> = ArrayList<Contact>()
private var editableContacts: ArrayList<Contact> = ArrayList<Contact>()

class MainActivity : AppCompatActivity() {
    lateinit var RView: RecyclerView
    private var adapter = CardAdapter(::goToDial)
    val SEARCH_FILTER = stringPreferencesKey("search_filter")
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Timber.plant(Timber.DebugTree())

        RView = findViewById<RecyclerView>(R.id.r_view);
        RView.layoutManager = LinearLayoutManager(this)

        CoroutineScope(Dispatchers.IO).launch {
            var text: String = "";
            val url: URL = URL(
                "https://drive.google.com/u/0/uc?id=1-KO" +
                        "-9GA3NzSgIc1dkAsNm8Dqw0fuPxcR&export=download"
            )
            val urlConn: HttpURLConnection = url.openConnection() as HttpURLConnection
            text = urlConn.inputStream.bufferedReader().readText()

            val gson = Gson();
            val itemType = object : TypeToken<ArrayList<Contact>>() {}.type
            contacts = gson.fromJson<ArrayList<Contact>>(text, itemType);

            withContext(Dispatchers.Main) {

                adapter.submitList(contacts.toMutableList());
                RView.adapter = adapter;

                setSupportActionBar(findViewById(R.id.toolbar));
                getSupportActionBar()?.setDisplayShowTitleEnabled(true);
            }
        }
    }

    suspend fun setValueToDataStore(text: String) {
        dataStore.edit { settings ->
            settings[SEARCH_FILTER] = text
        }
    }

    suspend fun getValueFromDataStore(): String? {
        var result: String? = ""
        dataStore.edit { settings ->
            result = settings[SEARCH_FILTER]
        }

        return result;
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_toolbar, menu)
        val menuItem = menu.findItem(R.id.et_search)
        val search: EditText = menuItem.actionView as EditText
        search.width = 300

        CoroutineScope(Dispatchers.IO).launch {
            val text: String = getValueFromDataStore() ?: ""
            search.setText(text);
        }

        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (RView.adapter != null) {
                    updateRView(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {
                CoroutineScope(Dispatchers.IO).launch {
                    setValueToDataStore(s.toString());
                }
            }

        })

        return true
    }

    fun goToDial(phone: String){
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:<${phone}>")
        ContextCompat.startActivity(this, intent, null)
    }

    fun updateRView(line: String) {
        if (line != "") {
            editableContacts = contacts.filter {
                it.name.contains(line) ||
                        it.type.contains(line) ||
                        it.phone.contains(line)
            } as ArrayList<Contact>
        } else {
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

