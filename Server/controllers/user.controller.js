const User = require("../accessLayer/User");
const { sha256 } = require("js-sha256");
const fs = require("fs");
const { generateSessionToken } = require("../services/jwt");
const Contact = require("../accessLayer/Contact");

const registerUser = async (req, res) => {
	try {
		const data = req.body;

		//encriptar contraseña
		const passwordHash = sha256(data.password);
		delete data.password;
		data.passwordHash = passwordHash;

		console.log("🚀 ~ file: user.controller.js ~ line 7 ~ registerUser ~ data", data);
		const result = await User.createUser(data);

		const token = generateSessionToken(result.id);

		let message = "User created succesfully!";
		res.statusMessage = message;
		res.status(200).send({ token, userData: result });
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

const editUser = async (req, res) => {
	try {
		const userId = req.params.userId;
		const data = req.body;

		//encriptar contraseña
		if (data.password) {
			const passwordHash = sha256(data.password);
			delete data.password;
			data.passwordHash = passwordHash;
		}

		console.log("🚀 ~ file: user.controller.js ~ line 7 ~ editUser ~ data", data);
		const result = await User.editUser(userId, data);

		if (result === null) {
			const error = "No se han encontrado usuarios con el ID proporcionado.";
			res.statusMessage = error;
			res.status(404).send({ ok: false, err: error });
			return;
		}

		let message = "User updated succesfully!";
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

const deleteUser = async (req, res) => {
	const userId = req.params.userId;

	const userDeleted = await User.deleteUser(userId);

	if (userDeleted === null) {
		const error = "No se han encontrado usuarios con el ID proporcionado.";
		res.statusMessage = error;
		res.status(404).send({ ok: false, err: error });
		return;
	}

	res.status(200).send({ ok: true, result: "Usuario eliminado exitosamente" });
};

const login = async (req, res) => {
	const { user, password } = req.body;

	const passwordHash = sha256(password);

	const userData = await User.getUserByCredentials(user, passwordHash);

	if (userData === null) {
		const error = "Usuario o contraseña incorrectos.";
		res.statusMessage = error;
		res.status(400).send({ err: error });
		return;
	}

	//autentificacion exitosa

	const token = generateSessionToken(userData.id);

	res.status(200).send({
		token,
		userData,
	});
};

const getSessionUserData = async (req, res) => {
	const userId = req.session.id;

	try {
		const user = new User(userId);

		const userData = await user.getData();

		if (userData == null) {
			throw { err: "No se encontró el usuario en sesión.", status: 400 };
		} else res.status(200).send(userData);
	} catch (ex) {
		console.log(ex);
		let error = ex?.err ?? "Ocurrio un error";
		let status = ex?.status ?? 500;

		res.statusMessage = error;
		res.status(status).send({ err: error });
	}
};

const newContact = async (req, res) => {
	try {
		const { targetUser, localId } = req.body;

		const user = new User(req.session?.id);

		const target = new User(targetUser);
		if (!(await target.getData())) throw { err: "El usuario objetivo no existe.", status: 404 };

		const result = await user.addContact({ targetUser, localId });
		res.status(200).send(result);
	} catch (ex) {
		console.log("ERROR IMPRESO: ", ex);
		let error = ex?.err ?? "Ocurrio un error";
		let status = ex?.status ?? 500;

		res.statusMessage = error;
		res.status(status).send({ err: error });
	}
};

const getContacts = async (req, res) => {
	try {

		const user = new User(req.session?.id);
		if (!(await user.getData())) throw { err: "El usuario no existe.", status: 404 };

		const result = await user.getContacts()
		res.status(200).send(result);
	} catch (ex) {
		console.log("ERROR IMPRESO: ", ex);
		let error = ex?.err ?? "Ocurrio un error";
		let status = ex?.status ?? 500;

		res.statusMessage = error;
		res.status(status).send({ err: error });
	}
};

exports.registerUser = registerUser;
exports.login = login;
exports.getSessionUserData = getSessionUserData;
exports.editUser = editUser;
exports.deleteUser = deleteUser;
exports.newContact = newContact;
exports.getContacts = getContacts