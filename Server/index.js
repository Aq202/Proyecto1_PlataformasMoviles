'use strict'

var mongoose = require('mongoose/index');
var port = 2004;
var app = require('./app');

mongoose.Promise = global.Promise;

mongoose.connect('mongodb+srv://equipodinamita:NTVt3OUnqOCv8nDf@proyectoplataformasdb.wp8l076.mongodb.net/?retryWrites=true&w=majority', { useNewUrlParser: true, useUnifiedTopology: true})
    .then(() => {
        console.log('Conexión correcta a la base de datos.');
        app.listen(port, () => {
            console.log('Servidor de express corriendo en el puerto: ', port)
        });
    }).catch(err => {
        console.log('Error de conexión.\n', err);
    });