package ui

import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Shapes
import androidx.compose.ui.unit.dp

val CalculatorShapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = CutCornerShape(12.dp),
    large = CutCornerShape(24.dp),
)