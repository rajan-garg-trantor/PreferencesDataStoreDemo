package com.rajangarg.preferencesdatastoredemo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.rajangarg.preferencesdatastoredemo.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private val preferencesDataStore: DataStore<Preferences> by preferencesDataStore(name = "user")
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btSave.setOnClickListener {
            val key = binding.etWriteKey.text
            val value = binding.etValue.text

            if (key.isNotBlank() && value.isNotBlank())
                GlobalScope.launch(Dispatchers.IO) {
                    saveToPreferencesStore(key.toString().trim(), value.toString())
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "Value Saved", Toast.LENGTH_LONG).show()
                    }
                }
        }

        binding.btRead.setOnClickListener {
            val key = binding.etReadKey.text

            if (key.isNotBlank())
                GlobalScope.launch(Dispatchers.IO) {
                    getFromPreferencesStore(key.toString().trim()).collect {
                        withContext(Dispatchers.Main) {
                            it?.let {
                                Toast.makeText(this@MainActivity, it, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
        }
    }

    private fun getFromPreferencesStore(stringKey: String) =
        preferencesDataStore.data.map { it[stringPreferencesKey(stringKey)] }

    private suspend fun saveToPreferencesStore(
        stringKey: String,
        stringValue: String
    ) {
        preferencesDataStore.edit { it[stringPreferencesKey(stringKey)] = stringValue }
    }
}