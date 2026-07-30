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
    val frameShape = CutCornerShape(topStart = 15.dp, topEnd = 15.dp, bottomStart = 15.dp, bottomEnd = 15.dp)
    val innerShape = CutCornerShape(topStart = 11.dp, topEnd = 11.dp, bottomStart = 11.dp, bottomEnd = 11.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(106.dp)
            .background(Color.Transparent)
            .padding(horizontal = 7.dp, vertical = 3.dp)
    ) {
        // Ombre massive + cadre extérieur en or vieilli.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp)
                .align(Alignment.TopCenter)
                .shadow(18.dp, frameShape, clip = false)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF6C4A17),
                            Color(0xFFE2B64D),
                            Color(0xFF8A5A18),
                            Color(0xFF3B260C)
                        )
                    ),
                    frameShape
                )
                .border(
                    1.dp,
                    Brush.verticalGradient(
                        listOf(Color(0xFFFFE9A6), Color(0xFFB67B20), Color(0xFF5A350D))
                    ),
                    frameShape
                )
                .padding(3.dp)
        ) {
            // Deuxième contour sculpté.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(Color(0xFF120E09), innerShape)
                    .border(
                        2.dp,
                        Brush.verticalGradient(
                            listOf(Color(0xFF4B2E0B), BrightGold, AntiqueGold, Color(0xFF3A230A))
                        ),
                        innerShape
                    )
                    .padding(3.dp)
            ) {
                // Pierre noire continue avec reflet métallique et ombre intérieure simulée.
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF28251F),
                                    Color(0xFF121311),
                                    Color(0xFF080A09),
                                    Color(0xFF19150F)
                                )
                            ),
                            CutCornerShape(8.dp)
                        )
                        .border(
                            1.dp,
                            Brush.verticalGradient(
                                listOf(Color.Black.copy(alpha = .9f), Color(0xFF5C513A), Color.Black)
                            ),
                            CutCornerShape(8.dp)
                        )
                        .padding(horizontal = 4.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items.forEachIndexed { index, item ->
                        val selected = selectedRoute == item.route
                        val scale by animateFloatAsState(
                            targetValue = if (selected) 1.07f else 1f,
                            animationSpec = tween(durationMillis = 180),
                            label = "fantasyNavScale"
                        )
                        val lift by animateFloatAsState(
                            targetValue = if (selected) -3f else 0f,
                            animationSpec = tween(durationMillis = 180),
                            label = "fantasyNavLift"
                        )
                        val glow by animateFloatAsState(
                            targetValue = if (selected) 1f else 0f,
                            animationSpec = tween(durationMillis = 180),
                            label = "fantasyNavGlow"
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(70.dp)
                                .offset(y = lift.dp)
                                .scale(scale)
                                .then(
                                    if (selected) {
                                        Modifier
                                            .shadow(
                                                elevation = (8f * glow).dp,
                                                shape = CutCornerShape(9.dp),
                                                clip = false,
                                                ambientColor = Emerald.copy(alpha = .8f * glow),
                                                spotColor = Emerald.copy(alpha = .9f * glow)
                                            )
                                            .background(
                                                Brush.verticalGradient(
                                                    listOf(
                                                        Color(0xFF347138),
                                                        Color(0xFF16451F),
                                                        Color(0xFF09250F)
                                                    )
                                                ),
                                                CutCornerShape(9.dp)
                                            )
                                            .border(
                                                2.dp,
                                                Brush.verticalGradient(
                                                    listOf(
                                                        Color(0xFFFFE7A0),
                                                        Color(0xFFD59B32),
                                                        Color(0xFF70420E)
                                                    )
                                                ),
                                                CutCornerShape(9.dp)
                                            )
                                    } else Modifier
                                )
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { onSelected(item) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (selected) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .fillMaxWidth(.72f)
                                        .height(2.dp)
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(Color.Transparent, Color(0xFFB6FFB8), Color.Transparent)
                                            )
                                        )
                                )
                            }
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label,
                                    tint = if (selected) Color(0xFFFFE9A8) else Color(0xFFD4AE62),
                                    modifier = Modifier.size(if (selected) 31.dp else 28.dp)
                                )
                                Text(
                                    text = item.label,
                                    color = if (selected) Color(0xFFFFF3CF) else Ivory.copy(alpha = .88f),
                                    fontSize = 11.sp,
                                    lineHeight = 12.sp,
                                    fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                    maxLines = 1
                                )
                            }
                        }

                        if (index < items.lastIndex) {
                            FantasyDivider(
                                emphasized = index == 1,
                                modifier = Modifier.height(58.dp)
                            )
                        }
                    }
                }
            }
        }

        // Émeraude centrale sertie, intégrée au cadre entre Ressources et Runes.
        EmeraldNavGem(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 1.dp)
                .size(44.dp)
        )

        // Rivets/coins décoratifs du cadre.
        FrameCornerOrnament(Modifier.align(Alignment.BottomStart).offset(x = 3.dp, y = (-4).dp))
        FrameCornerOrnament(Modifier.align(Alignment.BottomEnd).offset(x = (-3).dp, y = (-4).dp))
    }
}

