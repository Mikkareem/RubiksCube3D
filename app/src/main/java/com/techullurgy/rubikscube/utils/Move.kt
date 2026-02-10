package com.techullurgy.rubikscube.utils

data class Move(val x: Int, val y: Int, val z: Int, val dir: Int) {
    var angle = 0f

    fun update(angle: Float) {
        this.angle = angle
    }

    companion object {
        fun fromGameMove(move: GameMove): Move {
            return when (move) {
                GameMove.FRONT_CLOCKWISE -> Move(-1, 0, 0, -1)
                GameMove.FRONT_ANTICLOCKWISE -> Move(-1, 0, 0, 1)
                GameMove.BACK_CLOCKWISE -> Move(1, 0, 0, -1)
                GameMove.BACK_ANTICLOCKWISE -> Move(1, 0, 0, 1)
                GameMove.UP_CLOCKWISE -> Move(0, -1, 0, -1)
                GameMove.UP_ANTICLOCKWISE -> Move(0, -1, 0, 1)
                GameMove.DOWN_CLOCKWISE -> Move(0, 1, 0, -1)
                GameMove.DOWN_ANTICLOCKWISE -> Move(0, 1, 0, 1)
                GameMove.LEFT_CLOCKWISE -> Move(0, 0, -1, 1)
                GameMove.LEFT_ANTICLOCKWISE -> Move(0, 0, -1, -1)
                GameMove.RIGHT_CLOCKWISE -> Move(0, 0, 1, 1)
                GameMove.RIGHT_ANTICLOCKWISE -> Move(0, 0, 1, -1)
            }
        }
    }
}
