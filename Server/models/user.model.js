const { Schema, model } = require("mongoose");

const userSchema = Schema({
	name: { type: String, required: true },
	lastName: { type: String, required: true },
	email: { type: String, unique: true, required: true },
	birthDate: { type: Date, required: true },
	alias: { type: String, unique: true, required: true },
	passwordHash: { type: String, required: true },
	imageUrl: { type: String, default: null },
});

exports.UserSchema = model("user", userSchema);
