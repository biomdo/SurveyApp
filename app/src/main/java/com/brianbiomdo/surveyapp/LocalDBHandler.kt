package com.brianbiomdo.surveyapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

val DB_NAME = "FarmerDB"
val TABLE_SURVEY = "Surveys"
val COL_SURVEY_ID ="id"
val COL_SURVEY_JSON = "json"
val TABLE_NAME = "Farmers"
val COL_ID = "id"
val COL_NAME = "name"
val COL_GENDER = "gender"
val COL_SIZE_OF_FARM = "size_of_farm"

class LocalDBHandler(val context: Context):SQLiteOpenHelper(context, DB_NAME, null, 1) {
    var db = this.writableDatabase
    override fun onCreate(db: SQLiteDatabase) {
        val createSurveyTableSQL = "CREATE TABLE $TABLE_SURVEY ($COL_SURVEY_ID INTEGER PRIMARY KEY," +
                " $COL_SURVEY_JSON TEXT)"
        val createFarmerTableSQL = "CREATE TABLE $TABLE_NAME ($COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                " $COL_NAME VARCHAR(200), $COL_GENDER VARCHAR(20), $COL_SIZE_OF_FARM REAL)"
        db.execSQL(createSurveyTableSQL)
        db.execSQL(createFarmerTableSQL)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_SURVEY")
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertSurveyJSON(surveyJSON: SurveyJSON){
        db = this.writableDatabase
        var cv = ContentValues()
        cv.put(COL_SURVEY_ID, 1)
        cv.put(COL_SURVEY_JSON, surveyJSON.json)
        var result = db.insert(TABLE_SURVEY, null, cv)
        if(result == (-1).toLong())
            Toast.makeText(context, "Failed to save Survey JSON", Toast.LENGTH_LONG).show()
    }

    fun readSurveyJSONData():SurveyJSON{
        var surveyJSON: SurveyJSON = SurveyJSON();
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_SURVEY WHERE $COL_SURVEY_ID = 1"
        val result = db.rawQuery(query, null)
        if(result.moveToFirst()){
                surveyJSON.id = result.getString(result.getColumnIndex(COL_SURVEY_ID)).toInt()
                surveyJSON.json = result.getString(result.getColumnIndex(COL_SURVEY_JSON))
        }
        result.close()
        db.close()
        return surveyJSON
    }

    fun insertFarmer(farmer: Farmer): Boolean{
        db = this.writableDatabase
        var cv = ContentValues()
        cv.put(COL_NAME, farmer.name)
        cv.put(COL_GENDER, farmer.gender)
        cv.put(COL_SIZE_OF_FARM, farmer.size_of_farm)
        var result = db.insert(TABLE_NAME, null, cv)
        if(result == (-1).toLong()) {
            Toast.makeText(context, "Failed to save Farmer Details", Toast.LENGTH_LONG).show()
            return false
        }else{
            Toast.makeText(context, "Successfully saved Farmer Details", Toast.LENGTH_LONG).show()
            return true
        }
    }

    fun readFarmerData():MutableList<Farmer>{
        val farmerList: MutableList<Farmer> = ArrayList()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val result = db.rawQuery(query, null)
        if(result.moveToFirst()){
            do{
                var farmer = Farmer()
                farmer.id = result.getString(result.getColumnIndex(COL_ID)).toInt()
                farmer.name = result.getString(result.getColumnIndex(COL_NAME))
                farmer.gender = result.getString(result.getColumnIndex(COL_GENDER))
                farmer.size_of_farm = result.getString(result.getColumnIndex(COL_SIZE_OF_FARM)).toFloat()
                farmerList.add(farmer)
            }while(result.moveToNext())
        }
        result.close()
        db.close()
        return farmerList
    }
}