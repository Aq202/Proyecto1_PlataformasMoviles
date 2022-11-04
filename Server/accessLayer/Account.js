const { parseMongoObject, parseBooleanStrict, twoDecimals } = require("../helpers/parse");
const validateId = require("../helpers/validateId");
const { AccountModel } = require("../models/account.model");
const Operation = require("./Operation");

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

		//Desmarcar cuentas por default
		if (parseBooleanStrict(defaultAccount) === true) await this.uncheckDefaultAccounts(subject);

		const saved = await account.save();
		const parsedObject = parseMongoObject(saved);
		return parsedObject;
	}

	static async uncheckDefaultAccounts(subject) {
		await AccountModel.updateMany({ subject, defaultAccount: true }, { $set: { defaultAccount: false } });
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

	constructor(accountId) {
		this.id = validateId(accountId);
	}

	
}

module.exports = Account;
