const mysql = require('mysql');
//Database configuration
const connection = mysql.createPool({
    host: "127.0.0.1",
    user: "yongpi",
    database: "yongpi_air_info",
    password: "123123123a",
    port: 3306
});

module.exports = connection;