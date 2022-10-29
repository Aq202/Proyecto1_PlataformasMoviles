const { Schema, model } = require("mongoose");
const { ObjectId } = require("mongodb");


const operationSchema = Schema({
	localId: { type: Number, required: true },
	subject: { type: ObjectId, ref: "user", required: true },
	title: { type: String, required: true },
	account: { type: ObjectId, ref: "account", required: true },
	amount: { type: Number, required: true },
	active: { type: Boolean, required: true },
	date: { type: Date, required: true },
	description: { type: String },
	category: { type: String, default: null },
	favorite: { type: Boolean, default: false },
	imgUrl: { type: String },
});

exports.OperationModel = model("operation", operationSchema);
