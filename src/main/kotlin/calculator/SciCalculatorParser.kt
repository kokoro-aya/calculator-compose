package calculator

import ch.obermuhlner.math.big.BigDecimalMath
import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parser
import com.github.h0tk3y.betterParse.grammar.tryParseToEnd
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.parser.Parser
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

private val PI = "3.1415926535897932384626433832795028841971693993751058209749445923078164062862089986280348253421170679".toBigDecimal()
private val E = "2.7182818284590452353602874713526624977572470936999595749669676277240766303535475945713821785251664274".toBigDecimal()

private fun convertRadiansToDegrees(radians: BigDecimal): BigDecimal {
    return radians * BigDecimal(180) / PI
}

private fun convertDegreesToRadians(degrees: BigDecimal): BigDecimal {
    return degrees * PI / BigDecimal(180)
}

class SciCalculatorParser(deg: Boolean): Grammar<BigDecimal>() {
    private val num by regexToken("-?[\\d|A-F]+(.[\\d|A-F]+)?")
    private val e by literalToken("e")
    private val pi by literalToken("\uD835\uDF45")
    private val lparen by literalToken("(")
    private val rparen by literalToken(")")
    private val mul by literalToken("*")
    private val div by literalToken("/")
    private val mod by literalToken("%")
    private val add by literalToken("+")
    private val sub by literalToken("-")
    private val exp by literalToken("^")
    private val sqrt by literalToken("√(")
    private val sin by literalToken("sin(")
    private val cos by literalToken("cos(")
    private val tan by literalToken("tan(")
    private val ln by literalToken("ln(")
    private val log by literalToken("log(")
    private val ws by regexToken("\\s+", ignore = true) // 有坑，忘记了忽略ws的话会出错，但错误提示很不明显。。。

    private val var_num by num use { text.toBigDecimal() }
    private val const_pi by pi use { PI }
    private val const_e by e use { E }

    private val number: Parser<BigDecimal> by var_num or const_pi or const_e

    private val term: Parser<BigDecimal> by number or
            (skip(sub) and parser(::term) map { -it}) or
            (skip(sqrt) and parser(::rootParser) and skip(rparen) map {
                it.sqrt(MathContext.DECIMAL128)
            }) or
            (skip(sin) and parser(::rootParser) and skip(rparen) map {
                if (deg) {
                    convertDegreesToRadians(it).let {
                        BigDecimalMath.sin(it, MathContext.DECIMAL128)
                    }
                } else {
                    BigDecimalMath.sin(it, MathContext.DECIMAL128)
                }
            }) or
            (skip(cos) and parser(::rootParser) and skip(rparen) map {
                if (deg) {
                    convertDegreesToRadians(it).let {
                        BigDecimalMath.cos(it, MathContext.DECIMAL128)
                    }
                } else {
                    BigDecimalMath.cos(it, MathContext.DECIMAL128)
                }
            }) or
            (skip(tan) and parser(::rootParser) and skip(rparen) map {
                if (deg) {
                    convertDegreesToRadians(it).let {
                        BigDecimalMath.tan(it, MathContext.DECIMAL128)
                    }
                } else {
                    BigDecimalMath.tan(it, MathContext.DECIMAL128)
                }
            }) or
            (skip(ln) and parser(::rootParser) and skip(rparen) map {
                BigDecimalMath.log(it, MathContext.DECIMAL128)
            }) or
            (skip(log) and parser(::rootParser) and skip(rparen) map {
                BigDecimalMath.log10(it, MathContext.DECIMAL128)
            }) or
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
    println(SciCalculatorParser(true).tryParseToEnd("√(64 - 25)"))
}