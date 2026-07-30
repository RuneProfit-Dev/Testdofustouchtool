package com.runeprofittouch.app.ui.theme

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.ui.draw.drawBehind
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.runeprofittouch.app.R

@Composable
fun LuxuryBackground(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    Box(modifier = modifier.background(Color(0xFF070809))) {
        Image(
            painter = painterResource(R.drawable.stone_background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.34f),
                            Color(0xFF090A0B).copy(alpha = 0.48f),
                            Color.Black.copy(alpha = 0.42f)
                        )
                    )
                )
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(AntiqueGold.copy(alpha = 0.09f), Color.Transparent),
                            center = Offset(size.width * 0.20f, size.height * 0.10f),
                            radius = size.minDimension * 0.82f
                        ),
                        radius = size.minDimension * 0.82f,
                        center = Offset(size.width * 0.20f, size.height * 0.10f)
                    )
                },
            content = content
        )
    }
}

@Composable
fun LuxuryCard(
    modifier: Modifier = Modifier,
    accent: Color = MaterialTheme.colorScheme.primary,
    onClick: (() -> Unit)? = null,
    corner: Dp = 20.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.985f else 1f, tween(120), label = "cardScale")
    val shape = RoundedCornerShape(corner)
    val clickableModifier = if (onClick != null) {
        Modifier.clickable(interactionSource = interaction, indication = null, onClick = onClick)
    } else Modifier

    Box(
        modifier = modifier
            .scale(scale)
            .graphicsLayer {
                shadowElevation = 18.dp.toPx()
                this.shape = shape
                clip = false
                ambientShadowColor = accent.copy(alpha = 0.22f)
                spotShadowColor = Color.Black
            }
            .clip(shape)
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF1C1A16), Color(0xFF0A0B0E), Color(0xFF111215))
                )
            )
            .border(
                BorderStroke(
                    1.dp,
                    Brush.linearGradient(
                        listOf(accent.copy(alpha = 0.70f), Color.White.copy(alpha = 0.08f), accent.copy(alpha = 0.18f))
                    )
                ),
                shape
            )
            .then(clickableModifier)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.045f), Color.Transparent)
                    )
                )
                .padding(0.dp)
        )
        content()
    }
}


@Composable
fun FantasyScreenHeader(
    titleDrawable: Int,
    serverName: String,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth()) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(titleDrawable),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(112.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(
                    Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color.Transparent, BrightGold.copy(alpha = 0.9f))
                            )
                        )
                )
                Text(
                    text = serverName,
                    color = BrightGold,
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
                )
                Spacer(
                    Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(BrightGold.copy(alpha = 0.9f), Color.Transparent)
                            )
                        )
                )
            }
        }
    }
}
