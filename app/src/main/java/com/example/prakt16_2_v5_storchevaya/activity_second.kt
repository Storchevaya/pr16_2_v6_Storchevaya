package com.example.prakt16_2_v5_storchevaya

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SecondActivity : AppCompatActivity() {

    private lateinit var spinnerCurrency: Spinner
    private lateinit var etAmount: EditText
    private lateinit var btnCalculate: Button
    private lateinit var tvResult: TextView
    private lateinit var tvSaved: TextView
    private lateinit var sharedPreferences: SharedPreferences

    private val currencies = arrayOf("Доллар США (USD)", "Евро (EUR)", "Юань (CNY)")
    private val rates = doubleArrayOf(97.5, 105.3, 13.4)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        spinnerCurrency = findViewById(R.id.spinnerCurrency)
        etAmount = findViewById(R.id.etAmount)
        btnCalculate = findViewById(R.id.btnCalculate)
        tvResult = findViewById(R.id.tvResult)
        tvSaved = findViewById(R.id.tvSaved)
        sharedPreferences = getSharedPreferences("CurrencyPrefs", MODE_PRIVATE)

        val login = intent.getStringExtra("login") ?: "Не указан"
        val password = intent.getStringExtra("password") ?: "Не указан"

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, currencies)
        spinnerCurrency.adapter = adapter

        val lastPosition = sharedPreferences.getInt("lastCurrencyPosition_$login", 0)
        spinnerCurrency.setSelection(lastPosition)

        tvSaved.text = "Пользователь: $login"

        btnCalculate.setOnClickListener {
            calculateAndSave()
        }
    }

    private fun calculateAndSave() {
        val amountStr = etAmount.text.toString()
        if (amountStr.isEmpty()) {
            tvResult.text = "Введите сумму!"
            return
        }

        val login = intent.getStringExtra("login") ?: "user"
        val amount = amountStr.toDouble()
        val position = spinnerCurrency.selectedItemPosition
        val result = amount / rates[position]

        sharedPreferences.edit().apply {
            putInt("lastCurrencyPosition_$login", position)
            putFloat("lastAmount_$login", amount.toFloat())
            putFloat("lastResult_$login", result.toFloat())
            apply()
        }

        val currencyName = currencies[position].split(" ")[0]
        tvResult.text = String.format("Результат: %.2f %s", result, currencyName)

        // Сохраняем информацию в TextView
        tvSaved.text = "Пользователь: $login\nПоследний расчёт: %.2f руб → %.2f $currencyName".format(amount, result)
    }
}