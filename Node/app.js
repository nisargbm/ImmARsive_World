const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
const fetch = require("node-fetch");
const exec = require('child_process').execFile;
const { spawn } = require('child_process');

var app = express();
app.use(cors());

app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());

// curl -X GET --header "Accept: application/json" --header "user-key: eb11171f6e4362e41adbce490e74d163" "https://developers.zomato.com/api/v2.1/restaurant?res_id=16774318"

app.get('/res/:id', (request, response, next) => {
    var id = request.params.id;
    if(id === 'createIMDB') {
        next();
    }
    fetch("https://developers.zomato.com/api/v2.1/restaurant?res_id=" + id,
        {
            method: 'GET',
            headers: {
                Accept: 'application/json',
                'Content-Type': 'application/json',
                "user-key": "eb11171f6e4362e41adbce490e74d163"
            }
        }
    )
    .then(res => res.json())
    .then(result => response.send({result}))
    .catch(err => console.log(err));
});

app.get('/fetchIMDB', (request, response) => {
    createIMDB();
});

const createIMDB = () => {
    const imdb = spawn('arcoreimg.exe build-db --input_images_directory=./images/ --output_db_path=./ar.imgdb');
    imdb.stdout.on('data', (data) => {
       console.log(`stdout: ${data}`);
    });
    imdb.stdout.on('error', (err) => {
       console.log('error');
    });
    imdb.on('close', (code) => {
      console.log("close");
    });
}

app.listen(4000, (request, response) => {
    console.log("Listening on port #4000");
});
