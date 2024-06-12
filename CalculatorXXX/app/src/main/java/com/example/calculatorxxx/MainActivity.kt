package com.example.calculatorxxx

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.calculatorxxx.databinding.ActivityMainBinding
import com.example.calculatorxxx.vm.CalculatorViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: CalculatorViewModel
    private var isUpdatingText = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setUpViewModel()
        setUpInputEditText()
        setUpDigitButtons()
        setUpOperationButtons()

        setContentView(binding.root)
    }

    private fun setUpViewModel() {
        viewModel = ViewModelProvider(this)[CalculatorViewModel::class.java]

        viewModel.expression.observe(this) { newValue ->
            if (!isUpdatingText) {
                isUpdatingText = true
                binding.inputEditText.setText(newValue)
                binding.inputEditText.setSelection(viewModel.cursorPosition)
                isUpdatingText = false
            }
        }
    }

    private fun setUpInputEditText() {
        val inputEditText = binding.inputEditText

        inputEditText.showSoftInputOnFocus = false
        inputEditText.requestFocus()

        inputEditText.setOnClickListener {
            viewModel.updateCursorPosition(inputEditText.selectionStart)
        }

        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                //binding.inputEditText.setSelection(viewModel.cursorPosition)

                if (!isUpdatingText && s != null) {
                    isUpdatingText = true
                    viewModel.updateExpression(s.toString(), inputEditText.selectionStart)
                    isUpdatingText = false
                }
            }
        })
    }



    private fun setUpDigitButtons() {
        val buttons = listOf(
            binding.buttonZero,
            binding.buttonOne,
            binding.buttonTwo,
            binding.buttonThree,
            binding.buttonFour,
            binding.buttonFive,
            binding.buttonSix,
            binding.buttonSeven,
            binding.buttonEight,
            binding.buttonNine,
            binding.divideButton,
            binding.minusButton,
            binding.plusButton,
            binding.multiplyButton,
            binding.percentButton,
            binding.commaButton
        )

        buttons.forEach { button ->
            button.setOnClickListener {
                viewModel.insert(button.tag.toString())
            }
        }
    }

    private fun setUpOperationButtons() {

        val buttons = listOf(
            binding.divideButton,
            binding.minusButton,
            binding.plusButton,
            binding.multiplyButton,
            binding.percentButton,
            binding.commaButton
        )

        buttons.forEach { button ->
            button.setOnClickListener {
                viewModel.insertOperation(button.tag.toString())
            }
        }

        binding.removeButton.setOnClickListener {
            viewModel.remove()
        }
    }
}