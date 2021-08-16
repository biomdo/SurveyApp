package com.brianbiomdo.surveyapp

class Question (val id: String, val question_type: String, val answer_type: String, val question_text: String,
                val options: List<Option>?, val next: String? )