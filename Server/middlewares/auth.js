const jwt = require("jsonwebtoken");
const { jwtKey } = require("../services/code");

const ensureAuth = (req, res, next) => {
	const authToken = req.headers?.authorization;
	if (!authToken) {
		res.statusMessage = "No se ha especificado el token de autorización.";
		return res.sendStatus(401);
	}
	try {
		jwt.verify(authToken, jwtKey, err => {
			if (err) throw err;

			const { type, id } = jwt.decode(authToken);

			if (type !== "SESSION") throw null; //validar el tipo de token

			req.session = { id };

			next();
		});
	} catch (ex) {
		console.log("🚀 ~ file: auth.js ~ line 24 ~ ensureAuth ~ ex", ex);
		if (ex instanceof jwt.TokenExpiredError)
			res.statusMessage = "El token de autorización ha expirado.";
		else res.statusMessage = "Token de autorización invalido.";

		res.sendStatus(401);
	}
};

exports.ensureAuth = ensureAuth;
