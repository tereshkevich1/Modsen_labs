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

    fun insertOperation(symbol: String, selectionStart: Int, selectionEnd: Int) {

        val currentsExpression = _expression.value ?: ""

        val lastChar = currentsExpression.getOrNull(cursorPosition - 1)
        val nextChar = currentsExpression.getOrNull(cursorPosition)

        val currentExpression =
            if (selectionStart != selectionEnd) {
                val pair = updateSelectionIndices(currentsExpression, selectionStart, selectionEnd)
                val newSelectionStart = pair.first
                val newSelectionEnd = pair.second
                cursorPosition = selectionStart
                currentsExpression.substring(0, newSelectionStart) +
                        (currentsExpression.substring(newSelectionEnd) ?: "")
            } else if (lastChar?.let { it in "+-×÷%." } == true) {
                cursorPosition = selectionStart - 1
                val stringBuilder = StringBuilder(currentsExpression)
                stringBuilder.deleteCharAt(cursorPosition)
                stringBuilder.toString()
            } else if (nextChar?.let { it in "+-×÷%." } == true) {
                cursorPosition = selectionStart
                val stringBuilder = StringBuilder(currentsExpression)
                stringBuilder.deleteCharAt(cursorPosition)
                stringBuilder.toString()
            } else {
                cursorPosition = selectionStart
                currentsExpression
            }

        if (currentExpression.isNotEmpty() && cursorPosition != 0 || symbol == "-") {
            val newExpression = currentExpression.substring(
                0,
                cursorPosition
            ) + symbol + currentExpression.substring(cursorPosition)
            cursorPosition += symbol.length
            _expression.value = newExpression
        } else {
            _expression.value = currentExpression
        }
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

            val commaOrDecimal = if (selectionStart == 0 || pattern.containsMatchIn(expressionValue[selectionStart - 1].toString())) {
                "0."
            } else {
                "."
            }

            val newExpression = expressionValue.substring(0, selectionStart) +
                    commaOrDecimal + expressionValue.substring(selectionStart)

            val newPosition = selectionStart + commaOrDecimal.length
            cursorPosition = newPosition

            _expression.value = newExpression

        }
    }



    fun insertComma(selectionStart: Int, selectionEnd: Int) {
        if (_expression.value.isNullOrEmpty()) {
            insert("0.")
        } else {
            tryPutComma(selectionStart, selectionEnd)
        }
    }
    
}