const parseMongoObject = obj => {
	const result = {
		...obj._doc,
	};
	result.id = result._id?.valueOf();
	delete result._id;
	delete result.__v;
	return result;
};


exports.parseMongoObject = parseMongoObject;
