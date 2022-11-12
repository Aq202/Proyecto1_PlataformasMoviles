const { parseMongoObject } = require("../helpers/parse");
const validateId = require("../helpers/validateId");
const { ContactSchema } = require("../models/contact.model");
const { DebtSchema } = require("../models/debt.model");

class Contact {
	static async createContact({ localId, subject, userAsContact }) {
		const contact = new ContactSchema();

		contact.localId = localId;
		contact.subject = subject?.trim();
		contact.userAsContact = userAsContact?.trim();

		const saved = await contact.save();
		const parsedObject = parseMongoObject(saved);
		return parsedObject;
	}

	static async getContactBySubject(subjectId) {
		const result = await ContactSchema.find({ subject: subjectId }).populate("userAsContact");
		return result.map(contact => {
			const parsed = parseMongoObject(contact);
			parsed.userAsContact = parseMongoObject(parsed.userAsContact);
			return parsed;
		});
	}

	constructor(id, sessionUserId) {
		this.id = validateId(id);
		this.subject = validateId(sessionUserId);
	}

	async getData() {
		const data = await ContactSchema.findOne({ _id: this.id, subject: this.subject })
			.populate("debtsToAccept")
			.populate("debtsAccepted")
			.populate("userAsContact")
		if (!data) return null;

		const contactParsed = parseMongoObject(data);
		contactParsed.userAsContact = parseMongoObject(contactParsed.userAsContact)
		contactParsed.debtsToAccept =
			contactParsed.debtsToAccept?.map(debt => parseMongoObject(debt)) ?? [];
		contactParsed.debtsAccepted =
			contactParsed.debtsAccepted?.map(debt => parseMongoObject(debt)) ?? [];
		return contactParsed;
	}

	async addNewDebt(debtId) {
		debtId = validateId(debtId);
		const contact = await ContactSchema.findOne({ _id: this.id, subject: this.subject });
		if (!contact) throw { err: "No se encontró el contacto.", status: 400 };

		if (Array.isArray(contact.debtsAccepted)) contact.debtsAccepted.push(debtId);
		else contact.debtsAccepted = [debtId];

		await contact.save();
	}

	static async updateContact(contactId, newData) {
		const updated = await ContactSchema.findByIdAndUpdate(contactId, newData, { new: true });
		if (!updated) return null;
		const parsedObject = parseMongoObject(updated);
		return parsedObject;
	}

	static async deleteContact(contactId) {
		const deleted = await ContactSchema.findByIdAndDelete(contactId);
		if (!deleted) return null;
		const parsedObject = parseMongoObject(deleted);
		return parsedObject;
	}

	static async addDebtToAccept(contactId, debtId) {
		const debt = await DebtSchema.findById(debtId);
		if (!debt) return null;
		const updated = await ContactSchema.findByIdAndUpdate(contactId, {
			$push: { debtsToAccept: debtId },
		});
		if (!updated) return null;
		const parsedObject = parseMongoObject(updated);
		return parsedObject;
	}

	static async addDebtAccepted(contactId, debtId) {
		const debt = await DebtSchema.findById(debtId);
		if (!debt) return null;
		const updated = await ContactSchema.findByIdAndUpdate(contactId, {
			$push: { debtsAccepted: debtId },
		});
		if (!updated) return null;
		const parsedObject = parseMongoObject(updated);
		return parsedObject;
	}
}

module.exports = Contact;
