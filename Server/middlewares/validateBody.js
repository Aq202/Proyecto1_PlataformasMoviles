const validateBody = schema => async (req, res, next) => {
	try {
		await schema.validate(req.body);
		return next();
	} catch (err) {
		res.statusMessage = err.message;
		res.status(400).send({ err: err.message, status: 400, ok: false });
	}
};

module.exports = validateBody;
