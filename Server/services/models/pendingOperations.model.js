const { Schema, model } = require("mongoose");
const { ObjectId } = require("mongodb");


const pendingOperationSchema = Schema({
    localId: { type: Number, required: true },
	subject: { type: ObjectId, ref: "user", required: true },
    title: { type: String, required: true },
	amount: { type: Number, required: true },
	active: { type: Boolean, required: true },
	description: { type: String },
	category: { type: String, default: null },
    notificationDate: {type: Date, default: null}
});

exports.PendingOperationSchema = model("pendingOperation", pendingOperationSchema);
