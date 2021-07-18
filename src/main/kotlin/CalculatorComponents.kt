import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ExpressionAndResultView(
    expression: String,
    result: String,
    modifier: Modifier = Modifier
) {
    Card(
        elevation = 2.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            Surface(
                color = MaterialTheme.colors.primaryVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = expression,
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 4.dp)
                )
            }
            Surface(
                color = MaterialTheme.colors.primary,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = result,
                    style = MaterialTheme.typography.h3,
                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp, end = 8.dp, top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun ErrorView(
    onDismiss: () -> Unit,
    message: String?,
) {
    if (message != null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text("Calculation Failed")
            },
            text = {
                Text(message)
            },
            buttons = {
                Row(modifier = Modifier.padding(all = 4.dp),
                horizontalArrangement = Arrangement.Center) {
                    Button(
                        onClick = onDismiss
                    ) {
                        Text("Retry")
                    }
                }
            }
        )
    }
}

@Composable
fun KeyPadArrangement(
    numOpCallback: (Char) -> Unit,
    calcCallback: () -> Unit,
    resetCallback: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()) {
        KeyPadRow(
            listOf(null, null),
            listOf('(', ')'),
            List(2) { 0.5f },
            null,
            numOpCallback
        )
        KeyPadRow(
            listOf("AC", null, null, null),
            listOf(null, '^', '%', '/'),
            List(4) { 0.25f },
            resetCallback,
            numOpCallback
        )
        KeyPadRow(
            List(4) { null },
            listOf('7', '8', '9', '*'),
            List(4) { 0.25f },
            null,
            numOpCallback
        )
        KeyPadRow(
            List(4) { null },
            listOf('4', '5', '6', '-'),
            List(4) { 0.25f },
            null,
            numOpCallback
        )
        KeyPadRow(
            List(4) { null },
            listOf('1', '2', '3', '+'),
            List(4) { 0.25f },
            null,
            numOpCallback
        )
        KeyPadRow(
            listOf(null, null, "="),
            listOf('0', '.', null),
            listOf(0.5f, 0.25f, 0.25f),
            calcCallback,
            numOpCallback
        )
    }
}

@Composable
fun KeyPadRow(
    labels: List<String?>, values: List<Char?>, weights: List<Float>,
    actCallback: (() -> Unit)?,
    numOpCallback: (Char) -> Unit,
) {
    val zipped = labels.zip(values).zip(weights).map {
            pair -> Triple(pair.first.first, pair.first.second, pair.second) }
    Row(modifier = Modifier.fillMaxWidth()) {
        zipped.forEach { (label, value, weight) ->
            if (value == null) {
                check(label != null && actCallback != null)
                ActionButton(label, actCallback, Modifier.weight(weight))
            } else {
                check(label == null)
                NumOpButton(value, numOpCallback, Modifier.weight(weight))
            }
        }
    }
}

@Composable
fun NumOpButton(
    value: Char,
    onClick: (Char) -> Unit,
    modifier: Modifier = Modifier
) {
    val label: String = value.toString()
    Button(
        onClick = { onClick(value) },
        modifier = modifier.padding(4.dp),
    ) {
        Text(label)
    }
}

@Composable
fun ActionButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier.padding(4.dp),
    ) {
        Text(label)
    }
}