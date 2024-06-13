package com.example.calculatorxxx

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.calculatorxxx.databinding.ActivityMainBinding
import com.example.calculatorxxx.vm.CalculatorViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: CalculatorViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpViewModel()
        setUpInputEditText()
        setUpDigitButtons()
        setUpOperationButtons()
    }

    private fun setUpViewModel() {
        viewModel = ViewModelProvider(this)[CalculatorViewModel::class.java]

        viewModel.expression.observe(this) { newValue ->
            binding.inputEditText.setText(newValue)
        }

        viewModel.selectionStart.observe(this) { newStart ->
            binding.inputEditText.setSelection(newStart, binding.inputEditText.selectionEnd)
        }

        viewModel.selectionEnd.observe(this) { newEnd ->
            binding.inputEditText.setSelection(binding.inputEditText.selectionStart, newEnd)
        }

        viewModel.currentResult.observe(this){ it ->
            binding.resultTextView.text = it
        }
    }

    private fun setUpInputEditText() {
        val inputEditText = binding.inputEditText
        inputEditText.showSoftInputOnFocus = false
        inputEditText.requestFocus()

        inputEditText.setOnClickListener {
            viewModel.setNewSelection(
                inputEditText.selectionStart,
                0,
                viewModel.expression.value!!.length
            )
        }

        inputEditText.setText(viewModel.expression.value)
        inputEditText.setSelection(
            viewModel.selectionStart.value ?: 0,
            viewModel.selectionEnd.value ?: 0
        )
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
                viewModel.insert(
                    button.tag.toString(),
                    binding.inputEditText.selectionStart,
                    binding.inputEditText.selectionEnd
                )
            }
        }
    }

    private fun setUpOperationButtons() {

        val buttons = listOf(
            binding.divideButton,
            binding.minusButton,
            binding.plusButton,
            binding.multiplyButton,
            binding.percentButton
        )

        buttons.forEach { button ->
            button.setOnClickListener {
                viewModel.insertOperation(
                    button.tag.toString(),
                    binding.inputEditText.selectionStart,
                    binding.inputEditText.selectionEnd
                )
            }
        }

        binding.removeButton.setOnClickListener {
            viewModel.remove()
        }

        binding.commaButton.setOnClickListener {
            viewModel.insertComma(
                binding.inputEditText.selectionStart,
                binding.inputEditText.selectionEnd
            )
        }

        binding.equalsButton.setOnClickListener {
            viewModel.calculate(viewModel.expression.value.toString())
        }
    }
}