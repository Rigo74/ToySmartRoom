const models = require("../models/RoomModel")
const physicalAsset = new models.RoomModel()
const controller = new models.RoomModelOperator(physicalAsset)

function operationStarted(res){
    res.status(201).end()
}

function operationAccepted(res){
    res.status(202).end()
}

exports.getTemperature = (_, res) => {
    res.send({temperature: physicalAsset.getTemperature()})
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