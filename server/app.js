const express  = require('express');
const app = express();
let bodyParser = require('body-parser');

app.use(bodyParser.json({
    limit: "50mb"
}));
app.use(bodyParser.urlencoded({ 
    limit: "50mb",
    extended: true
}));

let postRouter = require('./routes/post_air_info');
app.use('/IoTpj_server', postRouter);

let getRouter = require('./routes/get_air_info');
app.use('/IoTpj_server', getRouter);


//Server
let server = app.listen(3553, function(){
    console.log('Server on...')
})