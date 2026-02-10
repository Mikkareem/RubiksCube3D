package com.techullurgy.gameengine

import android.opengl.GLES30
import dev.romainguy.kotlin.math.Float4
import dev.romainguy.kotlin.math.Mat4
import java.nio.ByteBuffer
import java.nio.ByteOrder

class Shader private constructor(
    source: ShaderProgramSource
) {
    private var programId = createShader(source)
    private val uniformLocationMap = mutableMapOf<String, Int>()
    private val attributesLocationMap = mutableMapOf<String, Int>()

    fun bind() {
        GLES30.glUseProgram(programId)
    }

    fun unbind() {
        GLES30.glUseProgram(0)
    }

    fun enableAttributeLocation(name: String, values: FloatArray, perSize: Int, stride: Int) {
        if (values.isEmpty()) {
            throw IllegalArgumentException("addBuffer values cannot be empty")
        }

        val location = if (attributesLocationMap.containsKey(name)) {
            attributesLocationMap[name]!!
        } else {
            val loc = GLES30.glGetAttribLocation(programId, name)
            attributesLocationMap[name] = loc
            loc
        }

        val vertexBuffer = ByteBuffer.allocateDirect(values.size * 4).apply {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(values)
                position(0)
            }
        }

        GLES30.glEnableVertexAttribArray(location)
        GLES30.glVertexAttribPointer(
            location,
            perSize,
            GLES30.GL_FLOAT,
            false,
            stride,
            vertexBuffer
        )
    }

    fun disableAttributeLocation(name: String) {
        if (!attributesLocationMap.containsKey(name)) {
            throw IllegalStateException("No Attribute Location found for $name")
        }

        val loc = attributesLocationMap[name]!!
        GLES30.glDisableVertexAttribArray(loc)
    }

    fun setUniform4fv(name: String, value: Float4) {
        val location = getUniformLocation(name)
        GLES30.glUniform4fv(location, 1, value.toFloatArray(), 0)
    }

    fun setUniform4f(name: String, value1: Float, value2: Float, value3: Float, value4: Float) {
        val location = getUniformLocation(name)
        GLES30.glUniform4f(location, value1, value2, value3, value4)
    }

    fun setUniformMatrix4fv(name: String, matrix: Mat4) {
        val location = getUniformLocation(name)
        GLES30.glUniformMatrix4fv(location, 1, true, matrix.toFloatArray(), 0)
    }

    private fun createShader(source: ShaderProgramSource): Int {
        val vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, source.vertex)
        val fragmentShader = loadShader(GLES30.GL_FRAGMENT_SHADER, source.fragment)

        return GLES30.glCreateProgram().also {
            GLES30.glAttachShader(it, vertexShader)
            GLES30.glAttachShader(it, fragmentShader)
            GLES30.glLinkProgram(it)
        }
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES30.glCreateShader(type).also { shader ->
            GLES30.glShaderSource(shader, shaderCode)
            GLES30.glCompileShader(shader)
        }
    }

    private fun getUniformLocation(name: String): Int {
        if (uniformLocationMap.containsKey(name)) {
            return uniformLocationMap[name]!!
        }

        val location = GLES30.glGetUniformLocation(programId, name)
        if (location == -1) {
            println("Warning: uniform $name doesn't exists!!!")
        }

        uniformLocationMap[name] = location

        return location
    }

    companion object {
        private var BASIC_SHADER_WITH_MVP: Shader? = null

        fun getBasicShaderWithMvp(): Shader {
            if (BASIC_SHADER_WITH_MVP == null) {
                synchronized(this) {
                    if (BASIC_SHADER_WITH_MVP == null) {
                        BASIC_SHADER_WITH_MVP = Shader(
                            ShaderProgramSource(
                                BASIC_VERTEX_SHADER_WITH_MVP,
                                BASIC_FRAGMENT_SHADER
                            )
                        )
                    }
                }
            }

            return BASIC_SHADER_WITH_MVP!!
        }
    }
}

private data class ShaderProgramSource(
    val vertex: String,
    val fragment: String
)

private val BASIC_VERTEX_SHADER_WITH_MVP = """
    uniform mat4 uMVPMatrix;
    attribute vec4 vPosition;
    void main() {
        gl_Position = uMVPMatrix * vPosition;
    }
""".trimIndent()

private val BASIC_FRAGMENT_SHADER = """
    precision mediump float;
    uniform vec4 vColor;
    void main() {
        gl_FragColor = vColor;
    }
""".trimIndent()
