package com.example.calculator

import android.Manifest
import android.app.ComponentCaller
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import net.objecthunter.exp4j.ExpressionBuilder
import com.example.calculator.ui.theme.CalculatorTheme


class MainActivity : ComponentActivity() {

    val CONTACT_PERMISSION_CODE = 1
    val CONTACT_PICK_CODE = 2
    public var realTrick = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val contact = findViewById<Button>(R.id.buttonperc)

        contact.setOnLongClickListener(){
            //We gotta check sum first
            print("Made it to longClick")
            handleLongClick()
            true
        }
    }

    private fun handleLongClick() {
        print("Made it to handleLongClick")
        if (checkContact()){
            print("Contacts have been checked, ready to pick contacts")
            pickContact()
        }

        else{
            requestInfo()
        }
    }

    private fun checkContact(): Boolean{

        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestInfo(){
        val permission = arrayOf(Manifest.permission.READ_CONTACTS)
        ActivityCompat.requestPermissions(this, permission, CONTACT_PERMISSION_CODE)
    }

    private fun pickContact(){
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        startActivityForResult(intent, CONTACT_PICK_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CONTACT_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickContact()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == CONTACT_PICK_CODE) {
            data?.data?.let { contactUri ->
                val cursor = contentResolver.query(
                    contactUri,
                    arrayOf(ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME),
                    null, null, null
                )

                if (cursor != null && cursor.moveToFirst()) {
                    val contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                    val contactName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))

                    cursor.close()

                    // Now fetch the contact's phone number using the contact ID
                    fetchContactNumber(contactId, contactName)
                }
            }
        } else {
            Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show()
        }
    }


    private fun fetchContactNumber(contactId: String, contactName: String) {
        val cursorPhone = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
            arrayOf(contactId),
            null
        )

        if (cursorPhone != null && cursorPhone.moveToFirst()) {
            val contactNumber = cursorPhone.getString(cursorPhone.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
            cursorPhone.close()

            // Store the contact number
            realTrick = contactNumber
        } else {
            Toast.makeText(this, "No phone number found!", Toast.LENGTH_SHORT).show()
        }
    }



    var equation = StringBuilder()

    fun typing(num: View){
        val button = num as Button
        Log.d("DEBUG", "Testing")
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

    fun add(sign: View){
        equation.append("+")
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

    fun eqls(sign: View){
        if (realTrick.isNotEmpty()){
            updateDisplayTrick()
        }
        else{
            arithmeticStuff()
        }
    }

    private fun arithmeticStuff() {
        val arithmetic = equation.toString().replace("x", "*")

        try {
            val result = eval(arithmetic) // Write an `eval` function to evaluate
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
            Double.NaN // Return NaN for invalid expressions
        }
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

    fun updateDisplayTrick(){
        var displayText = findViewById<TextView>(R.id.textView1)
        val element = StringBuilder()
        element.append(realTrick)
        element.delete(0, 3)

        var i = 0

        while (i < element.length){
            if (element[i] == '-'){
                Log.d("DEBUG", "The if statement works")
                element.delete(i, i+1)
            }
            else{
                i++
            }
        }
        displayText.text = element.toString()
    }

}



