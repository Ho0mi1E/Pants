package com.example.pants.presentation.compose.components

import android.graphics.Bitmap
import android.graphics.Color.HSVToColor
import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.pants.R

private const val PICKER_WIDTH = 300
private const val PICKER_HEIGHT = 40

@Composable
fun HuePicker(
    hue: Float,
    onHueChange: (Float) -> Unit,
) {
    val bitmap = remember { drawHueBitmap() }

    Box(
        modifier = Modifier
            .height(PICKER_HEIGHT.dp)
            .width(PICKER_WIDTH.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val newHue = (offset.x / size.width) * 360f
                        onHueChange(newHue.coerceIn(0f, 360f))
                    }
                }
        ) {
            drawIntoCanvas {
                it.nativeCanvas.drawBitmap(bitmap, null, RectF(0f, 0f, size.width, size.height), null)
            }
        }

        var cursorWidth by remember { mutableIntStateOf(0) }
        Image(
            painter = painterResource(id = R.drawable.rectangle_cursor),
            contentDescription = null,
            modifier = Modifier
                .height(PICKER_HEIGHT.dp)
                .onSizeChanged { newWidth ->
                    if (cursorWidth != newWidth.width) cursorWidth = newWidth.width
                }
                .offset(x = ((hue / 360f) * (PICKER_WIDTH - cursorWidth)).dp)
        )
    }
}

private fun drawHueBitmap(): Bitmap {
    val bitmap = Bitmap.createBitmap(PICKER_WIDTH, PICKER_HEIGHT, Bitmap.Config.ARGB_8888)
    val hueCanvas = android.graphics.Canvas(bitmap)
    val huePanel = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
    val hueColors = IntArray(bitmap.width)

    for (i in hueColors.indices) {
        hueColors[i] = HSVToColor(floatArrayOf(i * 360f / hueColors.size, 1f, 1f))
    }

    val linePaint = Paint().apply { strokeWidth = 0f }
    for (i in hueColors.indices) {
        linePaint.color = hueColors[i]
        hueCanvas.drawLine(i.toFloat(), 0F, i.toFloat(), huePanel.bottom, linePaint)
    }

    return bitmap
}

