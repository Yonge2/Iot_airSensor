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
                    tv_temper.text = home_air[home_air.size-1].temperature+" *C" //가장 최근 온도
                    tv_co2.text = home_air[home_air.size-1].co2+" ppm" //이산화탄소
                    tv_hum.text = home_air[home_air.size-1].humidity+" %" //습도
                    Log.d("Get_Info","성공")
                }
            }
            override fun onFailure(call: Call<get_air_info>, t: Throwable) {
                Log.d("Get_Info","t"+t.message)
            }
        })


        //----------------------------------온도--------------------------------------//
        iv_temper.setOnClickListener {

            var btn_preData = findViewById<Button>(R.id.btn_predata)
            var btn_orgData = findViewById<Button>(R.id.btn_orgdata)

            var time_temp_0 = home_air[home_air.size-1].check_time.chunked(5)
            var time_temp_1 = time_temp_0[1].chunked(1)
            var time_temp_2 = time_temp_0[2].chunked(1)
            var day = time_temp_1[0] + time_temp_1[1] + time_temp_1[2] +
                    time_temp_1[3] + time_temp_1[4]//현재날짜
            var current_time = time_temp_2[1] + time_temp_2[2] //현재시간
            Log.d("시간", current_time)

            if(current_time.toInt()>12){
                tv_amOrpm.text = day+" 오후"
            }
            else tv_amOrpm.text = day+" 오전"

            var dev_time = current_time.toInt()%12+1

            ydata = Array(12, { 0f } )
            var cnt = 0
            var for_pre_cnt = 0
            for(i in home_air.size-1 downTo home_air.size-dev_time){
                ydata[cnt] = home_air[i].temperature.toFloat()
                cnt++
            }
            //열두시간 전 데이터
            btn_preData.setOnClickListener {
                tv_amOrpm.text = day+" 오전"
                for(i in home_air.size-dev_time downTo home_air.size-(dev_time+11)){
                    ydata[for_pre_cnt] = home_air[i].temperature.toFloat()
                    for_pre_cnt ++
                }
                btn_preData.visibility = View.GONE
                btn_orgData.visibility = View.VISIBLE
                Draw_Chart(barChart, Add_entries(xdata!!, ydata!!),"temp")
            }
            //다시 원래 데이터
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

        //----------------------------------습도--------------------------------------//
        iv_hum.setOnClickListener {
            var btn_preData = findViewById<Button>(R.id.btn_predata)
            var btn_orgData = findViewById<Button>(R.id.btn_orgdata)

            var time_temp_0 = home_air[home_air.size-1].check_time.chunked(5)
            var time_temp_1 = time_temp_0[1].chunked(1)
            var time_temp_2 = time_temp_0[2].chunked(1)
            var day = time_temp_1[0] + time_temp_1[1] + time_temp_1[2] +
                    time_temp_1[3] + time_temp_1[4]//현재날짜
            var current_time = time_temp_2[1] + time_temp_2[2] //현재시간
            Log.d("시간", current_time)

            if(current_time.toInt()>12){
                tv_amOrpm.text = day+" 오후"
            }
            else tv_amOrpm.text = day+" 오전"

            var dev_time = current_time.toInt()%12+1

            ydata = Array(12, { 0f } )
            var cnt = 0
            var for_pre_cnt = 0
            for(i in home_air.size-1 downTo home_air.size-dev_time){
                ydata[cnt] = home_air[i].humidity.toFloat()
                cnt++
            }
            //열두시간 전 데이터
            btn_preData.setOnClickListener {
                for(i in home_air.size-dev_time downTo home_air.size-(dev_time+11)){
                    ydata[for_pre_cnt] = home_air[i].humidity.toFloat()
                    for_pre_cnt ++
                }
                btn_preData.visibility = View.GONE
                btn_orgData.visibility = View.VISIBLE
                Draw_Chart(barChart, Add_entries(xdata!!, ydata!!),"hum")
            }
            //다시 원래 데이터
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
                    time_temp_1[3] + time_temp_1[4]//현재날짜
            var current_time = time_temp_2[1] + time_temp_2[2] //현재시간
            Log.d("시간", current_time)

            if(current_time.toInt()>12){
                tv_amOrpm.text = day+" 오후"
            }
            else tv_amOrpm.text = day+" 오전"

            var dev_time = current_time.toInt()%12+1

            var cnt = 0
            var for_pre_cnt = 0
            for(i in home_air.size-1 downTo home_air.size-dev_time){
                ydata[cnt] = home_air[i].co2.toFloat()
                cnt++
            }
            //열두시간 전 데이터
            btn_preData.setOnClickListener {
                for(i in home_air.size-dev_time downTo home_air.size-(dev_time+11)){
                    ydata[for_pre_cnt] = home_air[i].co2.toFloat()
                    for_pre_cnt ++
                }
                btn_preData.visibility = View.GONE
                btn_orgData.visibility = View.VISIBLE
                Draw_Chart(barChart, Add_entries(xdata!!, ydata!!),"co2")
            }
            //다시 원래 데이터
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

    //-----------------------------그래프 그리기 위한 요소 추가 매서드------------------//
    fun Add_entries(x: Array<Float>, y:Array<Float>):ArrayList<BarEntry>{
        var entries = ArrayList<BarEntry>()
        var cnt = x.size-1
        for(i in 0..cnt){
            entries.add(BarEntry(x[i], y[i]))
        }
        return entries
    }

    //-----------------------------그래프 그리는 매서드-------------------------------------//
    fun Draw_Chart(barChart: BarChart, entries: ArrayList<BarEntry>, type: String){
        barChart.clear()

        var temp : Array<Float> = arrayOf(41f, 10f) //그래프 y축 설정
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
            description.isEnabled = false // 차트 옆에 별도로 표기되는 description을 안보이게 설정 (false)
            setMaxVisibleValueCount(12) // 최대 보이는 그래프 개수를 12개로 지정
            setPinchZoom(false) // 핀치줌(두손가락으로 줌인 줌 아웃하는것) 설정
            setDrawBarShadow(false) //그래프의 그림자
            setDrawGridBackground(true)//격자구조 넣을건지
            axisLeft.run { //왼쪽 축. 즉 Y방향 축을 뜻한다.
                axisMaximum = ymax!! // 맥시멈값 설정
                axisMinimum = 0f // 최소값 0
                granularity = yline!! // 단위마다 선을 그리려고 설정.
                setDrawLabels(true) // 값 적는거 허용 (0, 50, 100)
                setDrawGridLines(true) //격자 라인 활용
                setDrawAxisLine(false) // 축 그리기 설정
                axisLineColor = ContextCompat.getColor(context,R.color.design_default_color_secondary_variant) // 축 색깔 설정
                gridColor = ContextCompat.getColor(context,R.color.design_default_color_on_secondary) // 축 아닌 격자 색깔 설정
                textColor = ContextCompat.getColor(context,R.color.design_default_color_primary_dark) // 라벨 텍스트 컬러 설정
                textSize = 10f //라벨 텍스트 크기
            }
            xAxis.run {
                position = XAxis.XAxisPosition.BOTTOM //X축을 아래에다가 둔다.
                granularity = 0.5f // 1 단위만큼 간격 두기
                setDrawAxisLine(true) // 축 그림
                setDrawGridLines(false) // 격자
                textColor = ContextCompat.getColor(context,R.color.design_default_color_primary_dark) //라벨 색상
                textSize = 1f // 텍스트 크기
                valueFormatter = MyXAxisFormatter() // X축 라벨값(밑에 표시되는 글자) 바꿔주기 위해 설정
            }
            axisRight.isEnabled = false // 오른쪽 Y축을 안보이게 해줌.
            setTouchEnabled(false) // 그래프 터치해도 아무 변화없게 막음
            animateY(1000) // 밑에서부터 올라오는 애니매이션 적용
            legend.isEnabled = false //차트 범례 설정
        }

        var set = BarDataSet(entries,"DataSet") // 데이터셋 초기화
        set.color = ContextCompat.getColor(applicationContext!!,R.color.design_default_color_primary_dark) // 바 그래프 색 설정

        val dataSet :ArrayList<IBarDataSet> = ArrayList()
        dataSet.add(set)
        val data = BarData(dataSet)
        data.barWidth = 0.15f //막대 너비 설정
        barChart.run {
            this.data = data //차트의 데이터를 data로 설정해줌.
            setFitBars(true)
            invalidate()
        }

    }

    inner class MyXAxisFormatter : ValueFormatter() {
        private val days = arrayOf("1시","2시","3시","4시","5시","6시","7시","8시","9시"
            ,"10시","11시","12시")
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return days.getOrNull(value.toInt()-1) ?: value.toString()
        }
    }
}