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

	static async getGeneralBallance(subjectId) {
		const operationsList = await OperationModel.find({ subject: subjectId });

		const sortedOperations = operationsList.sort(
			(opA, opB) => new Date(opA.date) - new Date(opB.date)
		);
		const parsedOperations = operationsList.map(op => {
			const parsedOperation = parseMongoObject(op);
			parsedOperation.date = parseDate(parsedOperation.date);
			return parsedOperation;
		});

		//calcular balance y diferencia del mes anterior
		let ballance = 0;
		let lastMonthBallance = 0;

		sortedOperations.forEach(op => {
			//balance actual
			if (op.active) ballance += op.amount;
			else ballance -= op.amount;

			if (moment(op.date).isBefore(moment().startOf("month"))) {
				if (op.active) lastMonthBallance += op.amount;
				else lastMonthBallance -= op.amount;
			}
		});

		return {
			ballance:twoDecimals( ballance),
			differenceFromPreviousMonth: twoDecimals(ballance - lastMonthBallance),
			operations: parsedOperations,
		};
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
