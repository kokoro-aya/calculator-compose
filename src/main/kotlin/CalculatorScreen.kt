import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import calculator.Calculator

@Composable
fun Screen() {
    val (calc, setCalc) = remember { mutableStateOf(Calculator()) }

    val numOpCallback = { ch: Char ->
        setCalc(calc.appendCharacter(ch))
    }
    val resetCallback = {
        setCalc(calc.reinitialize())
    }
    val submitCallback = {
        setCalc(calc.evaluate())
    }
    val onDismiss = {
        setCalc(calc.reinitialize())
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
        verticalArrangement = Arrangement.Center) {
        ExpressionAndResultView(calc.showExpression, calc.showAnswer)
        Spacer(modifier = Modifier.height(16.dp))
        ErrorView(onDismiss, calc.showMessage)
        KeyPadArrangement(
            numOpCallback, submitCallback, resetCallback
        )
    }
}