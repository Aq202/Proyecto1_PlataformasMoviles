const jwt = require("jsonwebtoken");
const { jwtKey } = require("./code");

const generateSessionToken = userId => {
	return jwt.sign(
		{
			type: "SESSION",
			id: userId,
		},
		jwtKey
	);
};

exports.generateSessionToken = generateSessionToken;
