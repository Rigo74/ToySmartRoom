
const INITIAL_TEMP = 28
const COOLING_ACTION = 0
const HEATING_ACTION = 1

const INTERVAL_TIME = 3 * 1000

exports.RoomModel = class RoomModel {

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

    setTemperature(newTemperature) {
        this.temperature = newTemperature
    }

}

exports.RoomModelOperator = class RoomModelOperator {

    constructor(physicalAsset){
        this.physicalAsset = physicalAsset
        this.intervalId = null
        this.operationId = null
    }

    getState() {
        switch(this.operationId){
            case HEATING_ACTION: return "HEATING"
            case COOLING_ACTION: return "COOLING"
            default: return "STOPPED"
        }
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