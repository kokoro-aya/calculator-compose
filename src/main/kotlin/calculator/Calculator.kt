package calculator

import com.github.h0tk3y.betterParse.grammar.tryParseToEnd
import com.github.h0tk3y.betterParse.parser.ErrorResult
import com.github.h0tk3y.betterParse.parser.ParseResult
import com.github.h0tk3y.betterParse.parser.Parsed
import utils.fp.Result

enum class SciOper {
    SIN, COS, TAN, SQRT, E, PI, INV, LN, LOG
}

interface AbstractCalculator {
    var _expr: String
    var answer: String
    var message: String?

    fun reinitialize(): AbstractCalculator
    fun appendCharacter(ch: Char): AbstractCalculator
    fun evaluate(): AbstractCalculator

    fun scientificOperation(op: SciOper): AbstractCalculator

    val calcTerminated: Boolean
        get() = answer.isNotEmpty()

    val showExpression: String
        get() = _expr

    val showAnswer: String
        get() {
            return answer
        }

    val showMessage: String?
        get() {
            return when (message) {
                "" -> null
                else -> message
            }
        }

    fun <T> ParseResult<T>.toResult() = when (this) {
        is Parsed -> Result(this.value)
        is ErrorResult -> Result.failure(this.toString())
    }
}

data class SimpleCalculator(
    override var _expr: String = "0",
    override var answer: String = "0",
    override var message: String? = null,
): AbstractCalculator {
    override fun reinitialize(): SimpleCalculator = copy(_expr = "0", answer = "0", message = null)

    override fun appendCharacter(ch: Char): SimpleCalculator {
        check (ch in "0123456789.+-*/^%() ")
        return if (_expr == "0") copy(_expr = "$ch")
        else if (ch in "0123456789." && _expr.last() in "0123456789.")
            copy(_expr = "$_expr$ch")
        else
            copy(_expr = "$_expr $ch")
    }

    override fun evaluate(): SimpleCalculator {
//        println(_expr)
        return when (val res = SimpleCalculatorParser().tryParseToEnd(_expr).toResult()) {
            Result.Empty -> copy(answer = "0")
            is Result.Failure -> copy(message = res.exception.message)
            is Result.Success -> {
                val it = res.value
                copy(answer = when {
                    it.stripTrailingZeros().scale() <= 0 -> it.toBigInteger()
                    else -> it.stripTrailingZeros()
                }.toString())
            }
        }
    }

    override fun scientificOperation(op: SciOper): AbstractCalculator = throw Exception("This should not happen")
}

data class SciCalculator(
    override var _expr: String = "0",
    override var answer: String = "0",
    override var message: String? = null,
): AbstractCalculator {
    override fun reinitialize(): SciCalculator = copy(_expr = "0", answer = "0", message = null)

    override fun appendCharacter(ch: Char): SciCalculator {
        check (ch in "0123456789.+-*/^%() ")
        return if (_expr == "0") copy(_expr = "$ch")
        else if (ch in "0123456789." && _expr.last() in "0123456789.")
            copy(_expr = "$_expr$ch")
        else
            copy(_expr = "$_expr $ch")
    }

    override fun evaluate(): SciCalculator {
        return when (val res = SimpleCalculatorParser().tryParseToEnd(_expr).toResult()) {
            Result.Empty -> copy(answer = "0")
            is Result.Failure -> copy(message = res.exception.message)
            is Result.Success -> {
                val it = res.value
                copy(answer = when {
                    it.stripTrailingZeros().scale() <= 0 -> it.toBigInteger()
                    else -> it.stripTrailingZeros()
                }.toString())
            }
        }
    }

    override fun scientificOperation(op: SciOper): AbstractCalculator {
        val sp = if (_expr.last() in "0123456789.") " " else ""
        return when (op) {
            SciOper.SIN -> copy(_expr = "$_expr${sp}sin(")
            SciOper.COS -> copy(_expr = "$_expr${sp}cos(")
            SciOper.TAN -> copy(_expr = "$_expr${sp}tan(")
            SciOper.SQRT -> copy(_expr = "$_expr${sp}√(")
            SciOper.E -> copy(_expr = "$_expr${sp}e")
            SciOper.PI -> copy(_expr = "$_expr${sp}\uD835\uDF45")
            SciOper.INV -> copy(_expr = "1 /($_expr)")
            SciOper.LN -> copy(_expr = "$_expr${sp}ln(")
            SciOper.LOG -> copy(_expr = "$_expr${sp}log(")
        }
    }
}