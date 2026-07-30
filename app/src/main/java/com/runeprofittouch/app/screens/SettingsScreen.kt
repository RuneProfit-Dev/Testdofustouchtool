package com.runeprofittouch.app.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.runeprofittouch.app.R
import com.runeprofittouch.app.data.ServerStore
import com.runeprofittouch.app.data.ThemeStore
import com.runeprofittouch.app.ui.theme.LuxuryBackground
import com.runeprofittouch.app.ui.theme.LuxuryCard
import com.runeprofittouch.app.ui.theme.FantasyScreenHeader
import com.runeprofittouch.app.ui.theme.AntiqueGold
import com.runeprofittouch.app.ui.theme.BrightGold

@Composable
fun SettingsScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    val selectedServer by ServerStore.selectedServer.collectAsState()

    LuxuryBackground(Modifier.fillMaxSize()) {
    Column(modifier = Modifier.fillMaxSize()) {
        FantasyScreenHeader(
            titleDrawable = R.drawable.title_parametres,
            serverName = selectedServer,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
        TabRow(selectedTabIndex = selectedTab, containerColor = MaterialTheme.colorScheme.surface, contentColor = BrightGold) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Serveur") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Personnaliser") }
            )
        }

        when (selectedTab) {
            0 -> ServerSettings()
            else -> ThemeSettings()
        }
    }
    }
}

@Composable
private fun ServerSettings() {
    val context = LocalContext.current
    val selectedServer by ServerStore.selectedServer.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Chaque serveur possède ses propres prix de ressources, de runes et d’objets.",
            color = Color.White
        )
        ServerStore.servers.forEach { server ->
            Card(
                modifier = Modifier.fillMaxWidth().clickable {
                    ServerStore.select(context, server)
                }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedServer == server,
                        onClick = { ServerStore.select(context, server) }
                    )
                    Text(server, style = MaterialTheme.typography.titleMedium, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun ThemeSettings() {
    val context = LocalContext.current
    val selectedColor by ThemeStore.primaryColor.collectAsState()
    val darkMode by ThemeStore.darkMode.collectAsState()
    var hexValue by remember(selectedColor) {
        mutableStateOf("#%06X".format(selectedColor and 0xFFFFFF))
    }
    var invalidHex by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Choisissez la couleur des contours, boutons, sélections et icônes.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    ThemeStore.selectDarkMode(context, !darkMode)
                },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Thème sombre",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Fond sombre avec cadrages et cartes teintés.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Switch(
                    checked = darkMode,
                    onCheckedChange = {
                        ThemeStore.selectDarkMode(context, it)
                    }
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(ThemeStore.presets) { (name, value) ->
                Card(
                    onClick = { ThemeStore.select(context, value) },
                    shape = CircleShape,
                    border = if (selectedColor == value) {
                        BorderStroke(3.dp, MaterialTheme.colorScheme.onSurface)
                    } else {
                        null
                    },
                    colors = CardDefaults.cardColors(
                        containerColor = Color(value.toInt())
                    ),
                    modifier = Modifier.size(58.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = name.take(1),
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        OutlinedTextField(
            value = hexValue,
            onValueChange = {
                hexValue = it.take(7)
                invalidHex = false
            },
            label = { Text("Couleur personnalisée") },
            supportingText = {
                Text(
                    if (invalidHex) "Format invalide. Exemple : #7B1FA2"
                    else "Format hexadécimal, par exemple #7B1FA2"
                )
            },
            isError = invalidHex,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                val parsed = ThemeStore.parseHex(hexValue)
                invalidHex = parsed == null
                parsed?.let { ThemeStore.select(context, it) }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Appliquer la couleur")
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color(selectedColor.toInt()), CircleShape)
            )
            Text("Aperçu du thème sélectionné", color = Color.White)
        }
    }
}
