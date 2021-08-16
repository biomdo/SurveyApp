package com.brianbiomdo.surveyapp

import com.google.gson.annotations.SerializedName




class Farmer {
    @SerializedName("id")
    var id: Int = 0
    @SerializedName("name")
    var name: String? = ""
    @SerializedName("gender")
    var gender: String? = ""
    @SerializedName("size_of_farm")
    var size_of_farm: Float = -1.00F

    constructor(id: Int, name: String?, gender: String?, size_of_farm: Float){
        this.id = id
        this.name = name
        this.gender = gender
        this.size_of_farm = size_of_farm
    }

    constructor(){

    }
}