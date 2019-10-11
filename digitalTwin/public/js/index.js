const temperatureSelector = document.getElementById("temp-range")
const selectedTemperature = document.getElementById("selected-temperature")
const roomTemperature = document.getElementById("current-room-temperature")
const roomState = document.getElementById("current-room-state")

const minTemp = 15
const maxTemp = 45
const defaultTemp = (minTemp + maxTemp) / 2

function retrieveRoomData() {
    retrieveRoomTemperature()
    retrieveRoomState()
}

function retrieveRoomTemperature() {
    axios.get("http://localhost:3000/temperature", {
        headers: {
            'Accept': 'application/json'
        }
    })
    .then(result => roomTemperature.innerHTML = result.data.temperature)
    .catch(err => console.log(err))
}

function retrieveRoomState() {
    axios.get("http://localhost:3000/state")
        .then(result => roomState.innerHTML = result.data.state)
        .catch(err => console.log(err))
}

function updateSelectedTemperature() {
    let temperature = temperatureSelector.value
    selectedTemperature.innerHTML = temperature
    axios.post("http://localhost:3000/temperature", {temperature: temperature})
        .then(() => {})
        .catch(err => console.log(err))
}

function main() {
    temperatureSelector.setAttribute("min", minTemp)
    temperatureSelector.setAttribute("max", maxTemp)
    temperatureSelector.value = defaultTemp

    document.getElementById("min-temp").innerHTML = minTemp
    document.getElementById("mid-temp").innerHTML = defaultTemp
    document.getElementById("max-temp").innerHTML = maxTemp
    selectedTemperature.innerHTML = defaultTemp

    retrieveRoomData()
    setInterval(() => retrieveRoomData(), 1000)
}

main()