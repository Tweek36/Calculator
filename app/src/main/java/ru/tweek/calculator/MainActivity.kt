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
    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val input: TextView = findViewById(R.id.input)
        val output: TextView = findViewById(R.id.output)
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
        val buttonEq: Button = findViewById(R.id.button_eq)
        val buttonPlus: Button = findViewById(R.id.button_plus)
        val buttonMinus: Button = findViewById(R.id.button_minus)
        val buttonMul: Button = findViewById(R.id.button_mul)
        val buttonDiv: Button = findViewById(R.id.button_div)
        val buttonClear: Button = findViewById(R.id.button_clear)

        listOf(button0, button1, button2, button3, button4, button5, button6, button7, button8, button9).forEach { button ->
            button.setOnClickListener {
                input.append(button.text.toString())
            }
        }
        var action: String = "+"
        listOf(
            buttonPlus, buttonMinus, buttonDiv, buttonMul
        ).forEach { button: Button ->
            button.setOnClickListener {
                if (input.text.isNotEmpty()){
                    output.text = input.text.toString()
                    input.text = ""
                }
                action = button.text.toString()
            }
        }
        buttonEq.setOnClickListener {
            if (input.text.isEmpty()) {
                return@setOnClickListener
            }
            if (action == "+") {
                output.text = (output.text.toString().toDouble() + input.text.toString().toDouble()).toString()
            }
            if (action == "-") {
                output.text = (output.text.toString().toDouble() - input.text.toString().toDouble()).toString()
            }
            if (action == "*") {
                output.text = (output.text.toString().toDouble() * input.text.toString().toDouble()).toString()
            }
            if (action == "/") {
                output.text = (output.text.toString().toDouble() / input.text.toString().toDouble()).toString()
            }
            input.text = ""
        }
        buttonClear.setOnClickListener {
            input.text = ""
            output.text = "0"
        }
    }
}