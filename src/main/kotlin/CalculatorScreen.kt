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
import kotlinx.coroutines.launch

@Composable
fun Screen() {

    val history = remember { mutableStateListOf<Pair<String, String>>() }

    val state = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    val (calc, setCalc) = remember { mutableStateOf<AbstractCalculator>(SimpleCalculator()) }

    val numOpCallback = { ch: Char ->
        setCalc(calc.appendCharacter(ch))
    }
    val resetCallback = {
        setCalc(calc.reinitialize())
    }
    val submitCallback = {
        calc.evaluate().let {
            history.add(it.showExpression to it.answer)
            setCalc(it)
        }
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
                        onClick = { scope.launch {
                            state.drawerState.also {
                                if (it.isOpen) it.close() else it.open()
                            }
                        } },
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
                        setCalc(SciCalculator(isDeg))
                    }
                    // 这里有大坑，要反复试几次（
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
        },
        scaffoldState = state,
        drawerContent = {
            Column(
                Modifier.fillMaxWidth().padding(start = 24.dp, top = 32.dp)
            ) {
                Text("History calculations", style = MaterialTheme.typography.h5)
                Spacer(Modifier.height(24.dp))
                history.takeLast(20).forEach {
                    Text(
                        text = "${it.first} = ${it.second}",
                        style = MaterialTheme.typography.body1
                    )
                }
            }
        }
    )
}