const { parseMongoObject } = require("../helpers/parse");
const { UserSchema } = require("../models/user.model");

class User {
	static async createUser({ name, lastName, email, birthDate, alias, passwordHash, imageUrl }) {
		const user = new UserSchema();

		user.name = name.trim();
		user.lastName = lastName.trim();
		user.email = email.trim();
		user.birthDate = new Date(birthDate);
		user.alias = alias.trim();
		user.passwordHash = passwordHash;
		user.imageUrl = imageUrl || null;

		const saved = await user.save();
		return parseMongoObject(saved);
	}
}

module.exports = User;
