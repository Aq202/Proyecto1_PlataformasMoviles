const express = require("express");
const {
	registerUser,
	login,
	getSessionUserData,
	newContact,
	getContacts,
	getUserData,
	searchUser,
	editUser,
} = require("../controllers/user.controller");
const newContactSchema = require("../helpers/validationSchemas/newContactSchema");
const userLoginSchema = require("../helpers/validationSchemas/userLoginSchema");
const userRegistrationSchema = require("../helpers/validationSchemas/userRegistrationSchema");
const userUpdateSchema = require("../helpers/validationSchemas/userUpdateSchema");
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

router.post(
	"/edit",
	ensureAuth,
	profilePicPath,
	multer.single("image"),
	validateBody(userUpdateSchema),
	editUser
)

router.post("/login", validateBody(userLoginSchema), login);
router.get("/sessionUserData", ensureAuth, getSessionUserData);
router.post("/addContact", ensureAuth, validateBody(newContactSchema), newContact);
router.get("/contactsList", ensureAuth, getContacts)
router.get("/data/:userId", ensureAuth, getUserData)
router.get("/search", ensureAuth, searchUser)

module.exports = router;
