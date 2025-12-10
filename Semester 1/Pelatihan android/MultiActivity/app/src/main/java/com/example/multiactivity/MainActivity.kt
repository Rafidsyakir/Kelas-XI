package com.example.multiactivity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.multiactivity.ui.theme.MultiActivityTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen(
                onNavigateToSecond = {
                    //  Pindah ke SecondActivity
                    val intent = Intent(this, SecondActivity::class.java)
                    startActivity(intent)
                }
            )
        }
    }
}

@Composable
fun MainScreen(onNavigateToSecond: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Ini Activity Pertama", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = { onNavigateToSecond() }) {
                Text(text = "Pindah ke Activity Kedua")
            }
        }
    }
}

@Composable
fun MainScreenAlternate(onNavigate: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Ini Halaman Utama", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = onNavigate) {
            Text("Ke Halaman Kedua")
        }
    }
}
