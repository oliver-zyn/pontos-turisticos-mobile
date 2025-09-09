package com.example.projetogpsoliverpedro

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button
import androidx.appcompat.app.AlertDialog

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.projetogpsoliverpedro.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var btnVoltarHomeMapa: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        databaseHelper = DatabaseHelper(this)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        btnVoltarHomeMapa = findViewById(R.id.btnVoltarHomeMapa)
        btnVoltarHomeMapa.setOnClickListener {
            val intent = Intent(this, ListaPontosActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        aplicarConfiguracoesMapa()

        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)
        
        if (latitude != 0.0 && longitude != 0.0) {
            val location = LatLng(latitude, longitude)
            mMap.addMarker(MarkerOptions().position(location).title("Minha localização"))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, getZoomLevel()))
        } else {
            carregarPontosTuristicos()
        }

        mMap.setOnMarkerClickListener { marker ->
            val tag = marker.tag
            if (tag is PontoTuristico) {
                mostrarDetalhesPonto(tag)
            }
            true
        }
    }

    private fun aplicarConfiguracoesMapa() {
        val tipoMapa = sharedPreferences.getString("tipo_mapa", "normal")
        when (tipoMapa) {
            "normal" -> mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            "satelite" -> mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
            "terreno" -> mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
            "hibrido" -> mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        }
    }

    private fun getZoomLevel(): Float {
        return sharedPreferences.getString("zoom_level", "15")?.toFloatOrNull() ?: 15f
    }

    private fun carregarPontosTuristicos() {
        val pontos = databaseHelper.listarPontos()
        
        if (pontos.isNotEmpty()) {
            pontos.forEach { ponto ->
                val position = LatLng(ponto.latitude, ponto.longitude)
                val marker = mMap.addMarker(MarkerOptions()
                    .position(position)
                    .title(ponto.nome)
                    .snippet(ponto.descricao))
                marker?.tag = ponto
            }

            val primeiroLocal = LatLng(pontos[0].latitude, pontos[0].longitude)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(primeiroLocal, getZoomLevel()))
        } else {
            val brasil = LatLng(-14.2350, -51.9253)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(brasil, 4f))
        }
    }

    private fun mostrarDetalhesPonto(ponto: PontoTuristico) {
        val mensagem = """
            Nome: ${ponto.nome}
            
            Descrição: ${ponto.descricao}
            
            Coordenadas: ${ponto.latitude}, ${ponto.longitude}
            
            ${if (!ponto.endereco.isNullOrEmpty()) "Endereço: ${ponto.endereco}" else ""}
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("Detalhes do Ponto")
            .setMessage(mensagem)
            .setPositiveButton("OK", null)
            .show()
    }
}