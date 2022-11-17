const { Schema, model } = require("mongoose");
const { ObjectId } = require("mongodb");


const accountSchema = Schema({
	localId: { type: Number, required: true },
	subject: { type: ObjectId, ref: "user", required: true },
	title: { type: String, required: true },
	defaultAccount: { type: Boolean, default: false },
	allowNegativeValues: { type: Boolean, default: false },
	editable: { type: Boolean, default: true },
});

exports.AccountModel = model("account", accountSchema);
