
const INITIAL_TEMP = 28
const COOLING_ACTION = 0
const HEATING_ACTION = 1

const INTERVAL_TIME = 3 * 1000

exports.PhysicalAsset = class PhysicalAsset {

    constructor(){
        this.temperature = INITIAL_TEMP
    }

    heatTemperature() {
        this.temperature += 1
    }

    coolTemperature() {
        this.temperature -= 1
    }

    getTemperature() {
        return this.temperature
    }

}

exports.PhysicalAssetOperator = class PhysicalAssetOperator {

    constructor(physicalAsset){
        this.physicalAsset = physicalAsset
        this.intervalId = null
        this.operationId = null
    }

    startHeating() {
        this.doOperation(HEATING_ACTION, () => this.physicalAsset.heatTemperature())
    }

    startCooling() {
        this.doOperation(COOLING_ACTION, () => this.physicalAsset.coolTemperature())
    }

    stopOperation() {
        if(this.intervalId){
            clearInterval(this.intervalId)
            this.operationId = null
            this.intervalId = null
        }
    }

    doOperation(operationId, operationFunction) {
        if(this.intervalId && this.operationId !== operationId){
            clearInterval(this.intervalId)
        }
        if(this.operationId !== operationId){
            this.intervalId = setInterval(operationFunction, INTERVAL_TIME)
            this.operationId = operationId
        }
    }


}