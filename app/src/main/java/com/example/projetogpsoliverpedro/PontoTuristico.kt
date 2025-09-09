package com.example.projetogpsoliverpedro

data class PontoTuristico(
    val id: Long = 0,
    val nome: String,
    val descricao: String,
    val latitude: Double,
    val longitude: Double,
    val endereco: String? = null,
    val imagePath: String? = null
)