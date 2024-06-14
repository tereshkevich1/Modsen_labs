package com.example.calculatorxxx.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.calculatorxxx.Constants
import com.example.calculatorxxx.ExpressionCalculatorModule
import com.example.calculatorxxx.Result

interface ExpressionErrorHandler {
    fun onError(errorMessage: String)
    fun onSuccess(result: String)
}

/**ViewModel for processing logic with calculator string and result of calculations**/

class CalculatorViewModel : ViewModel() {

    private val _expression = MutableLiveData("")
    val expression: LiveData<String> get() = _expression

    private val _selectionStart = MutableLiveData(0)
    val selectionStart: LiveData<Int> get() = _selectionStart

    private val _selectionEnd = MutableLiveData(0)
    val selectionEnd: LiveData<Int> get() = _selectionEnd

    private val _currentResult = MutableLiveData("")
    val currentResult: LiveData<String> get() = _currentResult

    private val expressionCalculatorModule = ExpressionCalculatorModule()

    fun calculateFromEqualsButton(listener: ExpressionErrorHandler) {
        calculate(listener)
        _expression.value =
            expressionCalculatorModule.convertScientificToDecimal(
                currentResult.value ?: Constants.ZERO
            )
        val offset = if (currentResult.value == Constants.ZERO_COMMA_ZERO) 3
        else 1
        setNewSelection(_selectionStart.value!!, offset, expression.value!!.length)
    }

    fun calculate(listener: ExpressionErrorHandler) {
        val expression = if (expression.value!!.isEmpty()) Constants.ZERO
        else expression.value!!.toString()

        when (val result = expressionCalculatorModule.calculate(expression)) {
            is Result.Error -> {
                listener.onError(result.errorMessage)
            }

            is Result.Success -> {
                _currentResult.value = result.value.toString()
                listener.onSuccess(result.value.toString())
            }
        }
    }

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

    fun removeAll() {
        _expression.value = ""
        _currentResult.value = ""
        setNewSelection(0, 0, expression.value!!.length)
    }

    fun insertOperation(symbol: String, selectionStart: Int, selectionEnd: Int) {
        val currentExpression = _expression.value ?: ""

        val lastChar = currentExpression.getOrNull(selectionStart - 1)
        val nextChar = currentExpression.getOrNull(selectionEnd)

        val updatedExpression = when {

            // Handle selection replacement
            selectionStart != selectionEnd -> replaceSelection(
                currentExpression,
                selectionStart,
                selectionEnd,
                symbol
            )

            // Replace operator before the current position
            lastChar?.let { it in "+-×÷%." } == true -> replaceCharAt(
                currentExpression,
                selectionStart - 1,
                symbol
            )

            // Replace operator after the current position
            nextChar?.let { it in "+-×÷%." } == true -> replaceCharAt(
                currentExpression,
                selectionStart,
                symbol
            )

            else -> insertSymbol(currentExpression, selectionStart, symbol)
        }

        _expression.value = updatedExpression
        val newSelectionStart = when {
            lastChar?.let { it in "+-×÷%." } == true -> selectionStart - 1
            nextChar?.let { it in "+-×÷%." } == true -> selectionStart
            else -> selectionStart
        }
        setNewSelection(newSelectionStart, symbol.length, updatedExpression.length)
    }

    private fun replaceSelection(
        currentExpression: String,
        selectionStart: Int,
        selectionEnd: Int,
        symbol: String
    ): String {
        val (newSelectionStart, newSelectionEnd) = updateSelectionIndices(
            currentExpression,
            selectionStart,
            selectionEnd
        )
        return currentExpression.substring(
            0,
            newSelectionStart
        ) + symbol + currentExpression.substring(newSelectionEnd)
    }

    private fun replaceCharAt(currentExpression: String, index: Int, symbol: String): String {
        val stringBuilder = StringBuilder(currentExpression)
        stringBuilder.deleteCharAt(index)
        stringBuilder.insert(index, symbol)
        return stringBuilder.toString()
    }

    private fun insertSymbol(
        currentExpression: String,
        selectionStart: Int,
        symbol: String
    ): String {
        return currentExpression.substring(
            0,
            selectionStart
        ) + symbol + currentExpression.substring(selectionStart)
    }


