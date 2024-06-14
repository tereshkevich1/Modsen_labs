package com.example.calculatorxxx

import android.os.Bundle
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.calculatorxxx.databinding.ActivityMainBinding
import com.example.calculatorxxx.vm.CalculatorViewModel
import com.example.calculatorxxx.vm.ExpressionErrorHandler

class MainActivity : AppCompatActivity(), ExpressionErrorHandler {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: CalculatorViewModel
    private lateinit var inputEditText: EditText
    private lateinit var resultTextView: TextView
    private lateinit var scrollView: HorizontalScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        inputEditText = binding.inputEditText
        resultTextView = binding.resultTextView
        scrollView = binding.scrollView
        setContentView(binding.root)

        setUpViewModel()
        setUpInputEditText()
        setUpDigitButtons()
        setUpMathOperationButtons()
        setUpDefaultOperationButtons()
        setUpScrollView()
    }

    private fun setUpViewModel() {
        viewModel = ViewModelProvider(this)[CalculatorViewModel::class.java]

        viewModel.expression.observe(this) { newValue ->
            inputEditText.setText(newValue)
        }

        viewModel.selectionStart.observe(this) { newStart ->
            inputEditText.setSelection(newStart, binding.inputEditText.selectionEnd)
        }

        viewModel.selectionEnd.observe(this) { newEnd ->
            inputEditText.setSelection(binding.inputEditText.selectionStart, newEnd)
        }

        viewModel.currentResult.observe(this) { it ->
            binding.resultTextView.text = it
        }
    }

    private fun setUpInputEditText() {
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

    private fun setUpScrollView() {
        scrollView.post {
            scrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT)
        }
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
            binding.buttonNine
        )

        buttons.forEach { button ->
            button.setOnClickListener {
                viewModel.insert(
                    button.tag.toString(),
                    binding.inputEditText.selectionStart,
                    binding.inputEditText.selectionEnd
                )
                viewModel.calculate(this)
            }
        }
    }

    private fun setUpMathOperationButtons() {

        val buttons = listOf(
            binding.divideButton,
            binding.plusButton,
            binding.multiplyButton,
            binding.percentButton
        )

        buttons.forEach { button ->
            button.setOnClickListener {
                viewModel.insertOperation(
                    button.tag.toString(),
                    inputEditText.selectionStart,
                    inputEditText.selectionEnd
                )
            }
        }

        binding.minusButton.setOnClickListener {
            viewModel.insertMinus(
                inputEditText.selectionStart,
                inputEditText.selectionEnd
            )
        }

    }

    private fun setUpDefaultOperationButtons(){
        binding.removeButton.setOnClickListener {
            viewModel.remove()
            viewModel.calculate(this)
        }

        binding.removeButton.setOnLongClickListener {
            viewModel.removeAll()
            true
        }

        binding.commaButton.setOnClickListener {
            viewModel.insertComma(
                inputEditText.selectionStart,
                inputEditText.selectionEnd
            )
            viewModel.calculate(this)
        }

        binding.equalsButton.setOnClickListener {
            viewModel.calculateFromEqualsButton(this)
            scrollView.post {
                scrollView.fullScroll(HorizontalScrollView.FOCUS_LEFT)
            }
        }

        binding.plusMinusButton.setOnClickListener {
            viewModel.toggleSign(
                inputEditText.selectionStart,
                inputEditText.selectionEnd
            )
            viewModel.calculate(this)
        }
    }



    override fun onError(errorMessage: String) {
        inputEditText.setTextColor(getColor(R.color.remove_button_background_color))
        resultTextView.setTextColor(getColor(R.color.remove_button_background_color))
        resultTextView.text = errorMessage
    }

    override fun onSuccess(result: String) {
        inputEditText.setTextColor(getColor(R.color.white))
        resultTextView.setTextColor(getColor(R.color.result_text_color))
        resultTextView.text = result
    }
}