package com.example.iot_test

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.ValueFormatter

import com.github.mikephil.charting.components.YAxis

import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val get_ser = Retrofit_builder.service

        var tv_temper = findViewById<TextView>(R.id.tv_temperature)
        var tv_hum = findViewById<TextView>(R.id.tv_Humidity)
        var tv_co2 = findViewById<TextView>(R.id.tv_CO2)
        var tv_amOrpm = findViewById<TextView>(R.id.amOrpm)

        var iv_temper = findViewById<ImageView>(R.id.img_temperature)
        var iv_hum = findViewById<ImageView>(R.id.img_Humidity)
        var iv_co2 = findViewById<ImageView>(R.id.img_CO2)


        var barChart = findViewById<BarChart>(R.id.barChart)

        var xdata : Array<Float> = arrayOf(1f,2f,3f,4f,5f,6f,7f,8f,9f,10f,11f,12f)
        var ydata : Array<Float> = arrayOf(0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f)



        lateinit var home_air : List<air_info>


        //get air information from server
        get_ser.Get_air_info().enqueue(object : Callback<get_air_info> {
            override fun onResponse(call: Call<get_air_info>, response: Response<get_air_info>) {
                if(response.code() == 200){
                    home_air = response.body()!!.message
                    tv_temper.text = home_air[home_air.size-1].temperature+" *C" //?????? ?????? ??????
                    tv_co2.text = home_air[home_air.size-1].co2+" ppm" //???????????????
                    tv_hum.text = home_air[home_air.size-1].humidity+" %" //??????
                    Log.d("Get_Info","??????")
                }
            }
            override fun onFailure(call: Call<get_air_info>, t: Throwable) {
                Log.d("Get_Info","t"+t.message)
            }
        })


        //----------------------------------??????--------------------------------------//
        iv_temper.setOnClickListener {

            var btn_preData = findViewById<Button>(R.id.btn_predata)
            var btn_orgData = findViewById<Button>(R.id.btn_orgdata)

            var time_temp_0 = home_air[home_air.size-1].check_time.chunked(5)
            var time_temp_1 = time_temp_0[1].chunked(1)
            var time_temp_2 = time_temp_0[2].chunked(1)
            var day = time_temp_1[0] + time_temp_1[1] + time_temp_1[2] +
                    time_temp_1[3] + time_temp_1[4]//????????????
            var current_time = time_temp_2[1] + time_temp_2[2] //????????????
            Log.d("??????", current_time)

            if(current_time.toInt()>12){
                tv_amOrpm.text = day+" ??????"
            }
            else tv_amOrpm.text = day+" ??????"

            var dev_time = current_time.toInt()%12+1

            ydata = Array(12, { 0f } )
            var cnt = 0
            var for_pre_cnt = 0
            for(i in home_air.size-1 downTo home_air.size-dev_time){
                ydata[cnt] = home_air[i].temperature.toFloat()
                cnt++
            }
            //???????????? ??? ?????????
            btn_preData.setOnClickListener {
                tv_amOrpm.text = day+" ??????"
                for(i in home_air.size-dev_time downTo home_air.size-(dev_time+11)){
                    ydata[for_pre_cnt] = home_air[i].temperature.toFloat()
                    for_pre_cnt ++
                }
                btn_preData.visibility = View.GONE
                btn_orgData.visibility = View.VISIBLE
                Draw_Chart(barChart, Add_entries(xdata!!, ydata!!),"temp")
            }
            //?????? ?????? ?????????
            btn_orgData.setOnClickListener {
                var cnt = 0
                ydata = Array(12, { 0f } )
                for(i in home_air.size-1 downTo home_air.size-dev_time){
                    ydata[cnt] = home_air[i].temperature.toFloat()
                    cnt++
                }
                btn_preData.visibility = View.VISIBLE
                btn_orgData.visibility = View.GONE
                Draw_Chart(barChart, Add_entries(xdata!!, ydata!!),"temp")
            }
            Draw_Chart(barChart, Add_entries(xdata!!, ydata!!),"temp")
        }

        //----------------------------------??????--------------------------------------//
        iv_hum.setOnClickListener {
            var btn_preData = findViewById<Button>(R.id.btn_predata)
            var btn_orgData = findViewById<Button>(R.id.btn_orgdata)

            var time_temp_0 = home_air[home_air.size-1].check_time.chunked(5)
            var time_temp_1 = time_temp_0[1].chunked(1)
            var time_temp_2 = time_temp_0[2].chunked(1)
            var day = time_temp_1[0] + time_temp_1[1] + time_temp_1[2] +
                    time_temp_1[3] + time_temp_1[4]//????????????
            var current_time = time_temp_2[1] + time_temp_2[2] //????????????
            Log.d("??????", current_time)

            if(current_time.toInt()>12){
                tv_amOrpm.text = day+" ??????"
            }
            else tv_amOrpm.text = day+" ??????"

            var dev_time = current_time.toInt()%12+1

            ydata = Array(12, { 0f } )
            var cnt = 0
            var for_pre_cnt = 0
            for(i in home_air.size-1 downTo home_air.size-dev_time){
                ydata[cnt] = home_air[i].humidity.toFloat()
                cnt++
            }
            //???????????? ??? ?????????
            btn_preData.setOnClickListener {
                for(i in home_air.size-dev_time downTo home_air.size-(dev_time+11)){
                    ydata[for_pre_cnt] = home_air[i].humidity.toFloat()
                    for_pre_cnt ++
                }
                btn_preData.visibility = View.GONE
                btn_orgData.visibility = View.VISIBLE
                Draw_Chart(barChart, Add_entries(xdata!!, ydata!!),"hum")
            }
            //?????? ?????? ?????????
            btn_orgData.setOnClickListener {
                var cnt = 0
                ydata = Array(12, { 0f } )
                for(i in home_air.size-1 downTo home_air.size-dev_time){
                    ydata[cnt] = home_air[i].humidity.toFloat()
                    cnt++
                }
                btn_preData.visibility = View.VISIBLE
                btn_orgData.visibility = View.GONE
                Draw_Chart(barChart, Add_entries(xdata!!, ydata!!),"hum")
            }
            Draw_Chart(barChart, Add_entries(xdata!!, ydata!!),"hum")

        }

        //----------------------------------co2--------------------------------------//
        iv_co2.setOnClickListener {
            var btn_preData = findViewById<Button>(R.id.btn_predata)
            var btn_orgData = findViewById<Button>(R.id.btn_orgdata)

            var time_temp_0 = home_air[home_air.size-1].check_time.chunked(5)
            var time_temp_1 = time_temp_0[1].chunked(1)
            var time_temp_2 = time_temp_0[2].chunked(1)
            var day = time_temp_1[0] + time_temp_1[1] + time_temp_1[2] +
                    time_temp_1[3] + time_temp_1[4]//????????????
            var current_time = time_temp_2[1] + time_temp_2[2] //????????????
            Log.d("??????", current_time)

            if(current_time.toInt()>12){
                tv_amOrpm.text = day+" ??????"
            }
            else tv_amOrpm.text = day+" ??????"

            var dev_time = current_time.toInt()%12+1

            var cnt = 0
            var for_pre_cnt = 0
            for(i in home_air.size-1 downTo home_air.size-dev_time){
                ydata[cnt] = home_air[i].co2.toFloat()
                cnt++
            }
            //???????????? ??? ?????????
            btn_preData.setOnClickListener {
                for(i in home_air.size-dev_time downTo home_air.size-(dev_time+11)){
                    ydata[for_pre_cnt] = home_air[i].co2.toFloat()
                    for_pre_cnt ++
                }
                btn_preData.visibility = View.GONE
                btn_orgData.visibility = View.VISIBLE
                Draw_Chart(barChart, Add_entries(xdata!!, ydata!!),"co2")
            }
            //?????? ?????? ?????????
            btn_orgData.setOnClickListener {
                var cnt = 0
                ydata = Array(12, { 0f } )
                for(i in home_air.size-1 downTo home_air.size-dev_time){
                    ydata[cnt] = home_air[i].co2.toFloat()
                    cnt++
                }
                btn_preData.visibility = View.VISIBLE
                btn_orgData.visibility = View.GONE
                Draw_Chart(barChart, Add_entries(xdata!!, ydata!!),"co2")
            }
            Draw_Chart(barChart, Add_entries(xdata!!, ydata!!),"co2")
        }
    }

    //-----------------------------????????? ????????? ?????? ?????? ?????? ?????????------------------//
    fun Add_entries(x: Array<Float>, y:Array<Float>):ArrayList<BarEntry>{
        var entries = ArrayList<BarEntry>()
        var cnt = x.size-1
        for(i in 0..cnt){
            entries.add(BarEntry(x[i], y[i]))
        }
        return entries
    }

    //-----------------------------????????? ????????? ?????????-------------------------------------//
    fun Draw_Chart(barChart: BarChart, entries: ArrayList<BarEntry>, type: String){
        barChart.clear()

        var temp : Array<Float> = arrayOf(41f, 10f) //????????? y??? ??????
        var hum : Array<Float> = arrayOf(101f, 25f)
        var co : Array<Float> = arrayOf(301f, 75f)
        var ymax : Float? = null
        var yline : Float? = null

        if(type == "temp"){
            ymax = temp[0]
            yline = temp[1]
        }
        else if(type == "hum"){
            ymax = hum[0]
            yline = hum[1]
        }
        else {
            ymax = co[0]
            yline = co[1]
        }

        barChart.run {
            description.isEnabled = false // ?????? ?????? ????????? ???????????? description??? ???????????? ?????? (false)
            setMaxVisibleValueCount(12) // ?????? ????????? ????????? ????????? 12?????? ??????
            setPinchZoom(false) // ?????????(?????????????????? ?????? ??? ???????????????) ??????
            setDrawBarShadow(false) //???????????? ?????????
            setDrawGridBackground(true)//???????????? ????????????
            axisLeft.run { //?????? ???. ??? Y?????? ?????? ?????????.
                axisMaximum = ymax!! // ???????????? ??????
                axisMinimum = 0f // ????????? 0
                granularity = yline!! // ???????????? ?????? ???????????? ??????.
                setDrawLabels(true) // ??? ????????? ?????? (0, 50, 100)
                setDrawGridLines(true) //?????? ?????? ??????
                setDrawAxisLine(false) // ??? ????????? ??????
                axisLineColor = ContextCompat.getColor(context,R.color.design_default_color_secondary_variant) // ??? ?????? ??????
                gridColor = ContextCompat.getColor(context,R.color.design_default_color_on_secondary) // ??? ?????? ?????? ?????? ??????
                textColor = ContextCompat.getColor(context,R.color.design_default_color_primary_dark) // ?????? ????????? ?????? ??????
                textSize = 10f //?????? ????????? ??????
            }
            xAxis.run {
                position = XAxis.XAxisPosition.BOTTOM //X?????? ??????????????? ??????.
                granularity = 0.5f // 1 ???????????? ?????? ??????
                setDrawAxisLine(true) // ??? ??????
                setDrawGridLines(false) // ??????
                textColor = ContextCompat.getColor(context,R.color.design_default_color_primary_dark) //?????? ??????
                textSize = 1f // ????????? ??????
                valueFormatter = MyXAxisFormatter() // X??? ?????????(?????? ???????????? ??????) ???????????? ?????? ??????
            }
            axisRight.isEnabled = false // ????????? Y?????? ???????????? ??????.
            setTouchEnabled(false) // ????????? ???????????? ?????? ???????????? ??????
            animateY(1000) // ??????????????? ???????????? ??????????????? ??????
            legend.isEnabled = false //?????? ?????? ??????
        }

        var set = BarDataSet(entries,"DataSet") // ???????????? ?????????
        set.color = ContextCompat.getColor(applicationContext!!,R.color.design_default_color_primary_dark) // ??? ????????? ??? ??????

        val dataSet :ArrayList<IBarDataSet> = ArrayList()
        dataSet.add(set)
        val data = BarData(dataSet)
        data.barWidth = 0.15f //?????? ?????? ??????
        barChart.run {
            this.data = data //????????? ???????????? data??? ????????????.
            setFitBars(true)
            invalidate()
        }

    }

    inner class MyXAxisFormatter : ValueFormatter() {
        private val days = arrayOf("1???","2???","3???","4???","5???","6???","7???","8???","9???"
            ,"10???","11???","12???")
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return days.getOrNull(value.toInt()-1) ?: value.toString()
        }
    }
}