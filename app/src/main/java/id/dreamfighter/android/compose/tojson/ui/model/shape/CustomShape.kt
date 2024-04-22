package id.dreamfighter.android.compose.tojson.ui.model.shape

import android.graphics.PointF
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class CustomShape(private val startPoint: PointF,private val points:List<PointF>) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(

            Path().apply {
                //val radian = (180 - angle) * Math.PI / 90
                //val xOnOpposite = (size.width * tan(radian)).toFloat()
                moveTo(startPoint.x, startPoint.y)
                points.forEach {
                    lineTo(x = it.x, y = it.y)
                }
                //lineTo(x = size.width - angle, y = size.height)
                //lineTo(x = size.width, y = 0f)
                //lineTo(x = angle, y = 0f)
                //lineTo(x = xOnOpposite, y = size.height)
            }
        )
    }
}