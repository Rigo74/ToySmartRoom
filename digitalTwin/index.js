const express = require('express')
const bodyParser = require('body-parser')
const app = express()

let routes = require('./src/routes/routes')

app.use(bodyParser.urlencoded({ extended: true }))
app.use(bodyParser.json())
    
routes(app)

app.use(function(req, res) {
    res.status(404).send({url: req.originalUrl + ' not found'})
})

app.listen(3000,() => console.log(`Web Server now listening on port 3000!`))
