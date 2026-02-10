package com.techullurgy.rubikscube.shapes

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import com.techullurgy.gameengine.Transformation
import dev.romainguy.kotlin.math.Float3
import dev.romainguy.kotlin.math.translation
import kotlin.math.roundToInt

class Cube(
    private var position: Float3,
) {
    private val matrix get() = translation(position)

    val x: Int get() = position.x.roundToInt()
    val y: Int get() = position.y.roundToInt()
    val z: Int get() = position.z.roundToInt()

    private val faces = listOf(
        Face(Float3(0f, 0f, 1f), Color.Red),
        Face(Float3(0f, 0f, -1f), Color.Orange),
        Face(Float3(0f, 1f, 0f), Color.White),
        Face(Float3(0f, -1f, 0f), Color.Yellow),
        Face(Float3(1f, 0f, 0f), Color.Blue),
        Face(Float3(-1f, 0f, 0f), Color.Green)
    )

    fun turnFacesX(dir: Int) {
        faces.forEach { it.turnX(dir * 90f) }
    }

    fun turnFacesY(dir: Int) {
        faces.forEach { it.turnY(dir * 90f) }
    }

    fun turnFacesZ(dir: Int) {
        faces.forEach { it.turnZ(dir * 90f) }
    }

    fun update(pos: Float3) {
        position = pos.copy()
    }

    fun draw() {
        Transformation.push()
        Transformation.applyMatrix(matrix)
        faces.forEach { it.show() }
        Transformation.pop()
    }
}

@Stable
private val Color.Companion.Orange get() = Color(0xFFFF7F00)