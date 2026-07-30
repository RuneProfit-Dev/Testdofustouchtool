package com.runeprofittouch.app.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
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
import com.runeprofittouch.app.ui.theme.AntiqueGold
import com.runeprofittouch.app.ui.theme.BrightGold
import com.runeprofittouch.app.ui.theme.FantasyScreenHeader
import com.runeprofittouch.app.ui.theme.Ivory
import com.runeprofittouch.app.ui.theme.LuxuryBackground
import com.runeprofittouch.app.ui.theme.LuxuryCard

@Composable
fun SettingsScreen() {
    var showServerSelection by remember { mutableStateOf(false) }
    val selectedServer by ServerStore.selectedServer.collectAsState()

    LuxuryBackground(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            FantasyScreenHeader(
                titleDrawable = R.drawable.title_parametres,
                serverName = selectedServer,
                modifier = Modifier.padding(top = 6.dp)
            )

            if (showServerSelection) {
                ServerSelectionPage(onBack = { showServerSelection = false })
            } else {
                MoreHomePage(
                    selectedServer = selectedServer,
                    onServerClick = { showServerSelection = true }
                )
            }
        }
    }
}

@Composable
private fun MoreHomePage(
    selectedServer: String,
    onServerClick: () -> Unit
) {
    LuxuryCard(
        modifier = Modifier.fillMaxWidth(),
        accent = AntiqueGold,
        onClick = onServerClick,
        corner = 16.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Dns, contentDescription = null, tint = BrightGold)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 14.dp)
            ) {
                Text("Serveur", color = BrightGold, fontWeight = FontWeight.Bold)
                Text(selectedServer, color = Ivory.copy(alpha = 0.78f))
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = BrightGold)
        }
    }
}

@Composable
private fun ServerSelectionPage(onBack: () -> Unit) {
    val context = LocalContext.current
    val selectedServer by ServerStore.selectedServer.collectAsState()

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        LuxuryCard(
            modifier = Modifier.fillMaxWidth(),
            accent = AntiqueGold,
            onClick = onBack,
            corner = 14.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 13.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Retour", tint = BrightGold)
                Text(
                    "Sélection du serveur",
                    color = BrightGold,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
        }

        Text(
            "Chaque serveur possède ses propres prix de ressources, de runes et d’objets.",
            color = Ivory.copy(alpha = 0.82f)
        )

        ServerStore.servers.forEach { server ->
            LuxuryCard(
                modifier = Modifier.fillMaxWidth(),
                accent = if (selectedServer == server) BrightGold else AntiqueGold,
                onClick = { ServerStore.select(context, server) },
                corner = 14.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedServer == server,
                        onClick = { ServerStore.select(context, server) }
                    )
                    Text(server, color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
