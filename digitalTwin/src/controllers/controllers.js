const models = require("../models/PhysicalAsset")
const myPhysicalAsset = new models.PhysicalAsset()
const physicalAssetController = new models.PhysicalAssetOperator(myPhysicalAsset)

function operationStarted(res){
    res.status(201).end()
}

function operationAccepted(res){
    res.status(202).end()
}

exports.getTemperature = (_, res) => {
    res.send({temperature: myPhysicalAsset.getTemperature()})
}

exports.cool = (_, res) => {
    physicalAssetController.startCooling()
    operationStarted(res)
}

exports.heat = (_, res) => {
    physicalAssetController.startHeating()
    operationStarted(res)
}

exports.stopCurrentOperation = (_, res) => {
    physicalAssetController.stopOperation()
    operationStarted(res)
}