@Composable
private fun FantasyDivider(
    emphasized: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.width(if (emphasized) 14.dp else 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(54.dp)) {
            val centerX = size.width / 2f
            drawLine(
                brush = Brush.verticalGradient(
                    listOf(Color.Transparent, Color(0xFFF0C564), Color(0xFF765018), Color.Transparent)
                ),
                start = androidx.compose.ui.geometry.Offset(centerX, 3f),
                end = androidx.compose.ui.geometry.Offset(centerX, size.height - 3f),
                strokeWidth = if (emphasized) 2.4.dp.toPx() else 1.3.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawCircle(
                color = Color(0xFFDCA94B),
                radius = if (emphasized) 3.2.dp.toPx() else 2.2.dp.toPx(),
                center = center
            )
            drawCircle(
                color = Color(0xFF241507),
                radius = if (emphasized) 1.3.dp.toPx() else .8.dp.toPx(),
                center = center
            )
        }
    }
}

@Composable
private fun EmeraldNavGem(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .shadow(12.dp, RoundedCornerShape(15.dp), clip = false)
    ) {
        val w = size.width
        val h = size.height
        val outer = Path().apply {
            moveTo(w * .50f, 0f)
            lineTo(w * .88f, h * .22f)
            lineTo(w, h * .58f)
            lineTo(w * .72f, h)
            lineTo(w * .28f, h)
            lineTo(0f, h * .58f)
            lineTo(w * .12f, h * .22f)
            close()
        }
        drawPath(
            path = outer,
            brush = Brush.verticalGradient(
                listOf(Color(0xFFFFE8A0), Color(0xFFB67A1E), Color(0xFF51300A))
            )
        )
        drawPath(path = outer, color = Color(0xFFFFF1B6), style = Stroke(width = 1.2.dp.toPx()))

        val gem = Path().apply {
            moveTo(w * .50f, h * .12f)
            lineTo(w * .78f, h * .29f)
            lineTo(w * .87f, h * .57f)
            lineTo(w * .66f, h * .87f)
            lineTo(w * .34f, h * .87f)
            lineTo(w * .13f, h * .57f)
            lineTo(w * .22f, h * .29f)
            close()
        }
        drawPath(
            path = gem,
            brush = Brush.linearGradient(
                listOf(Color(0xFF8CFF9C), Color(0xFF17873B), Color(0xFF063B1B), Color(0xFF0A2213))
            )
        )
        drawPath(path = gem, color = Color(0xFFB4FFC1), style = Stroke(width = 1.dp.toPx()))

        drawLine(
            color = Color.White.copy(alpha = .55f),
            start = androidx.compose.ui.geometry.Offset(w * .30f, h * .31f),
            end = androidx.compose.ui.geometry.Offset(w * .52f, h * .20f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color(0xFF063117),
            start = androidx.compose.ui.geometry.Offset(w * .50f, h * .13f),
            end = androidx.compose.ui.geometry.Offset(w * .50f, h * .85f),
            strokeWidth = .8.dp.toPx()
        )
    }
}

@Composable
private fun FrameCornerOrnament(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(17.dp)
            .graphicsLayer { rotationZ = 45f }
            .background(
                Brush.linearGradient(listOf(Color(0xFFFFD875), Color(0xFF875815), Color(0xFF3F2508))),
                RoundedCornerShape(4.dp)
            )
            .border(1.dp, Color(0xFFFFE9A0), RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(Color(0xFF201508), RoundedCornerShape(2.dp))
                .border(.7.dp, AntiqueGold, RoundedCornerShape(2.dp))
        )
    }
}

