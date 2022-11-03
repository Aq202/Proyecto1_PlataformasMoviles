const { parseMongoObject } = require("../helpers/parse");
const { DebtSchema } = require("../models/debt.model");

class Debt {
	static async createDebt({ localId, subject, accountInvolved, amount, active, userInvolved }) {
		const debt = new DebtSchema();

        debt.localId = localId.trim();
        debt.subject = subject.trim();
        debt.accountInvolved = accountInvolved.trim();
        debt.amount = amount.trim();
        debt.active = amount.trim();
        debt.userInvolved = amount.trim();

		const saved = await debt.save();
		const parsedObject = parseMongoObject(saved);
		return parsedObject
	}
    
    static async updateDebt(debtId, newData){
		const updated = await DebtSchema.findByIdAndUpdate(debtId, newData, {new:true});
		if(!updated) return null;
		const parsedObject = parseMongoObject(updated);
		return parsedObject
	}

	static async deleteDebt(debtId){
		const deleted = await DebtSchema.findByIdAndDelete(debtId);
		if(!deleted) return null;
		const parsedObject = parseMongoObject(deleted);
		return parsedObject
	}
}

module.exports = Debt;