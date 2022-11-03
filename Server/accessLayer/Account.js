const { parseMongoObject } = require("../helpers/parse");
const { AccountSchema } = require("../models/account.model");

class Account {
	static async createAccount({ localId, subject, title, defaultAccount, total, allowNegativeValues, editable }) {
		const account = new AccountSchema();

        account.localId = localId.trim();
        account.subject = subject.trim();
        account.title = title.trim();
        account.defaultAccount = defaultAccount || false;
        account.total = total.trim();
        account.allowNegativeValues = allowNegativeValues || false;
        account.editable = editable || true;

		const saved = await account.save();
		const parsedObject = parseMongoObject(saved);
		return parsedObject
	}

    static async updateAccount(accountId, newData){
		const updated = await AccountSchema.findByIdAndUpdate(accountId, newData, {new:true});
		if(!updated) return null;
		const parsedObject = parseMongoObject(updated);
		return parsedObject
	}

	static async deleteAccount(accountId){
		const deleted = await AccountSchema.findByIdAndDelete(accountId);
		if(!deleted) return null;
		const parsedObject = parseMongoObject(deleted);
		return parsedObject
	}
	
	static async getAccount(accountId){
		const account = await AccountSchema.findById(accountId);
		if(!account) return null;
		else return account;
	}
}

module.exports = Account;