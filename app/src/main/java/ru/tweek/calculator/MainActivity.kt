package ru.tweek.calculator

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import java.util.LinkedList
import java.util.Stack
import kotlin.math.pow
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val input: TextView = findViewById(R.id.input)
        val output: TextView = findViewById(R.id.output)
        val prevResults: ListView = findViewById(R.id.list)
        val button0: Button = findViewById(R.id.button_0)
        val button1: Button = findViewById(R.id.button_1)
        val button2: Button = findViewById(R.id.button_2)
        val button3: Button = findViewById(R.id.button_3)
        val button4: Button = findViewById(R.id.button_4)
        val button5: Button = findViewById(R.id.button_5)
        val button6: Button = findViewById(R.id.button_6)
        val button7: Button = findViewById(R.id.button_7)
        val button8: Button = findViewById(R.id.button_8)
        val button9: Button = findViewById(R.id.button_9)
        val buttonDot: Button = findViewById(R.id.button_dot)
        val buttonDel: Button = findViewById(R.id.button_del)
        val buttonEq: Button = findViewById(R.id.button_eq)
        val buttonPlus: Button = findViewById(R.id.button_plus)
        val buttonMinus: Button = findViewById(R.id.button_minus)
        val buttonMul: Button = findViewById(R.id.button_mul)
        val buttonDiv: Button = findViewById(R.id.button_div)
        val buttonParenthesisL: Button = findViewById(R.id.button_parenthesis_l)
        val buttonParenthesisR: Button = findViewById(R.id.button_parenthesis_r)
        val buttonPow: Button = findViewById(R.id.button_pow)
        val buttonRoot: Button = findViewById(R.id.button_root)
        val buttonDivRem: Button = findViewById(R.id.button_divrem)
        val buttonClear: Button = findViewById(R.id.button_clear)

        val results: MutableList<String> = mutableListOf()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, results)
        prevResults.adapter = adapter

        prevResults.setOnItemClickListener { _, _, position, _ ->
            val text = prevResults.getItemAtPosition(position).toString()
            input.text = text
            output.text = text
        }

        listOf(button0, button1, button2, button3, button4, button5, button6, button7, button8, button9).forEach { button ->
            button.setOnClickListener {
                if (input.text.isNotEmpty() && input.text.last() == ')') {
                    return@setOnClickListener
                }
                if (input.text.length > 2 &&
                    (button.text.toString() == "0") &&
                    (input.text.last() == '0') &&
                    !input.text[input.text.length - 2].isDigit()) {
                    return@setOnClickListener
                }
                if (input.text.toString() == "0") {
                    input.text = button.text
                } else {
                    input.append(button.text)
                }

                val lexer = Lexer(input.text.toString())
                val parser = Parser(lexer)
                try {
                    val expression = parser.parseExpression()
                    val result = formatDouble(expression.evaluate())
                    output.text = result
                } catch (e: IllegalArgumentException) {
                    Toast.makeText(this, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        buttonRoot.setOnClickListener {
            if (input.text.toString() == "0") {
                input.text = "√("
                return@setOnClickListener
            }
            if (input.text.last().isDigit()) {
                return@setOnClickListener
            }
            if (input.text.last() == ')') (
                return@setOnClickListener
            )
            input.append("√(")
        }

        listOf(
            buttonPlus, buttonMinus, buttonDiv, buttonMul, buttonDivRem, buttonPow
        ).forEach { button: Button ->
            button.setOnClickListener {
                if (input.text.isEmpty()) {
                    return@setOnClickListener
                }
                if (!input.text.last().isDigit() && input.text.last() != ')') {
                    return@setOnClickListener
                }
                input.append(button.text)
            }
        }
        buttonDot.setOnClickListener {
            if (input.text.isNotEmpty() && input.text.last().isDigit()) {
                input.append(".")
            }
        }
        buttonParenthesisL.setOnClickListener {
            if (input.text.toString() == "0") {
                input.text = "("
                return@setOnClickListener
            }
            if (input.text.isNotEmpty() && input.text.last().isDigit()) {
                return@setOnClickListener
            }
            if (input.text.isNotEmpty() && (input.text.last() == ')')) {
                return@setOnClickListener
            }
            if (input.text.isNotEmpty() && (input.text.last() == '.')) {
                return@setOnClickListener
            }
            input.append("(")
        }
        buttonParenthesisR.setOnClickListener {
            if (input.text.isEmpty()) {
                return@setOnClickListener
            }
            if (input.text.count { it == '(' } <= input.text.count { it == ')' }) {
                return@setOnClickListener
            }
            if (input.text.last().isDigit() || input.text.last() == ')') {
                input.append(")")
            }
        }
        buttonDel.setOnClickListener {
            if (input.text.isEmpty()) {
                return@setOnClickListener
            }
            input.text = input.text.substring(0, input.text.length - 1)
            if (input.text.isEmpty()) {
                output.text = ""
                return@setOnClickListener
            }
            if (!input.text.last().isDigit()) {
                return@setOnClickListener
            }
            val lexer = Lexer(input.text.toString())
            val parser = Parser(lexer)
            try {
                val expression = parser.parseExpression()
                val result = formatDouble(expression.evaluate())
                output.text = result
            } catch (e: IllegalArgumentException) {
                Toast.makeText(this, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
        buttonEq.setOnClickListener {
            if (output.text.isEmpty()) {
                return@setOnClickListener
            }
            input.text = output.text
            if (results.isNotEmpty() && results.first() == output.text) {
                return@setOnClickListener
            }
            adapter.insert(output.text.toString(), 0)
        }
        buttonClear.setOnClickListener {
            input.text = "0"
            output.text = "0"
        }
    }
}

sealed class Expression {
    abstract fun evaluate(): Double
}

data class Number(val value: Double) : Expression() {
    override fun evaluate(): Double = value
}

data class BinaryOperation(val left: Expression, val operator: Operator, val right: Expression) : Expression() {
    override fun evaluate(): Double {
        val leftValue = left.evaluate()
        val rightValue = right.evaluate()
        return operator.apply(leftValue, rightValue)
    }
}

data class UnaryOperation(val operator: Operator, val operand: Expression) : Expression() {
    override fun evaluate(): Double {
        val operandValue = operand.evaluate()
        return operator.apply(operandValue)
    }
}

// Operators
sealed class Operator {
    abstract fun apply(a: Double, b: Double): Double
    abstract fun apply(a: Double): Double
}

data class BinaryOperator(val symbol: Char) : Operator() {
    override fun apply(a: Double, b: Double): Double {
        return when (symbol) {
            '+' -> a + b
            '-' -> a - b
            '*' -> a * b
            '/' -> a / b
            '%' -> a % b
            '^' -> a.pow(b)
            else -> throw IllegalArgumentException("Unknown operator: $symbol")
        }
    }

    override fun apply(a: Double): Double {
        throw UnsupportedOperationException("Binary operator $symbol cannot be applied to a single operand.")
    }
}

data class UnaryOperator(val symbol: Char) : Operator() {
    override fun apply(a: Double, b: Double): Double {
        throw UnsupportedOperationException("Unary operator $symbol cannot be applied to two operands.")
    }

    override fun apply(a: Double): Double {
        return when (symbol) {
            '√' -> sqrt(a)
            else -> throw IllegalArgumentException("Unknown operator: $symbol")
        }
    }
}

// Lexer
class Lexer(private var input: String) {
    private var position = 0

    init {
        val openBrackets = input.count { it == '(' }
        val closeBrackets = input.count { it == ')' }
        val bracketsToAdd = if (openBrackets > closeBrackets) {
            ")".repeat(openBrackets - closeBrackets)
        } else {
            ""
        }
        input += bracketsToAdd
        input = input.replace(',', '.')
    }

    fun getNextToken(): Token {
        while (position < input.length && input[position].isWhitespace()) {
            position++
        }

        if (position >= input.length) {
            return EndToken
        }

        val char = input[position]
        position++

        return when {
            char.isDigit() || (char == '.' && position < input.length && input[position].isDigit()) -> {
                val number = StringBuilder(char.toString())
                while (position < input.length && (input[position].isDigit() || input[position] == '.')) {
                    number.append(input[position])
                    position++
                }
                NumberToken(number.toString().toDouble())
            }
            char == '+' || char == '-' || char == '*' || char == '/' || char == '%' || char == '^' -> {
                OperatorToken(BinaryOperator(char))
            }
            char == '√' -> {
                OperatorToken(UnaryOperator(char))
            }
            char == '(' -> LeftParenthesisToken
            char == ')' -> RightParenthesisToken
            else -> throw IllegalArgumentException("Invalid character: $char")
        }
    }
}


sealed class Token

data class NumberToken(val value: Double) : Token()

data class OperatorToken(val operator: Operator) : Token()

data object LeftParenthesisToken : Token()

data object RightParenthesisToken : Token()

data object EndToken : Token()

// Parser
class Parser(private val lexer: Lexer) {
    private val operatorStack = Stack<Token>()
    private val outputQueue = LinkedList<Token>()

    fun parseExpression(): Expression {
        while (true) {
            when (val token = lexer.getNextToken()) {
                is NumberToken -> outputQueue.add(token)
                is OperatorToken -> {
                    while (operatorStack.isNotEmpty() && hasHigherPrecedence(operatorStack.peek(), token)) {
                        outputQueue.add(operatorStack.pop())
                    }
                    operatorStack.push(token)
                }
                LeftParenthesisToken -> operatorStack.push(token)
                RightParenthesisToken -> {
                    while (operatorStack.isNotEmpty() && operatorStack.peek() != LeftParenthesisToken) {
                        outputQueue.add(operatorStack.pop())
                    }
                    if (operatorStack.isEmpty() || operatorStack.peek() != LeftParenthesisToken) {
                        throw IllegalArgumentException("Mismatched parentheses")
                    }
                    operatorStack.pop()
                }
                EndToken -> break
            }
        }

        while (operatorStack.isNotEmpty()) {
            if (operatorStack.peek() is LeftParenthesisToken) {
                throw IllegalArgumentException("Mismatched parentheses")
            }
            outputQueue.add(operatorStack.pop())
        }

        val expressionStack = Stack<Expression>()
        for (token in outputQueue) {
            when (token) {
                is NumberToken -> expressionStack.push(Number(token.value))
                is OperatorToken -> {
                    if (token.operator is BinaryOperator) {
                        if (expressionStack.size < 2) {
                            throw IllegalArgumentException("Not enough operands for operator ${token.operator.symbol}")
                        }
                        val right = expressionStack.pop()
                        val left = expressionStack.pop()
                        expressionStack.push(BinaryOperation(left, token.operator, right))
                    } else if (token.operator is UnaryOperator) {
                        if (expressionStack.isEmpty()) {
                            throw IllegalArgumentException("Not enough operands for operator ${token.operator.symbol}")
                        }
                        val operand = expressionStack.pop()
                        expressionStack.push(UnaryOperation(token.operator, operand))
                    }
                }
                else -> throw IllegalArgumentException("Unexpected token: $token")
            }
        }

        if (expressionStack.size != 1) {
            throw IllegalArgumentException("Invalid expression")
        }

        return expressionStack.pop()
    }

    private fun hasHigherPrecedence(operator1: Token, operator2: Token): Boolean {
        if (operator1 !is OperatorToken || operator2 !is OperatorToken) {
            return false
        }
        return getPrecedence(operator1.operator) >= getPrecedence(operator2.operator)
    }

    private fun getPrecedence(operator: Operator): Int {
        return when (operator) {
            is BinaryOperator -> {
                when (operator.symbol) {
                    '+', '-' -> 1
                    '*', '/', '%' -> 2
                    '^' -> 3
                    else -> 0
                }
            }
            is UnaryOperator -> 4
        }
    }
}

fun formatDouble(doubleValue: Double): String {
    val formattedValue = if (doubleValue == doubleValue.toInt().toDouble()) {
        String.format("%.0f", doubleValue)
    } else {
        String.format("%.1f", doubleValue)
    }
    return formattedValue
}