//package com.example.translate
//
//import android.content.Intent
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.speech.RecognizerIntent
//import android.view.View
//import android.widget.*
//import com.google.android.gms.tasks.OnFailureListener
//import com.google.android.gms.tasks.OnSuccessListener
//import com.google.android.material.button.MaterialButton
//import com.google.android.material.textfield.TextInputEditText
//import com.google.firebase.ktx.Firebase
//import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
//import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
//import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
//import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions
//import java.util.*
//import kotlin.collections.ArrayList
//
//class MainActivity : AppCompatActivity() {
//    private val fromLanguages = arrayOf<String>("From", "English", "Afrikaans", "Arabic")
//    private val toLanguages = arrayOf<String>("To", "English", "Afrikaans", "Arabic")
//    private val REQUEST_PERMISSION_CODE: Int = 1
//    private val languageCode: Int = 0
//    private var fromLanguageCode: Int = 0
//    private var toLanguageCode: Int = 0
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//        val translatedTV: TextView = findViewById(R.id.idTVTranslatedTV)
//        val fromSpinner: Spinner = findViewById(R.id.idFromSpinner)
//        val toSpinner: Spinner = findViewById(R.id.idToSpinner)
//        val sourceEdt: TextInputEditText = findViewById(R.id.idEdtSource)
//        val micIV: ImageView = findViewById(R.id.idIVMic)
//        val translateBtn: MaterialButton = findViewById(R.id.idBtnTranslate)
//        fromSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//                fromLanguageCode = getLanguageCode(fromLanguages[p2])
//            }
//
//            override fun onNothingSelected(p0: AdapterView<*>?) {
//
//            }
//
//        }
//        val fromAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, fromLanguages)
//        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        fromSpinner.setAdapter(fromAdapter)
//
//        toSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//                toLanguageCode = getLanguageCode(toLanguages[p2])
//            }
//
//            override fun onNothingSelected(p0: AdapterView<*>?) {
//
//            }
//
//
//        }
//        val toAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, toLanguages)
//        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        toSpinner.setAdapter(toAdapter)
//
//        translateBtn.setOnClickListener(object : View.OnClickListener{
//            override fun onClick(v: View?) {
//                translatedTV.setText("")
//                if (sourceEdt.getText().toString().isEmpty()) {
//                    Toast.makeText(this@MainActivity, "Please enter your text to translate" ,Toast.LENGTH_LONG).show()
//                }
//                else if(fromLanguageCode==0){
//                    Toast.makeText(this@MainActivity, "Please select source language" ,Toast.LENGTH_LONG).show()
//                }
//                else if(toLanguageCode==0){
//                    Toast.makeText(this@MainActivity, "Please select the language to make translation" ,Toast.LENGTH_LONG).show()
//                }
//                else{
//                    translateText(fromLanguageCode,toLanguageCode,sourceEdt.getText().toString())
//                }
//            }
//        })
//        micIV.setOnClickListener(object : View.OnClickListener{
//            override fun onClick(v: View?) {
//                val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
//                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
//                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.getDefault())
//                i.putExtra(RecognizerIntent.EXTRA_PROMPT,"Speak to convert into text")
//                try {
//                    startActivityForResult(i, REQUEST_PERMISSION_CODE)
//                }catch (e: Exception){
//                    e.printStackTrace()
//                    Toast.makeText(this@MainActivity,""+e.message,Toast.LENGTH_SHORT).show()
//                }
//            }
//
//        })
//
//    }
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        val sourceEdt: TextInputEditText = findViewById(R.id.idEdtSource)
//        if (requestCode == REQUEST_PERMISSION_CODE){
//            if (requestCode == RESULT_OK && data != null){
//                val result : ArrayList<String> = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) as ArrayList<String>
//                sourceEdt.setText(result.get(0))
//            }
//        }
//    }
//
//    private fun translateText(fromLanguageCode:Int,toLanguageCode:Int,source:String) {
//        val translatedTV: TextView = findViewById(R.id.idTVTranslatedTV)
//        translatedTV.setText("Download Model .. ")
//        val options = FirebaseTranslatorOptions.Builder().setSourceLanguage(fromLanguageCode).setTargetLanguage(toLanguageCode).build()
//        val translator = FirebaseNaturalLanguage.getInstance().getTranslator(options)
//        val conditions = FirebaseModelDownloadConditions.Builder().build()
//        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(object : OnSuccessListener<Void>{
//            override fun onSuccess(p0: Void?) {
//                translatedTV.setText("Translating..")
//                translator.translate(source).addOnSuccessListener(object : OnSuccessListener<String>{
//                    override fun onSuccess(s: String?) {
//                        translatedTV.setText(s)
//                    }
//
//                }).addOnFailureListener(object :OnFailureListener{
//                    override fun onFailure(e: java.lang.Exception) {
//                        Toast.makeText(this@MainActivity,"Fail to translate: "+e.message,Toast.LENGTH_SHORT).show()
//                    }
//
//                })
//            }
//
//        }).addOnFailureListener(object : OnFailureListener{
//            override fun onFailure(e: java.lang.Exception) {
//                Toast.makeText(this@MainActivity,"Fail to download model: "+e.message,Toast.LENGTH_SHORT).show()
//            }
//
//        })
//
//    }
//
//    fun getLanguageCode(language: String): Int {
//        var languageCode = 0
//        when (language) {
//            "English" -> languageCode = FirebaseTranslateLanguage.EN
//            "Afrikaans" -> languageCode = FirebaseTranslateLanguage.AF
//            "Arabic" -> languageCode = FirebaseTranslateLanguage.AR
//            else -> languageCode = 0
//        }
//        return languageCode
//    }
//}


