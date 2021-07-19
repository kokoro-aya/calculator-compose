import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import calculator.AbstractCalculator
import calculator.SciCalculator
import calculator.SciOper
import calculator.SimpleCalculator

@Composable
fun Screen() {

    val history = remember { mutableStateListOf<Pair<String, String>>() }

    val (calc, setCalc) = remember { mutableStateOf<AbstractCalculator>(SimpleCalculator()) }

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

    val sciOpCallback = { op: SciOper ->
        setCalc(calc.scientificOperation(op))
    }

    val (isSci, setSci) = remember { mutableStateOf(false) }
    val (isDeg, setDeg) = remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Composable Calculator")
                },
                navigationIcon = {
                    IconButton(
                        onClick = {},
                    ) {
                        Icon(Icons.Default.Menu, "Drawer")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {},
                    ) {
                        Icon(Icons.Default.Share, "Save")
                    }
                },
                elevation = 4.dp,

            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                icon = {
                    Icon(
                        if (isSci) Icons.Default.Clear else Icons.Default.Add,
                        "Switch to sci mode"
                    )
                },
                text = {
                    Text(
                        text = if (isSci) "NORMAL" else "SCIENTIFIC"
                    )
                },
                onClick = {
                    setSci(!isSci)
                    if (isSci) {
                        setCalc(SimpleCalculator())
                    } else {
                        setCalc(SciCalculator())
                    }
                },
            )
        },
        content = {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
                verticalArrangement = Arrangement.Center) {
                ExpressionAndResultView(calc.showExpression, calc.showAnswer)
                Spacer(modifier = Modifier.height(16.dp))
                ErrorView(onDismiss, calc.showMessage)
                if (isSci) {
                    SciKeyPadArrangement(sciOpCallback, {
                        setDeg(!isDeg)
                    }, isDeg)
                    Spacer(modifier = Modifier.height(16.dp))
                }
                KeyPadArrangement(
                    numOpCallback, submitCallback, resetCallback
                )
            }
        }
    )
}