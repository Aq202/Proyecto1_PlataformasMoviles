const User = require("../accessLayer/User");
const { sha256 } = require("js-sha256");
const fs = require("fs");

const registerUser = async (req, res) => {
	try {
		const data = req.body;

		//encriptar contraseña
		const passwordHash = sha256(data.password);
		delete data.password;
		data.passwordHash = passwordHash;

		console.log("🚀 ~ file: user.controller.js ~ line 7 ~ registerUser ~ data", data);
		const result = await User.createUser(data);

		let message = "User created succesfully!";
		res.statusMessage = message;
		res.status(200).send({ ok: true, status: 200, message, result });
	} catch (ex) {
		console.log(ex);

		//delete profile image if exists
		if (req.body.imageUrl)
			fs.unlink(`./public${req.body.imageUrl}`, err => {
				if (!err) console.log("File deleted! ");
			});

		let error = "Ocurrió un error.",
			status = 500;

		if (ex.code === 11000) {
			status = 400;
			if (ex?.keyValue?.hasOwnProperty("email"))
				error = "El email ingresado ya se encuentra registrado.";
			else if (ex?.keyValue?.hasOwnProperty("alias"))
				error = "El alias ingresado ya ha sido ocupado por otro usuario.";
		}
		res.statusMessage = error;
		res.status(status).send({ ok: false, err: error });
	}
};

exports.registerUser = registerUser;
