package com.example.calculator

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu

class MainActivity : AppCompatActivity() {
    private lateinit var display: TextView
    private lateinit var operationDisplay: TextView
    private lateinit var menuButton: ImageButton
    private lateinit var calculatorModeText: TextView
    private lateinit var standardCalculator: View
    private lateinit var currencyConverter: View
    private lateinit var amountFrom: EditText
    private lateinit var amountTo: EditText
    private lateinit var spinnerFrom: Spinner
    private lateinit var spinnerTo: Spinner

    private var currentInput = StringBuilder()
    private var currentOperator: String? = null
    private var firstOperand: Double? = null
    private var lastResult: Double? = null
    private var isNewCalculation = true

    private val exchangeRates = mapOf(
        "USD" to 1.0,
        "EUR" to 0.84,
        "GBP" to 0.72,
        "JPY" to 110.33,
        "VND" to 25440.0  // Updated VND exchange rate
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupNumberButtons()
        setupOperatorButtons()
        setupSpecialButtons()
        setupMenuButton()
        setupCurrencyConverter()
    }

    private fun initializeViews() {
        display = findViewById(R.id.display)
        operationDisplay = findViewById(R.id.operation_display)
        menuButton = findViewById(R.id.menu_button)
        calculatorModeText = findViewById(R.id.calculator_mode)
        standardCalculator = findViewById(R.id.standard_calculator)
        currencyConverter = findViewById(R.id.currency_converter)
        amountFrom = findViewById(R.id.amount_from)
        amountTo = findViewById(R.id.amount_to)
        spinnerFrom = findViewById(R.id.spinner_from)
        spinnerTo = findViewById(R.id.spinner_to)
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

    private fun setupMenuButton() {
        menuButton.setOnClickListener {
            showPopupMenu()
        }
    }

    private fun showPopupMenu() {
        val popup = PopupMenu(this, menuButton)
        popup.menuInflater.inflate(R.menu.calculator_menu, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.standard_mode -> switchToStandardMode()
                R.id.currency_mode -> switchToCurrencyMode()
            }
            true
        }
        popup.show()
    }

    private fun switchToStandardMode() {
        calculatorModeText.text = "Standard"
        standardCalculator.visibility = View.VISIBLE
        currencyConverter.visibility = View.GONE
    }

    private fun switchToCurrencyMode() {
        calculatorModeText.text = "Currency Converter"
        standardCalculator.visibility = View.GONE
        currencyConverter.visibility = View.VISIBLE
    }

    private fun setupCurrencyConverter() {
        val currencies = arrayOf("USD", "EUR", "GBP", "JPY", "VND")  // Added VND to the array
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFrom.adapter = adapter
        spinnerTo.adapter = adapter

        spinnerFrom.setSelection(0)
        spinnerTo.setSelection(1)

        amountFrom.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty() && amountFrom.hasFocus()) {
                    convertCurrency(true)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        amountTo.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty() && amountTo.hasFocus()) {
                    convertCurrency(false)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        spinnerFrom.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                convertCurrency(true)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerTo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                convertCurrency(true)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun convertCurrency(isFromCurrency: Boolean) {
        val fromCurrency = spinnerFrom.selectedItem.toString()
        val toCurrency = spinnerTo.selectedItem.toString()

        if (isFromCurrency) {
            val fromAmount = amountFrom.text.toString().toDoubleOrNull() ?: 0.0
            val toAmount = fromAmount * (exchangeRates[toCurrency]!! / exchangeRates[fromCurrency]!!)
            amountTo.setText(String.format("%.2f", toAmount))
        } else {
            val toAmount = amountTo.text.toString().toDoubleOrNull() ?: 0.0
            val fromAmount = toAmount * (exchangeRates[fromCurrency]!! / exchangeRates[toCurrency]!!)
            amountFrom.setText(String.format("%.2f", fromAmount))
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
