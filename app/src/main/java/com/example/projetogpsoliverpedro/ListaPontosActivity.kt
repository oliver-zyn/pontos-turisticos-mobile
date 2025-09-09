package com.example.projetogpsoliverpedro

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListaPontosActivity : AppCompatActivity() {
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PontosAdapter
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var btnAdicionarPonto: Button
    private lateinit var btnVoltarHome: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_pontos)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        databaseHelper = DatabaseHelper(this)
        
        recyclerView = findViewById(R.id.recyclerViewPontos)
        btnAdicionarPonto = findViewById(R.id.btnAdicionarPonto)
        btnVoltarHome = findViewById(R.id.btnVoltarHome)

        setupRecyclerView()
        
        btnAdicionarPonto.setOnClickListener {
            val intent = Intent(this, CadastrarPontoActivity::class.java)
            startActivity(intent)
        }

        btnVoltarHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onResume() {
        super.onResume()
        carregarPontos()
    }

    private fun setupRecyclerView() {
        adapter = PontosAdapter(
            this,
            mutableListOf(),
            onMapClick = { ponto ->
                val intent = Intent(this, MapsActivity::class.java)
                intent.putExtra("latitude", ponto.latitude)
                intent.putExtra("longitude", ponto.longitude)
                startActivity(intent)
            },
            onEditClick = { ponto ->
                val intent = Intent(this, CadastrarPontoActivity::class.java)
                intent.putExtra("ponto_id", ponto.id)
                intent.putExtra("nome", ponto.nome)
                intent.putExtra("descricao", ponto.descricao)
                intent.putExtra("latitude", ponto.latitude)
                intent.putExtra("longitude", ponto.longitude)
                intent.putExtra("endereco", ponto.endereco)
                intent.putExtra("image_path", ponto.imagePath)
                startActivity(intent)
            },
            onDeleteClick = { ponto ->
                confirmarExclusao(ponto)
            }
        )
        
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun carregarPontos() {
        val pontos = databaseHelper.listarPontos()
        adapter.updatePontos(pontos)
    }

    private fun confirmarExclusao(ponto: PontoTuristico) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar Exclusão")
            .setMessage("Deseja realmente excluir o ponto ${ponto.nome}?")
            .setPositiveButton("Sim") { _, _ ->
                val resultado = databaseHelper.excluirPonto(ponto.id)
                if (resultado > 0) {
                    adapter.removePonto(ponto)
                    Toast.makeText(this, "Ponto excluído com sucesso", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Erro ao excluir ponto", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Não", null)
            .show()
    }
}