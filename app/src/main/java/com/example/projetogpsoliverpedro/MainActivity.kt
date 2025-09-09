package com.example.projetogpsoliverpedro

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.projetogpsoliverpedro.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btListarPontos.setOnClickListener {
            val intent = Intent(this, ListaPontosActivity::class.java)
            startActivity(intent)
        }

        binding.btConfiguracoes.setOnClickListener {
            val intent = Intent(this, ConfiguracoesActivity::class.java)
            startActivity(intent)
        }
    }
}