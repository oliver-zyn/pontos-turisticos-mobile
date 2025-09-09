package com.example.projetogpsoliverpedro

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ConfiguracoesActivity : AppCompatActivity() {

    private lateinit var seekBarZoom: SeekBar
    private lateinit var tvZoomValue: TextView
    private lateinit var radioGroupTipoMapa: RadioGroup
    private lateinit var rbNormal: RadioButton
    private lateinit var rbSatelite: RadioButton
    private lateinit var rbTerreno: RadioButton
    private lateinit var rbHibrido: RadioButton
    private lateinit var btnSalvarConfig: Button
    private lateinit var btnCancelarConfig: Button
    
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracoes)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        initViews()
        setupClickListeners()
        
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        carregarConfiguracoes()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun initViews() {
        seekBarZoom = findViewById(R.id.seekBarZoom)
        tvZoomValue = findViewById(R.id.tvZoomValue)
        radioGroupTipoMapa = findViewById(R.id.radioGroupTipoMapa)
        rbNormal = findViewById(R.id.rbNormal)
        rbSatelite = findViewById(R.id.rbSatelite)
        rbTerreno = findViewById(R.id.rbTerreno)
        rbHibrido = findViewById(R.id.rbHibrido)
        btnSalvarConfig = findViewById(R.id.btnSalvarConfig)
        btnCancelarConfig = findViewById(R.id.btnCancelarConfig)
    }

    private fun setupClickListeners() {
        seekBarZoom.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvZoomValue.text = "Zoom: $progress"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        btnSalvarConfig.setOnClickListener {
            salvarConfiguracoes()
        }

        btnCancelarConfig.setOnClickListener {
            finish()
        }
    }

    private fun carregarConfiguracoes() {
        val zoomLevel = sharedPreferences.getString("zoom_level", "15")?.toIntOrNull() ?: 15
        seekBarZoom.progress = zoomLevel
        tvZoomValue.text = "Zoom: $zoomLevel"

        val tipoMapa = sharedPreferences.getString("tipo_mapa", "normal")
        when (tipoMapa) {
            "normal" -> rbNormal.isChecked = true
            "satelite" -> rbSatelite.isChecked = true
            "terreno" -> rbTerreno.isChecked = true
            "hibrido" -> rbHibrido.isChecked = true
        }
    }

    private fun salvarConfiguracoes() {
        val editor = sharedPreferences.edit()
        
        editor.putString("zoom_level", seekBarZoom.progress.toString())
        
        val tipoMapa = when (radioGroupTipoMapa.checkedRadioButtonId) {
            R.id.rbNormal -> "normal"
            R.id.rbSatelite -> "satelite"
            R.id.rbTerreno -> "terreno"
            R.id.rbHibrido -> "hibrido"
            else -> "normal"
        }
        editor.putString("tipo_mapa", tipoMapa)
        
        editor.apply()
        
        Toast.makeText(this, "Configurações salvas!", Toast.LENGTH_SHORT).show()
        finish()
    }
}