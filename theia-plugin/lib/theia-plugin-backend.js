"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const theia = require("@theia/plugin");
const service = require("./services/HttpService");
function start(context) {
    const informationMessageTestCommand = {
        id: 'hello-world-example-generated',
        label: "Static Analysis"
    };
    context.subscriptions.push(theia.commands.registerCommand(informationMessageTestCommand, (...args) => {
        return service.sendZip();
    }));
}
exports.start = start;
function stop() {
}
exports.stop = stop;
//# sourceMappingURL=theia-plugin-backend.js.map