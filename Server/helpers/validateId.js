const { ObjectId } = require("mongodb");

module.exports = (id, message) => {
	try {
		if (new ObjectId(id).toString() === id.toString()) return id;
		else throw { err: `Id inválido. ${message ?? ""}`, status: 400 };
	} catch (ex) {
		console.log("Error en validación de id ::: ", ex);
		throw { err: `Id inválido. ${message ?? ""}`, status: 400 };
	}
};
