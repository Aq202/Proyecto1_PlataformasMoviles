const express = require("express");
const { registerUser } = require("../controllers/user.controller");
const userRegistrationSchema = require("../helpers/validationSchemas/userRegistrationSchema");
const { profilePicPath } = require("../middlewares/defineImagePath");
const validateBody = require("../middlewares/validateBody");
const multer = require("../services/multer");

const router = express.Router();

router.post(
	"/register",
	profilePicPath,
	multer.single("image"),
	validateBody(userRegistrationSchema),
	registerUser
);

module.exports = router;
