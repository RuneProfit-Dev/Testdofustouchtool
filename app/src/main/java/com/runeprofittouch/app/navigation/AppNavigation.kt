package com.runeprofittouch.app.navigation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CutCornerShape
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        NavigationItem("resources", "Ressource", Icons.Filled.Inventory2),
        NavigationItem("runes", "Rune", Icons.Filled.Diamond),
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
    val outerShape = CutCornerShape(13.dp)
    val innerShape = CutCornerShape(9.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(92.dp)
            .background(Color.Transparent)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(82.dp)
                .align(Alignment.TopCenter)
                .shadow(12.dp, outerShape, clip = false)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFFFFE38A),
                            Color(0xFFB47A20),
                            Color(0xFF4D2E09)
                        )
                    ),
                    outerShape
                )
                .border(
                    1.dp,
                    Brush.verticalGradient(
                        listOf(Color(0xFFFFF0B0), Color(0xFF9D6618), Color(0xFF3A2106))
                    ),
                    outerShape
                )
                .padding(3.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(76.dp)
                    .background(Color(0xFF100D09), innerShape)
                    .border(
                        1.5.dp,
                        Brush.verticalGradient(
                            listOf(Color(0xFF5E3C10), BrightGold, AntiqueGold, Color(0xFF392207))
                        ),
                        innerShape
                    )
                    .padding(2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF242018),
                                    Color(0xFF11110E),
                                    Color(0xFF080906),
                                    Color(0xFF16120C)
                                )
                            ),
                            CutCornerShape(7.dp)
                        )
                        .border(
                            1.dp,
                            Brush.verticalGradient(
                                listOf(Color.Black, Color(0xFF59482D), Color.Black)
                            ),
                            CutCornerShape(7.dp)
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items.forEachIndexed { index, item ->
                        val selected = selectedRoute == item.route
                        val scale by animateFloatAsState(
                            targetValue = if (selected) 1.035f else 1f,
                            animationSpec = tween(180),
                            label = "fantasyNavScale"
                        )
                        val lift by animateFloatAsState(
                            targetValue = if (selected) -1.5f else 0f,
                            animationSpec = tween(180),
                            label = "fantasyNavLift"
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(66.dp)
                                .offset(y = lift.dp)
                                .scale(scale)
                                .then(
                                    if (selected) {
                                        Modifier
                                            .background(
                                                Brush.verticalGradient(
                                                    listOf(
                                                        Color(0xFF235C2B),
                                                        Color(0xFF123A1A),
                                                        Color(0xFF081D0C)
                                                    )
                                                ),
                                                CutCornerShape(7.dp)
                                            )
                                            .border(
                                                1.5.dp,
                                                Brush.verticalGradient(
                                                    listOf(
                                                        Color(0xFFFFE69A),
                                                        Color(0xFFC98A26),
                                                        Color(0xFF69400D)
                                                    )
                                                ),
                                                CutCornerShape(7.dp)
                                            )
                                    } else Modifier
                                )
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { onSelected(item) },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label,
                                    tint = if (selected) Color(0xFFFFE7A0) else Color(0xFFD0A85D),
                                    modifier = Modifier.size(if (selected) 29.dp else 27.dp)
                                )
                                Text(
                                    text = item.label,
                                    color = if (selected) Color(0xFF54D970) else Ivory.copy(alpha = .76f),
                                    fontSize = 10.5.sp,
                                    lineHeight = 11.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
                                    maxLines = 1
                                )
                            }
                        }

                        if (index < items.lastIndex) {
                            FantasyDivider(
                                emphasized = false,
                                modifier = Modifier.height(48.dp)
                            )
                        }
                    }
                }
            }
        }

        // Petite émeraude centrale, sertie comme celle du titre.
        EmeraldNavGem(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-1).dp)
                .size(28.dp)
        )

        FrameCornerOrnament(
            Modifier
                .align(Alignment.BottomStart)
                .offset(x = 4.dp, y = (-5).dp)
                .size(12.dp)
        )
        FrameCornerOrnament(
            Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-4).dp, y = (-5).dp)
                .size(12.dp)
        )
    }
}

@Composable
private fun FantasyDivider(
    emphasized: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.width(7.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(46.dp)) {
            val centerX = size.width / 2f
            drawLine(
                brush = Brush.verticalGradient(
                    listOf(Color.Transparent, Color(0xFFE8B950), Color(0xFF6A4212), Color.Transparent)
                ),
                start = androidx.compose.ui.geometry.Offset(centerX, 4f),
                end = androidx.compose.ui.geometry.Offset(centerX, size.height - 4f),
                strokeWidth = 1.1.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawCircle(
                color = Color(0xFFD9A13E),
                radius = 1.8.dp.toPx(),
                center = center
            )
            drawCircle(
                color = Color(0xFF281707),
                radius = .7.dp.toPx(),
                center = center
            )
        }
    }
}

@Composable
private fun EmeraldNavGem(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.shadow(6.dp, RoundedCornerShape(8.dp), clip = false)) {
        val w = size.width
        val h = size.height

        val setting = Path().apply {
            moveTo(w * .50f, 0f)
            lineTo(w, h * .50f)
            lineTo(w * .50f, h)
            lineTo(0f, h * .50f)
            close()
        }
        drawPath(
            setting,
            Brush.linearGradient(
                listOf(Color(0xFFFFEAA0), Color(0xFFB97C20), Color(0xFF4E2D08))
            )
        )
        drawPath(setting, Color(0xFFFFF3BC), style = Stroke(1.dp.toPx()))

        val gem = Path().apply {
            moveTo(w * .50f, h * .16f)
            lineTo(w * .82f, h * .50f)
            lineTo(w * .50f, h * .84f)
            lineTo(w * .18f, h * .50f)
            close()
        }
        drawPath(
            gem,
            Brush.linearGradient(
                listOf(Color(0xFF9BFFAA), Color(0xFF1D963F), Color(0xFF06431D), Color(0xFF071A0E))
            )
        )
        drawPath(gem, Color(0xFFC2FFCA), style = Stroke(.8.dp.toPx()))
        drawLine(
            Color.White.copy(alpha = .62f),
            androidx.compose.ui.geometry.Offset(w * .34f, h * .34f),
            androidx.compose.ui.geometry.Offset(w * .50f, h * .21f),
            1.2.dp.toPx(),
            StrokeCap.Round
        )
    }
}

@Composable
private fun FrameCornerOrnament(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .graphicsLayer { rotationZ = 45f }
            .background(
                Brush.linearGradient(listOf(Color(0xFFFFD66A), Color(0xFF8B5916), Color(0xFF3E2408))),
                RoundedCornerShape(3.dp)
            )
            .border(.8.dp, Color(0xFFFFE8A0), RoundedCornerShape(3.dp)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(4.dp)
                .background(Color(0xFF211406), RoundedCornerShape(1.dp))
        )
    }
}
