const { Schema, model } = require("mongoose");
const { ObjectId } = require("mongodb");


const contactSchema = Schema({
    localId: { type: Number, required: true },
	subject: { type: ObjectId, ref: "user", required: true },
    userAsContact: { type: ObjectId, ref: "user", required: true },
	debtsToAccept: {type: Array},
    debtsAccepted: {type:Array}
});

exports.ContactSchema = model("contact", contactSchema);
