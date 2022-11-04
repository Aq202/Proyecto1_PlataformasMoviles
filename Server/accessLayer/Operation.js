const { MongoMissingCredentialsError } = require("mongodb");
const { parseMongoObject, parseDate, twoDecimals } = require("../helpers/parse");
const { OperationModel } = require("../models/operation.model");
const moment = require("moment");

class Operation {
	static async createOperation({
		localId,
		subject,
		title,
		account,
		amount,
		active,
		date,
		description,
		category,
		favourite,
		imgUrl,
	}) {
		const operation = new OperationModel();

		operation.localId = localId;
		operation.subject = subject.trim();
		operation.title = title.trim();
		operation.account = account.trim();
		operation.amount = twoDecimals(amount);
		operation.active = active.trim();
		operation.date = new Date(date);
		operation.description = description?.trim();
		operation.category = category;
		operation.favourite = favourite ?? false;
		operation.imgUrl = imgUrl?.trim();

		const saved = await operation.save();
		const parsedObject = parseMongoObject(saved);

		parsedObject.date = parseDate(parsedObject.date);
		return parsedObject;
	}

	static async getOperationsBySubject(subjectId) {
		const operationsList = await OperationModel.find({ subject: subjectId });

		const parsedOperations = operationsList
			.map(op => {
				const parsedOperation = parseMongoObject(op);
				parsedOperation.formattedDate = parseDate(parsedOperation.date);
				return parsedOperation;
			})
			.sort((opA, opB) => new Date(opB.date) - new Date(opA.date));

		return parsedOperations;
	}

	/**
	 *
	 * @param accountId
	 * @returns Lista de operaciones.
	 */
	static async getOperationsByAccount(accountId) {
		const operations = await OperationModel.find({ account: accountId });
		return operations
			.map(op => {
				const parsedOperation = parseMongoObject(op);
				parsedOperation.formattedDate = parseDate(parsedOperation.date);
				return parsedOperation;
			})
			.sort((opA, opB) => new Date(opB.date) - new Date(opA.date));
	}

	static async updateOperation(operationId, newData) {
		const updated = await OperationSchema.findByIdAndUpdate(operationId, newData, { new: true });
		if (!updated) return null;
		const parsedObject = parseMongoObject(updated);

		return parsedObject;
	}

	static async deleteOperation(operationId) {
		const deleted = await OperationSchema.findByIdAndDelete(operationId);
		if (!deleted) return null;
		const parsedObject = parseMongoObject(deleted);
		return parsedObject;
	}
}

module.exports = Operation;
