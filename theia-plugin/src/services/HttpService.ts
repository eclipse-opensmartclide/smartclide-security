const got = require('got');
const fs = require('fs');
const archiver = require('archiver');
const os = require('os');
const path = require('path');
const write = require('fs').promises;
const FormData = require('form-data');

 

function zip(){
  return new Promise((resolve, reject) => {
    var archive = archiver('zip');
    var output = fs.createWriteStream('tmp.zip');
    output.on('close', function(){
      console.log(archive.pointer() + 'total bytes');
      console.log('archiver has been finalized and the output file descriptor has closed.');
      return resolve();
    })
    archive.on('error', function(err: any){
      reject(err);
    })
    archive.pipe(output);
    archive.directory('/home/anasmarg/Desktop/CERTH/Code', false);
    archive.finalize();
  })
}


export async function sendZip(){
  try{
    let zip_response = await zip();
    const form = new FormData();
    form.append('folder', fs.createReadStream('tmp.zip'));
    form.append('dir', os.userInfo().username + '/');
    const metrics_data = await got.post('http://localhost:8080/code/uploadFolder',{
      body : form
    })

    const ckForm = new FormData();
    ckForm.append('dir', __dirname + '/');
    const csv_data = await got.get('http://localhost:8080/code/ckReport?path=' + os.userInfo().username + '/');

    console.log(metrics_data.body) 

    while(!fs.existsSync(path.join(__dirname, 'report'))){
      fs.mkdir(path.join(__dirname, 'report'), {recursive: true}, (err: any) => {
        if(err){
          throw err;
        }
        console.log('Directory is created.');
      });
    }
    
     await write.writeFile(path.join(__dirname, 'report/ckReport.csv'), csv_data.body);
     return metrics_data;
  }catch(error){
    console.log(error)
  }
}

