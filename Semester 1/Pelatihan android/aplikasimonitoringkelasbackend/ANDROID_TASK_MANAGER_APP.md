# Jetpack Compose Task Manager App

## Part 2: Android Application

This document contains all the necessary code for the Android Jetpack Compose app that performs CRUD operations on the Laravel Task API.

---

## 1. Dependencies (build.gradle.kts - Module level)

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android") // Optional: for Hilt DI
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
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
    // Core Android & Compose
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.1")
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // ViewModel & LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.5")

    // Retrofit & Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Gson for JSON parsing
    implementation("com.google.code.gson:gson:2.10.1")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Hilt for Dependency Injection (Optional but recommended)
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-android-compiler:2.48")
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
```

---

## 2. Data Layer

### 2.1 Task Data Class (`data/remote/dto/Task.kt`)

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

### 2.2 API Service Interface (`data/remote/ApiService.kt`)

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

### 2.3 Retrofit Client (`data/remote/RetrofitClient.kt`)

```kotlin
package com.example.taskmanager.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    
    // Use 10.0.2.2 for Android Emulator to access localhost
    // Use your computer's IP address for physical devices (e.g., "http://192.168.1.100:8000/api/")
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
    
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
```

---

## 3. Domain Layer

### 3.1 Task Repository Interface (`domain/repository/TaskRepository.kt`)

```kotlin
package com.example.taskmanager.domain.repository

import com.example.taskmanager.data.remote.dto.Task
import com.example.taskmanager.util.Resource

interface TaskRepository {
    suspend fun getTasks(): Resource<List<Task>>
    suspend fun addTask(task: Task): Resource<Task>
    suspend fun updateTask(id: Int, task: Task): Resource<Task>
    suspend fun deleteTask(id: Int): Resource<Unit>
}
```

### 3.2 Task Repository Implementation (`data/repository/TaskRepositoryImpl.kt`)

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
            Resource.Error("Network error: ${e.message}")
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
            Resource.Error("Network error: ${e.message}")
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
            Resource.Error("Network error: ${e.message}")
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
            Resource.Error("Network error: ${e.message}")
        }
    }
}
```

---

## 4. Utility Classes

### 4.1 Resource Wrapper (`util/Resource.kt`)

```kotlin
package com.example.taskmanager.util

sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T> : Resource<T>()
}
```

### 4.2 UI State (`presentation/TaskUiState.kt`)

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

## 5. Presentation Layer

### 5.1 ViewModel (`presentation/TaskViewModel.kt`)

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
                    getTasks() // Refresh the list
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
                }
                is Resource.Loading -> {}
            }
        }
    }
    
    fun deleteTask(task: Task) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = repository.deleteTask(task.id!!)) {
                is Resource.Success -> {
                    // Remove from local list
                    _uiState.update { state ->
                        state.copy(
                            tasks = state.tasks.filter { it.id != task.id },
                            isLoading = false
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
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
```

### 5.2 Main Screen (`presentation/TaskScreen.kt`)

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
                            contentDescription = "Refresh"
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
            
            // Task List
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
                        key = { it.id ?: it.hashCode() }
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

### 5.3 Task Item Component (`presentation/TaskItem.kt`)

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

## 6. Dependency Injection (Hilt)

### 6.1 App Module (`di/AppModule.kt`)

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

### 6.2 Application Class (`TaskManagerApp.kt`)

```kotlin
package com.example.taskmanager

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TaskManagerApp : Application()
```

---

## 7. MainActivity (`MainActivity.kt`)

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

## 8. Android Manifest (`AndroidManifest.xml`)

Add the following permissions and application configuration:

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <!-- Internet Permission -->
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

**Note:** `android:usesCleartextTraffic="true"` is required for HTTP connections (not HTTPS).

---

## 9. Build Configuration (Project-level build.gradle.kts)

```kotlin
plugins {
    id("com.android.application") version "8.1.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
}
```

---

## 10. Project Structure

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
│   │   ├── AndroidManifest.xml
│   │   └── res/
│   └── build.gradle.kts
└── build.gradle.kts (project-level)
```

---

## 11. Setup Instructions

### Laravel Backend:
1. Run migrations: `php artisan migrate`
2. Start the server: `php artisan serve`
3. API will be available at `http://localhost:8000/api/tasks`

### Android App:
1. Create a new Android Studio project
2. Copy all the code files to their respective locations
3. Sync Gradle dependencies
4. Update `BASE_URL` in `RetrofitClient.kt` if needed:
   - Emulator: `http://10.0.2.2:8000/api/`
   - Physical device: `http://YOUR_COMPUTER_IP:8000/api/`
5. Make sure Laravel server is running
6. Run the app

---

## 12. Testing the API

You can test the endpoints using:
- Postman
- Thunder Client (VS Code extension)
- cURL commands

Example cURL commands:
```bash
# Get all tasks
curl http://localhost:8000/api/tasks

# Create a task
curl -X POST http://localhost:8000/api/tasks \
  -H "Content-Type: application/json" \
  -d '{"title": "Test Task"}'

# Update a task
curl -X PUT http://localhost:8000/api/tasks/1 \
  -H "Content-Type: application/json" \
  -d '{"is_completed": true}'

# Delete a task
curl -X DELETE http://localhost:8000/api/tasks/1
```

---

## Additional Notes

1. **Error Handling:** The app includes comprehensive error handling with user-friendly messages.

2. **Loading States:** UI shows loading indicators during network operations.

3. **Optimistic Updates:** Checkbox changes update immediately for better UX.

4. **Clean Architecture:** The project follows clean architecture principles with separate layers.

5. **Dependency Injection:** Uses Hilt for dependency injection (can be adapted for Koin if preferred).

6. **State Management:** Uses StateFlow for reactive state management.

7. **Material 3:** Modern Material Design 3 components throughout.

This completes the full-stack Task Manager application!
