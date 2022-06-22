package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.widget.doOnTextChanged
import com.example.calculator.databinding.ActivityMainBinding
import org.mozilla.javascript.Context

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var operators: List<Button>
    var openParentheses = 0
    val operationsRegex = Regex("""[+\-÷×]""")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            clearB.setOnClickListener { clear() }
            parenthesesB.setOnClickListener { addParentheses() }
            deleteB.setOnClickListener { deleteLetter() }
            commaB.setOnClickListener { addComma() }
            equalB.setOnClickListener { equalButtonPressed() }
            percentB.setOnClickListener { addPercentSign() }
            calculationInputTV.doOnTextChanged { text, _, _, _ ->
                if (!text?.takeLast(1)!!.matches(operationsRegex) &&
                    text.contains(operationsRegex)
                ) {
                    preliminaryResultTV.text = getCalculatedResult()
                } else if (
                    text.takeLast(1).matches(operationsRegex) &&
                    text.count { it.toString().matches(operationsRegex) } == 1
                ) {
                    preliminaryResultTV.text = ""
                }
            }
        }
        operators = with(binding) {
            listOf(subtractB, plusB, multiplyB, divideB)
        }

    }

    private fun addPercentSign() {
        if (!getInputText().takeLast(1).matches(operationsRegex)) {
            addInputText("%")
        }
    }

    fun addDigit(view: View) {
        if (view is Button) {
            if (getInputText() == "0") {
                setInputText(view.text)
            } else addInputText(view.text)
        }
    }

    fun addOperator(view: View) {
        if (view is Button) {
            val text = getInputText()
            if (!text.takeLast(1).matches(operationsRegex)) {
                addInputText(view.text)
            }
        }
    }

    fun clear() {
        setInputText("")
        binding.preliminaryResultTV.text = ""
    }

    fun addParentheses() {
        val text = getInputText()
        if (openParentheses == 0 ||
            text.last() == '('
        ) {
            openParentheses++
            addInputText("(")


        } else if (!text.takeLast(1).matches(operationsRegex)) {
            openParentheses--
            addInputText(")")
        }
    }

    fun deleteLetter() {
        if (getInputText().length > 1) {
            setInputText(getInputText().dropLast(1))
        } else clear()
    }

    fun addComma() {
        val text = getInputText()
        val lastNumber = text.split(operationsRegex).last()
        if (text.takeLast(1) matches operationsRegex) {
            addInputText("0,")
        } else if (!lastNumber.contains(',')) {
            addInputText(",")
        }
    }

    fun equalButtonPressed() {
        setInputText(getCalculatedResult())
        binding.preliminaryResultTV.text = ""
    }

    fun getCalculatedResult(): String {
        val text = getInputText()
            .replace("×", "*")
            .replace("÷", "/")
            .replace("%", "/100")

        val context = Context.enter()
        context.optimizationLevel = -1

        val calculatingScript = context.initStandardObjects()
        val result = context.evaluateString(
            calculatingScript,
            text,
            "Javascript",
            1,
            null
        ).toString()
        return if (result.takeLast(2) == ".0") {
            result.dropLast(2)
        } else result
    }

    fun getInputText() = binding.calculationInputTV.text.toString()

    fun setInputText(newText: CharSequence) {
        binding.calculationInputTV.text = newText
    }

    fun addInputText(additionalText: CharSequence) {
        setInputText(getInputText() + additionalText)
    }
}