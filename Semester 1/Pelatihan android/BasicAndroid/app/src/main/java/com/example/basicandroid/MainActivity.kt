package com.example.basicandroid

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.basicandroid.ui.theme.BasicAndroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BasicAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier) {
    var nama by remember { mutableStateOf("") }
    var alamat by remember { mutableStateOf("") }
    var pesan by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Image at the top
        Image(
            painter = painterResource(id = R.drawable.google_logo),
            contentDescription = "Google Logo",
            modifier = Modifier.size(80.dp)
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Text label "Nama"
        Text(
            text = "Nama",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Text field for nama
        OutlinedTextField(
            value = nama,
            onValueChange = { nama = it },
            placeholder = { Text("Masukkan nama") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFE8E8E8),
                focusedContainerColor = Color(0xFFE8E8E8),
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent
            ),
            shape = RoundedCornerShape(4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Text label "Alamat"
        Text(
            text = "Alamat",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Text field for alamat
        OutlinedTextField(
            value = alamat,
            onValueChange = { alamat = it },
            placeholder = { Text("Masukkan alamat") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFE8E8E8),
                focusedContainerColor = Color(0xFFE8E8E8),
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent
            ),
            shape = RoundedCornerShape(4.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Button "Simpan"
        Button(
            onClick = { 
                if (nama.isNotEmpty() && alamat.isNotEmpty()) {
                    pesan = "Data tersimpan!\nNama: $nama\nAlamat: $alamat"
                } else {
                    pesan = "Mohon isi nama dan alamat terlebih dahulu"
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.7f),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6366F1)
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                "Simpan",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Display message when button is clicked
        if (pesan.isNotEmpty()) {
            Text(
                text = pesan,
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth(),
                color = if (pesan.contains("tersimpan")) Color(0xFF4CAF50) else Color(0xFFE53E3E)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BasicAndroidTheme {
        Greeting()
    }
}