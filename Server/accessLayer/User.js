const { parseMongoObject } = require("../helpers/parse");
const validateId = require("../helpers/validateId");
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
		const parsedObject = parseMongoObject(saved);
		delete parsedObject.passwordHash;
		return parsedObject;
	}

	/**
	 *
	 * @param User Alias o email del usuario
	 * @param passwordHash hash en formato sha256
	 *
	 * @return Retorna la información del usuario si sus credenciales coinciden. De lo contrario retorna null.
	 */
	static async getUserByCredentials(user, passwordHash) {
		console.log(user, "\n", passwordHash);
		const result = await UserSchema.findOne({
			$or: [
				{ email: user, passwordHash },
				{ alias: user, passwordHash },
			],
		});

		if (!result) return null;
		else {
			const objParsed = parseMongoObject(result);
			delete objParsed.passwordHash;
			return objParsed;
		}
	}

	constructor(userId) {
		this.id = validateId(userId);
	}

	async getData() {
		if (!this.data) {
			const data = await UserSchema.findById(this.id);
			const parsedData = parseMongoObject(data)
			delete parsedData.passwordHash
			this.data = parsedData;
		}

		return this.data;
	}
}

module.exports = User;
