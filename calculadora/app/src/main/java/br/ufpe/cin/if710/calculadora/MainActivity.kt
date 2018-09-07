package br.ufpe.cin.if710.calculadora

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity() {

    companion object {

        const val EXTRA_TEXT_INFO = "EXTRA_TEXT_INFO"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Passo 4
        savedInstanceState?.let {
            text_info.setText(it.getString(EXTRA_TEXT_INFO))
        }

        // Passo 1
        btn_0.setOnClickListener { clickOnButton(it as Button) }
        btn_1.setOnClickListener { clickOnButton(it as Button) }
        btn_2.setOnClickListener { clickOnButton(it as Button) }
        btn_3.setOnClickListener { clickOnButton(it as Button) }
        btn_4.setOnClickListener { clickOnButton(it as Button) }
        btn_5.setOnClickListener { clickOnButton(it as Button) }
        btn_6.setOnClickListener { clickOnButton(it as Button) }
        btn_7.setOnClickListener { clickOnButton(it as Button) }
        btn_8.setOnClickListener { clickOnButton(it as Button) }
        btn_9.setOnClickListener { clickOnButton(it as Button) }
        btn_Add.setOnClickListener { clickOnButton(it as Button) }
        btn_Divide.setOnClickListener { clickOnButton(it as Button) }
        btn_Dot.setOnClickListener { clickOnButton(it as Button) }
        btn_Multiply.setOnClickListener { clickOnButton(it as Button) }
        btn_Subtract.setOnClickListener { clickOnButton(it as Button) }
        btn_LParen.setOnClickListener { clickOnButton(it as Button) }
        btn_RParen.setOnClickListener { clickOnButton(it as Button) }
        btn_Power.setOnClickListener { clickOnButton(it as Button) }

        btn_Clear.setOnClickListener { text_calc.text.clear() }

        // Passo 2
        btn_Equal.setOnClickListener {

            val expression = text_calc.text.toString()

            // Passo 3 - try catch para tratarmos a exceção, disparando um Toast
            try {
                val result = eval(expression).toString()
                text_info.text = result
                text_calc.setText(result)

            } catch (e: Exception) {
                invalidExpression()
            }
        }
    }

    // Criei uma função inline para ser usada nos botões que adicionam um simbolo ao edittext
    val clickOnButton: (Button) -> Unit = {
        text_calc.setText(text_calc.text.toString() + it.text)
    }


    // Passo 4
    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putString(EXTRA_TEXT_INFO, text_info.text.toString())
        super.onSaveInstanceState(outState)
    }

    //Como usar a função:
    // eval("2+2") == 4.0
    // eval("2+3*4") = 14.0
    // eval("(2+3)*4") = 20.0
    //Fonte: https://stackoverflow.com/a/26227947
    fun eval(str: String): Double {
        return object : Any() {
            var pos = -1
            var ch: Char = ' '
            fun nextChar() {
                val size = str.length
                ch = if ((++pos < size)) str.get(pos) else (-1).toChar()
            }

            fun eat(charToEat: Char): Boolean {
                while (ch == ' ') nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < str.length) throw RuntimeException("Caractere inesperado: " + ch)
                return x
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            // | number | functionName factor | factor `^` factor
            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    if (eat('+'))
                        x += parseTerm() // adição
                    else if (eat('-'))
                        x -= parseTerm() // subtração
                    else
                        return x
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    if (eat('*'))
                        x *= parseFactor() // multiplicação
                    else if (eat('/'))
                        x /= parseFactor() // divisão
                    else
                        return x
                }
            }

            fun parseFactor(): Double {
                if (eat('+')) return parseFactor() // + unário
                if (eat('-')) return -parseFactor() // - unário
                var x: Double
                val startPos = this.pos
                if (eat('(')) { // parênteses
                    x = parseExpression()
                    eat(')')
                } else if ((ch in '0'..'9') || ch == '.') { // números
                    while ((ch in '0'..'9') || ch == '.') nextChar()
                    x = java.lang.Double.parseDouble(str.substring(startPos, this.pos))
                } else if (ch in 'a'..'z') { // funções
                    while (ch in 'a'..'z') nextChar()
                    val func = str.substring(startPos, this.pos)
                    x = parseFactor()
                    if (func == "sqrt")
                        x = Math.sqrt(x)
                    else if (func == "sin")
                        x = Math.sin(Math.toRadians(x))
                    else if (func == "cos")
                        x = Math.cos(Math.toRadians(x))
                    else if (func == "tan")
                        x = Math.tan(Math.toRadians(x))
                    else
                        throw RuntimeException("Função invalida")
                } else {
                    throw RuntimeException("Caractere inesperado: " + ch.toChar())
                }
                if (eat('^')) x = Math.pow(x, parseFactor()) // potência
                return x
            }
        }.parse()
    }

    fun invalidExpression() {
        Toast.makeText(applicationContext, "Função desconhecida", Toast.LENGTH_SHORT).show()
        text_calc.text.clear()
    }
}
