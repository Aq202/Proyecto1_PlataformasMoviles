const { parseMongoObject, parseDebtObject } = require("../helpers/parse");
const validateId = require("../helpers/validateId");
const { DebtModel } = require("../models/debt.model");

class Debt {
	constructor(id, subject) {
		this.id = validateId(id, "Debt Id.");
		this.subject = validateId(subject, "User Id as subject in Debt.");
	}

	async getData() {
		if (!this.data) {
			const data = await DebtModel.findOne({ _id: this.id, subject: this.subject }).populate("accountInvolved");

			if (data === null) return null;
			this.data = parseDebtObject(data)
		}
		return this.data;
	}


	static async createDebt({
		localId,
		subject,
		accountInvolved,
		amount,
		active,
		userInvolved,
		description,
	}) {
		const debt = new DebtModel();

		debt.localId = localId;
		debt.subject = validateId(subject, "Subject invalido.");
		debt.accountInvolved = validateId(accountInvolved, "accountInvolved inválido.");
		debt.amount = amount;
		debt.active = active;
		debt.userInvolved = validateId(userInvolved, "UserInvolved invalido.");
		debt.description = description?.trim() || "Sin descripción.";

		const saved = await (await debt.save()).populate("accountInvolved");
		return parseDebtObject(saved)
	}

	static async updateDebt(debtId, newData) {
		const updated = await DebtModel.findByIdAndUpdate(debtId, newData, { new: true });
		if (!updated) return null;
		const parsedObject = parseMongoObject(updated);
		return parsedObject;
	}

	async deleteDebt() {
		

		const data = await this.getData();

		if (!data) throw { err: "No se encontró la cuenta.", status: 404 };

		//Eliminar
		const result = await DebtModel.deleteOne({ _id: this.id, subject: data.subject });

		if (result?.deletedCount === 0) throw { err: "No se pudo eliminar la deuda.", status: 500 };
	}

	static async getDebtsListBySubject(subjectId){
		
		const result = await DebtModel.find({subject:subjectId}).populate("accountInvolved");

		const parsedList = result.map(debt => parseDebtObject(debt))

		return parsedList

	}
}

module.exports = Debt;
