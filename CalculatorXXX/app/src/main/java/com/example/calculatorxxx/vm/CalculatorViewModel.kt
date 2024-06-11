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

    fun remove(){
        val currentExpression = _expression.value ?: ""
        if (cursorPosition > 0 && currentExpression.isNotEmpty()) {
            val newExpression = currentExpression.substring(0, cursorPosition - 1) +
                    currentExpression.substring(cursorPosition)
            cursorPosition -= 1
            _expression.value = newExpression
        }
    }

    fun updateCursorPosition(position: Int) {
        cursorPosition = position
    }
}