package com.example.isopodcare

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.ModalNavigationDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    IsopodCareApp()
                }
            }
        }
    }
}

private val trackedCategories = listOf(
    "Feeding",
    "Misting",
    "Breeding Notes",
    "Attitude Check",
    "Tank Cleaning",
    "Population Count"
)

private val topIsopodSpecies = listOf(
    "Armadillidium vulgare (Common Pill Bug)",
    "Armadillidium klugii (Croatian Red)",
    "Armadillidium maculatum (Zebra)",
    "Armadillidium gestroi",
    "Armadillidium granulatocolle",
    "Armadillidium nasatum (Peach)",
    "Cubaris sp. 'Panda King'",
    "Cubaris sp. 'Rubber Ducky'",
    "Cubaris murina",
    "Porcellio laevis (Dairy Cow)",
    "Porcellio scaber (Orange)",
    "Porcellio hoffmannseggi",
    "Porcellio expansus",
    "Porcellio magnificus",
    "Porcellio dilatatus (Giant Canyon)",
    "Porcellionides pruinosus (Powder Orange)",
    "Porcellionides pruinosus 'White Out'",
    "Philoscia muscorum",
    "Trichorhina tomentosa (Dwarf White)",
    "Venezillo parvus"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IsopodCareApp() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Open)
    var selectedCategory by remember { mutableStateOf(trackedCategories.first()) }

    ModalNavigationDrawer(
        drawerContent = {
            ModalNavigationDrawerSheet(modifier = Modifier.fillMaxHeight()) {
                Text(
                    text = "Isopod Care",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(24.dp)
                )
                trackedCategories.forEach { category ->
                    NavigationDrawerItem(
                        label = { Text(category) },
                        selected = category == selectedCategory,
                        onClick = { selectedCategory = category },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Future Upgrades:\n• Wi-Fi/Bluetooth temp + humidity\n• Auto reminders",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = selectedCategory,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Tap species to log updates",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(topIsopodSpecies, key = { it }) { species ->
                    IsopodCard(speciesName = species, selectedCategory = selectedCategory)
                }
            }
        }
    }
}

@Composable
private fun IsopodCard(
    speciesName: String,
    selectedCategory: String
) {
    val context = LocalContext.current
    var isTaskDone by remember { mutableStateOf(false) }
    var notes by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        imageUri = uri
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            galleryLauncher.launch("image/*")
        }
    }

    val requiresRuntimePermission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    val mediaPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.medium)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = speciesName,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = selectedCategory,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Checkbox(checked = isTaskDone, onCheckedChange = { isChecked -> isTaskDone = isChecked })
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = if (isTaskDone) "Nice! Logged for today." else "Tap the box when you're done.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = {
            when {
                !requiresRuntimePermission -> {
                    galleryLauncher.launch("image/*")
                }
                hasMediaPermission(context, mediaPermission) -> {
                    galleryLauncher.launch("image/*")
                }
                else -> {
                    permissionLauncher.launch(mediaPermission)
                }
            }
        }) {
            Text(text = if (imageUri == null) "Add habitat photo" else "Replace photo")
        }

        imageUri?.let { uri ->
            Spacer(modifier = Modifier.height(8.dp))
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(uri)
                    .crossfade(true)
                    .build(),
                contentDescription = "Isopod habitat photo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        ExpandableNotes(
            notes = notes,
            onNotesChange = { notes = it }
        )
    }
}

@Composable
private fun ExpandableNotes(
    notes: String,
    onNotesChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.05f), shape = MaterialTheme.shapes.small)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Notes", style = MaterialTheme.typography.bodyLarge)
            TextButton(onClick = { expanded = !expanded }) {
                Text(text = if (expanded) "Hide" else "Add")
            }
        }
        if (expanded) {
            androidx.compose.material3.OutlinedTextField(
                value = notes,
                onValueChange = onNotesChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Write quick thoughts about this species today…") }
            )
        } else {
            Text(
                text = if (notes.isBlank()) "No notes yet" else notes,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun hasMediaPermission(context: Context, permission: String): Boolean {
    return androidx.core.content.ContextCompat.checkSelfPermission(
        context,
        permission
    ) == PackageManager.PERMISSION_GRANTED
}
