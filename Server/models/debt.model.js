const { Schema, model } = require("mongoose");
const { ObjectId } = require("mongodb");


const debtSchema = Schema({
	localId: { type: Number, required: true },
	subject: { type: ObjectId, ref: "user", required: true },
    accountInvolved: {type: ObjectId, ref:"account", required:true },
    amount : {type: Number, required:true, min:[0, "No se aceptan valores negativos."]},
    active: {type:Boolean, required: true},
    userInvolved: { type: ObjectId, ref: "user", required: true },
});

exports.Debt = model("debt", debtSchema);
