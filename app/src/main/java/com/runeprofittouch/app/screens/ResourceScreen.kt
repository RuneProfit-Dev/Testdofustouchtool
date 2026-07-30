package com.runeprofittouch.app.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.drawBehind
import com.runeprofittouch.app.R
import com.runeprofittouch.app.data.ServerStore
import com.runeprofittouch.app.database.PriceEntity
import com.runeprofittouch.app.database.ResourceEntity
import com.runeprofittouch.app.domain.PriceFreshness
import com.runeprofittouch.app.domain.ProfitabilityCalculator
import com.runeprofittouch.app.ui.theme.Amber
import com.runeprofittouch.app.ui.theme.AntiqueGold
import com.runeprofittouch.app.ui.theme.BrightGold
import com.runeprofittouch.app.ui.theme.Ivory
import com.runeprofittouch.app.ui.theme.Ember
import com.runeprofittouch.app.ui.theme.Emerald
import com.runeprofittouch.app.ui.theme.LuxuryBackground
import com.runeprofittouch.app.ui.theme.LuxuryCard
import com.runeprofittouch.app.ui.theme.FantasyScreenHeader
import com.runeprofittouch.app.viewmodel.CatalogPriceSort
import com.runeprofittouch.app.viewmodel.ResourceViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ResourceScreen(viewModel: ResourceViewModel) = CatalogPriceScreen(
    titleDrawable = R.drawable.title_ressources,
    searchPlaceholder = "Rechercher une ressource…",
    emptyMessage = "Aucune ressource trouvée.",
    viewModel = viewModel
)

@Composable
fun RuneScreen(viewModel: ResourceViewModel) = CatalogPriceScreen(
    titleDrawable = R.drawable.title_rune,
    searchPlaceholder = "Rechercher une rune…",
    emptyMessage = "Aucune rune trouvée.",
    viewModel = viewModel
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CatalogPriceScreen(
    titleDrawable: Int,
    searchPlaceholder: String,
    emptyMessage: String,
    viewModel: ResourceViewModel
) {
    val resources by viewModel.resources.collectAsState()
    val server by ServerStore.selectedServer.collectAsState()
    val searchText by viewModel.searchText.collectAsState()
    val prices by viewModel.latestPrices.collectAsState()
    val drafts by viewModel.priceDrafts.collectAsState()
    val sortMode by viewModel.sortMode.collectAsState()
    var sortExpanded by remember { mutableStateOf(false) }
    var filtersExpanded by remember { mutableStateOf(false) }

    LuxuryBackground(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                FantasyScreenHeader(
                    titleDrawable = titleDrawable,
                    serverName = server
                )
                OutlinedTextField(
                    value = searchText,
                    onValueChange = viewModel::updateSearchText,
                    placeholder = { Text(searchPlaceholder) },
                    leadingIcon = { Icon(Icons.Filled.Search, null) },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    colors = luxuryFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
                LuxuryCard(
                    modifier = Modifier.fillMaxWidth(),
                    accent = AntiqueGold,
                    onClick = { filtersExpanded = !filtersExpanded },
                    corner = 16.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Tune, null, tint = BrightGold)
                        Column(Modifier.weight(1f).padding(start = 12.dp)) {
                            Text("Tri et filtres", color = Color.White, fontWeight = FontWeight.Bold)
                            Text(sortMode.label, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                        }
                        Icon(
                            if (filtersExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                            null,
                            tint = BrightGold
                        )
                    }
                }
                AnimatedVisibility(visible = filtersExpanded) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        ExposedDropdownMenuBox(
                            expanded = sortExpanded,
                            onExpandedChange = { sortExpanded = !sortExpanded }
                        ) {
                            OutlinedTextField(
                                value = sortMode.label,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Trier par") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(sortExpanded) },
                                shape = RoundedCornerShape(18.dp),
                                colors = luxuryFieldColors(),
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = sortExpanded,
                                onDismissRequest = { sortExpanded = false }
                            ) {
                                CatalogPriceSort.entries.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option.label) },
                                        onClick = {
                                            viewModel.updateSortMode(option)
                                            sortExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                        Text(
                            text = if (sortMode == CatalogPriceSort.TO_UPDATE)
                                "Priorité : jamais renseigné, rouge, jaune, puis vert."
                            else "Les prix sont enregistrés automatiquement.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (resources.isEmpty()) {
                Text(emptyMessage, modifier = Modifier.padding(32.dp))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    itemsIndexed(resources, key = { _, item -> item.id }) { index, resource ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + slideInVertically(initialOffsetY = { 24 + index.coerceAtMost(4) * 8 })
                        ) {
                            CatalogPriceCard(
                                resource = resource,
                                value = drafts[resource.id] ?: prices[resource.id]?.price?.toString().orEmpty(),
                                price = prices[resource.id],
                                onValueChange = { viewModel.updatePrice(resource.id, it) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CatalogPriceCard(
    resource: ResourceEntity,
    value: String,
    price: PriceEntity?,
    onValueChange: (String) -> Unit
) {
    val freshness = price?.let { ProfitabilityCalculator.freshness(it.recordedAt) }
    val accent = when (freshness) {
        PriceFreshness.FRESH -> Emerald
        PriceFreshness.AGING -> Amber
        PriceFreshness.STALE -> Ember
        null -> MaterialTheme.colorScheme.primary
    }

    LuxuryCard(modifier = Modifier.fillMaxWidth(), accent = AntiqueGold, corner = 16.dp) {
        Box(
            Modifier
                .width(6.dp)
                .fillMaxHeight()
                .align(Alignment.CenterStart)
                .background(
                    Brush.horizontalGradient(
                        listOf(accent, accent.copy(alpha = 0.35f))
                    )
                )
        )
        Column(
            modifier = Modifier.padding(start = 22.dp, top = 18.dp, end = 18.dp, bottom = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .background(accent.copy(alpha = 0.10f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    ResourceThumbnail(
                        imageUrl = resource.imageUrl,
                        contentDescription = resource.name,
                        size = 56.dp
                    )
                }
                Column(Modifier.weight(1f).padding(start = 14.dp)) {
                    Text(resource.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    if (resource.resourceType.isNotBlank()) {
                        Text(resource.resourceType, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Box(Modifier.size(12.dp).background(accent, CircleShape))
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Prix actuel", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = price?.let { "${NumberFormat.getIntegerInstance(Locale.CANADA_FRENCH).format(it.price)} K" } ?: "—",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black
                    )
                }
                Text(
                    text = when (freshness) {
                        PriceFreshness.FRESH -> "À jour"
                        PriceFreshness.AGING -> "À vérifier"
                        PriceFreshness.STALE -> "Ancien"
                        null -> "Jamais renseigné"
                    },
                    color = accent,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.Bottom)
                )
            }

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text("Prix HDV unitaire") },
                suffix = { Text("K") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = luxuryFieldColors(accent),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun luxuryFieldColors(accent: Color = MaterialTheme.colorScheme.primary) =
    OutlinedTextFieldDefaults.colors(
        focusedBorderColor = accent,
        unfocusedBorderColor = accent.copy(alpha = 0.48f),
        focusedContainerColor = Color(0xFF12151B).copy(alpha = 0.92f),
        unfocusedContainerColor = Color(0xFF12151B).copy(alpha = 0.78f),
        cursorColor = accent,
        focusedLabelColor = accent,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
