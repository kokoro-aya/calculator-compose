import androidx.compose.desktop.Window
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import ui.AzulTheme

fun main() = Window(title = "Composable Calculator") {
    AzulTheme {
        Surface(color = MaterialTheme.colors.background) {
            Screen()
        }
    }
}