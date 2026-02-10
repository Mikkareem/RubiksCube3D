package com.techullurgy.rubikscube

import android.content.Context
import android.opengl.GLSurfaceView
import com.techullurgy.rubikscube.game.RubiksCubeGame
import com.techullurgy.rubikscube.utils.GameMove
import com.techullurgy.rubikscube.utils.Move
import kotlin.math.abs

object Dependency {
    val game: RubiksCubeGame by lazy { RubiksCubeGame() }
}

class RubiksCubeGameView(
    context: Context
) : GLSurfaceView(context) {
    private val renderer: RubiksCubeGameViewRenderer

    private val game = Dependency.game

    init {
        setEGLContextClientVersion(3)
        renderer = RubiksCubeGameViewRenderer()
        setRenderer(renderer)

        renderMode = RENDERMODE_WHEN_DIRTY
    }

    fun rotateCameraX(angle: Float) {
        renderer.rotateCameraX(angle)
        requestRender()
    }

    fun rotateCameraY(angle: Float) {
        renderer.rotateCameraY(angle)
        requestRender()
    }

    fun rotateCameraZ(angle: Float) {
        renderer.rotateCameraZ(angle)
        requestRender()
    }

    fun turn(gameMove: GameMove) {
        val move = Move.fromGameMove(gameMove)

        if (abs(move.x) > 0) {
            renderer.turnX(move.x, move.dir)
        } else if (abs(move.y) > 0) {
            renderer.turnY(move.y, move.dir)
        } else if (abs(move.z) > 0) {
            renderer.turnZ(move.z, move.dir)
        }
        requestRender()

        game.move(gameMove)
    }

    fun animateTurn(gameMove: GameMove, angle: Float) {
        val move = Move.fromGameMove(gameMove)
        renderer.updateCurrentMove(move, angle)
        requestRender()
    }
}