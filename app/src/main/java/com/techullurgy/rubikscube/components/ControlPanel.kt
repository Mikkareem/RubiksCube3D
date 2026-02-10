package com.techullurgy.rubikscube.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.techullurgy.rubikscube.screens.accent1Color
import com.techullurgy.rubikscube.screens.accent2Color
import com.techullurgy.rubikscube.screens.primaryColor
import com.techullurgy.rubikscube.utils.GameMove
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ControlPanel(
    onTurn: (GameMove) -> Unit,
    onAnimatedTurn: (GameMove, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val moveAngle = remember { Animatable(0f) }

        val scope = rememberCoroutineScope()

        val onControlClick: (GameMove) -> Unit = {
            scope.launch {
                try {
                    moveAngle.snapTo(0f)
                    moveAngle.animateTo(90f) {
                        onAnimatedTurn(it, value)
                    }
                } catch (e: Exception) {
                    throw e
                } finally {
                    onTurn(it)
                }
            }
        }

        ProvideTextStyle(
            LocalTextStyle.current.copy(
                color = Color.White,
                fontWeight = FontWeight.ExtraBold
            )
        ) {

            RotatingGradientRing(
                direction = -1f,
                colorStops = { primaryColor.colorStops },
                modifier = Modifier
                    .weight(1f)
            ) {
                Control("F'", accent1Color, { onControlClick(GameMove.FRONT_ANTICLOCKWISE) })
                Control("U'", accent1Color, { onControlClick(GameMove.UP_ANTICLOCKWISE) })
                Control("R'", accent1Color, { onControlClick(GameMove.RIGHT_ANTICLOCKWISE) })
                Control("B'", accent1Color, { onControlClick(GameMove.BACK_ANTICLOCKWISE) })
                Control("D'", accent1Color, { onControlClick(GameMove.DOWN_ANTICLOCKWISE) })
                Control("L'", accent1Color, { onControlClick(GameMove.LEFT_ANTICLOCKWISE) })
            }

            Spacer(Modifier.width(36.dp))

            RotatingGradientRing(
                direction = 1f,
                colorStops = { primaryColor.colorStops.reversed() },
                modifier = Modifier.weight(1f)
            ) {
                Control("F", accent2Color, { onControlClick(GameMove.FRONT_CLOCKWISE) })
                Control("U", accent2Color, { onControlClick(GameMove.UP_CLOCKWISE) })
                Control("R", accent2Color, { onControlClick(GameMove.RIGHT_CLOCKWISE) })
                Control("B", accent2Color, { onControlClick(GameMove.BACK_CLOCKWISE) })
                Control("D", accent2Color, { onControlClick(GameMove.DOWN_CLOCKWISE) })
                Control("L", accent2Color, { onControlClick(GameMove.LEFT_CLOCKWISE) })
            }

        }
    }
}

@Composable
private fun Control(
    name: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(color)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(name, fontSize = 24.sp)
    }
}


private val Color.colorStops
    get() = listOf(
        this,
        copy(alpha = 0.6f),
        copy(alpha = 0.2f),
        Color.Transparent
    )


@Composable
private fun RotatingGradientRing(
    modifier: Modifier = Modifier,
    direction: Float = -1f,
    colorStops: () -> List<Color> = { accent1Color.colorStops },
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = direction * 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1500,
                easing = LinearEasing
            )
        ),
        label = "rotationAnim"
    )

    Box(
        modifier = modifier
            .drawBehind {
                val strokePx = size.minDimension * .105f
                val radius = size.minDimension / 2f - strokePx / 2f

                rotate(rotation) {
                    drawCircle(
                        brush = Brush.sweepGradient(colorStops()),
                        radius = radius,
                        style = Stroke(width = strokePx, cap = StrokeCap.Round)
                    )
                }
            },
        content = {
            CircularLayout {
                content()
            }
        }
    )
}

@Composable
private fun CircularLayout(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->

        val placeables = measurables.map {
            it.measure(constraints.copy(minWidth = 0, minHeight = 0))
        }

        val maxMinDimension = minOf(constraints.maxWidth, constraints.maxHeight)

        val width = maxMinDimension
        val height = maxMinDimension

        val centerX = width / 2
        val centerY = height / 2

        val radiusPx = minOf(centerX, centerY)

        layout(width, height) {

            placeables.forEachIndexed { index, placeable ->

                val angle = (2 * Math.PI / placeables.size) * index - Math.PI / 2

                val x = centerX + (radiusPx * cos(angle)).toInt() - placeable.width / 2
                val y = centerY + (radiusPx * sin(angle)).toInt() - placeable.height / 2

                placeable.place(x, y)
            }
        }
    }
}
