const Operation = require("../accessLayer/Operation");
const User = require("../accessLayer/User");
const Account = require("../accessLayer/Account");

const createOperation = async (req, res) => {
	try {
		const data = req.body;

		console.log("🚀 ~ file: operation.controller.js ~ line 7 ~ createOperation ~ data", data);
		const subject = await User.getUser(data.subject);
		const account = await Account.getAccount(data.account);
		if (subject === null || account === null) {
			var error;
			if (subject === null)
				error = "No se han encontrado usuarios con el ID proporcionado para subject.";
			else if (account === null)
				error = "No se han encontrado cuentas con el ID proporcionado.";
			res.statusMessage = error;
			res.status(404).send({ ok: false, err: error });
			return;
		}

		const result = await Operation.createOperation(data);

		let message = "Operation created succesfully!";
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

const editOperation = async (req, res) => {
	try {
		const operationId = req.params.operationId;
		const data = req.body;

		console.log("🚀 ~ file: operation.controller.js ~ line 7 ~ editOperation ~ data", data);

		var subject = "";
		var account = "";
		if(data.subject)
			subject = await User.getUser(data.subject);
		if(data.account)
            account = await Account.getAccount(data.account);
		if (subject === null || account === null) {
			var error;
			if (subject === null)
				error = "No se han encontrado usuarios con el ID proporcionado para subject.";
			else if (userAsContact === null)
				error = "No se han encontrado cuentas con el ID proporcionado.";
			res.statusMessage = error;
			res.status(404).send({ ok: false, err: error });
			return;
		}

		const result = await Operation.editOperation(operationId, data);

		if (result === null) {
			const error = "No se han encontrado operaciones con el ID indicado.";
			res.statusMessage = error;
			res.status(404).send({ ok: false, err: error });
			return;
		}

		let message = "Operation updated succesfully!";
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

const deleteOperation = async (req, res) => {
	const operationId = req.params.operationId;

	const operationDeleted = await Operation.deleteOperation(operationId);

	if (operationDeleted === null) {
		const error = "No se han encontrado operaciones con el ID proporcionado.";
		res.statusMessage = error;
		res.status(404).send({ ok: false, err: error });
		return;
	}

	res.status(200).send({ ok: true, result: "Operación eliminada exitosamente" });
};

exports.createOperation = createOperation;
exports.editOperation = editOperation;
exports.deleteOperation = deleteOperation;