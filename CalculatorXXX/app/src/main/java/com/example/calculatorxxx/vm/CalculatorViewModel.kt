package com.example.calculatorxxx.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CalculatorViewModel : ViewModel() {

    private val _expression = MutableLiveData("")
    val expression: LiveData<String> get() = _expression

    var cursorPosition = 0

    fun insert(symbol: String) {
        val currentExpression = _expression.value ?: ""
        val newExpression = currentExpression.substring(0, cursorPosition) +
                symbol +
                currentExpression.substring(cursorPosition)
        cursorPosition += symbol.length
        _expression.value = newExpression
    }

    fun remove() {
        val currentExpression = _expression.value ?: ""
        if (cursorPosition > 0 && currentExpression.isNotEmpty()) {
            val newExpression = currentExpression.substring(0, cursorPosition - 1) +
                    currentExpression.substring(cursorPosition)
            cursorPosition -= 1
            _expression.value = newExpression
        }
    }

    fun updateCursorPosition(position: Int) {
        val currentExpression = _expression.value ?: ""
        cursorPosition = position.coerceIn(0, currentExpression.length)
    }

    fun updateExpression(newExpression: String, newPosition: Int) {
        _expression.value = newExpression
        cursorPosition = newPosition.coerceIn(0, newExpression.length)
    }

    fun insertOperation(symbol: String) {
        val currentExpression = _expression.value ?: ""
        val lastChar = currentExpression.getOrNull(cursorPosition - 1)
        val nextChar = currentExpression.getOrNull(cursorPosition)

        val isOperator = { char: Char? -> char?.let { it in "+-×÷%." } ?: false }
        val isNegativeFirstPosition = (cursorPosition == 0 && nextChar == '-')
        val isNegativeSecondPosition = (cursorPosition == 1 && lastChar == '-')

        // If the last character is an operator, REPLACE it
        if (isOperator(lastChar) && !isNegativeSecondPosition) {

            val newExpression = currentExpression.substring(0, cursorPosition - 1) +
                    symbol +
                    currentExpression.substring(cursorPosition)
            _expression.value = newExpression
        }

        // If the next character is an operator, REPLACE it
        else if (isOperator(nextChar) && !isNegativeFirstPosition) {

            val newExpression = currentExpression.substring(0, cursorPosition) +
                    symbol +
                    currentExpression.substring(cursorPosition + 1)

            cursorPosition += symbol.length
            _expression.value = newExpression

        } else if (currentExpression.isNotEmpty() && cursorPosition != 0 && !isNegativeSecondPosition) {

            val newExpression = currentExpression.substring(0, cursorPosition) +
                    symbol +
                    currentExpression.substring(cursorPosition)

            cursorPosition += symbol.length
            _expression.value = newExpression
        } else if (symbol == "-" && !isNegativeSecondPosition && !isNegativeFirstPosition) {

            val newExpression = currentExpression.substring(0, cursorPosition) +
                    symbol +
                    currentExpression.substring(cursorPosition)

            cursorPosition += symbol.length
            _expression.value = newExpression
        }

    }

}