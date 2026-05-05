package com.example.prakt16_2_v5_storchevaya

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var etLogin: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnSave: Button
    private lateinit var btnLoad: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etLogin = findViewById(R.id.etLogin)
        etPassword = findViewById(R.id.etPassword)
        btnSave = findViewById(R.id.btnSave)
        btnLoad = findViewById(R.id.btnLoad)
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        btnSave.setOnClickListener {
            val login = etLogin.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (login.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Заполните все поля!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            saveUser(login, password)

            sharedPreferences.edit().apply {
                putString("lastLogin", login)
                apply()
            }

            Toast.makeText(this, "Пользователь $login сохранён!", Toast.LENGTH_SHORT).show()

            showAlertAndGoToCurrencyScreen(login, password)
        }

        btnLoad.setOnClickListener {
            val login = etLogin.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (login.isEmpty()) {
                Toast.makeText(this, "Введите логин!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val savedPassword = getUserPassword(login)

            if (savedPassword.isEmpty()) {
                Toast.makeText(this, "Пользователь $login не найден!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (savedPassword != password) {
                Toast.makeText(this, "Неверный пароль!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            sharedPreferences.edit().apply {
                putString("lastLogin", login)
                apply()
            }

            showAlertAndGoToCurrencyScreen(login, password)
        }

        loadLastUser()
    }

    private fun saveUser(login: String, password: String) {
        val usersJson = sharedPreferences.getString("users", "{}") ?: "{}"
        val usersObject = JSONObject(usersJson)

        val userData = JSONObject()
        userData.put("password", password)
        userData.put("lastLogin", System.currentTimeMillis())

        usersObject.put(login, userData)

        sharedPreferences.edit().apply {
            putString("users", usersObject.toString())
            apply()
        }
    }

    private fun getUserPassword(login: String): String {
        val usersJson = sharedPreferences.getString("users", "{}") ?: "{}"
        val usersObject = JSONObject(usersJson)

        return if (usersObject.has(login)) {
            usersObject.getJSONObject(login).getString("password")
        } else {
            ""
        }
    }

    private fun loadLastUser() {
        val lastLogin = sharedPreferences.getString("lastLogin", "") ?: ""
        if (lastLogin.isNotEmpty()) {
            val password = getUserPassword(lastLogin)
            etLogin.setText(lastLogin)
            etPassword.setText(password)
        }
    }

    private fun showAlertAndGoToCurrencyScreen(login: String, password: String) {
        try {
            AlertDialog.Builder(this)
                .setTitle("Переход на следующий экран")
                .setMessage("Логин: $login\nПароль: $password\n\nВы будете перенаправлены на экран валют")
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                    val intent = Intent(this, SecondActivity::class.java)
                    intent.putExtra("login", login)
                    intent.putExtra("password", password)
                    startActivity(intent)
                }
                .setNegativeButton("Отмена") { dialog, _ ->
                    dialog.dismiss()
                }
                .setCancelable(false)
                .show()
        } catch (e: Exception) {
            Log.e("MainActivity", "Ошибка: ${e.message}", e)
            Toast.makeText(this, "Ошибка: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}