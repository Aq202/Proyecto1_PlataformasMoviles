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

exports.parseMongoObject = parseMongoObject;
exports.parseDate = parseDate
exports.twoDecimals = twoDecimals