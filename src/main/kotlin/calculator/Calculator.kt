package calculator

import com.github.h0tk3y.betterParse.grammar.tryParseToEnd
import com.github.h0tk3y.betterParse.parser.ErrorResult
import com.github.h0tk3y.betterParse.parser.ParseResult
import com.github.h0tk3y.betterParse.parser.Parsed
import utils.fp.Result
import java.math.BigDecimal

data class Calculator(
    var _expr: String = "0",
    var answer: String = "0",
    var message: String? = null,
) {
    fun reinitialize(): Calculator = copy(_expr = "0", answer = "0", message = null)

    fun appendCharacter(ch: Char): Calculator {
        check (ch in "0123456789.+-*/^%() ")
        return if (_expr == "0") copy(_expr = "$ch")
        else if (ch in "0123456789." && _expr.last() in "0123456789.")
            copy(_expr = "$_expr$ch")
        else
            copy(_expr = "$_expr $ch")
    }

    fun evaluate(): Calculator {
//        println(_expr)
        return when (val res = CalculatorParser().tryParseToEnd(_expr).toResult()) {
            Result.Empty -> copy(answer = "0")
            is Result.Failure -> copy(message = res.exception.message)
            is Result.Success -> {
                val it = res.value
                copy(answer = when {
                    it.stripTrailingZeros().scale() <= 0 -> it.toBigInteger()
                    else -> it
                }.toString())
            }
        }
    }

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

    private fun <T> ParseResult<T>.toResult() = when (this) {
        is Parsed -> Result(this.value)
        is ErrorResult -> Result.failure(this.toString())
    }

    private companion object Test {
        fun Calculator.parse(str: String) {
            str.toCharArray().forEach {
                this.appendCharacter(it)
            }
        }

        fun Calculator.debug() {
            println("Expression >")
            println("\t${this.showExpression}")
            println("Answer >")
            println("\t${this.showAnswer}")
            println("Message >")
            println("\t${this.showMessage}")
            println()
        }

//        @JvmStatic // deprecated
//        fun main(args: Array<String>) {
//            val calc = Calculator()
//            calc.parse("1 + 2 * 3 / 4 % 5").also {
//                calc.debug()
//                calc.evaluate()
//                calc.debug()
//            }
//            calc.reinitialize()
//            calc.parse("1 + (2 * 3) / 4 - (5 * 6)").also {
//                calc.debug()
//                calc.evaluate()
//                calc.debug()
//            }
//            calc.reinitialize()
//            calc.parse("(1 + (2 * 3)) / (4 - (5) * 6)").also {
//                calc.debug()
//                calc.evaluate()
//                calc.debug()
//            }
//            calc.reinitialize()
//            calc.parse("12.45 / 27.66 + (38.5 + 42.1 / 16.3) - 24.5 * (13.3 - (22.5 + 14.6))").also {
//                calc.debug()
//                calc.evaluate()
//                calc.debug()
//            }
//            calc.reinitialize()
//        }
    }
}