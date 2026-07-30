package com.runeprofittouch.app.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.runeprofittouch.app.R
import com.runeprofittouch.app.data.ServerStore
import com.runeprofittouch.app.database.ItemAnalysisEntity
import com.runeprofittouch.app.database.ItemEntity
import com.runeprofittouch.app.database.ItemStatEntity
import com.runeprofittouch.app.database.PriceEntity
import com.runeprofittouch.app.database.RecipeIngredientDetail
import com.runeprofittouch.app.database.ResourceEntity
import com.runeprofittouch.app.domain.PriceFreshness
import com.runeprofittouch.app.domain.ProfitabilityCalculator
import com.runeprofittouch.app.domain.RuneEstimator
import com.runeprofittouch.app.ui.theme.Amber
import com.runeprofittouch.app.ui.theme.AntiqueGold
import com.runeprofittouch.app.ui.theme.BrightGold
import com.runeprofittouch.app.ui.theme.Ember
import com.runeprofittouch.app.ui.theme.Emerald
import com.runeprofittouch.app.ui.theme.Ivory
import com.runeprofittouch.app.ui.theme.LuxuryBackground
import com.runeprofittouch.app.ui.theme.LuxuryCard
import com.runeprofittouch.app.viewmodel.ItemSortMode
import com.runeprofittouch.app.viewmodel.PriceViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private data class ProfessionVisual(val name: String, val drawable: Int)

private val professionVisuals = listOf(
    ProfessionVisual("Bijoutier", R.drawable.profession_bijoutier),
    ProfessionVisual("Forgeur de boucliers", R.drawable.profession_forgeur_boucliers),
    ProfessionVisual("Alchimiste", R.drawable.profession_alchimiste),
    ProfessionVisual("Bricoleur", R.drawable.profession_bricoleur),
    ProfessionVisual("Tailleur", R.drawable.profession_tailleur),
    ProfessionVisual("Forgeur de dagues", R.drawable.profession_forgeur_dagues),
    ProfessionVisual("Forgeur d'épées", R.drawable.profession_forgeur_epees),
    ProfessionVisual("Forgeur de haches", R.drawable.profession_forgeur_haches),
    ProfessionVisual("Forgeur de marteaux", R.drawable.profession_forgeur_marteaux),
    ProfessionVisual("Forgeur de pelles", R.drawable.profession_forgeur_pelles),
    ProfessionVisual("Sculpteur d'arcs", R.drawable.profession_sculpteur_arcs),
    ProfessionVisual("Sculpteur de bâtons", R.drawable.profession_sculpteur_batons),
    ProfessionVisual("Sculpteur de baguettes", R.drawable.profession_sculpteur_baguettes),
    ProfessionVisual("Cordonnier", R.drawable.profession_cordonnier)
)

private val FreshGreen = Color(0xFF2E7D32)
private val FreshYellow = Color(0xFFF9A825)
private val FreshRed = Color(0xFFC62828)
private val PanelBlack = Color(0xFF0A0B0E)
private val PanelRaised = Color(0xFF15171B)
private val SoftGold = Color(0xFF9A7332)

