package com.itskidan.currencyexchangeapp.ui.screens.calculator

import androidx.lifecycle.ViewModel
import com.itskidan.currencyexchangeapp.application.App
import com.itskidan.currencyexchangeapp.domain.Interactor
import timber.log.Timber
import java.math.BigDecimal
import javax.inject.Inject

class CalculatorViewModel : ViewModel() {
    @Inject
    lateinit var interactor: Interactor


    init {
        App.instance.dagger.inject(this)
    }


    suspend fun saveSelectedLastState(code: String, value: String) {
        interactor.saveSelectedLastState(code, value)
    }

    fun validateCalcInput(textState: String, key: String): String {
        val delimiters = charArrayOf('÷', '×', '—', '+')
        val parts = textState.split(*delimiters)
        val lastPart = parts.lastOrNull().orEmpty()
        val otherPart = textState.dropLast(lastPart.length)

        return when (key) {
            "C" -> ""
            "+/-" -> {
                val newLastPart = if (lastPart.startsWith("-")) lastPart.drop(1) else "-$lastPart"
                otherPart + newLastPart
            }

            "%" -> {
                val newLastPart = lastPart.ifEmpty { "0" }.replace(',', '.').toDouble() / 100
                otherPart + newLastPart.toString().replace('.', ',')
            }

            "÷", "×", "—", "+" -> validateOperationInput(key, textState)
            "=" -> textState
            "X" -> textState.dropLast(1)
            "," -> {
                if (lastPart.isEmpty() || lastPart == "-") {
                    otherPart + lastPart + "0,"
                } else {
                    if (',' in lastPart) textState else "$textState,"
                }
            }

            "0" -> if (lastPart == "0") textState else textState + key
            else -> if (lastPart == "0") otherPart + lastPart.drop(1) + key else otherPart + lastPart + key
        }
    }

    private fun validateOperationInput(key: String, textState: String): String {
        val mathOperations = setOf('%', '÷', '×', '—', '+')
        return when {
            textState.isEmpty() -> textState
            textState.last() == ',' -> textState.dropLast(1) + key
            textState.last() == '-' -> textState.dropLast(1)
            textState.last() in mathOperations -> textState.dropLast(1) + key
            else -> textState + key
        }
    }

    fun calculateExpression(expression: String): String {
        val delimiters = charArrayOf('÷', '×', '—', '+')

        if (expression.isEmpty() || delimiters.contains(expression.last()) || expression.last() == '-') {
            return ""
        }

        val operands = expression.split(*delimiters).map { it.replace(',', '.') }.toMutableList()
        val operators = expression.filter { it in delimiters }

        if (operators.isEmpty() || operands.isEmpty()) {
            return ""
        }
        return calculateWithPriority(operands, operators).replace('.', ',')
    }

    private fun calculateWithPriority(operands: MutableList<String>, operators: String): String {
        var existOperators = operators
        val prioritizedOperators = listOf("×", "÷")
        val secondaryOperators = listOf("—", "+")
        prioritizedOperators.forEach { op ->
            while (existOperators.contains(op)) {
                val index = existOperators.indexOf(op)
                if (BigDecimal(operands[index + 1]) == BigDecimal("0") && op == "÷") {
                    return "Divide by zero"
                }

                val result = mathOperation(operands[index], operands[index + 1], op)
                operands[index] = result
                operands.removeAt(index + 1)
                existOperators = existOperators.removeRange(index..index)
            }
        }

        for (op in secondaryOperators) {
            while (existOperators.contains(op)) {
                val index = existOperators.indexOf(op)
                val result = mathOperation(operands[index], operands[index + 1], op)
                operands[index] = result
                operands.removeAt(index + 1)
                existOperators = existOperators.removeRange(index..index)
            }
        }
        return operands.firstOrNull() ?: "Error"
    }

    private fun mathOperation(num1: String, num2: String, operation: String): String {
        val n1 = BigDecimal(num1)
        val n2 = BigDecimal(num2)
        return when (operation) {
            "+" -> (n1 + n2).toString()
            "—" -> (n1 - n2).toString()
            "×" -> (n1 * n2).toString()
            "÷" -> {
                (n1 / n2).toString()
            }

            else -> "0.0"
        }
    }

    suspend fun updateTotalBalanceCurrencyByCode(code: String, value: String) {
        interactor.updateTotalBalanceCurrencyByCode(code, value.toDoubleOrNull() ?: 0.0)
    }

}