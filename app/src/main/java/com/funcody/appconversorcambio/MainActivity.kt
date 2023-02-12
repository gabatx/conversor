package com.funcody.appconversorcambio

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.funcody.appconversorcambio.databinding.ActivityMainBinding
import org.json.JSONObject
import java.io.IOException
import java.io.Serializable

class MainActivity : AppCompatActivity() {

    // DECLARO EL BINDING
    private lateinit var binding: ActivityMainBinding
    // INICIALIZO Y DECLARO VARIABLES
    private var grupo:String = "Longitud"
    // Necesario para la escritura nada más iniciar
    private var puntoExiste = false
    private var pulsacion = ""
    // Variable global para saber cuando se acaba de convertir una valor
    private var conversionRealizada = false
    // Variable global declarada que va a ir guardando todos los resultados
    private var guardarResultados = mutableListOf<String>()

    // DECLARACIÓN DE ARRAYS DE UNIDADES (Le he puesto el mismo nombre de cada grupo para poder utilizar una función genérica para la llamada del spinner)
    private val unidades = mutableMapOf(
        "Longitud" to mutableListOf("Kilómetros", "Metros", "Centímetros", "Milímetros", "Micrómetros", "Nanómetros", "Millas", "Yardas", "Pies", "Pulgadas"),
        "Masa" to mutableListOf("Tonelada", "Kilogramo", "Gramo", "Miligramo", "Microgramo", "Tonelada larga", "Tonelada corta", "Stone", "libra", "Onza"),
        "Tiempo" to mutableListOf("nanosegundo", "microsegundo", "milisegundo", "segundo", "minuto", "hora", "dia", "semana", "mes", "año natural", "decada", "siglo"),
        "Energia" to mutableListOf("julio", "kilojoule", "gram calorie", "kilocaloria", "vatio-hora", "kilovatio-hora", "electronvoltio", "BTU", "US therm", "pie-libra fuerza"),
        "Frecuencia" to mutableListOf("hercio", "kilohertz", "megahercio", "gigahercio"),
        "Presion" to mutableListOf("atmosfera", "bar", "libra por pulgada cuadrada", "pascal", "torr"),
        "Datos" to mutableListOf("bit", "kilobit", "kibibit", "megabit", "mebibit", "gigabit", "gibibit", "terabit", "tebibit", "petabit", "pebibit", "byte", "kilobyte", "kibibyte", "megabyte", "mebibyte", "gigabyte", "gibibyte", "terabyte", "tebibyte", "petabyte", "pebibyte"),
        "Velocidad" to mutableListOf("milla por hora", "pies por segundo", "metro por segundo", "kilometro por hora", "nudo"),
        "Volumen" to mutableListOf("metro cubico", "litro", "mililitro", "pie cubico", "pulgada cubica"),
        "Temperatura" to mutableListOf("grado celsius", "grado fahrenheit", "kelvin"),
        "Area" to mutableListOf("kilometro cuadrado", "metro cuadrado", "milla cuadrada", "yarda cuadrada", "pie cuadrado", "pulgada cuadrada", "hectarea", "acre")
    )

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // COMIENZO:
        // Iniciar a cero los valores
        binding.includeSuperiorResultado.valorDe.text = "0"
        binding.includeSuperiorResultado.valorA.text = "0"
        crearSpinner(unidades[grupo]) //Asigno como spinner por defecto Longitud al abrir la aplicación

        // TECLADO NÚMEROS
        binding.includeBotones.botonUno.setOnTouchListener { _, event -> pulsacion(event, binding.includeBotones.botonUno, "1"); true }
        binding.includeBotones.botonDos.setOnTouchListener { _, event -> pulsacion(event, binding.includeBotones.botonDos, "2"); true }
        binding.includeBotones.botonTres.setOnTouchListener { _, event -> pulsacion(event, binding.includeBotones.botonTres, "3"); true }
        binding.includeBotones.botonCuatro.setOnTouchListener { _, event -> pulsacion(event, binding.includeBotones.botonCuatro, "4"); true }
        binding.includeBotones.botonCinco.setOnTouchListener { _, event -> pulsacion(event, binding.includeBotones.botonCinco, "5"); true }
        binding.includeBotones.botonSeis.setOnTouchListener { _, event -> pulsacion(event, binding.includeBotones.botonSeis, "6"); true }
        binding.includeBotones.botonSiete.setOnTouchListener { _, event -> pulsacion(event, binding.includeBotones.botonSiete, "7"); true }
        binding.includeBotones.botonOcho.setOnTouchListener { _, event -> pulsacion(event, binding.includeBotones.botonOcho, "8"); true }
        binding.includeBotones.botonNueve.setOnTouchListener { _, event -> pulsacion(event, binding.includeBotones.botonNueve, "9"); true }
        binding.includeBotones.botonPunto.setOnTouchListener { _, event -> pulsacion(event, binding.includeBotones.botonPunto, "."); true }
        binding.includeBotones.botonCero.setOnTouchListener { _, event -> pulsacion(event, binding.includeBotones.botonCero, "0"); true }
        binding.includeBotones.botonAc.setOnTouchListener { _, event -> pulsacion(event, binding.includeBotones.botonAc, "AC"); true }
        binding.botonConvertir.setOnTouchListener { _, event -> pulsacion(event, binding.botonConvertir, "botonConvertir"); true } // BOTÓN CONVERTIR

