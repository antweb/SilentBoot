package com.antweb.silentboot

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.antweb.silentboot.databinding.HelpActivityBinding

class HelpActivity : AppCompatActivity() {
    private lateinit var binding: HelpActivityBinding

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = HelpActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
    }
}
