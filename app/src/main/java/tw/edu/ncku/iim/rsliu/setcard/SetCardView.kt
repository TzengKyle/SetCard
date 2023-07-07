//SetCardView
package tw.edu.ncku.iim.rsliu.setcard

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View


/**
 * TODO: document your custom view class.
 */
class SetCardView : View {
    enum class Shape {
        OVAL, DIAMOND, WORM
    }

    enum class Shading {
        EMPTY, SOLID, STRIP
    }

    public var shapeCount: Int = 1
        set(value) {
            if (value >= 1 && value <= 3) {
                field = value
                invalidate()
            }
        }

    public var shape = Shape.OVAL
        set(value) {
            field = value
            invalidate()
        }

    public var color = Color.BLUE
        set(value) {
            field = value
            invalidate()
        }

    public var shading = Shading.EMPTY
        set(value) {
            field = value
            invalidate()
        }

    public var cardBackgroundColor = Color.WHITE
        set(value){
            field = value
            invalidate()
        }

    companion object SetCardConstants {
        const val CARD_STANDARD_HEIGHT = 240.0f
        const val CORNER_RADIUS = 12.0f
        const val SYMBOL_WIDTH_SCALE_FACTOR = 0.6f
        const val SYMBOL_HEIGHT_SCALE_FACTOR = 0.125f
        const val STRIP_DISTANCE_SCALE_FACTOR = 0.05f
    }

    private val cornerScaleFactor: Float
        get() {
            return height / CARD_STANDARD_HEIGHT
        }

    private val cornerRadius: Float
        get() {
            return CORNER_RADIUS * cornerScaleFactor
        }

