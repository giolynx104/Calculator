package com.example.calculator

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var display: TextView
    private var currentInput = StringBuilder()
    private var currentOperator: String? = null
    private var firstOperand: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        display = findViewById(R.id.display)

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
        display.text = currentInput.toString()
    }

    private fun handleOperator(operator: String) {
        if (currentInput.isNotEmpty()) {
            if (firstOperand == null) {
                firstOperand = currentInput.toString().toDouble()
                currentOperator = operator
                currentInput.clear()
            } else {
                calculateResult()
                currentOperator = operator
            }
        }
    }

    private fun calculateResult() {
        if (firstOperand != null && currentOperator != null && currentInput.isNotEmpty()) {
            val secondOperand = currentInput.toString().toDouble()
            val result = when (currentOperator) {
                "+" -> firstOperand!! + secondOperand
                "-" -> firstOperand!! - secondOperand
                "×" -> firstOperand!! * secondOperand
                "÷" -> firstOperand!! / secondOperand
                else -> return
            }
            display.text = result.toString()
            firstOperand = result
            currentInput.clear()
            currentInput.append(result)
            currentOperator = null
        }
    }

    private fun clearAll() {
        currentInput.clear()
        firstOperand = null
        currentOperator = null
        updateDisplay()
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
            if (currentInput.isEmpty()) currentInput.append("0")
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
        }
    }
}