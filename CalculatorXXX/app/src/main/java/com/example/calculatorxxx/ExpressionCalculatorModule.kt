package com.example.calculatorxxx

import java.lang.Exception
import java.math.BigDecimal

sealed class Result<out T : Any> {
    data class Success<out T : Any>(val value: T) : Result<T>()
    data class Error(val errorMessage: String) : Result<Nothing>()
}

/**A class for processing an arithmetic expression as a string**/

class ExpressionCalculatorModule {

    fun calculate(expression: String): Result<Double> {
        try {
            val numbers = java.util.Stack<Double>()
            val operations = java.util.Stack<Char>()

            var exprIndex = 0

            if (expression.isEmpty() || "+-×÷".contains(expression.last())) {
                return Result.Error(Constants.INVALID_EXPRESSION)
            }

            while (exprIndex < expression.length) {
                when {
                    expression[exprIndex].isWhitespace() -> exprIndex++
                    expression[exprIndex].isDigit() || expression[exprIndex] == '.' -> {
                        var number = ""
                        while (exprIndex < expression.length && (expression[exprIndex].isDigit() || expression[exprIndex] == '.')) {
                            number += expression[exprIndex]
                            exprIndex++
                        }
                        numbers.push(number.toDouble())
                    }

                    expression[exprIndex] == '-' && (exprIndex == 0 || !expression[exprIndex - 1].isDigit()) -> {
                        // Handle negative numbers
                        var number = "-"
                        exprIndex++
                        while (exprIndex < expression.length && (expression[exprIndex].isDigit() || expression[exprIndex] == '.')) {
                            number += expression[exprIndex]
                            exprIndex++
                        }
                        numbers.push(number.toDouble())
                    }

                    else -> {
                        while (!operations.empty() && hasPrecedence(
                                expression[exprIndex],
                                operations.peek()
                            )
                        ) {
                            if (numbers.size < 2) return Result.Error(Constants.INVALID_EXPRESSION)

                            numbers.push(applyOp(operations.pop(), numbers.pop(), numbers.pop()))
                        }
                        operations.push(expression[exprIndex])
                        exprIndex++
                    }
                }
            }

            while (!operations.empty()) {
                if (numbers.size < 2) return Result.Error(Constants.INVALID_EXPRESSION)
                numbers.push(applyOp(operations.pop(), numbers.pop(), numbers.pop()))
            }
            return if (numbers.isEmpty()) Result.Error(Constants.INVALID_EXPRESSION) else Result.Success(
                numbers.pop()
            )

        } catch (e: UnsupportedOperationException) {
            return Result.Error(Constants.DIVIDE_BY_ZERO_ERROR)
        }

        catch (e: Exception){
            return Result.Error(Constants.INVALID_EXPRESSION)
        }
    }

    private fun hasPrecedence(op1: Char, op2: Char) =
        !((op1 == '×' || op1 == '÷' || op1 == '%') && (op2 == '+' || op2 == '-'))


    private fun applyOp(op: Char, b: Double, a: Double): Double {
        return when (op) {
            '+' -> a + b
            '-' -> a - b
            '×' -> a * b
            '÷' -> {
                if (b == 0.0) throw UnsupportedOperationException(Constants.DIVIDE_BY_ZERO_ERROR)
                a / b
            }

            '%' -> a * (b / 100)
            else -> 0.0
        }
    }

    fun convertScientificToDecimal(scientific: String): String {
        return try {
            val bigDecimal = BigDecimal(scientific)
            bigDecimal.toPlainString()
        } catch (e: NumberFormatException) {
            Constants.INVALID_EXPRESSION
        }
    }
}