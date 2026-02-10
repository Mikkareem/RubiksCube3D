package com.techullurgy.gameengine

import java.nio.ByteBuffer
import java.nio.ByteOrder

class VertexArray<T> {
    private val mRendererId = 0

    private enum class ArrayType {
        F, // Float
        S, // Short
        I // Int
    }

    fun addVertexBuffer(values: FloatArray) {
        if (values.isEmpty()) {
            throw IllegalArgumentException("addBuffer values cannot be empty")
        }

        ByteBuffer.allocateDirect(values.size * 4).apply {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(values)
            }
        }
    }
}
