package com.example.isopod.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FilterList(viewModel: FilterViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    FilterList(
        state = uiState,
        onToggle = viewModel::toggleFilter
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterList(
    state: FilterUiState,
    onToggle: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text(text = "Filters", style = MaterialTheme.typography.titleLarge) }
            )
        }
    ) { padding ->
        FilterColumn(
            state = state,
            onToggle = onToggle,
            modifier = modifier.fillMaxSize(),
            contentPadding = padding
        )
    }
}

@Composable
private fun FilterColumn(
    state: FilterUiState,
    onToggle: (Int) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(state.filters) { item ->
            FilterChip(
                selected = item.id in state.selectedIds,
                onClick = { onToggle(item.id) },
                label = { Text(text = item.label) },
                modifier = Modifier
            )
        }
    }
}
