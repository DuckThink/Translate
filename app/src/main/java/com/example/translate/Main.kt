package com.example.translate

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions
import java.lang.Exception
import java.util.*
class Main : AppCompatActivity() {
    var fromLaguages = arrayOf<String?>("From", "English", "Vietnamese")
    var toLagueges = arrayOf<String?>("To", "English", "Vietnamese")
    var laguageCode = 0
    var fromLaguageCode = 0
    var toLaguageCode = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var fromSpinner: Spinner? = null
        var toSpinner: Spinner? = null
        var sourceEdt: TextInputEditText? = null
        var micIV: ImageView? = null
        var translateBtn: MaterialButton? = null
        var translateTV: TextView? = null
        fromSpinner = findViewById(R.id.idFromSpinner)
        toSpinner = findViewById(R.id.idToSpinner)
        sourceEdt = findViewById(R.id.idEdtSource)
        micIV = findViewById(R.id.idIVMic)
        translateBtn = findViewById(R.id.idBtnTranslate)
        translateTV = findViewById(R.id.idTVTranslatedTV)
        fromSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                fromLaguageCode = getLaguageCode(fromLaguages[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })
        val fromAdapter: ArrayAdapter<*> =
            ArrayAdapter<Any?>(this, R.layout.spinner_item, fromLaguages)
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        fromSpinner.setAdapter(fromAdapter)
        toSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                toLaguageCode = getLaguageCode(toLagueges[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })
        val toAdapter: ArrayAdapter<*> = ArrayAdapter<Any?>(this, R.layout.spinner_item, toLagueges)
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        toSpinner.setAdapter(toAdapter)
        translateBtn.setOnClickListener(View.OnClickListener {
            translateTV.setText("")
            if (sourceEdt.getText().toString().isEmpty()) {
                Toast.makeText(
                    this@Main,
                    "Please enter your text translate",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (fromLaguageCode == 0) {
                Toast.makeText(
                    this@Main,
                    "Please select source laguage",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (toLaguageCode == 0) {
                Toast.makeText(
                    this@Main,
                    "Please select the laguage to make translation",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                translateText(fromLaguageCode, toLaguageCode, sourceEdt.getText().toString())
            }
        })
        micIV.setOnClickListener(View.OnClickListener {
            val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            i.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to convert into text")
            try {
                startActivityForResult(
                    i,REQUEST_PERMISSION_CODE
                )
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@Main, "" + e.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
    val sourceEdt: TextInputEditText = findViewById(R.id.idEdtSource)
    val translateTV: TextView = findViewById(R.id.idTVTranslatedTV)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (requestCode == RESULT_OK && data != null) {
                val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                sourceEdt!!.setText(result!![0])
            }
        }
    }

    private fun translateText(fromLaguageCode: Int, toLaguageCode: Int, source: String) {
        translateTV!!.text = "Downloading Modal.."
        val options = FirebaseTranslatorOptions.Builder().setSourceLanguage(fromLaguageCode)
            .setTargetLanguage(toLaguageCode)
            .build()
        val translator = FirebaseNaturalLanguage.getInstance().getTranslator(options)
        val conditions = FirebaseModelDownloadConditions.Builder().build()
        translator.downloadModelIfNeeded(conditions).addOnSuccessListener {
            translateTV!!.text = "Translating.."
            translator.translate(source).addOnSuccessListener { s -> translateTV!!.text = s }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this@Main,
                        "Fail to translate :" + e.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }.addOnFailureListener { e ->
            Toast.makeText(
                this@Main,
                "Fail to download laguage Modal" + e.message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    //String[] toLagueges = {"To", "English","Vietnamese"};
    fun getLaguageCode(laguage: String?): Int {
        var laguageCode = 0
        laguageCode = when (laguage) {
            "English" -> FirebaseTranslateLanguage.EN
            "Vietnamese" -> FirebaseTranslateLanguage.VI
            else -> 0
        }
        return laguageCode
    }

    companion object {
        private const val REQUEST_PERMISSION_CODE = 1
    }
}