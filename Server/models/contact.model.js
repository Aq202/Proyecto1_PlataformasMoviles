const { Schema, model } = require("mongoose");
const { ObjectId } = require("mongodb");


const contactSchema = Schema({
    localId: { type: Number, required: true },
	subject: {  type: ObjectId, ref: "user", required: true },
    userAsContact: { type: ObjectId, ref: "user", required: true },
	debtsToAccept: [{type:ObjectId, ref:"debt"}],
    debtsAccepted: [{type:ObjectId, ref:"debt"}]
});

exports.ContactSchema = model("contact", contactSchema);
