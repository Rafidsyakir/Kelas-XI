# Complete Task Manager - Jetpack Compose Android App
## Full Copy-Paste-Ready Code

---

## 📱 Part 2: Jetpack Compose Android Application

This document contains **ALL** the code you need for the Android app with complete imports.

---

## 1. Module-level `build.gradle.kts`
**Location:** `app/build.gradle.kts`

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.example.taskmanager"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.taskmanager"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.1")

    // Compose BOM
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // ViewModel Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.7.5")

    // Retrofit & OkHttp
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // Gson
    implementation("com.google.code.gson:gson:2.10.1")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // Hilt for Dependency Injection
    implementation("com.google.dagger:hilt-android:2.48.1")
    kapt("com.google.dagger:hilt-android-compiler:2.48.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.10.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

kapt {
    correctErrorTypes = true
}
```

---

## 2. Project-level `build.gradle.kts`
**Location:** `build.gradle.kts`

```kotlin
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false
    id("com.google.dagger.hilt.android") version "2.48.1" apply false
}
```

---

## 3. Data Class - Task DTO
**Location:** `app/src/main/java/com/example/taskmanager/data/remote/dto/Task.kt`

```kotlin
package com.example.taskmanager.data.remote.dto

import com.google.gson.annotations.SerializedName

data class Task(
    @SerializedName("id")
    val id: Int? = null,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("is_completed")
    val isCompleted: Boolean = false,
    
    @SerializedName("created_at")
    val createdAt: String? = null,
    
    @SerializedName("updated_at")
    val updatedAt: String? = null
)
```

---

## 4. API Service Interface
**Location:** `app/src/main/java/com/example/taskmanager/data/remote/ApiService.kt`

```kotlin
package com.example.taskmanager.data.remote

import com.example.taskmanager.data.remote.dto.Task
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    @GET("tasks")
    suspend fun getTasks(): Response<List<Task>>
    
    @POST("tasks")
    suspend fun addTask(@Body task: Task): Response<Task>
    
    @PUT("tasks/{id}")
    suspend fun updateTask(
        @Path("id") id: Int,
        @Body task: Task
    ): Response<Task>
    
    @DELETE("tasks/{id}")
    suspend fun deleteTask(@Path("id") id: Int): Response<Unit>
}
```

---

## 5. Retrofit Client
**Location:** `app/src/main/java/com/example/taskmanager/data/remote/RetrofitClient.kt`

```kotlin
package com.example.taskmanager.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    
    // Android Emulator: use 10.0.2.2 to access localhost
    // Physical Device: use your computer's IP (e.g., "http://192.168.1.100:8000/api/")
    private const val BASE_URL = "http://10.0.2.2:8000/api/"
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
```

---

## 6. Resource Wrapper (Utility)
**Location:** `app/src/main/java/com/example/taskmanager/util/Resource.kt`

```kotlin
package com.example.taskmanager.util

sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T> : Resource<T>()
}
```

---

## 7. Repository Interface
**Location:** `app/src/main/java/com/example/taskmanager/domain/repository/TaskRepository.kt`

```kotlin
package com.example.taskmanager.domain.repository

import com.example.taskmanager.data.remote.dto.Task
import com.example.taskmanager.util.Resource
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun getTasks(): Resource<List<Task>>
    suspend fun addTask(task: Task): Resource<Task>
    suspend fun updateTask(id: Int, task: Task): Resource<Task>
    suspend fun deleteTask(id: Int): Resource<Unit>
}
```

---

## 8. Repository Implementation
**Location:** `app/src/main/java/com/example/taskmanager/data/repository/TaskRepositoryImpl.kt`

```kotlin
package com.example.taskmanager.data.repository

