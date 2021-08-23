package com.brianbiomdo.surveyapp

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.question_layout.view.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainAdapter(val survey: Survey): RecyclerView.Adapter<MainAdapter.CustomViewHolder>() {
    //Number of Items
    override fun getItemCount(): Int {
        return 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflator = LayoutInflater.from(parent.context)
        val questionLayout = layoutInflator.inflate(R.layout.question_layout, parent, false)
        return CustomViewHolder(questionLayout)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        var farmerName: String? = null
        var farmerGender: String? = null
        var farmerSizeOfFarm: Float = -1.00F

        var currentQuestion: Question?  = null
        var nextQuestion: Question? = null
        val context = holder.view.btnAction.context
        val txtQuestion = holder.view.txtQuestion
        val txtAnswer = holder.view.txtAnswer
        val radioOPtions = holder.view.radioOptions
        val btnAction = holder.view.btnAction
        val txtError = holder.view.txtError

        var db = LocalDBHandler(context)

        txtError.visibility = View.GONE
        fun setCurrentQuestion(currentQuestionID: String?){
            //Set The question to Display
            survey.questions.map { question ->
                if(question.id.equals(currentQuestionID)){
                    currentQuestion = question
                }
                if(currentQuestion?.next.equals(question.id)){
                    nextQuestion = question
                }
            }
            var questionText = survey.strings.en.get(currentQuestion?.id)
            txtQuestion.text = questionText

            //Check for options and if exist display them
            if(!currentQuestion?.options?.isEmpty()!!){
                txtAnswer.visibility = View.GONE
                radioOPtions.removeAllViews()
                radioOPtions.visibility = View.VISIBLE
                currentQuestion?.options?.map { option ->
                    val radioOption = RadioButton(holder.view.radioOptions.context)
                    radioOption.layoutParams = LinearLayout.LayoutParams(
                        ViewGroup
                            .LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    radioOption.id = currentQuestion?.options?.indexOf(option)!!
                    radioOption.text = survey.strings.en.get(option.display_text)
                    radioOPtions.addView(radioOption)
                }
            }else{
                radioOPtions.visibility = View.GONE
                radioOPtions.removeAllViews()
                txtAnswer.visibility = View.VISIBLE
            }

            //Set only accept number for size of land
            if(currentQuestion?.question_type.equals("TYPE_VALUE")){
               txtAnswer.inputType =
                   InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            }else{
                txtAnswer.inputType = InputType.TYPE_CLASS_TEXT
            }

            //Update Btn Text
            if (currentQuestion?.next == null) {
                btnAction.text = "Submit"
            }else{
                btnAction.text = "Next Question"
            }
        }

        //Store Farmer to DB
        fun storeFarmer(farmer: Farmer?){
            if(db.insertFarmer(farmer!!)) {
                //Restart the Survey
                currentQuestion = survey.questions.get(0)
                nextQuestion = survey.questions.get(0)
            }
        }

        //Init the first Question
        setCurrentQuestion(survey.start_question_id)

        //Btn Action
        holder.view.btnAction.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                txtError.visibility = View.GONE
                var isValidResponse = false
                if (!currentQuestion?.question_type.equals("SELECT_ONE")) {
                    if (!txtAnswer.text.isEmpty()) {
                        if (currentQuestion?.id.equals("q_farmer_name")) {
                            farmerName = txtAnswer.text.toString().trim()
                            txtAnswer.setText("")
                            isValidResponse = true
                        } else {
                            if (txtAnswer.text.toString().equals(".")) {
                                txtError.text = "Please enter the size of farm in hectares"
                                txtError.visibility = View.VISIBLE
                                txtAnswer.requestFocus()
                                isValidResponse = false
                            } else {
                                farmerSizeOfFarm = txtAnswer.text.toString().toFloat()
                                txtAnswer.setText("")
                                isValidResponse = true
                                storeFarmer(Farmer(0, farmerName, farmerGender, farmerSizeOfFarm))
                            }
                        }
                    } else {
                        txtError.text = "Please type in your response"
                        txtError.visibility = View.VISIBLE
                        txtAnswer.requestFocus()
                        isValidResponse = false
                    }
                } else {
                    if (radioOPtions.checkedRadioButtonId != -1) {
                        val option: Option? =
                            currentQuestion?.options?.get(radioOPtions.checkedRadioButtonId)
                        farmerGender = option?.value!!
                        isValidResponse = true
                    } else {
                        txtError.text = "Please select your gender"
                        txtError.visibility = View.VISIBLE
                        radioOPtions.requestFocus()
                        isValidResponse = false
                    }
                }
                if (isValidResponse) {
                    setCurrentQuestion(nextQuestion?.id)
                }
            }
        })

        //Read Farmer Data and send to Dummy API
        fun publishFarmerData(){
            val BASE_URL = "https://api.dummy.survey/"
            val gson = GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create()

            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
            val apiService: DummyAPIInterface = retrofit.create(DummyAPIInterface::class.java)

            var data = db.readFarmerData()
            for(i in 0 until data.size){
                //var farmer: Call<Farmer> = apiService.createFarmer(data, )
                //Etc...to continue with code for dummy API
                Log.d("PUBLISH_TO_API: ", "published Farmer Data to Dummy API")
            }
        }

        //Check for Internet Connection
        fun isOnline(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE)
            return connectivityManager != null
        }

        //Call the function to publich after every 15Mins
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            if(isOnline(context)){
                publishFarmerData()
            } },15000)
    }
    inner class CustomViewHolder(val view: View): RecyclerView.ViewHolder(view)
}

