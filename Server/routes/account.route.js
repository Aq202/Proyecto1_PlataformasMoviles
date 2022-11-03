const express = require("express");
const { createAccount, getAccountList } = require("../controllers/account.controller");
const newAccountSchema = require("../helpers/validationSchemas/newAccountSchema");
const { ensureAuth } = require("../middlewares/auth");
const validateBody = require("../middlewares/validateBody");

const router = express.Router();

router.post(
    "/create",
    validateBody(newAccountSchema),
    createAccount
)

router.get("/list", ensureAuth, getAccountList)
module.exports =router