package com.example.projetogpsoliverpedro

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    
    companion object {
        private const val DATABASE_NAME = "turismo.db"
        private const val DATABASE_VERSION = 1
        
        private const val TABLE_PONTOS = "pontos_turisticos"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NOME = "nome"
        private const val COLUMN_DESCRICAO = "descricao"
        private const val COLUMN_LATITUDE = "latitude"
        private const val COLUMN_LONGITUDE = "longitude"
        private const val COLUMN_ENDERECO = "endereco"
        private const val COLUMN_IMAGE_PATH = "image_path"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_PONTOS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NOME TEXT NOT NULL,
                $COLUMN_DESCRICAO TEXT,
                $COLUMN_LATITUDE REAL NOT NULL,
                $COLUMN_LONGITUDE REAL NOT NULL,
                $COLUMN_ENDERECO TEXT,
                $COLUMN_IMAGE_PATH TEXT
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PONTOS")
        onCreate(db)
    }

    fun inserirPonto(ponto: PontoTuristico): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOME, ponto.nome)
            put(COLUMN_DESCRICAO, ponto.descricao)
            put(COLUMN_LATITUDE, ponto.latitude)
            put(COLUMN_LONGITUDE, ponto.longitude)
            put(COLUMN_ENDERECO, ponto.endereco)
            put(COLUMN_IMAGE_PATH, ponto.imagePath)
        }
        val result = db.insert(TABLE_PONTOS, null, values)
        db.close()
        return result
    }

    fun listarPontos(): List<PontoTuristico> {
        val pontos = mutableListOf<PontoTuristico>()
        val db = readableDatabase
        val cursor = db.query(TABLE_PONTOS, null, null, null, null, null, null)
        
        if (cursor.moveToFirst()) {
            do {
                val ponto = PontoTuristico(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    nome = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOME)),
                    descricao = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRICAO)),
                    latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE)),
                    longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE)),
                    endereco = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ENDERECO)),
                    imagePath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_PATH))
                )
                pontos.add(ponto)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return pontos
    }

    fun atualizarPonto(ponto: PontoTuristico): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOME, ponto.nome)
            put(COLUMN_DESCRICAO, ponto.descricao)
            put(COLUMN_LATITUDE, ponto.latitude)
            put(COLUMN_LONGITUDE, ponto.longitude)
            put(COLUMN_ENDERECO, ponto.endereco)
            put(COLUMN_IMAGE_PATH, ponto.imagePath)
        }
        val result = db.update(TABLE_PONTOS, values, "$COLUMN_ID = ?", arrayOf(ponto.id.toString()))
        db.close()
        return result
    }

    fun excluirPonto(id: Long): Int {
        val db = writableDatabase
        val result = db.delete(TABLE_PONTOS, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
        return result
    }
}