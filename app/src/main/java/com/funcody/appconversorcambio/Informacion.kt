package com.funcody.appconversorcambio

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.funcody.appconversorcambio.databinding.ActivityInformacionBinding

class Informacion : AppCompatActivity() {

    // DECLARO EL BINDING
    private lateinit var binding: ActivityInformacionBinding

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInformacionBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // RECOJO EL ARRAY:
        val bundle: Bundle? = intent.extras
        var arrayResultados = mutableListOf<String>()
        if (bundle!!.containsKey("arrayResultados")) {
            arrayResultados = bundle.getSerializable("arrayResultados") as MutableList<String>
            println(arrayResultados[0])
            println(arrayResultados[0][0])
        }

        // Envía a cualquier aplicación que comparta texto
        binding.includeAcercaDe.iconoCompartir.setOnClickListener {
            val conversion = "Conversión: ${arrayResultados[arrayResultados.size - 1][0]} ${arrayResultados[arrayResultados.size - 1][1]} = ${arrayResultados[arrayResultados.size - 1][2]} ${arrayResultados[arrayResultados.size - 1][3]} "
            println(conversion)
            val sendIntent = Intent(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_TEXT,
                    conversion)
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        }

        // Envía un correo electrónico
        binding.includeAcercaDe.iconoFeeback.setOnClickListener {
            val direccionEmail = arrayOf("funcody.contacto@gmail.com") // IMPORTANTE, DEBE DE IR DENTRO DE UN ARRAY
            val asunto = "Esta es mi opinión sobre la app Conversor"
            val texto = "Hola funCody, te comento: "

            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, direccionEmail)
                putExtra(Intent.EXTRA_SUBJECT, asunto)
                putExtra(Intent.EXTRA_TEXT, texto)
            }

            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "Debe configurar su cliente de correo", Toast.LENGTH_SHORT).show()
            }
        }

        // Te lleva a la playStore para que comentes
        binding.includeAcercaDe.iconoValora.setOnClickListener {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
            }
        }
    }

    private fun convertirArrays(arrayResultados:ArrayList<String>):MutableList<String> {

        val arraySeparados = mutableListOf<String>()
        arrayResultados.forEach{
            arraySeparados.add(it.replace("[", "").replace("]","").split(",").toString())
        }
        return arraySeparados
    }
}
