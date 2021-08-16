package com.brianbiomdo.surveyapp

import retrofit2.Callback
import retrofit2.http.Body
import retrofit2.http.POST

open interface DummyAPIInterface {
        // Request method and URL specified in the annotation
        // Callback for the parsed response is the last parameter
        @POST("dummy/API/farmer/new")
        fun createFarmer(@Body farmer: Farmer?, cb: Callback<Farmer?>?)
}