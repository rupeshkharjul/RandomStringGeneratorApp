package com.rupesh.randomstringgeneratorapp

import android.content.ContentResolver
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<RandomStringViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Scaffold(topBar = {
                TopAppBar(
                    colors = topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = {
                        Text("RandomStringGeneratorApp")
                    }
                )
            }) { innerPadding ->
                Column(modifier = Modifier.padding(innerPadding)) {
                    RandomStringApp(contentResolver, viewModel)
                }
            }
        }
    }
}

@Composable
fun RandomStringApp(contentResolver: ContentResolver, viewModel: RandomStringViewModel) {
    var length by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = length,
            onValueChange = { length = it },
            label = { Text("Enter string length") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Button(onClick = {
                try {
                    val len = length.toInt()
                    viewModel.generateString(contentResolver, len)
                    errorMessage = null
                } catch (e: Exception) {
                    errorMessage = "Please enter valid length"
                }
            }) {
                Text("Generate")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                viewModel.clearAll()
            }) {
                Text("Delete All")
            }
        }
        errorMessage?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(viewModel.stringDataState, key = { it.id }) { data ->
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Value: ${data.value}")
                        Text("Length: ${data.length}")
                        Text("Created: ${data.created}")
                        Button(onClick = { viewModel.delete(data) }) {
                            Text("Delete")
                        }
                    }
                }
            }
        }
    }
}