    private val mPaint = Paint() // For drawing border and face images
    private val mTextPaint = TextPaint() // for drawing pips (text)

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context, attrs, defStyle
    ) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet? = null) {
        mPaint.setAntiAlias(true);
        mTextPaint.setAntiAlias(true);


        val a = context.obtainStyledAttributes(attrs, R.styleable.SetCardView)

        val shapeValue  = a.getString(R.styleable.SetCardView_shape)
        shape = when (shapeValue) {
            "OVAL" -> SetCardView.Shape.OVAL
            "DIAMOND" -> SetCardView.Shape.DIAMOND
            "WORM" -> SetCardView.Shape.WORM
            else -> SetCardView.Shape.OVAL
        }
        val shadingValue = a.getString(R.styleable.SetCardView_shading)
        shading = when (shadingValue) {
            "EMPTY" -> SetCardView.Shading.EMPTY
            "SOLID" -> SetCardView.Shading.SOLID
            "STRIP" -> SetCardView.Shading.STRIP
            else -> SetCardView.Shading.EMPTY
        }
        color = a.getColor(R.styleable.SetCardView_color, Color.BLUE)
        shapeCount = a.getInt(R.styleable.SetCardView_shapeCount, 1)

        cardBackgroundColor  = a.getColor(R.styleable.SetCardView_cardBackgroundColor, Color.WHITE)

        a.recycle()

    }


    private fun drawShapeWithVerticalOffset(canvas: Canvas, voffset: Float) {
        val path = Path()
        val width = width * SYMBOL_WIDTH_SCALE_FACTOR
        val height = height * SYMBOL_HEIGHT_SCALE_FACTOR

        if (shape == Shape.OVAL) { // OVAL
            val left = (getWidth() / 2 - width / 2).toFloat()
            val top = (getHeight() / 2 - height / 2 + voffset).toFloat()
            val right = (getWidth() / 2 + width / 2).toFloat()
            val bottom = (getHeight() / 2 + height / 2 + voffset).toFloat()

            val rectF = RectF(left, top, right, bottom)
            path.addOval(rectF, Path.Direction.CW)

            canvas.drawPath(path, mPaint)
        } else if (shape == Shape.DIAMOND) { // DIAMOND
            val width = width * SYMBOL_WIDTH_SCALE_FACTOR * 1.1f
            //val height = height * SYMBOL_HEIGHT_SCALE_FACTOR * 0.9f
            val center = PointF((getWidth() / 2).toFloat(), getHeight() / 2 + voffset)

            val centerX = center.x
            val centerY = center.y

            val size = Math.min(width, height) * SYMBOL_WIDTH_SCALE_FACTOR

            path.moveTo(centerX.toFloat(), (centerY - height).toFloat())
            path.lineTo((centerX + width).toFloat(), centerY.toFloat())
            path.lineTo(centerX.toFloat(), (centerY + height).toFloat())
            path.lineTo((centerX - width).toFloat(), centerY.toFloat())
            path.close()

            canvas.drawPath(path, mPaint)
        } else { // WORM
            val center = PointF((getWidth() / 2).toFloat(), getHeight() / 2 + voffset)
            path.moveTo(center.x - width / 2, center.y + height / 2)

            val cp1 = PointF(center.x - width / 4, center.y - height * 1.5f)
            val cp2 = PointF(center.x + width / 4, center.y)
            val dst = PointF(center.x + width / 2, center.y - height / 2)
            path.cubicTo(cp1.x, cp1.y, cp2.x, cp2.y, dst.x, dst.y)

            cp1.x = center.x + width / 2
            cp1.y = center.y + height * 2
            cp2.x = center.x - width / 2
            cp2.y = center.y

            dst.x = center.x - width / 2
            dst.y = center.y + height / 2

            path.cubicTo(cp1.x, cp1.y, cp2.x, cp2.y, dst.x, dst.y)
            canvas.drawPath(path, mPaint)
        }

        drawShadingInPath(canvas, path);
    }

    private fun drawShapes(canvas: Canvas) {
        //color
        mPaint.color = color
        //number
        when (shapeCount) {
            1 -> drawShapeWithVerticalOffset(canvas, 0f) // 繪製單個圖形
            2 -> {
                val yOffset = height * 0.13f // 第二個圖形的垂直位移
                drawShapeWithVerticalOffset(canvas, -yOffset) // 繪製第一個圖形
                drawShapeWithVerticalOffset(canvas, yOffset) // 繪製第二個圖形
            }

            3 -> {
                val yOffset = height * 0.26f // 第二個和第三個圖形的垂直位移
                drawShapeWithVerticalOffset(canvas, -yOffset) // 繪製第一個圖形
                drawShapeWithVerticalOffset(canvas, 0f) // 繪製第二個圖形
                drawShapeWithVerticalOffset(canvas, yOffset) // 繪製第三個圖形
            }
        }
        //drawShapeWithVerticalOffset(canvas, 0f)
    }


    private fun drawShadingInPath(canvas: Canvas, path: Path) {
        canvas.save();

        if (shading == Shading.SOLID) {
            mPaint.style = Paint.Style.FILL
            canvas.drawPath(path, mPaint)
        } else if (shading == Shading.STRIP) {
            canvas.clipPath(path);
            val path = Path();

            val strip_distance = width * STRIP_DISTANCE_SCALE_FACTOR;
            var x = 0f
            while (x < width) {
                path.moveTo(x, 0f);
                path.lineTo(x, height.toFloat());
                x += strip_distance
            }
            canvas.drawPath(path, mPaint);
        }
        canvas.restore();
    }

    private fun drawBackgroundColor(canvas: Canvas, path:Path){
        if(cardBackgroundColor  == Color.WHITE){
            // fill
            mPaint.style = Paint.Style.FILL
            mPaint.color = Color.WHITE//有改這裡
            canvas.drawPath(path, mPaint)
        }else if(cardBackgroundColor  == Color.BLACK){
            mPaint.style = Paint.Style.FILL
            mPaint.color = Color.BLACK//有改這裡
            canvas.drawPath(path, mPaint)
        }
        else{ //Color.YELLOW
            // fill
            mPaint.style = Paint.Style.FILL
            mPaint.color = Color.YELLOW//有改這裡
            canvas.drawPath(path, mPaint)
        }
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val path = Path()

        val rect = RectF(0f, 0f, width.toFloat(), height.toFloat())
        path.addRoundRect(rect, cornerRadius, cornerRadius, Path.Direction.CW)
        // Intersect the current clip with the specified path
        canvas.clipPath(path)
        // fill
        drawBackgroundColor(canvas, path)
        // border
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = 3.0f
        mPaint.color = Color.BLACK
        canvas.drawPath(path, mPaint)
//
        if(cardBackgroundColor == Color.WHITE || cardBackgroundColor == Color.YELLOW){
            drawShapes(canvas)
        }
//        drawShapes(canvas)
    }
}
