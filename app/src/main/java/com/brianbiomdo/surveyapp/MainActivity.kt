package com.brianbiomdo.surveyapp

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {
    var prefs: SharedPreferences? = null
    lateinit var db: LocalDBHandler
    val gson = GsonBuilder().create()

    fun fetchURLData(){
        val url = "https://run.mocky.io/v3/d628facc-ec18-431d-a8fc-9c096e00709a"
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object: Callback{
            override fun onResponse(call: Call, response: Response) {
                val body = response?.body?.string()
                db.insertSurveyJSON(SurveyJSON(1, body))
                val survey = gson.fromJson(body, Survey::class.java)
                runOnUiThread{
                    rvMain.adapter = MainAdapter(survey)
                }
            }
            override fun onFailure(call: Call, e: IOException) {
                println("Response failed.")
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db = LocalDBHandler(rvMain.context)
        prefs = getSharedPreferences("com.brianbiomdo.surveyapp", MODE_PRIVATE)
    }

    override fun onResume() {
        super.onResume()
        rvMain.layoutManager = LinearLayoutManager(this)
        if (prefs!!.getBoolean("firstrun", true)) {
            fetchURLData()
            prefs!!.edit().putBoolean("firstrun", false).commit();
        }else{
            var surveyJSON: SurveyJSON = db.readSurveyJSONData()
            val survey = gson.fromJson(surveyJSON.json, Survey::class.java)
            runOnUiThread{
                rvMain.adapter = MainAdapter(survey)
            }
        }
    }
}