///////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
class MainActivity : AppCompatActivity() {
    var fromLaguages = arrayOf<String?>("From", "English", "Vietnamese")
    var toLagueges = arrayOf<String?>("To", "English", "Vietnamese")
    var laguageCode = 0
    var fromLaguageCode = 0
    var toLaguageCode = 0
    private val REQUEST_PERMISSION_CODE: Int = 1
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
                    this@MainActivity ,
                    "Please enter your text translate",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (fromLaguageCode == 0) {
                Toast.makeText(
                    this@MainActivity ,
                    "Please select source laguage",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (toLaguageCode == 0) {
                Toast.makeText(
                    this@MainActivity ,
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
                Toast.makeText(this@MainActivity, "" + e.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val sourceEdt: TextInputEditText = findViewById(R.id.idEdtSource)
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (requestCode == RESULT_OK && data != null) {
                val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                sourceEdt.setText(result!![0])
            }
        }
    }

    private fun translateText(fromLaguageCode: Int, toLaguageCode: Int, source: String) {
        val translateTV: TextView = findViewById(R.id.idTVTranslatedTV)
        translateTV.text = "Downloading Modal.."
        val options : FirebaseTranslatorOptions = FirebaseTranslatorOptions.Builder().setSourceLanguage(fromLaguageCode)
            .setTargetLanguage(toLaguageCode)
            .build()
        val conditions : FirebaseModelDownloadConditions = FirebaseModelDownloadConditions.Builder().build()
        val translator : FirebaseTranslator = FirebaseNaturalLanguage.getInstance().getTranslator(options)

//        translator.downloadModelIfNeeded(conditions).addOnSuccessListener {
//            translateTV.text = "Translating.."
//            translator.translate(source).addOnSuccessListener { s -> translateTV!!.text = s }
//                .addOnFailureListener { e ->
//                    Toast.makeText(
//                        this@MainActivity,
//                        "Fail to translate :" + e.message,
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//        }.addOnFailureListener { e ->
//            Toast.makeText(
//                this@MainActivity,
//                "Fail to download laguage Modal" + e.message,
//                Toast.LENGTH_SHORT
//            ).show()
//        }
    }
    fun getLaguageCode(laguage: String?): Int {
        var laguageCode = 0
        laguageCode = when (laguage) {
            "English" -> FirebaseTranslateLanguage.EN
            "Vietnamese" -> FirebaseTranslateLanguage.VI
            else -> 0
        }
        return laguageCode
    }

}