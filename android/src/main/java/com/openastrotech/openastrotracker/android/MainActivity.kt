package com.openastrotech.openastrotracker.android

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

fun greet(): String {
    return "Hello world"
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tv: TextView = findViewById(R.id.text_view)
        tv.text = greet()
    }
}
