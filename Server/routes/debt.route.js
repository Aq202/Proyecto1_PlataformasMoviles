const express = require("express");
const { createDebt } = require("../controllers/debt.controller");
const newDebtSchema = require("../helpers/validationSchemas/newDebtSchema");
const { ensureAuth } = require("../middlewares/auth");
const validateBody = require("../middlewares/validateBody");


const router = express.Router();

router.post("/create", ensureAuth, validateBody(newDebtSchema), createDebt)

module.exports = router;
