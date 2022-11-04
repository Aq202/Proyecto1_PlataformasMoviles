const express = require("express");
const {
	createAccount,
	getAccountList,
	getAccountBallance,
} = require("../controllers/account.controller");
const newAccountSchema = require("../helpers/validationSchemas/newAccountSchema");
const { ensureAuth } = require("../middlewares/auth");
const validateBody = require("../middlewares/validateBody");

const router = express.Router();

router.post("/create", ensureAuth, validateBody(newAccountSchema), createAccount);


router.get("/list", ensureAuth, getAccountList);
module.exports = router;
