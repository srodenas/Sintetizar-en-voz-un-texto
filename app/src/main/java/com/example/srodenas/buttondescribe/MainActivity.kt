package com.example.srodenas.buttondescribe

import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.srodenas.buttondescribe.databinding.ActivityMainBinding
import java.util.Locale
/*
La clase TextToSpeech es parte del framework que lo utilizamos para reproducir
voz a partir de texto. Permite convertir texto en voz y reproducir el resultado a
través de los altavoces del dispositivo físico.

1.- Necesitamos una instancia de la clase TextToSpeech. Más adelante se puede hacer
un singleton. La inicialización necesita del contexto de la aplicación, no del activity,
y cargarle un listener para su inicialización. Lo que haremos es comprobar si se produce error
en la inicialización del componente. Si no hay error, cogeremos por defecto el lenguaje que
tenemos en nuestro dispositivo Local.

2.- Para que nos describa un texto, llamamos a una función nuestra que nos describa un texto en voz.
Para ello, nuestro objeto, llama a la función speak, donde le pasamos como parámetro una cadena,
A ello lo llamamos sintetizar la voz. Existen dos parámetros:
   2.1. QUEVE_FLUSH (Borra la cola y comienza a hablar inmediatamente con el nuevo texto)
   2.2. QUEVE_ADD (Agrega el texto a la cola y espera su turno para ser hablado. Lo podemos utilizar
   para formar frases.)
   Los otros dos parámetros los dejamos a null.

3.- Google nos aconseja deterner y liberar recursos cuando ya no utilicemos la APP. Tiene
sentido que liberemos el uso del altavoz y del objeto que sintetiza la voz.
El uso de ::instancia.propiedad, lo hacemos para verificar que esa instancia existe, siempre y cuando
hayamos definido la propiedad como lateinit, es decir. Siempre y cuando tengamos una propiedad del tipo
lateinit, podemos recurrir a verificar si se ha inicializado de la forma anteriormente.
 Si no hubiéramos puesto la propiedad como lateinit, tendríamos que comprobar con la comparación de null.
 */


class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var textToSpeech: TextToSpeech  //descriptor de voz
    private val TOUCH_MAX_TIME = 500 // en milisegundos
    private var touchLastTime: Long = 0  //para saber el tiempo entre toque.
    private var touchNumber = 0   //numero de toques dado (por si acaso). De momento no nos hace falta.
    private lateinit var handler: Handler
    val MYTAG = "LOGCAT"  //para mirar logs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configureTextToSpeech()  //configuramos nuestro textToSpeech
        initHander()    //lanzaremos un hilo para el progressBar. No es necesario un hilo.
        initEvent()     //Implementación del botón.
    }

    private fun initHander() {
        handler = Handler(Looper.getMainLooper())  //queremos que el tema de la IU, la llevemos al hilo principal.
        binding.progressBar.visibility = View.VISIBLE  //hacemos visible el progress
        binding.btnExample.visibility = View.GONE  //ocultamos el botón.

        Thread{
            Thread.sleep(3000)
            handler.post{
                binding.progressBar.visibility = View.GONE  //ocultamos el progress
                val description = getString(R.string.describe).toString()
                speakMeDescription(description)  //que nos comente de qué va esto...
                Thread.sleep(4000)
                Log.i(MYTAG,"Se ejecuta correctamente el hilo")
                binding.btnExample.visibility = View.VISIBLE

            }
        }.start()
    }



    private fun configureTextToSpeech() {
        textToSpeech = TextToSpeech(applicationContext, TextToSpeech.OnInitListener {
            if(it != TextToSpeech.ERROR){
                textToSpeech.language = Locale.getDefault()
               // textToSpeech.setSpeechRate(1.0f)
                Log.i(MYTAG,"Sin problemas en la configuración TextToSpeech")
            }else{
                Log.i(MYTAG,"Error en la configuración TextToSpeech")
            }
        })
    }

/*
Como he dicho esta mañana, System.currentTimeMillis() devuelve el tiempo actual
en milisegundos. Por cada click el touchLastTime se vuelve a actualizar, para hacer
la diferencia de tiempos y comprobar si es menor de 500 msg.
 */
    private fun initEvent() {
        val chiste = resources.getString(R.string.chiste)
        binding.btnExample.setOnClickListener{
            //Sacamos el tiempo actual
            val currentTime = System.currentTimeMillis()
            //Comprobamos si el margen entre pulsación, da lugar a una doble pulsación.
            if (currentTime - touchLastTime < TOUCH_MAX_TIME){
              //  touchNumber=0
                executorDoubleTouch(chiste)  //hemos pulsado dos veces, por tanto lanzamos el chiste.
                Log.i(MYTAG,"Escuchamos el chiste")
            }
            else{
              //  touchNumber++
                Log.i(MYTAG,"Hemos pulsado 1 vez.")
                //Describimos el botón, 1 sóla pulsación
                speakMeDescription("Botón para escuchar un chiste")
            }

            touchLastTime = currentTime
          /*  if (touchNumber == 2) {
                Log.i(MYTAG,"Detectamos 2 pulsaciones.")
                touchNumber = 0
            }
*/
        }  //fin listener
    }

    //Habla
    private fun speakMeDescription(s: String) {
        Log.i(MYTAG,"Intenta hablar")
        textToSpeech.speak(s, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    private fun executorDoubleTouch(chiste: String) {
        speakMeDescription(chiste)
        // Toast.makeText(this,"doble pulsacion-> Ejecuto la acción",Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        //Si hemos inicializado la propiedad textToSpeech, es porque existe.
        if (::textToSpeech.isInitialized){
            textToSpeech.stop()
            textToSpeech.shutdown()

        }
        super.onDestroy()
    }
}