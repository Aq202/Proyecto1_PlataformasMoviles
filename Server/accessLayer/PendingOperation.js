const { parseMongoObject } = require("../helpers/parse");
const { PendingOperationSchema } = require("../models/pendingOperations.model");

class PendingOperation {
	static async createPendingOperation({ localId, subject, title, amount, active, description, category, notificationDate }) {
		const pendingOperation = new PendingOperationSchema();

        pendingOperation.localId = localId.trim();
        pendingOperation.subject = subject.trim();
        pendingOperation.title = title.trim();
        pendingOperation.amount = amount.trim();
        pendingOperation.active = active.trim();
        pendingOperation.description = description.trim();
        pendingOperation.category = category.trim() || null;
        pendingOperation.notificationDate = notificationDate.trim() || null;

		const saved = await pendingOperation.save();
		const parsedObject = parseMongoObject(saved);
		return parsedObject
	}

    static async updatePendingOperation(pendingOperationId, newData){
		const updated = await PendingOperationSchema.findByIdAndUpdate(pendingOperationId, newData, {new:true});
		if(!updated) return null;
		const parsedObject = parseMongoObject(updated);
		return parsedObject
	}

	static async deletePendingOperation(pendingOperationId){
		const deleted = await PendingOperationSchema.findByIdAndDelete(pendingOperationId);
		if(!deleted) return null;
		const parsedObject = parseMongoObject(deleted);
		return parsedObject
	}
}

module.exports = PendingOperation;