@Composable
fun PriceScreen(
    favoritesOnly: Boolean = false,
    priceViewModel: PriceViewModel = viewModel()
) {
    val searchText by priceViewModel.searchText.collectAsState()
    val selectedProfessions by priceViewModel.selectedProfessions.collectAsState()
    val selectedServer by ServerStore.selectedServer.collectAsState()
    val selectedRecipeSlots by priceViewModel.selectedRecipeSlots.collectAsState()
    val selectedRuneFilters by priceViewModel.selectedRuneFilters.collectAsState()
    val items by priceViewModel.sortedItems.collectAsState()
    val favoriteIds by priceViewModel.favoriteIds.collectAsState()
    val sortMode by priceViewModel.sortMode.collectAsState()
    val profitByItemId by priceViewModel.profitByItemId.collectAsState()
    val selectedItem by priceViewModel.selectedItem.collectAsState()
    val selectedRecipe by priceViewModel.selectedRecipe.collectAsState()
    val selectedStats by priceViewModel.selectedStats.collectAsState()
    val prices by priceViewModel.latestResourcePrices.collectAsState()
    val runePrices by priceViewModel.latestRunePrices.collectAsState()
    val runes by priceViewModel.runes.collectAsState()
    val analysis by priceViewModel.selectedAnalysis.collectAsState()
    val resourceDrafts by priceViewModel.resourcePriceDrafts.collectAsState()
    val manualCraftCost by priceViewModel.manualCraftCostText.collectAsState()
    val coefficient by priceViewModel.crushingCoefficientText.collectAsState()

    var sortMenuExpanded by remember { mutableStateOf(false) }
    var runeFilterDialogVisible by remember { mutableStateOf(false) }
    var filtersExpanded by remember { mutableStateOf(true) }
    val displayedItems = if (favoritesOnly) items.filter { it.id in favoriteIds } else items

    LuxuryBackground(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 7.dp)
                .drawBehind {
                    drawRoundRect(
                        color = Color.Black.copy(alpha = 0.42f),
                        topLeft = Offset(5.dp.toPx(), 9.dp.toPx()),
                        size = androidx.compose.ui.geometry.Size(size.width - 10.dp.toPx(), size.height - 12.dp.toPx()),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(18.dp.toPx())
                    )
                }
                .background(Color(0x9C090A0D), RoundedCornerShape(18.dp))
        ) {
            GoldHeader(
                title = if (favoritesOnly) "FAVORIS" else "RECHERCHE",
                subtitle = selectedServer
            )
            GoldSearchField(
                value = searchText,
                onValueChange = priceViewModel::updateSearchText,
                modifier = Modifier.padding(horizontal = 14.dp)
            )
            Spacer(Modifier.height(10.dp))
            GoldPanel(
                modifier = Modifier.padding(horizontal = 14.dp),
                onClick = { filtersExpanded = !filtersExpanded }
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Tune, null, tint = BrightGold, modifier = Modifier.size(28.dp))
                    Column(Modifier.weight(1f).padding(start = 12.dp)) {
                        Text("TRI ET FILTRES", color = BrightGold, style = MaterialTheme.typography.titleMedium)
                        Text(
                            if (selectedProfessions.isEmpty()) "Tous les métiers • ${sortMode.label}"
                            else "${selectedProfessions.size} métier(s) • ${sortMode.label}",
                            color = Ivory.copy(alpha = .78f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Icon(if (filtersExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore, null, tint = BrightGold)
                }
            }

            AnimatedVisibility(filtersExpanded) {
                Column(Modifier.fillMaxWidth()) {
                    GoldSectionTitle(
                        title = "MÉTIERS",
                        action = if (selectedProfessions.isEmpty()) "TOUS" else "RÉINITIALISER",
                        onAction = priceViewModel::clearProfessions
                    )
                    Row(
                        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(9.dp)
                    ) {
                        professionVisuals.forEach { profession ->
                            ProfessionTile(
                                profession = profession,
                                selected = profession.name in selectedProfessions,
                                onClick = { priceViewModel.toggleProfession(profession.name) }
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Box(Modifier.padding(horizontal = 14.dp)) {
                        GoldSortSelector(sortMode.label) { sortMenuExpanded = true }
                    }
                    Spacer(Modifier.height(10.dp))
                    GoldPanel(
                        modifier = Modifier.padding(horizontal = 14.dp),
                        onClick = { runeFilterDialogVisible = true },
                        accent = Color(0xFF7C4AB5)
                    ) {
                        Row(
                            Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 11.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text("RUNES OBTENUES", color = BrightGold, style = MaterialTheme.typography.labelLarge)
                                Text(
                                    if (selectedRuneFilters.isEmpty()) "Toutes les runes" else selectedRuneFilters.sorted().joinToString(),
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Text("›", color = BrightGold, fontSize = 32.sp)
                        }
                    }
                    GoldSectionTitle(title = "NOMBRE DE CASES DE LA RECETTE")
                    Row(
                        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        (2..8).forEach { count ->
                            RecipeSlotChip(
                                number = count,
                                selected = count in selectedRecipeSlots,
                                onClick = { priceViewModel.toggleRecipeSlot(count) }
                            )
                        }
                    }
                    Spacer(Modifier.height(11.dp))
                }
            }

            HorizontalDivider(color = AntiqueGold.copy(alpha = .45f))
            if (displayedItems.isEmpty()) {
                Text(
                    if (favoritesOnly) "Aucun favori. Touchez l’étoile d’un objet dans Recherche."
                    else "Aucun résultat. Modifiez la recherche ou les filtres.",
                    color = Color.White,
                    modifier = Modifier.padding(28.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(displayedItems, key = ItemEntity::id) { item ->
                        ItemCard(
                            item = item,
                            isFavorite = item.id in favoriteIds,
                            estimatedProfit = profitByItemId[item.id],
                            onToggleFavorite = { priceViewModel.toggleFavorite(item.id) },
                            onClick = { priceViewModel.selectItem(item) }
                        )
                    }
                }
            }
        }
    }

    if (sortMenuExpanded) {
        FantasyChoiceDialog(
            title = "TRIER PAR",
            options = ItemSortMode.entries.map { it.label },
            selected = sortMode.label,
            onDismiss = { sortMenuExpanded = false },
            onSelected = { label ->
                ItemSortMode.entries.firstOrNull { it.label == label }?.let(priceViewModel::updateSortMode)
                sortMenuExpanded = false
            }
        )
    }

    if (runeFilterDialogVisible) {
        FantasyDialog(
            title = "RUNES OBTENUES",
            onDismiss = { runeFilterDialogVisible = false },
            footer = {
                FantasyTextButton("EFFACER", priceViewModel::clearRuneFilters)
                FantasyTextButton("TERMINER") { runeFilterDialogVisible = false }
            }
        ) {
            LazyColumn(Modifier.fillMaxWidth().height(420.dp)) {
                items(priceViewModel.runeFilterOptions, key = { it }) { runeName ->
                    Row(
                        Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { priceViewModel.toggleRuneFilter(runeName) }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = runeName in selectedRuneFilters,
                            onCheckedChange = { priceViewModel.toggleRuneFilter(runeName) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = BrightGold,
                                uncheckedColor = AntiqueGold,
                                checkmarkColor = PanelBlack
                            )
                        )
                        Text(runeName, color = Color.White)
                    }
                }
            }
        }
    }

    selectedItem?.let { item ->
        ProfitabilityDialog(
            item = item,
            recipe = selectedRecipe,
            stats = selectedStats,
            runes = runes,
            prices = prices,
            runePrices = runePrices,
            resourceDrafts = resourceDrafts,
            analysis = analysis,
            manualCraftCost = manualCraftCost,
            coefficient = coefficient,
            onResourcePriceChanged = priceViewModel::updateResourcePrice,
            onManualCraftCostChanged = priceViewModel::updateManualCraftCost,
            onCoefficientChanged = priceViewModel::updateCrushingCoefficient,
            onClose = priceViewModel::closeRecipe
        )
    }
}

@Composable
private fun GoldHeader(title: String, subtitle: String) {
    val topInset = 28.dp
    Column(
        Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp, top = topInset + 6.dp, bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (title == "RECHERCHE") {
            Image(
                painter = painterResource(R.drawable.title_recherche),
                contentDescription = title,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxWidth().height(74.dp)
            )
        } else {
            Text(
                title,
                color = BrightGold,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.width(72.dp).height(1.dp).background(Brush.horizontalGradient(listOf(Color.Transparent, BrightGold))))
            Text(subtitle, color = BrightGold, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(horizontal = 14.dp), fontWeight = FontWeight.Bold)
            Box(Modifier.width(72.dp).height(1.dp).background(Brush.horizontalGradient(listOf(BrightGold, Color.Transparent))))
        }
    }
}

@Composable
private fun GoldSearchField(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = TextStyle(color = Color.White, fontSize = 17.sp),
        cursorBrush = Brush.verticalGradient(listOf(BrightGold, BrightGold)),
        modifier = modifier.fillMaxWidth().height(58.dp),
        decorationBox = { inner ->
            GoldPanel(Modifier.fillMaxSize()) {
                Row(Modifier.fillMaxSize().padding(horizontal = 15.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Search, null, tint = BrightGold, modifier = Modifier.size(29.dp))
                    Box(Modifier.weight(1f).padding(start = 13.dp)) {
                        if (value.isBlank()) Text("Rechercher un objet…", color = Ivory.copy(alpha = .60f), fontSize = 17.sp)
                        inner()
                    }
                }
            }
        }
    )
}

@Composable
private fun GoldPanel(
    modifier: Modifier = Modifier,
    accent: Color = BrightGold,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(12.dp)
    val click = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
    Box(
        modifier.clip(shape)
            .background(Brush.verticalGradient(listOf(Color(0xFF1C1A16), PanelBlack, Color(0xFF111215))))
            .border(BorderStroke(1.dp, Brush.linearGradient(listOf(accent, SoftGold, accent.copy(alpha = .45f)))), shape)
            .drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(listOf(accent.copy(alpha = .08f), Color.Transparent)),
                    radius = size.minDimension,
                    center = Offset(size.width * .18f, size.height * .2f)
                )
            }
            .then(click)
    ) { content() }
}

@Composable
private fun GoldSectionTitle(title: String, action: String? = null, onAction: (() -> Unit)? = null) {
    Row(
        Modifier.fillMaxWidth().padding(start = 15.dp, end = 15.dp, top = 12.dp, bottom = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = BrightGold, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.width(8.dp))
        Box(Modifier.weight(1f).height(1.dp).background(Brush.horizontalGradient(listOf(AntiqueGold, Color.Transparent))))
        if (action != null) {
            Text(action, color = Emerald, modifier = Modifier.clickable(enabled = onAction != null) { onAction?.invoke() }, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ProfessionTile(profession: ProfessionVisual, selected: Boolean, onClick: () -> Unit) {
    val selection = if (selected) Emerald else Color.Transparent
    val outerShape = RoundedCornerShape(10.dp)

    Box(
        Modifier
            .size(width = 112.dp, height = 178.dp)
            .background(Color(0xE6080909), outerShape)
            .border(if (selected) 2.dp else 1.dp, if (selected) Emerald else AntiqueGold.copy(alpha = .62f), outerShape)
            .clickable(onClick = onClick)
            .drawBehind {
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = .045f), Color.Transparent, Color.Black.copy(alpha = .45f))
                    ),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(10.dp.toPx())
                )
                if (selected) {
                    drawCircle(selection.copy(alpha = .17f), radius = size.minDimension * .82f, center = Offset(size.width / 2f, size.height * .38f))
                }
            }
    ) {
        Column(Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(128.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF07140C), Color(0xFF041008), Color(0xFF080909))
                        ),
                        RoundedCornerShape(topStart = 9.dp, topEnd = 9.dp)
                    )
                    .padding(2.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(profession.drawable),
                    contentDescription = profession.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Box(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        Brush.verticalGradient(listOf(Color(0xFF1B160D), Color(0xFF090A09)))
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(listOf(Color.Transparent, AntiqueGold, Color.Transparent)),
                        shape = RoundedCornerShape(bottomStart = 9.dp, bottomEnd = 9.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    profession.name,
                    color = Ivory,
                    fontSize = 11.sp,
                    lineHeight = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 3.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 2.dp)
                .size(if (selected) 12.dp else 9.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(if (selected) Emerald else AntiqueGold)
                .border(1.dp, BrightGold, RoundedCornerShape(2.dp))
                .drawBehind { },
            contentAlignment = Alignment.Center
        ) {
            if (selected) {
                Box(Modifier.size(5.dp).background(Color(0xFFB5FFC0), RoundedCornerShape(1.dp)))
            }
        }
    }
}

@Composable
private fun GoldSortSelector(label: String, onClick: () -> Unit) {
    GoldPanel(Modifier.fillMaxWidth(), onClick = onClick) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 11.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("TRIER PAR", color = BrightGold, style = MaterialTheme.typography.labelSmall)
                Text(label, color = Color.White, style = MaterialTheme.typography.bodyLarge)
            }
            Icon(Icons.Filled.ArrowDropDown, null, tint = BrightGold)
        }
    }
}

