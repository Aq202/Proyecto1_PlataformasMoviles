const express = require("express");
const {
	registerUser,
	login,
	getSessionUserData,
	newContact,
	getContacts,
} = require("../controllers/user.controller");
const newContactSchema = require("../helpers/validationSchemas/newContactSchema");
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
router.get("/sessionUserData", ensureAuth, getSessionUserData);
router.post("/addContact", ensureAuth, validateBody(newContactSchema), newContact);
router.get("/contactsList", ensureAuth, getContacts)
module.exports = router;