        // BOTONES GRUPOS
        binding.includeOpciones.botonGrupoLongitud.setOnClickListener {grupo = binding.includeOpciones.botonGrupoLongitud.text.toString(); crearSpinner(unidades[grupo]);resetearValores();}
        binding.includeOpciones.botonGrupoMasa.setOnClickListener {grupo = binding.includeOpciones.botonGrupoMasa.text.toString(); crearSpinner(unidades[grupo]);resetearValores()}
        binding.includeOpciones.botonGrupoTiempo.setOnClickListener{grupo = binding.includeOpciones.botonGrupoTiempo.text.toString(); crearSpinner(unidades[grupo]);resetearValores()}
        binding.includeOpciones.botonGrupoEnergia.setOnClickListener{grupo = binding.includeOpciones.botonGrupoEnergia.text.toString(); crearSpinner(unidades[grupo]);resetearValores()}
        binding.includeOpciones.botonGrupoFrecuencia.setOnClickListener{grupo = binding.includeOpciones.botonGrupoFrecuencia.text.toString(); crearSpinner(unidades[grupo]);resetearValores()}
        binding.includeOpciones.botonGrupoPresion.setOnClickListener{grupo = binding.includeOpciones.botonGrupoPresion.text.toString(); crearSpinner(unidades[grupo]);resetearValores()}
        binding.includeOpciones.botonGrupoDatos.setOnClickListener{grupo = binding.includeOpciones.botonGrupoDatos.text.toString(); crearSpinner(unidades[grupo]);resetearValores()}
        binding.includeOpciones.botonGrupoVelocidad.setOnClickListener{grupo = binding.includeOpciones.botonGrupoVelocidad.text.toString(); crearSpinner(unidades[grupo]);resetearValores()}
        binding.includeOpciones.botonGrupoVolumen.setOnClickListener{grupo = binding.includeOpciones.botonGrupoVolumen.text.toString(); crearSpinner(unidades[grupo]);resetearValores()}
        binding.includeOpciones.botonGrupoTemperatura.setOnClickListener{grupo = binding.includeOpciones.botonGrupoTemperatura.text.toString(); crearSpinner(unidades[grupo]);resetearValores()}
        binding.includeOpciones.botonGrupoArea.setOnClickListener{grupo = binding.includeOpciones.botonGrupoArea.text.toString(); crearSpinner(unidades[grupo]);resetearValores()}

