package com.example.kotlinflowinviewsystem

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    val viewModel: MainViewModel by viewModels()
    private lateinit var liveDataText: TextView
    private lateinit var stateFlowText: TextView
    private lateinit var flowText: TextView
    private lateinit var sharedFlowText: TextView
    private lateinit var main: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        main = findViewById(R.id.main)

        // LiveData
        liveDataText = findViewById(R.id.liveDataText)
        findViewById<Button>(R.id.liveDataButton).setOnClickListener {
            viewModel.triggerLiveData()
        }
        // StateFlow
        stateFlowText = findViewById(R.id.stateFlowText)
        findViewById<Button>(R.id.stateFlowButton).setOnClickListener {
            viewModel.triggerStateFlow()
        }
        // Flow
        flowText = findViewById(R.id.flowText)
        findViewById<Button>(R.id.flowButton).setOnClickListener {
            lifecycleScope.launch {
                viewModel.triggerFlow().collectLatest {
                    flowText.text = it
                }
            }
        }
        // SharedFlow
        sharedFlowText = findViewById(R.id.sharedFlowText)
        findViewById<Button>(R.id.sharedFlowButton).setOnClickListener {
            viewModel.triggerSharedFlow()
        }
        subscribeToObservables()
    }

    private fun subscribeToObservables() {
        viewModel.liveData.observe(this) {
            liveDataText.text = it
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateFlow.collectLatest {
                    stateFlowText.text = it
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sharedFlow.collectLatest {
                    Snackbar.make(
                        main,
                        it,
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
