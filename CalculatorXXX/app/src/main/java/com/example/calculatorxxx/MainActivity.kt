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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[CalculatorViewModel::class.java]

        viewModel.expression.observe(this) { newValue ->
            binding.inputEditText.setText(newValue)
        }

        binding.inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.inputEditText.setSelection(viewModel.cursorPosition)
            }
        })

        binding.inputEditText.setOnClickListener {
            viewModel.updateCursorPosition(binding.inputEditText.selectionStart)
        }

        setUpInputEditText()
        setUpDigitButton()
        setUpOperationButtons()

        setContentView(binding.root)
    }

    private fun setUpInputEditText() {
        binding.inputEditText.showSoftInputOnFocus = false
        binding.inputEditText.requestFocus()
    }


    private fun setUpDigitButton() {
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
            binding.buttonNine
        )

        buttons.forEach { button ->
            button.setOnClickListener {
                viewModel.insert(button.tag.toString())
            }
        }
    }

    private fun setUpOperationButtons() {
        binding.removeButton.setOnClickListener {
            viewModel.remove()
        }
    }

}