    fun insertMinus(selectionStart: Int, selectionEnd: Int) {
        val currentExpression = _expression.value ?: ""

        val lastChar = currentExpression.getOrNull(selectionStart - 1)
        val nextChar = currentExpression.getOrNull(selectionEnd)

        val updatedExpression = when {
            // Allow minus sign insertion after multiplication and division, but not consecutively
            (lastChar?.let { it in "×÷" } == true || lastChar?.isDigit() ?: false) && nextChar != '-' && lastChar != '-' -> {
                currentExpression.substring(0, selectionStart) + "-" + currentExpression.substring(
                    selectionStart
                )
            }

            ((selectionStart == 0 && !(nextChar?.let { it in "×÷+-%." } ?: false))) -> {
                currentExpression.substring(0, selectionStart) + "-" + currentExpression.substring(
                    selectionStart
                )
            }

            else -> currentExpression
        }

        if (updatedExpression != currentExpression) {
            _expression.value = updatedExpression
            setNewSelection(selectionStart, 1, updatedExpression.length)
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

    private fun canInsertComma(selectionStart: Int, selectionEnd: Int): Boolean {
        val expressionValue = _expression.value ?: return false

        if (selectionStart > 0 && expressionValue[selectionStart - 1].isDigit()) {
            val isSelectionAtStringEnd = selectionEnd == expressionValue.length

            return if (isSelectionAtStringEnd) {
                val lastSegment =
                    expressionValue.substring(0, selectionEnd).split(Constants.pattern).last()
                !lastSegment.contains('.')
            } else {
                val beforeSelection = expressionValue.substring(0, selectionStart)
                val afterSelection = expressionValue.substring(selectionEnd)

                val segmentBeforeCursor = beforeSelection.split(Constants.pattern).last()
                val segmentAfterCursor = afterSelection.split(Constants.pattern).first()

                !segmentBeforeCursor.contains('.') && !segmentAfterCursor.contains('.')
            }
        } else if (selectionStart == 0) {
            return !expressionValue.split(Constants.pattern).first().contains('.')
        }

        return false
    }

    private fun tryPutComma(selectionStart: Int, selectionEnd: Int) {
        if (canInsertComma(selectionStart, selectionEnd)) {
            val expressionValue = _expression.value ?: return

            val commaOrDecimal =
                if (selectionStart == 0 || Constants.pattern.containsMatchIn(expressionValue[selectionStart - 1].toString())) {
                    Constants.ZERO_COMMA
                } else {
                    Constants.COMMA
                }

            val newExpression = expressionValue.substring(0, selectionStart) +
                    commaOrDecimal + expressionValue.substring(selectionStart)

            _expression.value = newExpression

            setNewSelection(selectionStart, commaOrDecimal.length, expression.value!!.length)
        }
    }

    fun insertComma(selectionStart: Int, selectionEnd: Int) {
        if (_expression.value.isNullOrEmpty()) {
            insert(Constants.ZERO_COMMA, selectionStart, selectionEnd)
        } else {
            tryPutComma(selectionStart, selectionEnd)
        }
    }

    fun toggleSign(selectionStart: Int, selectionEnd: Int) {
        val currentExpression = _expression.value ?: ""

        if (currentExpression.isEmpty()) return

        val (start, end) = findNumberBounds(currentExpression, selectionStart, selectionEnd)

        val number = currentExpression.substring(start, end)
        val toggledNumber = if (number.startsWith("-")) {
            number.substring(1)
        } else "-$number"

        val updatedExpression = currentExpression.substring(0, start) +
                toggledNumber +
                currentExpression.substring(end)

        _expression.value = updatedExpression
        setNewSelection(start, toggledNumber.length, updatedExpression.length)
    }

    private fun findNumberBounds(
        expression: String,
        selectionStart: Int,
        selectionEnd: Int
    ): Pair<Int, Int> {
        var start = selectionStart
        var end = selectionEnd

        // Extend selectionStart to the left to find the start of the number
        while (start > 0 && expression[start - 1].isDigitOrDot()) {
            start--
        }

        // Extend selectionEnd to the right to find the end of the number
        while (end < expression.length && expression[end].isDigitOrDot()) {
            end++
        }

        // Include preceding minus sign if it exists
        if (start > 0 && expression[start - 1] == '-') {
            start--
        }
        return Pair(start, end)
    }

    private fun Char.isDigitOrDot(): Boolean {
        return this.isDigit() || this == '.'
    }

}