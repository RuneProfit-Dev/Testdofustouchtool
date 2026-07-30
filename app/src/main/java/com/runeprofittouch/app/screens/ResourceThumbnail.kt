package com.runeprofittouch.app.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent

@Composable
fun ResourceThumbnail(
    imageUrl: String,
    contentDescription: String,
    size: Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl.isBlank()) {
            ResourcePlaceholder(contentDescription)
        } else {
            SubcomposeAsyncImage(
                model = imageUrl,
                contentDescription = contentDescription,
                contentScale = ContentScale.Fit,
                loading = { ResourcePlaceholder(contentDescription) },
                error = { ResourcePlaceholder(contentDescription) },
                success = { SubcomposeAsyncImageContent() },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun ResourcePlaceholder(contentDescription: String) {
    Icon(
        imageVector = Icons.Filled.Inventory2,
        contentDescription = contentDescription,
        tint = MaterialTheme.colorScheme.primary
    )
}
