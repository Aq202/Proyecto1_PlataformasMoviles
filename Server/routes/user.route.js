const express = require("express");
const { registerUser, login, getSessionUserData, getGeneralBallance } = require("../controllers/user.controller");
const userLoginSchema = require("../helpers/validationSchemas/userLoginSchema");
const userRegistrationSchema = require("../helpers/validationSchemas/userRegistrationSchema");
const { ensureAuth } = require("../middlewares/auth");
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

router.post("/login", validateBody(userLoginSchema), login);
router.get("/sessionUserData", ensureAuth, getSessionUserData)

module.exports = router;
