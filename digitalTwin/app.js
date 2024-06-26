const express = require('express')
const bodyParser = require('body-parser')
const path = require('path')
const app = express()

app.use(express.static('public'))

let routes = require('./src/routes/routes')
global.appRoot = path.resolve(__dirname)

app.use(bodyParser.urlencoded({ extended: true }))
app.use(bodyParser.json())
    
routes(app)

app.use(function(req, res) {
    res.status(404).send({url: req.originalUrl + ' not found'})
})

app.listen(3000,() => console.log(`Room Digital Twin now listening on port 3000!`))
