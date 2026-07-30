package com.runeprofittouch.app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.runeprofittouch.app.data.ServerStore

@Composable
fun HomeScreen(
    onOpenCalculator: () -> Unit,
    onOpenResources: () -> Unit,
    onOpenRunes: () -> Unit,
    onOpenServer: () -> Unit
) {
    val server by ServerStore.selectedServer.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "RuneProfit Touch",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text("Serveur actif : $server", style = MaterialTheme.typography.titleMedium)
        Text(
            text = "Analysez vos crafts et votre rentabilité.",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.height(32.dp))

        Button(onClick = onOpenCalculator, modifier = Modifier.fillMaxWidth()) {
            Text("Rechercher et analyser un craft")
        }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = onOpenResources, modifier = Modifier.fillMaxWidth()) {
            Text("Prix des ressources")
        }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = onOpenRunes, modifier = Modifier.fillMaxWidth()) {
            Text("Prix des runes")
        }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = onOpenServer, modifier = Modifier.fillMaxWidth()) {
            Text("Changer de serveur")
        }
    }
}
