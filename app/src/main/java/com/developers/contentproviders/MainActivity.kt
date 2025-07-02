package com.developers.contentproviders

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.developers.contentproviders.adapter.VillainAdapter
import com.developers.contentproviders.data.Villains
import com.developers.contentproviders.databinding.ActivityMainBinding
import com.developers.contentproviders.viewmodel.VillainsViewModel
import kotlinx.coroutines.launch

/**
 * Main Activity demonstrating Content Provider usage with modern Android architecture
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: VillainAdapter
    private val villainsViewModel: VillainsViewModel by viewModels()

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeViewModel()
        insertSampleDataThroughContentProvider()
    }

    private fun setupRecyclerView() {
        adapter = VillainAdapter { villain ->
            // Handle item click
            Log.d(TAG, "Clicked on villain: ${villain.villainName}")
        }
        
        binding.recyclerView.apply {
            this.adapter = this@MainActivity.adapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun observeViewModel() {
        villainsViewModel.allVillains.observe(this) { villains ->
            // Update the cached copy of the villains in the adapter.
            villains?.let { 
                adapter.submitList(it)
                Log.d(TAG, "Updated villains list with ${it.size} items")
            }
        }
    }

    /**
     * Demonstrates inserting data through Content Provider
     */
    private fun insertSampleDataThroughContentProvider() {
        lifecycleScope.launch {
            try {
                val values = ContentValues().apply {
                    put(Villains.VILLAIN_NAME, "Gustavo Fring")
                    put(Villains.VILLAIN_SERIES, "Breaking Bad")
                }
                
                val uri = contentResolver.insert(VillainProvider.VILLAINS_URI, values)
                Log.d(TAG, "Inserted villain through ContentProvider: $uri")
            } catch (e: Exception) {
                Log.e(TAG, "Error inserting data through ContentProvider", e)
            }
        }
    }
}