@Composable
private fun RecipeSlotChip(number: Int, selected: Boolean, onClick: () -> Unit) {
    Row(
        Modifier.height(43.dp).clip(RoundedCornerShape(8.dp)).background(PanelRaised)
            .border(1.dp, if (selected) BrightGold else AntiqueGold.copy(alpha = .45f), RoundedCornerShape(8.dp))
            .clickable(onClick = onClick).padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Text(number.toString(), color = BrightGold, fontWeight = FontWeight.Bold)
        Box(
            Modifier.size(23.dp).clip(RoundedCornerShape(4.dp))
                .background(if (selected) Color(0xFF5A367F) else Color(0xFF1B1720))
                .border(1.dp, if (selected) Color(0xFFC6A5F0) else Color(0xFF5E5068), RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) { if (selected) Icon(Icons.Filled.Check, null, tint = Color.White, modifier = Modifier.size(17.dp)) }
    }
}

@Composable
private fun ProfitabilityDialog(
    item: ItemEntity,
    recipe: List<RecipeIngredientDetail>,
    stats: List<ItemStatEntity>,
    runes: List<ResourceEntity>,
    prices: Map<Int, PriceEntity>,
    runePrices: Map<Int, PriceEntity>,
    resourceDrafts: Map<Int, String>,
    analysis: ItemAnalysisEntity?,
    manualCraftCost: String,
    coefficient: String,
    onResourcePriceChanged: (Int, String) -> Unit,
    onManualCraftCostChanged: (String) -> Unit,
    onCoefficientChanged: (String) -> Unit,
    onClose: () -> Unit
) {
    val detailedCost = recipe.sumOf { ingredient ->
        val unitPrice = resourceDrafts[ingredient.resourceId]?.toLongOrNull()
            ?: prices[ingredient.resourceId]?.price
            ?: 0L
        unitPrice * ingredient.quantity
    }
    val manualCost = manualCraftCost.toLongOrNull() ?: 0L
    val effectiveCost = manualCost.takeIf { it > 0 } ?: detailedCost
    val coefficientValue = coefficient.replace(',', '.').toDoubleOrNull() ?: 100.0
    val runeEstimates = RuneEstimator.estimate(
        stats = stats,
        runes = runes,
        runePrices = runePrices,
        coefficientPercent = coefficientValue
    )
    val estimatedRuneValue = runeEstimates.sumOf { it.estimatedValue }
    val estimatedProfit = estimatedRuneValue - effectiveCost
    val estimatedRoi =
        if (effectiveCost > 0) estimatedProfit.toDouble() / effectiveCost * 100.0 else 0.0

    FantasyDialog(
        title = item.name.uppercase(),
        onDismiss = onClose,
        footer = { FantasyTextButton("FERMER", onClose) }
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
                Text("Prix des ressources", fontWeight = FontWeight.Bold)
                if (recipe.isEmpty()) {
                    Text("Aucune recette disponible pour cet objet.")
                } else {
                    recipe.forEach { ingredient ->
                        IngredientPriceRow(
                            ingredient = ingredient,
                            value = resourceDrafts[ingredient.resourceId]
                                ?: prices[ingredient.resourceId]?.price?.toString().orEmpty(),
                            recordedAt = prices[ingredient.resourceId]?.recordedAt,
                            onValueChanged = { onResourcePriceChanged(ingredient.resourceId, it) }
                        )
                    }
                    Text("Coût détaillé : ${formatKamas(detailedCost)}")
                }

                HorizontalDivider()
                Text("Calcul rapide", fontWeight = FontWeight.Bold)
                PriceField(
                    value = manualCraftCost,
                    onValueChange = onManualCraftCostChanged,
                    label = "Valeur totale manuelle du craft"
                )
                Text(
                    if (manualCost > 0) "Le calcul utilise la valeur manuelle."
                    else "Le calcul utilise le total des ressources.",
                    style = MaterialTheme.typography.bodySmall
                )

                OutlinedTextField(
                    value = coefficient,
                    onValueChange = onCoefficientChanged,
                    label = { Text("Coefficient de brisage (%)") },
                    suffix = { Text("%") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                analysis?.let {
                    FreshnessIndicator(it.updatedAt)
                    Text(
                        text = "Mis à jour : ${formatDateTime(it.updatedAt)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                HorizontalDivider()
                Text("Runes estimées", fontWeight = FontWeight.Bold)
                if (runeEstimates.isEmpty()) {
                    Text(
                        "Aucune caractéristique compatible ou aucun prix de rune disponible.",
                        style = MaterialTheme.typography.bodySmall
                    )
                } else {
                    runeEstimates.forEach { estimate ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "${estimate.runeName} ×${String.format(Locale.FRANCE, "%.2f", estimate.estimatedQuantity)}",
                                modifier = Modifier.weight(1f)
                            )
                            Text(formatKamas(estimate.estimatedValue))
                        }
                    }
                }

                HorizontalDivider()
                ResultLine("Coût retenu", effectiveCost)
                ResultLine("Valeur estimée des runes", estimatedRuneValue)
                ResultLine("Profit estimé", estimatedProfit, estimatedProfit >= 0)
                Text(
                    text = "ROI estimé : ${String.format(Locale.FRANCE, "%.1f", estimatedRoi)} %",
                    fontWeight = FontWeight.Bold,
                    color = if (estimatedRoi >= 0) FreshGreen else FreshRed
                )
                Text(
                    text = "Estimation basée sur le jet moyen de l’objet et les runes de base.",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Les modifications sont enregistrées automatiquement.",
                    style = MaterialTheme.typography.bodySmall
                )
            Spacer(Modifier.height(4.dp))
        }
    }
}

@Composable
private fun IngredientPriceRow(
    ingredient: RecipeIngredientDetail,
    value: String,
    recordedAt: Long?,
    onValueChanged: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ResourceThumbnail(
                imageUrl = ingredient.resourceImageUrl,
                contentDescription = ingredient.resourceName,
                size = 42.dp,
                modifier = Modifier.padding(end = 10.dp)
            )
            Text(
                "${ingredient.resourceName} ×${ingredient.quantity}",
                modifier = Modifier.weight(1f)
            )
            recordedAt?.let { FreshnessDot(it) }
        }
        PriceField(value, onValueChanged, "Prix unitaire")
        recordedAt?.let {
            Text(
                "Mis à jour : ${formatDateTime(it)}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun PriceField(value: String, onValueChange: (String) -> Unit, label: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        suffix = { Text("K") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun FreshnessIndicator(recordedAt: Long) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FreshnessDot(recordedAt)
        Text(freshnessLabel(recordedAt), style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun FreshnessDot(recordedAt: Long) {
    Box(
        modifier = Modifier
            .size(12.dp)
            .background(freshnessColor(recordedAt), CircleShape)
    )
}

@Composable
private fun ResultLine(label: String, value: Long, highlight: Boolean? = null) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label)
        Text(
            text = formatKamas(value),
            fontWeight = FontWeight.Bold,
            color = when (highlight) {
                true -> FreshGreen
                false -> FreshRed
                null -> MaterialTheme.colorScheme.onSurface
            }
        )
    }
}

@Composable
private fun ItemCard(
    item: ItemEntity,
    isFavorite: Boolean,
    estimatedProfit: Long?,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit
) {
    val profitColor = when {
        estimatedProfit == null -> BrightGold
        estimatedProfit >= 0L -> Emerald
        else -> Ember
    }
    GoldPanel(modifier = Modifier.fillMaxWidth(), accent = BrightGold, onClick = onClick) {
        Row(
            Modifier.fillMaxWidth().height(118.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.width(112.dp).fillMaxHeight()
                    .background(Brush.radialGradient(listOf(profitColor.copy(alpha = .17f), PanelBlack)))
                    .border(BorderStroke(1.dp, BrightGold.copy(alpha = .55f)), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize().padding(12.dp)
                )
            }
            Column(
                Modifier.weight(1f).padding(horizontal = 13.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    item.name,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "${item.itemType} • Niv. ${item.itemLevel}",
                    color = BrightGold,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1
                )
                Text(
                    "${item.profession} • Requis ${item.requiredProfessionLevel}",
                    color = Ivory.copy(alpha = .76f),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                estimatedProfit?.let { profit ->
                    Text(
                        if (profit >= 0) "+${formatKamas(profit)}" else formatKamas(profit),
                        color = profitColor,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
            IconButton(onClick = onToggleFavorite, modifier = Modifier.align(Alignment.Top)) {
                Icon(
                    if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                    if (isFavorite) "Retirer des favoris" else "Ajouter aux favoris",
                    tint = if (isFavorite) BrightGold else Ivory.copy(alpha = .72f)
                )
            }
        }
    }
}

@Composable
private fun FantasyChoiceDialog(
    title: String,
    options: List<String>,
    selected: String,
    onDismiss: () -> Unit,
    onSelected: (String) -> Unit
) {
    FantasyDialog(title = title, onDismiss = onDismiss) {
        Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
            options.forEach { option ->
                val active = option == selected
                Row(
                    Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(9.dp))
                        .background(if (active) BrightGold.copy(alpha = .13f) else Color.Transparent)
                        .border(1.dp, if (active) BrightGold else AntiqueGold.copy(alpha = .35f), RoundedCornerShape(9.dp))
                        .clickable { onSelected(option) }
                        .padding(horizontal = 13.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(option, color = if (active) BrightGold else Color.White, modifier = Modifier.weight(1f))
                    if (active) Icon(Icons.Filled.Check, null, tint = BrightGold, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
private fun FantasyDialog(
    title: String,
    onDismiss: () -> Unit,
    footer: (@Composable RowScope.() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            Modifier.fillMaxWidth(.92f)
                .clip(RoundedCornerShape(16.dp))
                .background(Brush.verticalGradient(listOf(Color(0xFF201B12), PanelBlack, Color(0xFF121317))))
                .border(BorderStroke(2.dp, Brush.linearGradient(listOf(BrightGold, SoftGold, BrightGold))), RoundedCornerShape(16.dp))
                .padding(3.dp)
        ) {
            Column(
                Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(13.dp))
                    .border(1.dp, AntiqueGold.copy(alpha = .5f), RoundedCornerShape(13.dp))
                    .padding(16.dp)
            ) {
                Text(
                    title,
                    color = BrightGold,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Box(
                    Modifier.fillMaxWidth().padding(vertical = 10.dp).height(1.dp)
                        .background(Brush.horizontalGradient(listOf(Color.Transparent, BrightGold, Color.Transparent)))
                )
                content()
                if (footer != null) {
                    Spacer(Modifier.height(12.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                        content = footer
                    )
                }
            }
        }
    }
}

@Composable
private fun FantasyTextButton(label: String, onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Text(label, color = BrightGold, fontWeight = FontWeight.Bold)
    }
}

private fun freshnessColor(recordedAt: Long): Color = when (
    ProfitabilityCalculator.freshness(recordedAt)
) {
    PriceFreshness.FRESH -> FreshGreen
    PriceFreshness.AGING -> FreshYellow
    PriceFreshness.STALE -> FreshRed
}

private fun freshnessLabel(recordedAt: Long): String = when (
    ProfitabilityCalculator.freshness(recordedAt)
) {
    PriceFreshness.FRESH -> "Prix récent (0–3 jours)"
    PriceFreshness.AGING -> "Prix à vérifier bientôt (4–6 jours)"
    PriceFreshness.STALE -> "Prix ancien (7 jours ou plus)"
}

private fun formatDateTime(timestamp: Long): String =
    SimpleDateFormat("dd/MM/yyyy à HH:mm", Locale.FRANCE).format(Date(timestamp))

private fun formatKamas(value: Long): String =
    "${NumberFormat.getIntegerInstance(Locale.FRANCE).format(value)} K"
