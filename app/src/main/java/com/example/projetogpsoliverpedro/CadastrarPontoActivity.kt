package com.example.projetogpsoliverpedro

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class CadastrarPontoActivity : AppCompatActivity(), LocationListener {

    private lateinit var etNome: EditText
    private lateinit var etDescricao: EditText
    private lateinit var etLatitude: EditText
    private lateinit var etLongitude: EditText
    private lateinit var tvEndereco: TextView
    private lateinit var ivPreviewImagem: ImageView
    private lateinit var btnUsarLocalizacaoAtual: Button
    private lateinit var btnEscolherFoto: Button
    private lateinit var btnSalvar: Button
    private lateinit var btnCancelar: Button
    
    private lateinit var locationManager: LocationManager
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var photoFile: File
    
    private var isEditMode = false
    private var pontoId: Long = 0
    private var currentImagePath: String? = null

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            try {
                val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri))
                ivPreviewImagem.setImageBitmap(bitmap)
                
                photoFile = createImageFile()
                val out = FileOutputStream(photoFile)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                out.close()
                currentImagePath = photoFile.absolutePath
            } catch (e: Exception) {
                Toast.makeText(this, "Erro ao carregar imagem", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastrar_ponto)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        initViews()
        setupClickListeners()
        
        databaseHelper = DatabaseHelper(this)
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        checkIfEditMode()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun initViews() {
        etNome = findViewById(R.id.etNome)
        etDescricao = findViewById(R.id.etDescricao)
        etLatitude = findViewById(R.id.etLatitude)
        etLongitude = findViewById(R.id.etLongitude)
        tvEndereco = findViewById(R.id.tvEndereco)
        ivPreviewImagem = findViewById(R.id.ivPreviewImagem)
        btnUsarLocalizacaoAtual = findViewById(R.id.btnUsarLocalizacaoAtual)
        btnEscolherFoto = findViewById(R.id.btnEscolherFoto)
        btnSalvar = findViewById(R.id.btnSalvar)
        btnCancelar = findViewById(R.id.btnCancelar)
    }

    private fun setupClickListeners() {
        btnUsarLocalizacaoAtual.setOnClickListener {
            obterLocalizacaoAtual()
        }

        btnEscolherFoto.setOnClickListener {
            escolherFoto()
        }

        btnSalvar.setOnClickListener {
            salvarPonto()
        }

        btnCancelar.setOnClickListener {
            finish()
        }
    }

    private fun checkIfEditMode() {
        pontoId = intent.getLongExtra("ponto_id", 0)
        if (pontoId != 0L) {
            isEditMode = true
            preencherDadosEdicao()
        }
    }

    private fun preencherDadosEdicao() {
        etNome.setText(intent.getStringExtra("nome"))
        etDescricao.setText(intent.getStringExtra("descricao"))
        etLatitude.setText(intent.getDoubleExtra("latitude", 0.0).toString())
        etLongitude.setText(intent.getDoubleExtra("longitude", 0.0).toString())
        tvEndereco.text = intent.getStringExtra("endereco")
        
        val imagePath = intent.getStringExtra("image_path")
        if (!imagePath.isNullOrEmpty()) {
            val file = File(imagePath)
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(imagePath)
                ivPreviewImagem.setImageBitmap(bitmap)
                currentImagePath = imagePath
            }
        }
    }

    private fun obterLocalizacaoAtual() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }
        
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
        Toast.makeText(this, "Obtendo localização...", Toast.LENGTH_SHORT).show()
    }

    override fun onLocationChanged(location: Location) {
        etLatitude.setText(location.latitude.toString())
        etLongitude.setText(location.longitude.toString())
        locationManager.removeUpdates(this)
        
        obterEndereco(location.latitude, location.longitude)
    }

    private fun obterEndereco(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
                    runOnUiThread {
                        addresses.firstOrNull()?.let { address ->
                            tvEndereco.text = "${address.thoroughfare}, ${address.subThoroughfare}, ${address.subAdminArea}, ${address.adminArea}"
                        }
                    }
                }
            } else {
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                addresses?.firstOrNull()?.let { address ->
                    tvEndereco.text = "${address.thoroughfare}, ${address.subThoroughfare}, ${address.subAdminArea}, ${address.adminArea}"
                }
            }
        } catch (e: IOException) {
            tvEndereco.text = "Endereço não disponível"
        }
    }

    private fun escolherFoto() {
        galleryLauncher.launch("image/*")
    }

    private fun createImageFile(): File {
        val timeStamp = System.currentTimeMillis().toString()
        val storageDir = File(filesDir, "images")
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        return File(storageDir, "JPEG_${timeStamp}.jpg")
    }

    private fun salvarPonto() {
        val nome = etNome.text.toString().trim()
        val descricao = etDescricao.text.toString().trim()
        val latitudeStr = etLatitude.text.toString().trim()
        val longitudeStr = etLongitude.text.toString().trim()

        if (nome.isEmpty()) {
            Toast.makeText(this, "Nome é obrigatório", Toast.LENGTH_SHORT).show()
            return
        }

        if (latitudeStr.isEmpty() || longitudeStr.isEmpty()) {
            Toast.makeText(this, "Latitude e longitude são obrigatórias", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val latitude = latitudeStr.toDouble()
            val longitude = longitudeStr.toDouble()
            val endereco = tvEndereco.text.toString()

            val ponto = PontoTuristico(
                id = if (isEditMode) pontoId else 0,
                nome = nome,
                descricao = descricao,
                latitude = latitude,
                longitude = longitude,
                endereco = endereco,
                imagePath = currentImagePath
            )

            val resultado = if (isEditMode) {
                databaseHelper.atualizarPonto(ponto).toLong()
            } else {
                databaseHelper.inserirPonto(ponto)
            }

            if (resultado > 0L) {
                Toast.makeText(this, if (isEditMode) "Ponto atualizado!" else "Ponto salvo!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Erro ao salvar ponto", Toast.LENGTH_SHORT).show()
            }

        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Valores de latitude/longitude inválidos", Toast.LENGTH_SHORT).show()
        }
    }
}