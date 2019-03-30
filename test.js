var exec = require('child_process').execFile;

var fun =function(){
   console.log("fun() start");
   exec('arcoreimg.exe', [build-db,--input_images_directory='C:\\Yogesh\\Hackathons\\Bit-Camp 2019 Hackathon\\ImmARsive_World\\Images', --output_db_path="C:\\Yogesh\\Hackathons\\Bit-Camp 2019 Hackathon\\ImmARsive_World\\image2.imgdb"] function(err, data) {  
        console.log(err)
        console.log(data.toString());                       
    });  
}

fun();