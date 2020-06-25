package com.ersiver.minipaint

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.res.ResourcesCompat

private const val STROKE_WIDTH = 12f

class MyCanvasView(context: Context?) : View(context) {

    //bitmap and canvas for caching what has been drawn before.
    lateinit var extraCanvas: Canvas
    lateinit var extraBitmap: Bitmap

    private val bgColor = ResourcesCompat.getColor(resources, R.color.colorBackground, null)
    private val drawColor = ResourcesCompat.getColor(resources, R.color.colorPaint, null)

    // Set up the paint with which to draw.
    private val paint = Paint().apply {
        color = drawColor
        isAntiAlias = true // Smooths out edges of what is drawn without affecting shape.
        isDither =
            true  // Dithering affects how colors with higher-precision than the device are down-sampled.
        style = Paint.Style.STROKE // default: FILL
        strokeJoin = Paint.Join.ROUND // default: MITER
        strokeCap = Paint.Cap.ROUND // default: BUTT
        strokeWidth = STROKE_WIDTH // default: Hairline-width (really thin)

    }

    private var path = Path()

    //Variables for caching the x and y coordinates of the current touch event (the MotionEvent coordinates).
    private var motionTouchEventX = 0F
    private var motionTouchEventY = 0F


    //variables to cache the latest x and y values. After the user stops moving and lifts their touch,
    // these are the starting point for the next path
    private var currentX = 0f
    private var currentY = 0f

    //If the finger has barely moved, there is no need to draw.
    //If the finger has moved less than the touchTolerance distance, don't draw.
    //scaledTouchSlop returns the distance in pixels a touch can wander before the system thinks the user is scrolling.
   private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop

    private lateinit var frame: Rect

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        if (::extraBitmap.isInitialized)
            extraBitmap.recycle()

        extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
        extraCanvas.drawColor(bgColor)

        // Calculate a rectangular frame around the picture.
        val inset = 40
        frame = Rect(inset, inset, width - inset, height - inset)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        super.onDraw(canvas)

        //The 2D coordinate system used for drawing on a Canvas is in pixels,
        // and the origin (0,0) is at the top left corner of the Canvas.
        canvas.drawBitmap(extraBitmap, 0F, 0F, null)

        // Draw a frame around the canvas.
        canvas.drawRect(frame, paint)
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        motionTouchEventX = event.x
        motionTouchEventY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> touchStart()
            MotionEvent.ACTION_MOVE -> touchMove()
            MotionEvent.ACTION_UP -> touchUp()
        }
        performClick()
        return true
    }

    private fun touchUp() {

        // Reset the path so it doesn't get drawn again.
        path.reset()
    }

    private fun touchMove() {

        //Calculate the traveled distance (dx, dy)
        val dx = Math.abs(motionTouchEventX - currentY)
        val dy = Math.abs((motionTouchEventY - currentY))

        //if the movement was further than the touch tolerance, add a segment to the path.
       if (dx >= touchTolerance || dy >= touchTolerance) {
            // QuadTo() adds a quadratic bezier from the last point,
            //Using quadTo() instead of lineTo() create a smoothly drawn line without corners.
            path.quadTo(
                currentX,
                currentY,
                (motionTouchEventX + currentX) / 2,
                (motionTouchEventY + currentY) / 2
            )
            currentX = motionTouchEventX
            currentY = motionTouchEventY

            // Draw the path in the extra bitmap to cache it.
            extraCanvas.drawPath(path, paint)
        }

        //Call invalidate() to (eventually call onDraw() and) redraw the view.
        invalidate()
    }

    //This method is called when the user first touches the screen.
    private fun touchStart() {
        path.reset()
        path.moveTo(motionTouchEventX, motionTouchEventY)
        currentX = motionTouchEventX
        currentY = motionTouchEventY
    }
}