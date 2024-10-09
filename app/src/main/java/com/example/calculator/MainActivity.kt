package com.example.calculator

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var display: TextView
    private lateinit var operationDisplay: TextView
    private var currentInput = StringBuilder()
    private var currentOperator: String? = null
    private var firstOperand: Double? = null
    private var lastResult: Double? = null
    private var isNewCalculation = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        display = findViewById(R.id.display)
        operationDisplay = findViewById(R.id.operation_display)

        setupNumberButtons()
        setupOperatorButtons()
        setupSpecialButtons()
    }

    private fun setupNumberButtons() {
        val numberIds = listOf(R.id.button_0, R.id.button_1, R.id.button_2, R.id.button_3,
                               R.id.button_4, R.id.button_5, R.id.button_6, R.id.button_7,
                               R.id.button_8, R.id.button_9)
        
        numberIds.forEach { id ->
            findViewById<Button>(id).setOnClickListener {
                if (isNewCalculation) {
                    currentInput.clear()
                    isNewCalculation = false
                }
                currentInput.append((it as Button).text)
                updateDisplay()
            }
        }
    }

    private fun setupOperatorButtons() {
        val operatorIds = listOf(R.id.button_plus, R.id.button_minus, R.id.button_multiply, R.id.button_divide)
        
        operatorIds.forEach { id ->
            findViewById<Button>(id).setOnClickListener {
                handleOperator((it as Button).text.toString())
            }
        }

        findViewById<Button>(R.id.button_equals).setOnClickListener {
            calculateResult()
        }
    }

    private fun setupSpecialButtons() {
        findViewById<Button>(R.id.button_clear).setOnClickListener {
            clearAll()
        }

        findViewById<Button>(R.id.button_clear_entry).setOnClickListener {
            clearEntry()
        }

        findViewById<Button>(R.id.button_backspace).setOnClickListener {
            backspace()
        }

        findViewById<Button>(R.id.button_decimal).setOnClickListener {
            addDecimal()
        }

        findViewById<Button>(R.id.button_sign).setOnClickListener {
            changeSign()
        }
    }

    private fun updateDisplay() {
        display.text = if (currentInput.isEmpty()) "0" else currentInput.toString()
    }

    private fun updateOperationDisplay() {
        operationDisplay.text = when {
            firstOperand != null && currentOperator != null -> 
                "${formatResult(firstOperand!!)} $currentOperator"
            lastResult != null -> formatResult(lastResult!!)
            else -> ""
        }
    }

    private fun handleOperator(operator: String) {
        if (currentInput.isNotEmpty() || lastResult != null) {
            if (firstOperand == null) {
                firstOperand = currentInput.toString().toDoubleOrNull() ?: lastResult
                currentOperator = operator
                currentInput.clear()
                isNewCalculation = false
            } else {
                calculateResult()
                currentOperator = operator
            }
            updateOperationDisplay()
        } else if (firstOperand != null) {
            currentOperator = operator
            updateOperationDisplay()
        }
    }

    private fun calculateResult() {
        if (firstOperand != null && currentOperator != null && (currentInput.isNotEmpty() || lastResult != null)) {
            val secondOperand = currentInput.toString().toDoubleOrNull() ?: lastResult ?: return
            val result = when (currentOperator) {
                "+" -> firstOperand!! + secondOperand
                "-" -> firstOperand!! - secondOperand
                "×" -> firstOperand!! * secondOperand
                "÷" -> {
                    if (secondOperand == 0.0) {
                        display.text = "Error"
                        clearAll()
                        return
                    }
                    firstOperand!! / secondOperand
                }
                else -> return
            }
            display.text = formatResult(result)
            lastResult = result
            firstOperand = result
            currentInput.clear()
            currentOperator = null
            isNewCalculation = true
            updateOperationDisplay()
        }
    }

    private fun formatResult(result: Double): String {
        return if (result == result.toLong().toDouble()) {
            result.toLong().toString()
        } else {
            String.format("%.8f", result).trimEnd('0').trimEnd('.')
        }
    }

    private fun clearAll() {
        currentInput.clear()
        firstOperand = null
        currentOperator = null
        lastResult = null
        isNewCalculation = true
        updateDisplay()
        updateOperationDisplay()
    }

    private fun clearEntry() {
        currentInput.clear()
        updateDisplay()
    }

    private fun backspace() {
        if (currentInput.isNotEmpty()) {
            currentInput.deleteCharAt(currentInput.length - 1)
            updateDisplay()
        }
    }

    private fun addDecimal() {
        if (!currentInput.contains(".")) {
            if (currentInput.isEmpty() || isNewCalculation) {
                currentInput.clear()
                currentInput.append("0")
                isNewCalculation = false
            }
            currentInput.append(".")
            updateDisplay()
        }
    }

    private fun changeSign() {
        if (currentInput.isNotEmpty()) {
            if (currentInput[0] == '-') {
                currentInput.deleteCharAt(0)
            } else {
                currentInput.insert(0, "-")
            }
            updateDisplay()
        } else if (lastResult != null) {
            currentInput.append((-lastResult!!).toString())
            lastResult = null
            isNewCalculation = false
            updateDisplay()
        }
    }
}