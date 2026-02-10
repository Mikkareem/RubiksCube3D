package com.techullurgy.gameengine

import android.opengl.GLES30
import java.nio.ShortBuffer

object SceneRenderer {
    fun drawTriangleStrips(indices: ShortBuffer, size: Int) {
        GLES30.glDrawElements(GLES30.GL_TRIANGLE_STRIP, size, GLES30.GL_UNSIGNED_SHORT, indices)
    }
}
