package com.example.capstone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContentProviderCompat.requireContext
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.jetbrains.anko.startActivity

class SignUpActivity : AppCompatActivity() {
    val classList = Array(8, {i -> i + 1})
    val gradeList = Array(3, {i -> i + 1})
    val phoneList = arrayOf("SKT", "KT", "LG")

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        SignUpGoBackButton.setOnClickListener {
            startActivity<LoginActivity>()
        }

        SignUpClassDropdown.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, classList)
        SignUpClassDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                println("반")
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                println("$p2")
            }
        }

        SignUpGradeDropdown.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, gradeList)
        SignUpGradeDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                println("학년")
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                println("$p2")
            }
        }

        SignUpPhoneDropdown.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, phoneList)
        SignUpPhoneDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                println("통신사")
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                println("$p2")
            }
        }
    }
}