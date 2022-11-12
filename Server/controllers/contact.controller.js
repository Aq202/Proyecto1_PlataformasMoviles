const Contact = require("../accessLayer/Contact");
const User = require("../accessLayer/User");

const createContact = async (req, res) => {
	try {
		const data = req.body;

		console.log("🚀 ~ file: contact.controller.js ~ line 7 ~ createContact ~ data", data);
		const subject = await User.getUser(data.subject);
		const userAsContact = await User.getUser(data.userAsContact);
		if (subject === null || userAsContact === null) {
			var error;
			if (subject === null)
				error = "No se han encontrado usuarios con el ID proporcionado para subject.";
			else if (userAsContact === null)
				error = "No se han encontrado usuarios con el ID proporcionado para userAsContact.";
			res.statusMessage = error;
			res.status(404).send({ ok: false, err: error });
			return;
		}

		const result = await Contact.createContact(data);

		let message = "Contact created succesfully!";
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

const editContact = async (req, res) => {
	try {
		const contactId = req.params.contactId;
		const data = req.body;

		console.log("🚀 ~ file: contact.controller.js ~ line 7 ~ editContact ~ data", data);

		var subject = "";
		var userAsContact = "";
		if(data.subject)
			subject = await User.getUser(data.subject);
		if(data.userAsContact)
			userAsContact = await User.getUser(data.userAsContact);
		if (subject === null || userAsContact === null) {
			var error;
			if (subject === null)
				error = "No se han encontrado usuarios con el ID proporcionado para subject.";
			else if (userAsContact === null)
				error = "No se han encontrado usuarios con el ID proporcionado para userAsContact.";
			res.statusMessage = error;
			res.status(404).send({ ok: false, err: error });
			return;
		}

		const result = await Contact.editContact(contactId, data);

		if (result === null) {
			const error = "No se han encontrado contactos con el ID indicado.";
			res.statusMessage = error;
			res.status(404).send({ ok: false, err: error });
			return;
		}

		let message = "Contact updated succesfully!";
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

const deleteContact = async (req, res) => {
	const contactId = req.params.contactId;

	const contactDeleted = await Contact.deleteContact(contactId);

	if (contactDeleted === null) {
		const error = "No se han encontrado contactos con el ID proporcionado.";
		res.statusMessage = error;
		res.status(404).send({ ok: false, err: error });
		return;
	}

	res.status(200).send({ ok: true, result: "Contacto eliminado exitosamente" });
};

const addDebtToAccept = async (req, res) => {
	const contactId = req.params.contactId;
	const debtId = req.body.debtId;

	const updatedContact = await Contact.addDebtToAccept(contactId, debtId);
	
	if(updatedContact === null){
		const error = "El ID proporcionado para el contacto o la deuda no es válido.";
		res.statusMessage = error;
		res.status(404).send({ ok: false, err: error });
		return;
	}
	res.status(200).send({ ok: true, result: "Deuda por aceptar agregada exitosamente" });
}

const addDebtAccepted = async (req, res) => {
	const contactId = req.params.contactId;
	const debtId = req.body.debtId;

	const updatedContact = await Contact.addDebtAccepted(contactId, debtId);
	
	if(updatedContact === null){
		const error = "El ID proporcionado para el contacto o la deuda no es válido.";
		res.statusMessage = error;
		res.status(404).send({ ok: false, err: error });
		return;
	}
	res.status(200).send({ ok: true, result: "Deuda por aceptada agregada exitosamente" });
}

const getContactData = async (req, res) => {

	try{

		const contactId = req.params.contactId;
		const userId = req.session?.id
		const contact = new Contact(contactId, userId)

		const result = await contact.getData()

		if(result) res.status(200).send(result)
		else throw {err: "No se encontró el contacto.", status:404}

	}catch(ex){

		console.log("ERROR IMPRESO: ", ex);
		let error = ex?.err ?? "Ocurrio un error";
		let status = ex?.status ?? 500;

		res.statusMessage = error;
		res.status(status).send({ err: error });
	}

}

exports.createContact = createContact;
exports.editContact = editContact;
exports.deleteContact = deleteContact;
exports.addDebtToAccept = addDebtToAccept;
exports.addDebtAccepted = addDebtAccepted;
exports.getContactData = getContactData