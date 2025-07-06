package com.developers.contentproviders

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.developers.contentproviders.adapter.VillainAdapter
import com.developers.contentproviders.data.Villains
import com.developers.contentproviders.databinding.ActivityMainBinding
import com.developers.contentproviders.viewmodel.VillainsViewModel
import com.developers.contentproviders.viewmodel.UiState
import com.google.android.material.snackbar.Snackbar
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

        setupToolbar()
        setupRecyclerView()
        setupFab()
        observeViewModel()
        insertSampleDataThroughContentProvider()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = getString(R.string.app_name)
        }
    }

    private fun setupRecyclerView() {
        adapter = VillainAdapter { villain ->
            // Handle item click
            Log.d(TAG, "Clicked on villain: ${villain.villainName}")
            showVillainDetails(villain)
        }
        
        binding.recyclerView.apply {
            this.adapter = this@MainActivity.adapter
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
        }
    }

    private fun setupFab() {
        binding.fab.setOnClickListener {
            // Add a new villain
            addRandomVillain()
        }
    }

    private fun observeViewModel() {
        villainsViewModel.uiState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    adapter.submitList(state.data)
                    Log.d(TAG, "Updated villains list with ${state.data.size} items")
                }
                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    showError(state.exception.message ?: getString(R.string.error_occurred))
                }
            }
        }
    }

    private fun showVillainDetails(villain: Villains) {
        Snackbar.make(
            binding.root,
            "${villain.villainName} from ${villain.villainSeries}",
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setAction(getString(R.string.retry)) {
                // Retry loading
                insertSampleDataThroughContentProvider()
            }.show()
    }

    private fun addRandomVillain() {
        val randomVillains = listOf(
            "Thanos" to "Marvel",
            "Darkseid" to "DC",
            "Magneto" to "X-Men",
            "Loki" to "Thor",
            "Green Goblin" to "Spider-Man"
        )
        
        val randomVillain = randomVillains.random()
        
        lifecycleScope.launch {
            try {
                val values = ContentValues().apply {
                    put(Villains.VILLAIN_NAME, randomVillain.first)
                    put(Villains.VILLAIN_SERIES, randomVillain.second)
                }
                
                val uri = contentResolver.insert(VillainProvider.VILLAINS_URI, values)
                Log.d(TAG, "Inserted villain through ContentProvider: $uri")
                
                Snackbar.make(
                    binding.root,
                    getString(R.string.villain_added),
                    Snackbar.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Log.e(TAG, "Error inserting data through ContentProvider", e)
                showError(getString(R.string.error_occurred))
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
                Log.d(TAG, "Inserted sample villain through ContentProvider: $uri")
            } catch (e: Exception) {
                Log.e(TAG, "Error inserting sample data through ContentProvider", e)
            }
        }
    }
}
