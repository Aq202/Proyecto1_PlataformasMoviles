const Account = require("../accessLayer/Account");
const Contact = require("../accessLayer/Contact");
const Debt = require("../accessLayer/Debt");
const User = require("../accessLayer/User");

const createDebt = async (req, res) => {
	try {
		const { account: accountId, contactId, localId, amount, active, description } = req.body;
		const sessionId = req.session?.id;

		const sessionUser = new User(sessionId);
		const sessionUserData = await sessionUser.getData();

		if (!sessionUserData) throw { err: "No se encontró el usuario en sesión.", status: 400 };

		const account = new Account(accountId, sessionId);
		const accountData = await account.getData();

		if (!accountData) throw { err: "No se encontró la cuenta.", status: 400 };
		if (!accountData.editable) throw { err: "La cuenta no es editable", status: 400 };

		const contact = new Contact(contactId, sessionId);
		const contactData = await contact.getData();

		if (!contactData) throw { err: "No se encontró el contacto.", status: 400 };

		const result = await Debt.createDebt({
			localId,
			subject: sessionId,
			accountInvolved: accountId,
			amount,
			active,
			userInvolved: contactData.userAsContact.id,
			description,
		});

		//añadir al contacto del usuario
		contact.addNewDebt(result.id);

		res.status(200).send(result);
	} catch (ex) {
		console.log("ERROR IMPRESO: ", ex);
		let error = ex?.err ?? "Ocurrio un error";
		let status = ex?.status ?? 500;

		res.statusMessage = error;
		res.status(status).send({ err: error });
	}
};

const deleteDebt = async (req, res) => {
	const debtId = req.params.debtId;
	const userId = req.session?.id;

	try {
		const sessionUser = new User(userId);
		const sessionUserData = await sessionUser.getData();

		if (!sessionUserData) throw { err: "No se encontró el usuario en sesión.", status: 400 };

		const debt = new Debt(debtId, userId);
		await debt.deleteDebt();

		res.sendStatus(200);
	} catch (ex) {
		console.log("🚀 ~ file: debt.controller.js ~~ deleteDebt ~ ex", ex);
		let error = ex?.err ?? "Ocurrio un error";
		let status = ex?.status ?? 500;

		res.statusMessage = error;
		res.status(status).send({ err: error });
	}
};

const getDebtsList = async (req, res) => {
	try {
		const userId = req.session?.id;

		const sessionUser = new User(userId);
		const sessionUserData = await sessionUser.getData();

		if (!sessionUserData) throw { err: "No se encontró el usuario en sesión.", status: 400 };

		const result = await Debt.getDebtsListBySubject(userId)		
		res.status(200).send(result)
	
	} catch (ex) {
		console.log("🚀 ~ file: debt.controller.js ~ getDebtsList ~ ex", ex)
		let error = ex?.err ?? "Ocurrio un error";
		let status = ex?.status ?? 500;

		res.statusMessage = error;
		res.status(status).send({ err: error });
	}
}

const getDebtData = async (req, res) => {
	try {
		const debtId = req.params.debtId;
		const userId = req.session?.id;

		const sessionUser = new User(userId);
		const sessionUserData = await sessionUser.getData();

		if (!sessionUserData) throw { err: "No se encontró el usuario en sesión.", status: 400 };

		const debt = new Debt(debtId, userId);
		const result = await debt.getData();

		if(!result) throw {err: "No se encontró la deuda en cuestión.", status:404}
		
		res.status(200).send(result)
	
	} catch (ex) {
		console.log("🚀 ~ file: debt.controller.js ~  getDebtData ~ ex", ex)
		let error = ex?.err ?? "Ocurrio un error";
		let status = ex?.status ?? 500;

		res.statusMessage = error;
		res.status(status).send({ err: error });
	}
};

exports.createDebt = createDebt;
exports.deleteDebt = deleteDebt;
exports.getDebtData = getDebtData
exports.getDebtsList = getDebtsList