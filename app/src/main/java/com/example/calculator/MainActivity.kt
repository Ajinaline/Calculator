package com.example.calculator

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.tooling.preview.Preview
import com.example.calculator.ui.theme.CalculatorTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    var equation = StringBuilder()

    fun typing(num: View){
        val button = num as Button
        equation.append(button.text)
        updateDisplay()
    }

    fun multiply(sign: View){
        equation.append("x")
        updateDisplay()
    }

    fun divide(sign: View){
        equation.append("/")
        updateDisplay()
    }

    fun del(sign: View){
        equation.clear()
        updateDisplay()
    }

    fun backspace(sign: View){
        equation.delete((equation.length-1), equation.length)
        updateDisplay()
    }

    fun bracket(sign: View){
        if (equation.substring(0) == ""){
            equation.append("(")
            updateDisplay()
        }

        else if (equation.substring(equation.length - 1) == "/"){
            equation.append("(")
            updateDisplay()
        }

        else if (equation.substring(equation.length - 1) == "X"){
            equation.append("(")
            updateDisplay()
        }

        else if (equation.substring(equation.length - 1) == "-"){
            equation.append("(")
            updateDisplay()
        }

        else if (equation.substring(equation.length - 1) == "+"){
            equation.append("(")
            updateDisplay()
        }

        else if (equation.substring(equation.length - 1) == "%"){
            equation.append("(")
            updateDisplay()
        }

        else if (equation.substring(equation.length - 1) == "("){
            equation.append("(")
            updateDisplay()
        }

        else{
            equation.append(")")
            updateDisplay()
        }

    }


    fun updateDisplay(){
        var displayText = findViewById<TextView>(R.id.textView1)
        displayText.text = equation.toString()
    }

}



