const mongoose = require("mongoose");

class DBConnection{

    static connection = null;

    constructor(){
        this.uri = "mongodb+srv://equipodinamita:NTVt3OUnqOCv8nDf@proyectoplataformasdb.wp8l076.mongodb.net/?retryWrites=true&w=majority";
    }

    async connect(){
        if(!DBConnection.connection) DBConnection.connection = await mongoose.connect(this.uri, { useNewUrlParser: true, useUnifiedTopology: true});
        return DBConnection.connection;
    }
}


module.exports = DBConnection;