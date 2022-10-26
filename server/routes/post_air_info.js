const express = require("express");
const router = express.Router();
const connection = require("../db/Iotpj_db");
let bodyParser = require("body-parser");
const http = require('http');
router.use(bodyParser.json());
router.use(bodyParser.urlencoded({ extended: true }));
let moment = require('moment');
require('moment-timezone');
moment.tz.setDefault("Asia/Seoul");


router.post('/post_air_info/', function(req, res){
    let check_time = moment().format('YYYY-MM-DD HH:mm:ss');
    let temperature = req.body.temperature;
    let humidity = req.body.humidity;
    let co2 = req.body.co2;

    console.log('---입력값---');
    console.log('시간 '+ check_time);
    console.log('습도 '+ humidity);
    console.log('온도 '+ temperature);
    console.log('co2 '+ co2);
    console.log('----------');

    let postsql = 'insert into air_info set ?';
    let info = {'check_time':check_time, 'temperature':temperature, 'humidity':humidity, 'co2':co2};
   
    
    //insert data to air_info DB table
    connection.query(postsql, info, function(err, result1){
        if (err) {
            res.status(500).json ({
                'code': 500,
                'message': 'DB 오류가 발생했습니다.' + info
            })
            console.log('insert error from air_info table');
            console.log(err);
        }
        
        else{
            res.status(200).json ({
                'code': 200,
                'message': '데이터 저장완료'
            })
        }
    })   
})

module.exports = router;