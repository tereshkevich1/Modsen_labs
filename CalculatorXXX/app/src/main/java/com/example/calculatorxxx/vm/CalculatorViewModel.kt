package com.example.calculatorxxx.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class CalculatorViewModel : ViewModel() {

    private val _expression = MutableLiveData("-5×-5-5%-5")
    val expression: LiveData<String> get() = _expression

    private val _selectionStart = MutableLiveData(0)
    val selectionStart: LiveData<Int> get() = _selectionStart

    private val _selectionEnd = MutableLiveData(0)
    val selectionEnd: LiveData<Int> get() = _selectionEnd

    private val _currentResult = MutableLiveData("")
    val currentResult: LiveData<String> get() = _currentResult

    fun setNewSelection(selectionStart: Int, length: Int, expressionLength: Int) {
        val newSelectionStart = selectionStart + length
        _selectionStart.value = newSelectionStart.coerceIn(0, expressionLength)
        _selectionEnd.value = newSelectionStart.coerceIn(0, expressionLength)
    }

    fun insert(symbol: String, selectionStart: Int, selectionEnd: Int) {
        val currentExpression = _expression.value ?: ""

        val newExpression =
            currentExpression.substring(0, selectionStart) + symbol + currentExpression.substring(
                selectionEnd
            )

        _expression.value = newExpression
        setNewSelection(selectionStart, symbol.length, expression.value!!.length)
    }


    fun remove() {
        val currentExpression = _expression.value ?: ""
        if (_selectionStart.value!! > 0 && currentExpression.isNotEmpty()) {
            val newExpression = currentExpression.substring(0, _selectionEnd.value!! - 1) +
                    currentExpression.substring(_selectionEnd.value!!)
            _selectionStart.value = selectionStart.value!! - 1
            _selectionEnd.value = selectionEnd.value!! - 1
            _expression.value = newExpression
            setNewSelection(_selectionStart.value!!, 0, expression.value!!.length)
        }
    }

    fun insertOperation(symbol: String, selectionStart: Int, selectionEnd: Int) {
        val currentExpression = _expression.value ?: ""

        val lastChar = currentExpression.getOrNull(selectionStart - 1)
        val nextChar = currentExpression.getOrNull(selectionEnd)

        if (selectionStart == 0) {
            return
        }

        val updatedExpression = when {
            selectionStart != selectionEnd -> {
                val pair = updateSelectionIndices(currentExpression, selectionStart, selectionEnd)
                val newSelectionStart = pair.first
                val newSelectionEnd = pair.second

                currentExpression.substring(0, newSelectionStart) +
                        symbol +
                        currentExpression.substring(newSelectionEnd)
            }

            lastChar?.let { it in "+-×÷%." } == true -> {
                val stringBuilder = StringBuilder(currentExpression)
                stringBuilder.deleteCharAt(selectionStart - 1)
                stringBuilder.insert(selectionStart - 1, symbol)
                stringBuilder.toString()
            }

            nextChar?.let { it in "+-×÷%." } == true -> {
                val stringBuilder = StringBuilder(currentExpression)
                stringBuilder.deleteCharAt(selectionStart)
                stringBuilder.insert(selectionStart, symbol)
                stringBuilder.toString()
            }

            else -> {
                currentExpression.substring(
                    0,
                    selectionStart
                ) + symbol + currentExpression.substring(selectionStart)
            }
        }

        _expression.value = updatedExpression
        val newSelectionStart = when {
            lastChar?.let { it in "+-×÷%." } == true -> selectionStart - 1
            nextChar?.let { it in "+-×÷%." } == true -> selectionStart
            else -> selectionStart
        }

        setNewSelection(newSelectionStart, symbol.length, updatedExpression.length)
    }

    private fun updateSelectionIndices(
        currentsExpression: String,
        selectionStart: Int,
        selectionEnd: Int
    ): Pair<Int, Int> {
        val selectionStartW =
            if (currentsExpression.getOrNull(selectionStart - 1)?.let { it in "+-×÷%." } == true) {
                selectionStart - 1
            } else {
                selectionStart
            }

        val selectionEndW =
            if (currentsExpression.getOrNull(selectionEnd + 1)?.let { it in "+-×÷%." } == true) {
                selectionEnd + 1
            } else {
                selectionEnd
            }

        return Pair(selectionStartW, selectionEndW)
    }

    private val pattern = Regex("[+×÷^\\-]")

    private fun canInsertComma(selectionStart: Int, selectionEnd: Int): Boolean {
        val expressionValue = _expression.value ?: return false

        if (selectionStart > 0 && expressionValue[selectionStart - 1].isDigit()) {
            val isSelectionAtStringEnd = selectionEnd == expressionValue.length

            return if (isSelectionAtStringEnd) {
                val lastSegment = expressionValue.substring(0, selectionEnd).split(pattern).last()
                !lastSegment.contains('.')
            } else {
                val beforeSelection = expressionValue.substring(0, selectionStart)
                val afterSelection = expressionValue.substring(selectionEnd)

                val segmentBeforeCursor = beforeSelection.split(pattern).last()
                val segmentAfterCursor = afterSelection.split(pattern).first()

                !segmentBeforeCursor.contains('.') && !segmentAfterCursor.contains('.')
            }
        } else if (selectionStart == 0) {
            return !expressionValue.split(pattern).first().contains('.')
        }

        return false
    }


    private fun tryPutComma(selectionStart: Int, selectionEnd: Int) {
        if (canInsertComma(selectionStart, selectionEnd)) {
            val expressionValue = _expression.value ?: return

            val commaOrDecimal =
                if (selectionStart == 0 || pattern.containsMatchIn(expressionValue[selectionStart - 1].toString())) {
                    "0."
                } else {
                    "."
                }

            val newExpression = expressionValue.substring(0, selectionStart) +
                    commaOrDecimal + expressionValue.substring(selectionStart)

            _expression.value = newExpression

            setNewSelection(selectionStart, commaOrDecimal.length, expression.value!!.length)


        }
    }


    fun insertComma(selectionStart: Int, selectionEnd: Int) {
        if (_expression.value.isNullOrEmpty()) {
             insert("0.",selectionStart, selectionEnd)
        } else {
            tryPutComma(selectionStart, selectionEnd)
        }
    }


    fun calculate(expression: String) {
        val numbers = java.util.Stack<Double>()
        val operations = java.util.Stack<Char>()

        var i = 0

        // Early validation for trailing operators
        if (expression.isEmpty() || "+-×÷".contains(expression.last())) {
            _currentResult.value = "Invalid Expression"
            return
        }

        while (i < expression.length) {
            when {
                expression[i].isWhitespace() -> i++
                expression[i].isDigit() || expression[i] == '.' -> {
                    var number = ""
                    while (i < expression.length && (expression[i].isDigit() || expression[i] == '.')) {
                        number += expression[i]
                        i++
                    }
                    numbers.push(number.toDouble())
                }
                expression[i] == '-' && (i == 0 || !expression[i - 1].isDigit()) -> {
                    // Handle negative numbers
                    var number = "-"
                    i++
                    while (i < expression.length && (expression[i].isDigit() || expression[i] == '.')) {
                        number += expression[i]
                        i++
                    }
                    numbers.push(number.toDouble())
                }
                else -> {
                    while (!operations.empty() && hasPrecedence(expression[i], operations.peek())) {
                        if (numbers.size < 2) {
                            _currentResult.value = "Invalid Expression"
                            return
                        }
                        numbers.push(applyOp(operations.pop(), numbers.pop(), numbers.pop()))
                    }
                    operations.push(expression[i])
                    i++
                }
            }
        }

        while (!operations.empty()) {
            if (numbers.size < 2) {
                _currentResult.value = "Invalid Expression"
                return
            }
            numbers.push(applyOp(operations.pop(), numbers.pop(), numbers.pop()))
        }

        _currentResult.value = if (numbers.isEmpty()) "Invalid Expression" else numbers.pop().toString()
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


}