        binding.botonInfo.setOnClickListener {
            startActivity(
                Intent(
                    this, Informacion::class.java
                ).putExtra("arrayResultados", guardarResultados as Serializable)
            )
        }
    }
    // --------------------------
    // -------  FUNCIONES -------
    // --------------------------

    private fun reseteaSiRecienCovertido() {
        if (conversionRealizada) {
            resetearValores()
        }
    }

    // FUNCIÓN DE ESCRITURA: Escribe el valor al final principio del text
    @SuppressLint("SetTextI18n")
    private fun escribeValor(valorPulsacion: String) {

        // Tengo que crear una variable que se le pueda asignar el valor recibido pulsado, para poder modificarlo dependiendo
        pulsacion = valorPulsacion
        val valorEnPantallaDe:TextView = binding.includeSuperiorResultado.valorDe

        // Validación para escribir el punto una sola vez
        if (pulsacion === "." && !puntoExiste) {
            pulsacion = "."
            puntoExiste = true
        } else if (pulsacion === "." && puntoExiste) {
            pulsacion = ""
        }

        if (valorEnPantallaDe.text.toString() === "0" && !puntoExiste) {
            // Lo hago para que limpie primero quitando el cero y luego muestre el número
            valorEnPantallaDe.text = ""
            valorEnPantallaDe.text = valorEnPantallaDe.text.toString() + pulsacion
        } else {
            validaTamanioValorPantalla(valorEnPantallaDe.text.toString().length)
            valorEnPantallaDe.text = valorEnPantallaDe.text.toString() + pulsacion
        }

        if (valorPulsacion === "AC") {
            resetearValores()
            puntoExiste = false
        }
    }

    private fun resetearValores() {

        val valorEnPantallaDe:TextView = binding.includeSuperiorResultado.valorDe
        val valorEnPantallaA:TextView = binding.includeSuperiorResultado.valorA

        valorEnPantallaDe.text = "0"
        valorEnPantallaA.text = "0"
        binding.includeSuperiorResultado.valorA.textSize = validaTamanioValorPantalla(valorEnPantallaA.text.toString().length)
        binding.includeSuperiorResultado.valorDe.textSize= validaTamanioValorPantalla(valorEnPantallaDe.text.toString().length)
        puntoExiste = false
        conversionRealizada = false
    }

    private fun validaTamanioValorPantalla(longitudNumero: Int) : Float {
        // hacer lo mismo con un when:
        return when (longitudNumero) {
            in 0..6 -> 50f
            in 7..11 -> 40f
            in 12..18 -> 30f
            in 19..30 -> 20f
            in 31..50 -> 10f
            else -> 50f
        }
    }

    /* FUNCIÓN PULSACIÓN
    Función que ejecuta una serie de acciones al ser pulsado y otras al soltar el botón.
     - evento: MotionEvent -> Objeto utilizado para informar eventos de movimiento
     - vista: TextView -> Estoy pasándole el Binding donde aplicaré el ContextCompat.getColor
     - valor: String -> El valor que introduciremos en la pantalla resultado al pulsar */
    private fun pulsacion(evento: MotionEvent, vista: TextView, valorPulsacion: String) {
        // Declaro los dos colores que voy a utilizar al pulsar y al soltar
        val colorPulsacion = ContextCompat.getColor(this, R.color.pulsacion)
        val colorSoltarPulsacion = ContextCompat.getColor(this, R.color.white)
        when (evento.action) {
            MotionEvent.ACTION_DOWN -> {
                if (valorPulsacion === "botonConvertir") {

                    vista.setTextColor(colorPulsacion)

                    if (validaIntroduceDatosValorDe()) {
                        conversionRealizada = true
                        mostrarResultado(convertir())
                        guardarConversion(convertir()["ValorDe"].toString(), convertir()["ValorA"].toString())
                    }

                } else { // Todas las demás
                    vista.setTextColor(colorPulsacion)
                    reseteaSiRecienCovertido()
                    escribeValor(valorPulsacion)
                }
            }
            MotionEvent.ACTION_UP   -> {
                vista.setTextColor(colorSoltarPulsacion)
            }
        }
    }

    private fun validaIntroduceDatosValorDe():Boolean {
        if (binding.includeSuperiorResultado.valorDe.text !== "0" ) {
            return true
        }
        return false
    }

    private fun guardarConversion(valorDe:String, ValorA:String) {
        val conversion: MutableList<String> = mutableListOf(
            valorDe,
            binding.includeSuperiorResultado.spinnerValorDe.selectedItem.toString(),
            ValorA,
            binding.includeSuperiorResultado.spinnerValorA.selectedItem.toString()
        )
        guardarResultados.add(conversion.toString())
    }

    private fun mostrarResultado(resultado: MutableMap<String, String>) {

        binding.includeSuperiorResultado.valorDe.text = resultado["ValorDe"]
        binding.includeSuperiorResultado.valorA.text = resultado["ValorA"]
        binding.includeSuperiorResultado.valorA.textSize = validaTamanioValorPantalla(binding.includeSuperiorResultado.valorA.text.toString().length)
        binding.includeSuperiorResultado.valorDe.textSize= validaTamanioValorPantalla(binding.includeSuperiorResultado.valorDe.text.toString().length)
    }

    // SPINNER - FUNCIÓN QUE LO CREA Y QUE SE LLAMA AL HACER CLICK
    private fun crearSpinner(grupo: MutableList<String>?) {
        val spinner = ArrayAdapter( this, R.layout.layout_spinner_valor_de_inicio, R.id.contenedorSpinnerValorDeInicio, grupo!! )
        spinner.setDropDownViewResource(R.layout.layout_spinner_elije_unidad)
        //Asigno el spinner Longitud al abrir la aplicación como spinner por defecto
        binding.includeSuperiorResultado.spinnerValorDe.adapter = spinner
        binding.includeSuperiorResultado.spinnerValorA.adapter = spinner
    }

    // JSON - CONECTA: Función que conecta con el json y devuelve en forma de string
    private fun getAssetJsonData(context: Context): String? {
        val json: String? = try {
            val `is` = context.assets.open("Unidades.json")
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            val charset = Charsets.UTF_8
            String(buffer, charset)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    // CONVERSOR
    private fun convertir(): MutableMap<String, String> {
        // Inicializo variables
        val valorDe:TextView = binding.includeSuperiorResultado.valorDe
        val unidadDe = binding.includeSuperiorResultado.spinnerValorDe.selectedItem.toString()
        val unidadA =  binding.includeSuperiorResultado.spinnerValorA.selectedItem.toString()
        var resultadoValorA = 0.0

        // RECOJO JSON:
        val jsonUnidades = getAssetJsonData(this)
        if (jsonUnidades!!.isNotEmpty()) {

            val json = JSONObject(jsonUnidades)
            val equivalencia = json
                .getJSONObject(grupo)
                .getJSONObject("equivalencias")
                .getJSONObject(unidadDe)[unidadA]


            resultadoValorA = equivalencia.toString().toDouble() * valorDe.text.toString().toDouble()
        }
        return mutableMapOf(
            "ValorDe" to valorDe.text.toString().toDouble().toString(),
            "ValorA" to resultadoValorA.toString()
        )
    }
}