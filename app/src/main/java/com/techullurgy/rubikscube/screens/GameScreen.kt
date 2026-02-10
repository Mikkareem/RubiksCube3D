package com.techullurgy.rubikscube.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.techullurgy.rubikscube.Dependency
import com.techullurgy.rubikscube.RubiksCubeGameView
import com.techullurgy.rubikscube.components.CameraGears
import com.techullurgy.rubikscube.components.ControlPanel
import com.techullurgy.rubikscube.utils.GameMove

val primaryColor = Color(0xFFF06B0C)
val accent1Color = Color(0xFF4C7DF8)
val accent2Color = Color(0xFF9B55EC)
val accentGreenColor = Color(0xFF104404)

@Composable
fun GameScreen() {

    val game = remember { Dependency.game }

    val isSolved by game.gameState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        var surfaceView by remember {
            mutableStateOf<RubiksCubeGameView?>(null)
        }

        GameView(
            onView = { surfaceView = it },
            onResetView = { surfaceView = it },
            onReleaseView = { surfaceView = null },
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        )

        GameControlsView(
            isSolved = isSolved,
            onRotateCameraX = { surfaceView?.rotateCameraX(it) },
            onRotateCameraY = { surfaceView?.rotateCameraY(it) },
            onRotateCameraZ = { surfaceView?.rotateCameraZ(it) },
            onTurn = {
                surfaceView?.turn(it)
            },
            onAnimatedTurn = { move, value ->
                surfaceView?.animateTurn(move, value)
            },
            onScramble = {
                scrambleMoves.forEach {
                    surfaceView?.turn(it)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(32.dp)
        )
    }
}

@Composable
private fun GameView(
    onView: (RubiksCubeGameView) -> Unit,
    onResetView: (RubiksCubeGameView) -> Unit,
    onReleaseView: () -> Unit,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { RubiksCubeGameView(context = it).also(onView) },
        onReset = onResetView,
        onRelease = { onReleaseView() },
        modifier = modifier
    )
}

@Composable
private fun GameControlsView(
    isSolved: Boolean,
    onRotateCameraX: (Float) -> Unit,
    onRotateCameraY: (Float) -> Unit,
    onRotateCameraZ: (Float) -> Unit,
    onTurn: (GameMove) -> Unit,
    onAnimatedTurn: (GameMove, Float) -> Unit,
    onScramble: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AnimatedVisibility(isSolved) {
            GameState(
                onScramble = onScramble
            )
        }
        CameraGears(
            onRotateCameraX = onRotateCameraX,
            onRotateCameraY = onRotateCameraY,
            onRotateCameraZ = onRotateCameraZ,
        )
        ControlPanel(
            onTurn = onTurn,
            onAnimatedTurn = onAnimatedTurn
        )
    }
}

@Composable
private fun GameState(
    onScramble: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26f))
            .background(accentGreenColor)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = buildAnnotatedString {
                val spanStyle = SpanStyle(fontWeight = FontWeight.Bold, color = primaryColor)
                append("The cube is in ")
                withStyle(spanStyle) {
                    append("Solved ")
                }
                append("state, ")
                withStyle(spanStyle) {
                    append("Scramble now ")
                }
                append("to play again")
            },
            fontSize = 14.sp,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )
        ScrambleButton(onScramble)
    }
}

@Composable
private fun ScrambleButton(
    onScrambleClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20f))
            .background(primaryColor)
            .padding(4.dp)
            .clickable(onClick = onScrambleClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            ScrambleIcon(Modifier.size(32.dp))
            Text("Scramble", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

//private val scrambleMoves = List(20) { GameMove.entries.random() }
private val scrambleMoves = listOf(
    GameMove.BACK_CLOCKWISE,
    GameMove.DOWN_CLOCKWISE,
    GameMove.UP_ANTICLOCKWISE
)

@Composable
private fun ScrambleIcon(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {

        val stroke = Stroke(width = 1.8f.toScaled(), cap = StrokeCap.Round, join = StrokeJoin.Round)

        // === Cube faces ===
        val cubePath = Path().apply {
            moveTo(7f.toScaledX(), 8f.toScaledY())
            lineTo(12f.toScaledX(), 6f.toScaledY())
            lineTo(17f.toScaledX(), 8f.toScaledY())
            lineTo(17f.toScaledX(), 14f.toScaledY())
            lineTo(12f.toScaledX(), 16f.toScaledY())
            lineTo(7f.toScaledX(), 14f.toScaledY())
            close()
        }

        val cubeEdgePath = Path().apply {
            moveTo(12f.toScaledX(), 10f.toScaledY())
            lineTo(12f.toScaledX(), 15f.toScaledY())
        }

        // === Shuffle arrows (bottom-left) ===
        val shuffleBottomCurve = Path().apply {
            moveTo(4f.toScaledX(), 15f.toScaledY())
            cubicTo(
                4f.toScaledX(), 17.2f.toScaledY(),   // control point 1
                5.8f.toScaledX(), 19f.toScaledY(),   // control point 2
                8f.toScaledX(), 19f.toScaledY()      // end
            )
            lineTo(10f.toScaledX(), 19f.toScaledY())
        }

        val shuffleBottomArrow = Path().apply {
            moveTo(8f.toScaledX(), 21f.toScaledY())
            lineTo(10f.toScaledX(), 19f.toScaledY())
            lineTo(8f.toScaledX(), 17f.toScaledY())
        }

        // === Shuffle arrows (top-right) ===
        val shuffleTopCurve = Path().apply {
            moveTo(20f.toScaledX(), 7f.toScaledY())
            cubicTo(
                20f.toScaledX(), 4.8f.toScaledY(),   // control point 1
                18.2f.toScaledX(), 3f.toScaledY(),   // control point 2
                16f.toScaledX(), 3f.toScaledY()      // end
            )
            lineTo(14f.toScaledX(), 3f.toScaledY())
        }

        val shuffleTopArrow = Path().apply {
            moveTo(16f.toScaledX(), 1f.toScaledY())
            lineTo(14f.toScaledX(), 3f.toScaledY())
            lineTo(16f.toScaledX(), 5f.toScaledY())
        }

        // === Draw ===
        drawPath(cubePath, color = Color.Black, style = stroke)
        drawPath(cubeEdgePath, color = Color.Black, style = stroke)

        drawPath(shuffleBottomCurve, color = Color.Black, style = stroke)
        drawPath(shuffleBottomArrow, color = Color.Black, style = stroke)

        drawPath(shuffleTopCurve, color = Color.Black, style = stroke)
        drawPath(shuffleTopArrow, color = Color.Black, style = stroke)
    }
}

context(scope: DrawScope) private fun Float.toScaledX(): Float {
    val current = this
    return with(scope) {
        val scaleFactor = size.width / 24.dp.toPx()
        current.dp.toPx() * scaleFactor
    }
}

context(scope: DrawScope) private fun Float.toScaledY(): Float {
    val current = this
    return with(scope) {
        val scaleFactor = size.height / 24.dp.toPx()
        current.dp.toPx() * scaleFactor
    }
}

context(scope: DrawScope) private fun Float.toScaled(): Float {
    val current = this
    return with(scope) {
        val scaleFactor = size.minDimension / 24.dp.toPx()
        current.dp.toPx() * scaleFactor
    }
}

@Preview
@Composable
private fun ControlsPreview() {
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(.5f)
        )

        GameControlsView(
            isSolved = false,
            onRotateCameraX = {},
            onRotateCameraY = {},
            onRotateCameraZ = {},
            onTurn = {},
            onAnimatedTurn = { _, _ -> },
            onScramble = {},
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(32.dp)
        )
    }
}