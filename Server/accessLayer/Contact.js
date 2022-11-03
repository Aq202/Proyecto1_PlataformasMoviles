const { parseMongoObject } = require("../helpers/parse");
const { ContactSchema } = require("../models/contact.model");
const { DebtSchema } = require("../models/debt.model");

class Contact {
	static async createContact({ localId, subject, userAsContact, debtsToAccept, debtsAccepted }) {
		const contact = new ContactSchema();

		contact.localId = localId.trim();
		contact.subject = subject.trim();
		contact.userAsContact = subject.trim();
		contact.debtsToAccept = debtsToAccept.trim();
		contact.debtsAccepted = debtsAccepted.trim();

		const saved = await contact.save();
		const parsedObject = parseMongoObject(saved);
		return parsedObject
	}

	static async updateContact(contactId, newData) {
		const updated = await ContactSchema.findByIdAndUpdate(contactId, newData, { new: true });
		if (!updated) return null;
		const parsedObject = parseMongoObject(updated);
		return parsedObject
	}

	static async deleteContact(contactId) {
		const deleted = await ContactSchema.findByIdAndDelete(contactId);
		if (!deleted) return null;
		const parsedObject = parseMongoObject(deleted);
		return parsedObject
	}

	static async addDebtToAccept(contactId, debtId) {
		const debt = await DebtSchema.findById(debtId);
		if(!debt) return null
		const updated = await ContactSchema.findByIdAndUpdate(contactId, { $push: { 'debtsToAccept': debtId } })
		if(!updated) return null
		const parsedObject = parseMongoObject(updated);
		return parsedObject
	}

	static async addDebtAccepted(contactId, debtId) {
		const debt = await DebtSchema.findById(debtId);
		if(!debt) return null
		const updated = await ContactSchema.findByIdAndUpdate(contactId, { $push: { 'debtsAccepted': debtId } })
		if(!updated) return null
		const parsedObject = parseMongoObject(updated);
		return parsedObject
	}
}

module.exports = Contact;
