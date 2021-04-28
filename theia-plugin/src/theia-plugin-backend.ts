import * as theia from '@theia/plugin';
import * as service from './services/HttpService';

export function start(context: theia.PluginContext) {
    const informationMessageTestCommand = {
        id: 'hello-world-example-generated',
        label: "Static Analysis"
    };
    context.subscriptions.push(theia.commands.registerCommand(informationMessageTestCommand, (...args: any[]) => {
        return service.sendZip();
    }));

}

export function stop() {

}
