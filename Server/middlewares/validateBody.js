const fs = require("fs");

const validateBody = schema => async (req, res, next) => {
	try {
		await schema.validate(req.body);
		return next();
	} catch (err) {
		//delete profile image if exists
		fs.unlink(`./public${req.body.imageUrl}`, err => {
			if (!err) console.log("File deleted! ");
		});

		res.statusMessage = err.message;
		res.status(400).send({ err: err.message, status: 400, ok: false });
	}
};

module.exports = validateBody;
