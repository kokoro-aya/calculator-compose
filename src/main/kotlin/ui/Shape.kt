package ui

import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Shapes
import androidx.compose.ui.unit.dp

val CalculatorShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = CutCornerShape(topStart = 24.dp),
    large = CutCornerShape(topStart = 32.dp),
)