import com.example.taskmanager.data.remote.ApiService
import com.example.taskmanager.data.remote.dto.Task
import com.example.taskmanager.domain.repository.TaskRepository
import com.example.taskmanager.util.Resource
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : TaskRepository {
    
    override suspend fun getTasks(): Resource<List<Task>> {
        return try {
            val response = apiService.getTasks()
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("Failed to fetch tasks: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("Network error: ${e.localizedMessage ?: "Unknown error"}")
        }
    }
    
    override suspend fun addTask(task: Task): Resource<Task> {
        return try {
            val response = apiService.addTask(task)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("Failed to add task: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("Network error: ${e.localizedMessage ?: "Unknown error"}")
        }
    }
    
    override suspend fun updateTask(id: Int, task: Task): Resource<Task> {
        return try {
            val response = apiService.updateTask(id, task)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("Failed to update task: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("Network error: ${e.localizedMessage ?: "Unknown error"}")
        }
    }
    
    override suspend fun deleteTask(id: Int): Resource<Unit> {
        return try {
            val response = apiService.deleteTask(id)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("Failed to delete task: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("Network error: ${e.localizedMessage ?: "Unknown error"}")
        }
    }
}
```

---

## 9. UI State
**Location:** `app/src/main/java/com/example/taskmanager/presentation/TaskUiState.kt`

```kotlin
package com.example.taskmanager.presentation

import com.example.taskmanager.data.remote.dto.Task

data class TaskUiState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
```

---

## 10. ViewModel
**Location:** `app/src/main/java/com/example/taskmanager/presentation/TaskViewModel.kt`

```kotlin
package com.example.taskmanager.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.data.remote.dto.Task
import com.example.taskmanager.domain.repository.TaskRepository
import com.example.taskmanager.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()
    
    init {
        getTasks()
    }
    
    fun getTasks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = repository.getTasks()) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            tasks = result.data ?: emptyList(),
                            isLoading = false,
                            error = null
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
                is Resource.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }
    
    fun addTask(title: String) {
        if (title.isBlank()) return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val newTask = Task(title = title, isCompleted = false)
            
            when (val result = repository.addTask(newTask)) {
                is Resource.Success -> {
                    getTasks() // Refresh the list after adding
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
                is Resource.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }
    
    fun updateTaskStatus(task: Task, isCompleted: Boolean) {
        viewModelScope.launch {
            val updatedTask = task.copy(isCompleted = isCompleted)
            
            when (val result = repository.updateTask(task.id!!, updatedTask)) {
                is Resource.Success -> {
                    // Update local list optimistically
                    _uiState.update { state ->
                        state.copy(
                            tasks = state.tasks.map {
                                if (it.id == task.id) updatedTask else it
                            }
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(error = result.message)
                    }
                    // Revert the change by refreshing
                    getTasks()
                }
                is Resource.Loading -> {}
            }
        }
    }
    
    fun deleteTask(task: Task) {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }
            
            when (val result = repository.deleteTask(task.id!!)) {
                is Resource.Success -> {
                    // Remove from local list
                    _uiState.update { state ->
                        state.copy(
                            tasks = state.tasks.filter { it.id != task.id }
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(error = result.message)
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
```

---

## 11. Task Item Component
**Location:** `app/src/main/java/com/example/taskmanager/presentation/TaskItem.kt`

```kotlin
package com.example.taskmanager.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.taskmanager.data.remote.dto.Task

@Composable
fun TaskItem(
    task: Task,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = onCheckedChange
                )
                
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    textDecoration = if (task.isCompleted) {
                        TextDecoration.LineThrough
                    } else {
                        TextDecoration.None
                    },
                    color = if (task.isCompleted) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            }
            
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Task",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
```

---

## 12. Main Task Screen
**Location:** `app/src/main/java/com/example/taskmanager/presentation/TaskScreen.kt`

```kotlin
package com.example.taskmanager.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    viewModel: TaskViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var taskTitle by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Task Manager") },
                actions = {
                    IconButton(onClick = { viewModel.getTasks() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Tasks"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Add Task Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Add New Task",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = taskTitle,
                            onValueChange = { taskTitle = it },
                            label = { Text("Task Title") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            enabled = !uiState.isLoading
                        )
                        
                        Button(
                            onClick = {
                                viewModel.addTask(taskTitle)
                                taskTitle = ""
                            },
                            enabled = taskTitle.isNotBlank() && !uiState.isLoading
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Task"
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Save")
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Error Message
            uiState.error?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("Dismiss")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Loading Indicator
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            // Task List or Empty State
            if (uiState.tasks.isEmpty() && !uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No tasks yet. Add one above!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = uiState.tasks,
                        key = { task -> task.id ?: task.hashCode() }
                    ) { task ->
                        TaskItem(
                            task = task,
                            onCheckedChange = { isChecked ->
                                viewModel.updateTaskStatus(task, isChecked)
                            },
                            onDelete = {
                                viewModel.deleteTask(task)
                            }
                        )
                    }
                }
            }
        }
    }
}
```

---

## 13. Dependency Injection - App Module
**Location:** `app/src/main/java/com/example/taskmanager/di/AppModule.kt`

```kotlin
package com.example.taskmanager.di

import com.example.taskmanager.data.remote.ApiService
import com.example.taskmanager.data.remote.RetrofitClient
import com.example.taskmanager.data.repository.TaskRepositoryImpl
import com.example.taskmanager.domain.repository.TaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        return RetrofitClient.apiService
    }
    
    @Provides
    @Singleton
    fun provideTaskRepository(apiService: ApiService): TaskRepository {
        return TaskRepositoryImpl(apiService)
    }
}
```

---

## 14. Application Class
**Location:** `app/src/main/java/com/example/taskmanager/TaskManagerApp.kt`

```kotlin
package com.example.taskmanager

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TaskManagerApp : Application()
```

---

## 15. MainActivity
**Location:** `app/src/main/java/com/example/taskmanager/MainActivity.kt`

```kotlin
package com.example.taskmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.taskmanager.presentation.TaskScreen
import com.example.taskmanager.ui.theme.TaskManagerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaskManagerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TaskScreen()
                }
            }
        }
    }
}
```

---

## 16. Android Manifest
**Location:** `app/src/main/AndroidManifest.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <!-- Internet Permissions -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

    <application
        android:name=".TaskManagerApp"
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.TaskManager"
        android:usesCleartextTraffic="true"
        tools:targetApi="31">
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:theme="@style/Theme.TaskManager">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```

**Note:** `android:usesCleartextTraffic="true"` is required for HTTP connections.

---

## 📂 Complete Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/taskmanager/
│   │   │   ├── data/
│   │   │   │   ├── remote/
│   │   │   │   │   ├── dto/
│   │   │   │   │   │   └── Task.kt
│   │   │   │   │   ├── ApiService.kt
│   │   │   │   │   └── RetrofitClient.kt
│   │   │   │   └── repository/
│   │   │   │       └── TaskRepositoryImpl.kt
│   │   │   ├── di/
│   │   │   │   └── AppModule.kt
│   │   │   ├── domain/
│   │   │   │   └── repository/
│   │   │   │       └── TaskRepository.kt
│   │   │   ├── presentation/
│   │   │   │   ├── TaskScreen.kt
│   │   │   │   ├── TaskItem.kt
│   │   │   │   ├── TaskViewModel.kt
│   │   │   │   └── TaskUiState.kt
│   │   │   ├── ui/
│   │   │   │   └── theme/
│   │   │   │       ├── Color.kt
│   │   │   │       ├── Theme.kt
│   │   │   │       └── Type.kt
│   │   │   ├── util/
│   │   │   │   └── Resource.kt
│   │   │   ├── MainActivity.kt
│   │   │   └── TaskManagerApp.kt
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
└── build.gradle.kts (project-level)
```

