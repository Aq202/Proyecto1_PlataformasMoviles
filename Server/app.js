"use strict";

const express = require("express");
const http = require("http");
const port = process.env.PORT || require("./services/port");
const SocketServer = require("./services/socketServer");
const DBConnection = require("./services/DBConnection");
const UserRoute = require("./routes/user.route");
const OperationRoute = require("./routes/operation.route");
const AccountRoute = require("./routes/account.route");
const ContactRoute = require("./routes/contact.route");
const DebtRoute = require("./routes/debt.route");



const app = express();
const httpServer = http.createServer(app);

app.use(express.json());
app.use(express.urlencoded({ extended: true }));

app.use(express.static(__dirname + "/public"));

//agregar routers
app.use("/user", UserRoute);
app.use("/operation", OperationRoute);
app.use("/account", AccountRoute)
app.use("/contact", ContactRoute)
app.use("/debt", DebtRoute)


app.use(function (req, res, next) {
	res.setHeader("Access-Control-Allow-Origin", "*");
	res.setHeader("Access-Control-Allow-Credentials", "true");
	res.setHeader("Access-Control-Allow-Methods", "GET,DELETE,POST,PUT");
	res.setHeader(
		"Access-Control-Allow-Headers",
		"Access-Control-Allow-Headers, Origin,Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers, Authorization"
	);
	next();
});

app.get("/", (req, res) => {
	res.send({msg:"Hello World!"})
})

process.on("unhandledRejection", (error, p) => {
	console.log("=== UNHANDLED REJECTION ===");
	console.dir(error);
});

new SocketServer(httpServer);

const db = new DBConnection();

db.connect()
	.then(() => {
		console.log("Conexión exitosa a la base de datos.");
		httpServer.listen(port, () => {
			console.log("Servidor corriendo en puerto " + port);
		});
	})
	.catch(err => {
		console.log("Error de conexión a la base de datos.\n", err);
	});
