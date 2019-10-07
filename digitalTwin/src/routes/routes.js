module.exports = (app) => {

    var controller = require('../controllers/controllers')

    app.route('/temperature')
        .get(controller.getTemperature)

    app.route('/action/heat')
        .post(controller.heat)

    app.route('/action/cool')
        .post(controller.cool)

    app.route('/action/stop')
        .post(controller.stopCurrentOperation)

}