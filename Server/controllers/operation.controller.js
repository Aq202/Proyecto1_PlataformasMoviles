const Operation = require("../accessLayer/Operation");
const User = require("../accessLayer/User");
const Account = require("../accessLayer/Account");

const createOperation = async (req, res) => {
	try {
		const data = req.body;

		const subject = new User(req.session.id);
		const account = new Account(data.account, req.session.id);

		const subjectData = await subject.getData();
		const accountData = await account.getData();

		let error = null;
		if (subjectData === null)
			error = "No se han encontrado usuarios con el ID proporcionado para subject.";
		else if (accountData === null) error = "No se han encontrado cuentas con el ID proporcionado.";

		if (error !== null) {
			res.statusMessage = error;
			res.status(400).send({ err: error });
			return;
		}

		data.subject = req.session.id;

		const result = await Operation.createOperation(data);

		let message = "Operation created succesfully!";
		res.statusMessage = message;
		res.status(200).send(result);
	} catch (ex) {
		console.log(ex);

		let error = ex?.err ?? "Ocurrió un error.",
			status = ex?.status ?? 500;

		res.statusMessage = error;
		res.status(status).send({ err: error });
	}
};



const editOperation = async (req, res) => {
	try {
		const operationId = req.params.operationId;
		const data = req.body;

		console.log("🚀 ~ file: operation.controller.js ~ line 7 ~ editOperation ~ data", data);

		var subject = "";
		var account = "";
		if (data.subject) subject = await User.getUser(data.subject);
		if (data.account) account = await Account.getAccount(data.account);
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

	res.status(200).send({ok: true, result:"Operación eliminada"});
};

const getAllOperations = async (req, res) => {
	try{

	const operations = await Operation.getOperationsBySubject(req.session.id)

		res.status(200).send({operations});
		
	}catch(ex){
		console.log("🚀 ~ file: user.controller.js ~ line 163 ~ getGeneralBallance ~ ex", ex)
		let error = ex?.err ?? "Ocurrio un error";
		let status = ex?.status ?? 500;

		res.statusMessage = error;
		res.status(status).send({ err: error });
	}
};

exports.createOperation = createOperation;
exports.editOperation = editOperation;
exports.deleteOperation = deleteOperation;
exports.getAllOperations = getAllOperations