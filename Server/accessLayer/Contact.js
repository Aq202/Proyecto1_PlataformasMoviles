const { parseMongoObject } = require("../helpers/parse");
const { ContactSchema } = require("../models/contact.model");
const { DebtSchema } = require("../models/debt.model");

class Contact {
	static async createContact({ localId, subject, userAsContact}) {
		const contact = new ContactSchema();

		contact.localId = localId
		contact.subject = subject?.trim();
		contact.userAsContact = userAsContact?.trim();

		const saved = await contact.save();
		const parsedObject = parseMongoObject(saved);
		return parsedObject
	}

	static async getContactBySubject(subjectId){
		const result = await ContactSchema.find({subject:subjectId}).populate("userAsContact")
		return result.map(contact => {
			const parsed = parseMongoObject(contact)
			parsed.userAsContact = parseMongoObject(parsed.userAsContact)
			return parsed
		})
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
