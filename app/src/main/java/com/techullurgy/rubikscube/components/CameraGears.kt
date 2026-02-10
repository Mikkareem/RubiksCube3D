package com.techullurgy.rubikscube.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toOffset
import com.techullurgy.rubikscube.screens.primaryColor
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

private val GearColor = Color.White

@Composable
fun CameraGears(
    onRotateCameraX: (Float) -> Unit,
    onRotateCameraY: (Float) -> Unit,
    onRotateCameraZ: (Float) -> Unit,
    modifier: Modifier = Modifier
) {

    val xAngleAnimatable = remember { Animatable(0f) }
    val yAngleAnimatable = remember { Animatable(0f) }
    val zAngleAnimatable = remember { Animatable(0f) }

    val scope = rememberCoroutineScope()

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(28.dp)
    ) {
        Gear(
            degree = xAngleAnimatable.value,
            onDegreeChange = { degree ->
                scope.launch {
                    if (degree == 0f) {
                        xAngleAnimatable.animateTo(degree) {
                            onRotateCameraX(value)
                        }
                    } else {
                        xAngleAnimatable.snapTo(degree)
                        onRotateCameraX(xAngleAnimatable.value)
                    }
                }
            },
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f)
        )
        Gear(
            degree = yAngleAnimatable.value,
            onDegreeChange = { degree ->
                scope.launch {
                    if (degree == 0f) {
                        yAngleAnimatable.animateTo(degree) {
                            onRotateCameraY(value)
                        }
                    } else {
                        yAngleAnimatable.snapTo(degree)
                        onRotateCameraY(yAngleAnimatable.value)
                    }
                }
            },
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f)
        )
        Gear(
            degree = zAngleAnimatable.value,
            onDegreeChange = { degree ->
                scope.launch {
                    if (degree == 0f) {
                        zAngleAnimatable.animateTo(degree) {
                            onRotateCameraZ(value)
                        }
                    } else {
                        zAngleAnimatable.snapTo(degree)
                        onRotateCameraZ(zAngleAnimatable.value)
                    }
                }
            },
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f)
        )
    }
}

@Composable
private fun Gear(
    degree: Float,
    onDegreeChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isGearDragging by remember { mutableStateOf(false) }

    var startedDegree by remember { mutableFloatStateOf(0f) }

    val accumulatedDegree = degree + startedDegree

    val onGearDragCancel = {
        isGearDragging = false
        startedDegree = 0f
        onDegreeChange(0f)
    }

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        val translatedPos = it - size.center.toOffset()
                        val theta = atan2(translatedPos.y, translatedPos.x) * (180f / PI.toFloat())
                        startedDegree = theta
                    },
                    onDragEnd = onGearDragCancel,
                    onDragCancel = onGearDragCancel
                ) { change, _ ->
                    isGearDragging = true
                    val translatedPos = change.position - size.center.toOffset()
                    val theta = atan2(translatedPos.y, translatedPos.x) * (180f / PI.toFloat())
                    onDegreeChange(theta - startedDegree)
                }
            }
            .drawBehind {
                drawCircle(
                    color = primaryColor,
                    style = Stroke(10.dp.toPx())
                )

                if (isGearDragging) {
                    val unitX = cos(Math.toRadians(accumulatedDegree.toDouble())).toFloat()
                    val unitY = sin(Math.toRadians(accumulatedDegree.toDouble())).toFloat()

                    val scaleFactor = 150f

                    val pos = Offset(unitX * scaleFactor, unitY * scaleFactor)

                    val path = Path().apply {
                        moveTo(center.x, center.y)
                        relativeLineTo(pos.x, pos.y)
                    }

                    drawPath(
                        path = path,
                        style = Stroke(7.dp.toPx()),
                        color = GearColor
                    )

                    drawCircle(
                        color = GearColor,
                        center = center + pos,
                        radius = 15.dp.toPx()
                    )
                } else {
                    drawCircle(
                        color = GearColor,
                        radius = 15.dp.toPx()
                    )
                }
            }
    )
}
