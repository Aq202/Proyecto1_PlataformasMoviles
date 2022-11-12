const { parseMongoObject } = require("../helpers/parse");
const validateId = require("../helpers/validateId");
const { DebtModel } = require("../models/debt.model");

class Debt {
	static async createDebt({ localId, subject, accountInvolved, amount, active, userInvolved }) {
		const debt = new DebtModel();

        debt.localId = localId;
        debt.subject = validateId(subject);
        debt.accountInvolved = validateId(accountInvolved);
        debt.amount = amount;
        debt.active = active;
        debt.userInvolved = validateId(userInvolved);

		const saved = await debt.save();
		const parsedObject = parseMongoObject(saved);
		return parsedObject
	}
    
    static async updateDebt(debtId, newData){
		const updated = await DebtModel.findByIdAndUpdate(debtId, newData, {new:true});
		if(!updated) return null;
		const parsedObject = parseMongoObject(updated);
		return parsedObject
	}

	static async deleteDebt(debtId){
		const deleted = await DebtModel.findByIdAndDelete(debtId);
		if(!deleted) return null;
		const parsedObject = parseMongoObject(deleted);
		return parsedObject
	}
}

module.exports = Debt;