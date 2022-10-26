const express = require("express");
const router = express.Router();
const connection = require("../db/Iotpj_db");
let bodyParser = require("body-parser");
const http = require('http');
router.use(bodyParser.json());
router.use(bodyParser.urlencoded({ extended: true }));

router.get('/get_air_info/', function(req, res){
    let check_time = req.body.check_time;
    let temperature = req.body.temperature;
    let humidity = req.body.humidity;
    let co2 = req.body.co2;

    let getsql = 'select check_time, temperature, humidity, co2 from air_info';
    let info = {'check_time':check_time, 'temperature':temperature, 'humidity':humidity, 'co2':co2};

    connection.query(getsql, function(err, result) {
        if (err) {
            console.log(err)
            res.status(500).json ({
                'code': 500,
                'message': 'DB 오류가 발생했습니다.'
            })
            console.log('select error from air_info table');
            console.log(err);
        }
        else{
            res.status(200).json ({
                'code': 200,
                'message': result
            })
            console.log('Success Get');

        }
    })
})

module.exports = router;