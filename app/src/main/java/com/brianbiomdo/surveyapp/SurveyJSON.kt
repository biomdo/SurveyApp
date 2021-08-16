package com.brianbiomdo.surveyapp

class SurveyJSON {
    var id: Int = 0
    var json: String? = null

    constructor(id: Int, json: String?){
        this.id = id
        this.json = json
    }

    constructor(){

    }
}