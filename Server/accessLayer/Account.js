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
		defaultAccount = parseBooleanStrict(defaultAccount) && parseBooleanStrict(editable) !== false;

		account.localId = localId;
		account.subject = subject?.trim();
		account.title = title?.trim();
		account.defaultAccount = defaultAccount ?? false;
		account.total = total;
		account.allowNegativeValues = allowNegativeValues ?? false;
		account.editable = editable ?? true;

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
			const data = await AccountModel.findOne({ _id: this.id, subject: this.subject });

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

	async updateAccount({ subject, title, defaultAccount, total }) {
		const account = await AccountModel.findOne({ _id: this.id, subject });

		if (account == null) throw { err: "La cuenta proporcionada no existe.", status: 400 };

		//Titulo y valor default solo pueden cambiarse para cuentas editables
		if (parseBooleanStrict(account.editable) === true) {
			if (title) account.title = title?.trim();

			//verificar si el valor de cuenta por defaultCambió
			if (
				defaultAccount !== null &&
				defaultAccount !== undefined &&
				account.defaultAccount !== parseBooleanStrict(defaultAccount)
			) {
				account.defaultAccount = defaultAccount;
				//Desmarcar cuentas por default
				if (parseBooleanStrict(defaultAccount) === true)
					await Account.uncheckDefaultAccounts(subject);
			}
		}

		if (total !== null && total !== undefined) account.total = total;

		const saved = await account.save();
		const parsedObject = parseMongoObject(saved);
		return parsedObject;
	}

	async deleteAccount() {
		const data = await this.getData();

		if (!data) throw { err: "No se encontró la cuenta.", status: 404 };
		if (data.editable !== true)
			throw { err: "No está permitido eliminar esta cuenta.", status: 403 };

		//Eliminar
		const result = await AccountModel.deleteOne({ _id: this.id, subject: data.subject });

		if (result?.deletedCount === 0) throw { err: "No se pudo eliminar la cuenta", status: 500 };

		//eliminar operaciones vinculadas
		await OperationModel.deleteMany({ account: this.id, subject: data.subject });

		if (data.defaultAccount === true) {
			//Asignar una cuenta arbitraria como default
			const randomAccount = await AccountModel.findOne({ editable: true, subject: data.subject });
			if (randomAccount && randomAccount != null) {
				randomAccount.defaultAccount = true;
				randomAccount.save();
				return parseMongoObject(randomAccount);
			}
		}

		return null;
	}

	static async getUserAccountsList(subjectId) {
		const list = await AccountModel.find({ subject: subjectId });
		const parsedList = list.map(item => parseMongoObject(item));
		return parsedList;
	}

	constructor(accountId, subject) {
		this.id = validateId(accountId, "Account ID.");
		this.subject = validateId(subject, "UserID as subject in account.");
	}

	static async createInitialDebtsAccount(subjectId) {
		Account.createAccount({
			localId: 1,
			subject: validateId(subjectId, "Id del usuario creado inválido."),
			title: "Deudas",
			defaultAccount: false,
			total: 0,
			allowNegativeValues: true,
			editable: false,
		});
	}
}

module.exports = Account;
