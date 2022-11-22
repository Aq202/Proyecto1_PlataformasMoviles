const parseMongoObject = obj => {
	const result = {
		...obj._doc,
	};
	result.id = result._id?.valueOf();
	delete result._id;
	delete result.__v;
	return result;
};

const parseDate = date => {
	const day = `${date.getDate()}`.padStart(2, "0");
	const month = `${date.getMonth() + 1}`.padStart(2, "0");
	const year = `${date.getFullYear()}`.padStart(2, "0");

	return `${day}/${month}/${year}`;
};

/**
 *
 * @param {*} number float.
 * @returns float. Two decimals number.
 */
const twoDecimals = number => {
	const fixedNumber = parseFloat(number).toFixed(2);
	return parseFloat(fixedNumber);
};

/**
 * Función que convierte un string ('true' o 'false') a un valor booleano.
 * @param {*} bool
 * @returns true o false. Null si el valor no corresponde a ninguno.
 */
const parseBooleanStrict = bool => {
	if (bool === true || bool === "true" || bool === 1 || bool === "1") return true;
	else if (bool === false || bool === "false" || bool === 0 || bool === "0") return false;

	return null;
};

const parseUserObject = object => {
	const parsedObject = parseMongoObject(object);
	delete parsedObject.passwordHash;
	parsedObject.birthDate = parseDate(parsedObject.birthDate); //parse Date
	return parsedObject;
};

const parseDebtObject = object => {
	const parsedObj = parseMongoObject(object);
	parsedObj.accountInvolved = parseMongoObject(parsedObj.accountInvolved);
	parsedObj.userInvolved = parseUserObject(parsedObj.userInvolved);
	return parsedObj;
};

exports.parseMongoObject = parseMongoObject;
exports.parseDate = parseDate;
exports.twoDecimals = twoDecimals;
exports.parseBooleanStrict = parseBooleanStrict;
exports.parseUserObject = parseUserObject;
exports.parseDebtObject = parseDebtObject;
