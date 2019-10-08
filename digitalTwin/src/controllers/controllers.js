const models = require("../models/RoomModel")
const physicalAsset = new models.RoomModel()
const controller = new models.RoomModelOperator(physicalAsset)

function operationStarted(res){
    res.status(201).end()
}

function operationAccepted(res){
    res.status(202).end()
}

exports.getRoomTemperature = (_, res) => {
    res.send({temperature: physicalAsset.getTemperature()})
}

exports.setRoomTemperature = (req, res) => {
    physicalAsset.setTemperature(req.body.temperature)
    res.status(200).end()
}

exports.getRoomState = (_, res) => {
    res.send({state: controller.getState()})
}

exports.cool = (_, res) => {
    controller.startCooling()
    operationStarted(res)
}

exports.heat = (_, res) => {
    controller.startHeating()
    operationStarted(res)
}

exports.stopCurrentOperation = (_, res) => {
    controller.stopOperation()
    operationStarted(res)
}