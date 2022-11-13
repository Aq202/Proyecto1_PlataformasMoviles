const express = require("express");
const {
	createAccount,
	getAccountList,
	getAccountBallance,
	setAsDefaultAccount,
	deleteAccount,
	updateAccount,
	getAccountData,
} = require("../controllers/account.controller");
const newAccountSchema = require("../helpers/validationSchemas/newAccountSchema");
const { ensureAuth } = require("../middlewares/auth");
const validateBody = require("../middlewares/validateBody");

const router = express.Router();

router.post("/create", ensureAuth, validateBody(newAccountSchema), createAccount);
router.get("/list", ensureAuth, getAccountList);
router.post("/:accountId/setAsDefault", ensureAuth, setAsDefaultAccount)
router.delete("/:accountId", ensureAuth, deleteAccount)
router.post("/update/:accountId", ensureAuth, updateAccount)
router.get("/getAccount/:accountId", ensureAuth, getAccountData)

module.exports = router;
