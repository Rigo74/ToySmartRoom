module.exports = (app) => {

    var controller = require('../controllers/controllers')

    app.route('/')
        .get((_, res) => res.sendFile(`${appRoot}/public/views/index.html`))

    app.route('/temperature')
        .get(controller.getRoomTemperature)
        .post(controller.setRoomTemperature)

    app.route('/state')
        .get(controller.getRoomState)

    app.route('/action/heat')
        .post(controller.heat)

    app.route('/action/cool')
        .post(controller.cool)

    app.route('/action/stop')
        .post(controller.stopCurrentOperation)

}