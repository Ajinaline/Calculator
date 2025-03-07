package com.example.calculator

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import net.objecthunter.exp4j.ExpressionBuilder

class MainActivity : ComponentActivity() {

    private var realTrick = ""
    private val equation = StringBuilder()

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Log.d("DEBUG", "Permission granted, picking contact now")
                pickContact()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    private val pickContactLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                result.data?.data?.let { contactUri ->
                    contentResolver.query(contactUri,
                        arrayOf(ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME),
                        null, null, null)?.use { cursor ->
                        if (cursor.moveToFirst()) {
                            val contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                            val contactName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
                            fetchContactNumber(contactId, contactName)
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val contact = findViewById<Button>(R.id.buttonperc)
        contact.setOnLongClickListener {
            Log.d("DEBUG", "Made it to longClick")
            handleLongClick()
            true
        }
    }

    private fun handleLongClick() {
        Log.d("DEBUG", "Handling long click")
        if (checkContactPermission()) {
            Log.d("DEBUG", "Contacts permission granted, picking contact")
            pickContact()
        } else {
            requestInfo()
        }
    }

    private fun checkContactPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestInfo() {
        requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
    }

    private fun pickContact() {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        pickContactLauncher.launch(intent)
    }

    private fun fetchContactNumber(contactId: String, contactName: String) {
        contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
            "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
            arrayOf(contactId),
            null
        )?.use { cursorPhone ->
            if (cursorPhone.moveToFirst()) {
                realTrick = cursorPhone.getString(cursorPhone.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
            } else {
                Toast.makeText(this, "No phone number found!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun typing(num: View) {
        val button = num as Button
        Log.d("DEBUG", "Button clicked: ${button.text}")
        equation.append(button.text)
        updateDisplay()
    }

    fun multiply(sign: View) {
        equation.append("x")
        updateDisplay()
    }

    fun divide(sign: View) {
        equation.append("/")
        updateDisplay()
    }

    fun add(sign: View) {
        equation.append("+")
        updateDisplay()
    }

    fun del(sign: View) {
        equation.clear()
        updateDisplay()
    }

    fun backspace(sign: View) {
        if (equation.isNotEmpty()) {
            equation.deleteCharAt(equation.length - 1)
        }
        updateDisplay()
    }

    fun eqls(sign: View) {
        if (realTrick.isNotEmpty()) {
            updateDisplayTrick()
        } else {
            arithmeticStuff()
        }
    }

    private fun arithmeticStuff() {
        val arithmetic = equation.toString().replace("x", "*")
        try {
            val result = eval(arithmetic)
            equation.clear().append(result)
            updateDisplay()
        } catch (e: Exception) {
            equation.clear().append("Error")
            updateDisplay()
        }
    }

    fun eval(expression: String): Double {
        return try {
            ExpressionBuilder(expression).build().evaluate()
        } catch (e: Exception) {
            Double.NaN
        }
    }

    fun bracket(sign: View) {
        val lastChar = if (equation.isNotEmpty()) equation.last() else ' '
        if (lastChar in listOf(' ', '/', 'x', '-', '+', '%', '(')) {
            equation.append("(")
        } else {
            equation.append(")")
        }
        updateDisplay()
    }

    private fun updateDisplay() {
        findViewById<TextView>(R.id.textView1).text = equation.toString()
    }

    private fun updateDisplayTrick() {
        val displayText = findViewById<TextView>(R.id.textView1)
        val element = StringBuilder(realTrick.replace("+1", ""))


        var i = 0
        while (i < element.length) {
            if (element[i] == '-') {
                Log.d("DEBUG", "Removing dash at position $i")
                element.delete(i, i + 1)
            } else {
                i++
            }
        }

        displayText.text = element.toString()
        realTrick = ""
    }
}
