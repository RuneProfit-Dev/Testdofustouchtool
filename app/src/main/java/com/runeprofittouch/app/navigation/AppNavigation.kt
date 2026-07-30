package com.runeprofittouch.app.navigation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.runeprofittouch.app.database.DatabaseProvider
import com.runeprofittouch.app.repository.ResourceRepository
import com.runeprofittouch.app.screens.PriceScreen
import com.runeprofittouch.app.screens.ResourceScreen
import com.runeprofittouch.app.screens.RuneScreen
import com.runeprofittouch.app.screens.SettingsScreen
import com.runeprofittouch.app.ui.theme.AntiqueGold
import com.runeprofittouch.app.ui.theme.BrightGold
import com.runeprofittouch.app.ui.theme.Emerald
import com.runeprofittouch.app.ui.theme.GraphiteRaised
import com.runeprofittouch.app.ui.theme.Ivory
import com.runeprofittouch.app.ui.theme.Obsidian
import com.runeprofittouch.app.ui.theme.ObsidianSoft
import com.runeprofittouch.app.viewmodel.PriceViewModel
import com.runeprofittouch.app.viewmodel.ResourceViewModel
import com.runeprofittouch.app.viewmodel.ResourceViewModelFactory

private data class NavigationItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val priceViewModel: PriceViewModel = viewModel(key = "shared-price")
    val navigationItems = listOf(
        NavigationItem("search", "Recherche", Icons.Filled.Search),
        NavigationItem("resources", "Ressources", Icons.Filled.Inventory2),
        NavigationItem("runes", "Runes", Icons.Filled.Diamond),
        NavigationItem("settings", "Plus", Icons.Filled.MoreHoriz)
    )

    Scaffold(
        containerColor = Obsidian,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = backStackEntry?.destination
            GameBottomBar(
                items = navigationItems,
                selectedRoute = currentDestination?.route,
                onSelected = { item ->
                    if (currentDestination?.route != item.route) {
                        navController.navigate(item.route) {
                            popUpTo("search") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "search",
            modifier = Modifier
                .padding(innerPadding)
                
        ) {
            composable("search") { PriceScreen(priceViewModel = priceViewModel) }
            composable("resources") {
                val database = DatabaseProvider.getDatabase(context)
                val resourceViewModel: ResourceViewModel = viewModel(
                    key = "resources",
                    factory = ResourceViewModelFactory(
                        repository = ResourceRepository(database.resourceDao()),
                        priceDao = database.priceDao(),
                        subjectType = "RESOURCE",
                        usedResourceIds = database.recipeIngredientDao().observeUsedResourceIds(),
                        equipmentItemIds = database.itemDao().observeAllIds()
                    )
                )
                ResourceScreen(resourceViewModel)
            }
            composable("runes") {
                val database = DatabaseProvider.getDatabase(context)
                val runeViewModel: ResourceViewModel = viewModel(
                    key = "runes",
                    factory = ResourceViewModelFactory(
                        repository = ResourceRepository(database.resourceDao()),
                        priceDao = database.priceDao(),
                        subjectType = "RUNE",
                        usedResourceIds = database.recipeIngredientDao().observeUsedResourceIds(),
                        equipmentItemIds = database.itemDao().observeAllIds()
                    )
                )
                RuneScreen(runeViewModel)
            }
            composable("settings") { SettingsScreen() }
        }
    }
}

@Composable
private fun GameBottomBar(
    items: List<NavigationItem>,
    selectedRoute: String?,
    onSelected: (NavigationItem) -> Unit
) {
    val outerShape = RoundedCornerShape(22.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(horizontal = 10.dp, vertical = 7.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .graphicsLayer {
                    shadowElevation = 18.dp.toPx()
                    shape = outerShape
                    clip = false
                    ambientShadowColor = Color.Black.copy(alpha = 0.75f)
                    spotShadowColor = Color.Black
                }
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF15110C), Color(0xFF090A0B), Color(0xFF141110))
                    ),
                    outerShape
                )
                .border(
                    BorderStroke(
                        2.dp,
                        Brush.verticalGradient(
                            listOf(Color(0xFFF3D37A), BrightGold, AntiqueGold)
                        )
                    ),
                    outerShape
                )
                .padding(horizontal = 6.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, item ->
                    val selected = selectedRoute == item.route
                    val scale by animateFloatAsState(
                        targetValue = if (selected) 1.015f else 1f,
                        animationSpec = spring(stiffness = 460f),
                        label = "navScale"
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .height(68.dp)
                            .scale(scale)
                            .background(
                                if (selected) {
                                    Brush.verticalGradient(
                                        listOf(Color(0xFF235A21), Color(0xFF123B15), Color(0xFF081D0B))
                                    )
                                } else {
                                    Brush.verticalGradient(
                                        listOf(Color.Transparent, Color.Transparent)
                                    )
                                },
                                RoundedCornerShape(16.dp)
                            )
                            .then(
                                if (selected) Modifier.border(
                                    1.8.dp,
                                    Brush.verticalGradient(listOf(Color(0xFFFFE8A2), AntiqueGold, BrightGold)),
                                    RoundedCornerShape(16.dp)
                                ) else Modifier
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onSelected(item) },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = if (selected) Color(0xFFFFE7A0) else AntiqueGold.copy(alpha = .94f),
                            modifier = Modifier.size(if (selected) 30.dp else 26.dp)
                        )
                        Text(
                            text = item.label,
                            color = if (selected) Color.White else Ivory.copy(alpha = .90f),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
                            maxLines = 1
                        )
                    }
                    if (index < items.lastIndex) {
                        Box(
                            Modifier
                                .height(56.dp)
                                .size(width = 1.dp, height = 56.dp)
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color.Transparent, AntiqueGold.copy(alpha = .42f), Color.Transparent)
                                    )
                                )
                        )
                    }
                }
            }
        }
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 8.dp)
                .size(24.dp)
                .background(Color(0xFF1A160F), RoundedCornerShape(6.dp))
                .border(1.8.dp, BrightGold, RoundedCornerShape(6.dp))
                .graphicsLayer { rotationZ = 45f },
            contentAlignment = Alignment.Center
        ) {
            Box(
                Modifier
                    .size(12.dp)
                    .background(Emerald, RoundedCornerShape(3.dp))
                    .border(1.dp, Color(0xFFA1FFB0), RoundedCornerShape(3.dp))
            )
        }
    }
}