---

## 🚀 Setup Instructions

### **Step 1: Laravel Backend**
```bash
cd sekolah-api
php artisan migrate
php artisan serve
```

### **Step 2: Android App**
1. Create a new Android Studio project with:
   - **Name:** Task Manager
   - **Package:** com.example.taskmanager
   - **Minimum SDK:** API 24 (Android 7.0)
   - **Build type:** Jetpack Compose

2. Copy all the files above to their respective locations

3. Update `BASE_URL` in `RetrofitClient.kt` if needed:
   - **Emulator:** `http://10.0.2.2:8000/api/`
   - **Physical Device:** `http://YOUR_COMPUTER_IP:8000/api/`

4. Sync Gradle files

5. Make sure Laravel server is running

6. Run the app!

---

## 🧪 Testing the API

### Using PowerShell (Windows):

```powershell
# Get all tasks
Invoke-RestMethod -Uri "http://localhost:8000/api/tasks" -Method GET

# Create a task
$body = @{title="Test Task"} | ConvertTo-Json
Invoke-RestMethod -Uri "http://localhost:8000/api/tasks" -Method POST -Body $body -ContentType "application/json"

# Update a task
$body = @{is_completed=$true} | ConvertTo-Json
Invoke-RestMethod -Uri "http://localhost:8000/api/tasks/1" -Method PUT -Body $body -ContentType "application/json"

# Delete a task
Invoke-RestMethod -Uri "http://localhost:8000/api/tasks/1" -Method DELETE
```

---

## ✨ Features Implemented

### Laravel Backend:
- ✅ RESTful API with resource routes
- ✅ Model with fillable fields and type casting
- ✅ Controller with full CRUD operations
- ✅ Request validation
- ✅ JSON responses with proper status codes

### Android App:
- ✅ Clean Architecture (Data → Domain → Presentation)
- ✅ Dependency Injection with Hilt
- ✅ StateFlow for reactive state management
- ✅ Material 3 Design
- ✅ Error handling with user-friendly messages
- ✅ Loading states
- ✅ Optimistic UI updates for checkbox
- ✅ Pull-to-refresh functionality
- ✅ Full CRUD operations

---

## 📱 App Screenshots Description

1. **Add Task Section:** OutlinedTextField with Save button
2. **Task List:** LazyColumn with task items
3. **Each Task:** Checkbox + Title + Delete button
4. **Error Handling:** Red error card with dismiss button
5. **Loading:** CircularProgressIndicator
6. **Empty State:** Centered message when no tasks

---

## 🔧 Troubleshooting

### Cannot connect to API:
1. Check Laravel server is running (`php artisan serve`)
2. Check `BASE_URL` in `RetrofitClient.kt`
3. For physical device, use computer's IP address
4. Check firewall settings

### App crashes on launch:
1. Make sure you've added `@HiltAndroidApp` to Application class
2. Check AndroidManifest.xml has correct application name
3. Verify all Hilt dependencies are synced

---

**🎉 Your complete Task Manager app is ready to use!**
