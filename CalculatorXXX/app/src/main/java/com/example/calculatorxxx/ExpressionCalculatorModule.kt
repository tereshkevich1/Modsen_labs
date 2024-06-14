package com.example.calculatorxxx

import java.math.BigDecimal

sealed class Result<out T : Any> {
    data class Success<out T : Any>(val value: T) : Result<T>()
    data class Error(val errorMessage: String) : Result<Nothing>()
}

class ExpressionCalculatorModule {

    fun calculate(expression: String): Result<Double> {
        val numbers = java.util.Stack<Double>()
        val operations = java.util.Stack<Char>()

        var exprIndex = 0

        // Early validation for trailing operators
        if (expression.isEmpty() || "+-×÷".contains(expression.last())) {
            return Result.Error("Invalid Expression")
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
                        if (numbers.size < 2) return Result.Error("Invalid Expression")

                        numbers.push(applyOp(operations.pop(), numbers.pop(), numbers.pop()))
                    }
                    operations.push(expression[exprIndex])
                    exprIndex++
                }
            }
        }

        while (!operations.empty()) {
            if (numbers.size < 2) return Result.Error("Invalid Expression")
            numbers.push(applyOp(operations.pop(), numbers.pop(), numbers.pop()))
        }

        return if (numbers.isEmpty()) Result.Error("Invalid Expression") else Result.Success(numbers.pop())
    }

    private fun hasPrecedence(op1: Char, op2: Char): Boolean {
        if (op2 == '(' || op2 == ')') return false
        return !((op1 == '×' || op1 == '÷' || op1 == '%') && (op2 == '+' || op2 == '-'))
    }

    private fun applyOp(op: Char, b: Double, a: Double): Double {
        return when (op) {
            '+' -> a + b
            '-' -> a - b
            '×' -> a * b
            '÷' -> {
                if (b == 0.0) throw UnsupportedOperationException("Cannot divide by zero")
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
            "Invalid input"
        }
    }
}