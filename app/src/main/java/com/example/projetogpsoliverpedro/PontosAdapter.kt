package com.example.projetogpsoliverpedro

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class PontosAdapter(
    private val context: Context,
    private val pontos: MutableList<PontoTuristico>,
    private val onMapClick: (PontoTuristico) -> Unit,
    private val onEditClick: (PontoTuristico) -> Unit,
    private val onDeleteClick: (PontoTuristico) -> Unit
) : RecyclerView.Adapter<PontosAdapter.PontoViewHolder>() {

    class PontoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivPontoImage: ImageView = view.findViewById(R.id.ivPontoImage)
        val tvNome: TextView = view.findViewById(R.id.tvNome)
        val tvDescricao: TextView = view.findViewById(R.id.tvDescricao)
        val tvEndereco: TextView = view.findViewById(R.id.tvEndereco)
        val btnVerMapa: Button = view.findViewById(R.id.btnVerMapa)
        val btnEditar: Button = view.findViewById(R.id.btnEditar)
        val btnExcluir: Button = view.findViewById(R.id.btnExcluir)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PontoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ponto_turistico, parent, false)
        return PontoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PontoViewHolder, position: Int) {
        val ponto = pontos[position]
        
        holder.tvNome.text = ponto.nome
        holder.tvDescricao.text = ponto.descricao
        holder.tvEndereco.text = ponto.endereco ?: "Lat: ${ponto.latitude}, Lng: ${ponto.longitude}"

        ponto.imagePath?.let { imagePath ->
            val file = File(imagePath)
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(imagePath)
                holder.ivPontoImage.setImageBitmap(bitmap)
            }
        }

        holder.btnVerMapa.setOnClickListener {
            onMapClick(ponto)
        }

        holder.btnEditar.setOnClickListener {
            onEditClick(ponto)
        }

        holder.btnExcluir.setOnClickListener {
            onDeleteClick(ponto)
        }
    }

    override fun getItemCount(): Int = pontos.size

    fun updatePontos(novosPontos: List<PontoTuristico>) {
        pontos.clear()
        pontos.addAll(novosPontos)
        notifyDataSetChanged()
    }

    fun removePonto(ponto: PontoTuristico) {
        val index = pontos.indexOf(ponto)
        if (index != -1) {
            pontos.removeAt(index)
            notifyItemRemoved(index)
        }
    }
}