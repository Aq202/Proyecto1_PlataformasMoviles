const Account = require("../accessLayer/Account");
const User = require("../accessLayer/User");

const createAccount = async (req, res) => {
	try {
		const data = req.body;

		console.log("🚀 ~ file: account.controller.js ~ line 7 ~ createAccount ~ data", data);
		const user = await User.getUser(data.subject);
		if (user === null) {
			const error = "No se han encontrado usuarios con el ID proporcionado.";
			res.statusMessage = error;
			res.status(404).send({ ok: false, err: error });
			return;
		}
		const result = await Account.createAccount(data);

		let message = "Account created succesfully!";
		res.statusMessage = message;
		res.status(200).send({ ok: true, status: 200, message, result });
	} catch (ex) {
		console.log(ex);

		let error = "Ocurrió un error.",
			status = 500;

		if (ex.code === 11000) {
			status = 400;
			if (ex?.keyValue?.hasOwnProperty("localId"))
				error = "El id ingresado ya se encuentra registrado.";
		}
		res.statusMessage = error;
		res.status(status).send({ ok: false, err: error });
	}
};

const editAccount = async (req, res) => {
	try {
		const accountId = req.params.accountId;
		const data = req.body;

		console.log("🚀 ~ file: account.controller.js ~ line 7 ~ editAccount ~ data", data);
		if(data.user){
			const user = await User.getUser(data.subject);
			if (user === null) {
				const error = "No se han encontrado usuarios con el ID proporcionado.";
				res.statusMessage = error;
				res.status(404).send({ ok: false, err: error });
				return;
			}
			const result = await Account.editAccount(accountId, data);

			if (result === null) {
				const error = "No se han encontrado cuentas con el ID indicado.";
				res.statusMessage = error;
				res.status(404).send({ ok: false, err: error });
				return;
			}
		}

		let message = "Account updated succesfully!";
		res.statusMessage = message;
		res.status(200).send({ ok: true, status: 200, message, result });
	} catch (ex) {
		console.log(ex);

		let error = "Ocurrió un error.",
			status = 500;

		if (ex.code === 11000) {
			status = 400;
			if (ex?.keyValue?.hasOwnProperty("localId"))
				error = "El localId ingresado ya se encuentra registrado.";
		}
		res.statusMessage = error;
		res.status(status).send({ ok: false, err: error });
	}
};

const deleteAccount = async (req, res) => {
	const accountId = req.params.accountId;

	const accountDeleted = await Account.deleteAccount(accountId);

	if (accountDeleted === null) {
		const error = "No se han encontrado cuentas con el ID proporcionado.";
		res.statusMessage = error;
		res.status(404).send({ ok: false, err: error });
		return;
	}

	res.status(200).send({ ok: true, result: "Cuenta eliminada exitosamente" });
};

exports.createAccount = createAccount;
exports.editAccount = editAccount;
exports.deleteAccount = deleteAccount;