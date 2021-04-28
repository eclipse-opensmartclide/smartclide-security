"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : new P(function (resolve) { resolve(result.value); }).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
Object.defineProperty(exports, "__esModule", { value: true });
const got = require('got');
const fs = require('fs');
const archiver = require('archiver');
const os = require('os');
const path = require('path');
const write = require('fs').promises;
const FormData = require('form-data');
function zip() {
    return new Promise((resolve, reject) => {
        var archive = archiver('zip');
        var output = fs.createWriteStream('tmp.zip');
        output.on('close', function () {
            console.log(archive.pointer() + 'total bytes');
            console.log('archiver has been finalized and the output file descriptor has closed.');
            return resolve();
        });
        archive.on('error', function (err) {
            reject(err);
        });
        archive.pipe(output);
        archive.directory('/home/anasmarg/Desktop/CERTH/Code', false);
        archive.finalize();
    });
}
function sendZip() {
    return __awaiter(this, void 0, void 0, function* () {
        try {
            let zip_response = yield zip();
            const form = new FormData();
            form.append('folder', fs.createReadStream('tmp.zip'));
            form.append('dir', os.userInfo().username + '/');
            const metrics_data = yield got.post('http://localhost:8080/code/uploadFolder', {
                body: form
            });
            const ckForm = new FormData();
            ckForm.append('dir', __dirname + '/');
            const csv_data = yield got.get('http://localhost:8080/code/ckReport?path=' + os.userInfo().username + '/');
            console.log(metrics_data.body);
            while (!fs.existsSync(path.join(__dirname, 'report'))) {
                fs.mkdir(path.join(__dirname, 'report'), { recursive: true }, (err) => {
                    if (err) {
                        throw err;
                    }
                    console.log('Directory is created.');
                });
            }
            yield write.writeFile(path.join(__dirname, 'report/ckReport.csv'), csv_data.body);
            return metrics_data;
        }
        catch (error) {
            console.log(error);
        }
    });
}
exports.sendZip = sendZip;
//# sourceMappingURL=HttpService.js.map