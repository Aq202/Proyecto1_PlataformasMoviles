const multer = require("multer");
const path = require("path")

const storage = multer.diskStorage({
	destination: function (req, file, callback) {
		callback(null, `./public/${req.imagesPath}`);
	},

	filename: function (req, file, callback) {
		let newFilename = Date.now() + "-" + file.originalname;
		callback(null, newFilename);
		req.body.imageUrl = req.imagesPath + newFilename;
		req.body.imageCreated = true
	},
});

const fileFilter = (req, file, callback) => {
	var ext = path.extname(file.originalname);
	if (ext !== ".png" && ext !== ".jpg" && ext !== ".jpeg") {
		req.body.fileValidationError = true
		return callback(null, false, req.body.fileValidationError);
	}
	callback(null, true);
};


module.exports = multer({ storage: storage, fileFilter});
