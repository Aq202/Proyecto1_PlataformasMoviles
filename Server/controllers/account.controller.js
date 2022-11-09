const Account = require("../accessLayer/Account");
const User = require("../accessLayer/User");

const createAccount = async (req, res) => {
	try {
		const data = req.body;

		//validar existencia del subject
		const user = new User(req.session.id);
		const userData = await user.getData();

		if (userData === null) {
			const error = "No se han encontrado usuarios con el ID proporcionado.";
			res.statusMessage = error;
			res.status(400).send({ ok: false, err: error });
			return;
		}

		data.subject = req.session.id;

		//crear nueva cuenta
		const result = await Account.createAccount(data);

		let message = "Account created succesfully!";
		res.statusMessage = message;
		res.status(200).send(result);
	} catch (ex) {
		console.log(ex);

		let error = ex?.err ?? "Ocurrió un error.",
			status = ex?.status ?? 500;

		if (ex.code === 11000 && ex?.keyValue?.hasOwnProperty("localId")) {
			status = 400;
			error = "El id ingresado ya se encuentra registrado.";
		}
		res.statusMessage = error;
		res.status(status).send({ err: error });
	}
};

const getAccountList = async (req, res) => {
	const subject = req.session.id;

	const accountsList = await Account.getUserAccountsList(subject);

	res.status(200).send({ accounts: accountsList });
};

const editAccount = async (req, res) => {
	try {
		const accountId = req.params.accountId;
		const data = req.body;

		console.log("🚀 ~ file: account.controller.js ~ line 7 ~ editAccount ~ data", data);
		if (data.user) {
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
	try {
		const accountId = req.params?.accountId;
		const account = new Account(accountId);

		const newDefaultAccount=  await account.deleteAccount();

		res.status(200).send({newDefaultAccount})
	} catch (ex) {
		console.log(ex);

		let error = ex?.err ?? "Ocurrió un error.",
			status = ex?.status ?? 500;

		res.statusMessage = error;
		res.status(status).send({ err: error });
	}
};

const setAsDefaultAccount = async (req, res) => {
	try {
		const accountId = req.params.accountId;
		const account = new Account(accountId);
		const result = await account.setAsDefaultAccount();

		if (result?.modifiedCount > 0) res.sendStatus(200);
		else res.sendStatus(500);
	} catch (ex) {
		console.log(ex);

		let error = ex?.err ?? "Ocurrió un error.",
			status = ex?.status ?? 500;

		res.statusMessage = error;
		res.status(status).send({ err: error });
	}
};

exports.createAccount = createAccount;
exports.editAccount = editAccount;
exports.deleteAccount = deleteAccount;
exports.getAccountList = getAccountList;
exports.setAsDefaultAccount = setAsDefaultAccount;
