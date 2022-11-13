const { parseMongoObject } = require("../helpers/parse");
const validateId = require("../helpers/validateId");
const { DebtModel } = require("../models/debt.model");

class Debt {
	constructor(id, subject) {
		this.id = validateId(id, "Debt Id.");
		this.subject = validateId(subject, "User Id as subject in Debt.");
	}

	static async createDebt({ localId, subject, accountInvolved, amount, active, userInvolved }) {
		const debt = new DebtModel();

		debt.localId = localId;
		debt.subject = validateId(subject, "Subject invalido.");
		debt.accountInvolved = validateId(accountInvolved, "accountInvolved inválido.");
		debt.amount = amount;
		debt.active = active;
		debt.userInvolved = validateId(userInvolved, "UserInvolved invalido.");

		const saved = await debt.save();
		const parsedObject = parseMongoObject(saved);
		return parsedObject;
	}

	static async updateDebt(debtId, newData) {
		const updated = await DebtModel.findByIdAndUpdate(debtId, newData, { new: true });
		if (!updated) return null;
		const parsedObject = parseMongoObject(updated);
		return parsedObject;
	}

	static async deleteDebt() {
		const deleted = await DebtModel.deleteOne({ subject: this.subject, _id: this.id });
		if (!deleted) return null;
		const parsedObject = parseMongoObject(deleted);
		return parsedObject;
	}
}

module.exports = Debt;
