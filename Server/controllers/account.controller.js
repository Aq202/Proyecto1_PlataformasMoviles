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

const updateAccount = async (req, res) => {
	try {

		const body = req.body
	
		//validar existencia del subject
		const user = new User(req.session?.id);
		const userData = await user.getData();

		if (userData === null) {
			const error = "No se han encontrado usuarios con el ID proporcionado.";
			res.statusMessage = error;
			res.status(400).send({ ok: false, err: error });
			return;
		}

		//actualizar cuenta
		const account = new Account(req.params?.accountId, req.session.id)
		const result = await account.updateAccount({...body, subject:req.session.id})

		if(!result) throw {err:"No se completó la actualización.", status:500}

		let message = "Account updated succesfully!";
		res.statusMessage = message;
		res.status(200).send(result);
	} catch (ex) {
		console.log(ex);

		let error = ex?.err ?? "Ocurrió un error.",
			status = ex?.status ?? 500;

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
		const account = new Account(accountId, req.session.id);

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
		const account = new Account(accountId, req.session.id);
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
exports.updateAccount = updateAccount;
exports.deleteAccount = deleteAccount;
exports.getAccountList = getAccountList;
exports.setAsDefaultAccount = setAsDefaultAccount;
