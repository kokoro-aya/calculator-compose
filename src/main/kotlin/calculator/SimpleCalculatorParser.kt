package calculator

import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parser
import com.github.h0tk3y.betterParse.grammar.tryParseToEnd
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.parser.Parser
import java.math.BigDecimal
import java.math.RoundingMode

class SimpleCalculatorParser: Grammar<BigDecimal>() {
    private val num by regexToken("-?\\d+(.\\d+)?")
    private val lparen by literalToken("(")
    private val rparen by literalToken(")")
    private val mul by literalToken("*")
    private val div by literalToken("/")
    private val mod by literalToken("%")
    private val add by literalToken("+")
    private val sub by literalToken("-")
    private val exp by literalToken("^")
    private val ws by regexToken("\\s+", ignore = true)

    private val number by num use { text.toBigDecimal() }
    private val term: Parser<BigDecimal> by number or
            (skip(sub) and parser(::term) map { -it }) or
            (skip(lparen) and parser(::rootParser) and skip(rparen))
    private val highPrecedence by leftAssociative(term, exp) { lhs, _, rhs ->
        lhs.pow(rhs.toInt())
    }
    private val mediumPrecedence by leftAssociative(highPrecedence, mul or div or mod use { type }) { lhs, op, rhs ->
        when (op) {
            mul -> lhs * rhs
            div -> lhs.divide(rhs, 10, RoundingMode.FLOOR).stripTrailingZeros()
            mod -> lhs % rhs
            else -> throw Exception("This is impossible")
        }
    }
    private val lowPrecedence by leftAssociative(mediumPrecedence, add or sub use { type }) { lhs, op, rhs ->
        when (op) {
            add -> lhs + rhs
            sub -> lhs - rhs
            else -> throw Exception("This is impossible")
        }
    }

    override val rootParser: Parser<BigDecimal> by lowPrecedence
}

fun main() {
    println(SimpleCalculatorParser().tryParseToEnd("1 / (8 + 2) - (7)"))
}