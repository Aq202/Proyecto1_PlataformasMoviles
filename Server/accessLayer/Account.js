const { parseMongoObject } = require("../helpers/parse");
const validateId = require("../helpers/validateId");
const { AccountModel } = require("../models/account.model");

class Account {
	static async createAccount({
		localId,
		subject,
		title,
		defaultAccount,
		total,
		allowNegativeValues,
		editable,
	}) {
		const account = new AccountModel();

		account.localId = localId.trim();
		account.subject = subject.trim();
		account.title = title.trim();
		account.defaultAccount = defaultAccount || false;
		account.total = total.trim();
		account.allowNegativeValues = allowNegativeValues || false;
		account.editable = editable || true;

		const saved = await account.save();
		const parsedObject = parseMongoObject(saved);
		return parsedObject;
	}

	constructor(userId) {
		this.id = validateId(userId);
	}

	async getData() {
		if (!this.data) {
			const data = await AccountModel.findById(this.id);

			if (data === null) return null;
			this.data = parseMongoObject(data);
		}
		return this.data;
	}

	static async updateAccount(accountId, newData) {
		const updated = await AccountSchema.findByIdAndUpdate(accountId, newData, { new: true });
		if (!updated) return null;
		const parsedObject = parseMongoObject(updated);
		return parsedObject;
	}

	static async deleteAccount(accountId) {
		const deleted = await AccountSchema.findByIdAndDelete(accountId);
		if (!deleted) return null;
		const parsedObject = parseMongoObject(deleted);
		return parsedObject;
	}

	static async getUserAccountsList(subjectId) {
		const list = await AccountModel.find({ subject: subjectId });
		const parsedList = list.map(item => parseMongoObject(item));
		return parsedList;
	}
}

module.exports = Account;
