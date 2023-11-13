package com.example.srodenas.buttondescribe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.widget.TextView
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
   2.2. QUEVE_ADD (Agrega el texto a la cola y espera su turno para ser hablado. Lo podemos utilzar
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configureTextToSpeech()
        initEvent()
    }



    private fun configureTextToSpeech() {
        textToSpeech = TextToSpeech(applicationContext, TextToSpeech.OnInitListener {
            if(it != TextToSpeech.ERROR){
                textToSpeech.language = Locale.getDefault()
            }
        })
    }


    private fun initEvent() {
        binding.btnExample.setOnClickListener{
            val textButton = binding.btnExample.text.toString()
            speakMeDescription("Boton: $textButton")
        }
    }

    private fun speakMeDescription(s: String) {
        textToSpeech.speak(s, TextToSpeech.QUEUE_FLUSH, null, null)
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