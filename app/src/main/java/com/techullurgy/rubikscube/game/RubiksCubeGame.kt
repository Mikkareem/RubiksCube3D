package com.techullurgy.rubikscube.game

import com.techullurgy.rubikscube.utils.GameMove
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 *
 * Corner: (8 pieces)
 * Top layer: (4 pieces)
 * 0 = UFR (Up–Front–Right)
 * 1 = UFL (Up–Front–Left)
 * 2 = UBL (Up–Back–Left)
 * 3 = UBR (Up–Back–Right)
 *
 * Bottom layer: (4 pieces)
 * 4 = DFR (Down–Front–Right)
 * 5 = DFL (Down–Front–Left)
 * 6 = DBL (Down–Back–Left)
 * 7 = DBR (Down–Back–Right)
 *
 * cornerPermutations[0] = 4 means "The corner piece that originally belonged to DFR is now sitting in the UFR position."
 *
 *
 * Edge (12 pieces)
 * Top layer (4 pieces):
 * 0 = UF
 * 1 = UL
 * 2 = UB
 * 3 = UR
 *
 * Middle layer (4 pieces):
 * 8 = FR
 * 9 = FL
 * 10 = BL
 * 11 = BR
 *
 * Bottom layer (4 pieces):
 * 4 = DF
 * 5 = DL
 * 6 = DB
 * 7 = DR
 *
 * edgePermutations[0] = 8 means "The FR edge piece is now in the UF Position"
 */
class RubiksCubeGame {

    // There are 8 corner pieces in a 3x3x3 cube
    private val cornerPermutations = Array(8) { it }

    // There are 12 edge pieces in a 3x3x3 cube
    private val edgePermutations = Array(12) { it }

    private val _gameState = MutableStateFlow(isSolved())
    val gameState = _gameState.asStateFlow()

    fun move(m: GameMove) {
        when (m) {
            GameMove.UP_CLOCKWISE -> up()
            GameMove.UP_ANTICLOCKWISE -> {
                up(); up(); up()
            }

            GameMove.DOWN_CLOCKWISE -> down()
            GameMove.DOWN_ANTICLOCKWISE -> {
                down(); down(); down()
            }

            GameMove.RIGHT_CLOCKWISE -> right()
            GameMove.RIGHT_ANTICLOCKWISE -> {
                right(); right(); right()
            }

            GameMove.LEFT_CLOCKWISE -> left()
            GameMove.LEFT_ANTICLOCKWISE -> {
                left(); left(); left()
            }

            GameMove.FRONT_CLOCKWISE -> front()
            GameMove.FRONT_ANTICLOCKWISE -> {
                front(); front(); front()
            }

            GameMove.BACK_CLOCKWISE -> back()
            GameMove.BACK_ANTICLOCKWISE -> {
                back(); back(); back()
            }
        }
    }

    fun reset() {
        cornerPermutations.indices.forEach {
            cornerPermutations[it] = it
        }
        edgePermutations.indices.forEach {
            edgePermutations[it] = it
        }
    }

    private fun up() {
        cycleSwap(cornerPermutations, 0, 3, 2, 1)
        cycleSwap(edgePermutations, 0, 3, 2, 1)
        updateGame()
    }

    private fun down() {
        cycleSwap(cornerPermutations, 4, 5, 6, 7)
        cycleSwap(edgePermutations, 4, 5, 6, 7)
        updateGame()
    }

    private fun right() {
        cycleSwap(cornerPermutations, 0, 4, 7, 3)
        cycleSwap(edgePermutations, 0, 8, 4, 11)
        updateGame()
    }

    private fun left() {
        cycleSwap(cornerPermutations, 1, 2, 6, 5)
        cycleSwap(edgePermutations, 2, 10, 6, 9)
        updateGame()
    }

    private fun front() {
        cycleSwap(cornerPermutations, 0, 1, 5, 4)
        cycleSwap(edgePermutations, 1, 9, 5, 8)
        updateGame()
    }

    private fun back() {
        cycleSwap(cornerPermutations, 2, 3, 7, 6)
        cycleSwap(edgePermutations, 3, 11, 7, 10)
        updateGame()
    }

    private fun updateGame() {
        _gameState.value = isSolved()
    }

    private fun isSolved(): Boolean {
        return cornerPermutations.indices.all { cornerPermutations[it] == it }
                && edgePermutations.indices.all { edgePermutations[it] == it }
    }

    private fun cycleSwap(arr: Array<Int>, i: Int, j: Int, k: Int, l: Int) {
        val temp = arr[i]
        arr[i] = arr[j]
        arr[j] = arr[k]
        arr[k] = arr[l]
        arr[l] = temp
    }
}