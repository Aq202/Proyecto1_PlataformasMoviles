const { parseMongoObject, parseBooleanStrict, twoDecimals } = require("../helpers/parse");
const validateId = require("../helpers/validateId");
const { AccountModel } = require("../models/account.model");
const { OperationModel } = require("../models/operation.model");
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

		defaultAccount = (parseBooleanStrict(editable) && parseBooleanStrict(defaultAccount))

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
		await AccountModel.updateMany(
			{ subject, defaultAccount: true },
			{ $set: { defaultAccount: false } }
		);
	}

	async getData() {
		if (!this.data) {
			const data = await AccountModel.findById(this.id);

			if (data === null) return null;
			this.data = parseMongoObject(data);
		}
		return this.data;
	}

	async setAsDefaultAccount() {
		const account = await this.getData();

		if (account == null) throw { status: 400, err: "La cuenta proporcionada no existe." };

		await Account.uncheckDefaultAccounts(account.subject);
		const result = await AccountModel.updateOne(
			{ _id: this.id, subject: account.subject },
			{ $set: { defaultAccount: true } }
		);

		return result;
	}

	static async updateAccount(accountId, newData) {
		const updated = await AccountModel.findByIdAndUpdate(accountId, newData, { new: true });
		if (!updated) return null;
		const parsedObject = parseMongoObject(updated);
		return parsedObject;
	}

	async deleteAccount() {
		const data = await this.getData()
		
		if (!data) throw { err: "No se encontró la cuenta.", status: 404 };
		if (data.editable !== true)
			throw { err: "No está permitido eliminar esta cuenta.", status: 403 };

		//Eliminar
		const result = await AccountModel.deleteOne({ _id: this.id, subject:data.subject });
		
		if (result?.deletedCount === 0) throw { err: "No se pudo eliminar la cuenta", status: 500 };

		//eliminar operaciones vinculadas
		await OperationModel.deleteMany({ account: this.id, subject:data.subject });

		if (data.defaultAccount === true) {
			//Asignar una cuenta arbitraria como default
			const randomAccount = await AccountModel.findOne({ editable: true, subject:data.subject });
			if (randomAccount) {
				randomAccount.defaultAccount = true;
				randomAccount.save();
			}

			return parseMongoObject(randomAccount)
		}


		return null;
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
