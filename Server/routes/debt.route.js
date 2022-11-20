const express = require("express");
const { createDebt, deleteDebt, getDebtData, getDebtsList } = require("../controllers/debt.controller");
const newDebtSchema = require("../helpers/validationSchemas/newDebtSchema");
const { ensureAuth } = require("../middlewares/auth");
const validateBody = require("../middlewares/validateBody");


const router = express.Router();

router.post("/create", ensureAuth, validateBody(newDebtSchema), createDebt)
router.delete("/:debtId", ensureAuth, deleteDebt)
router.get("/data/:debtId", ensureAuth, getDebtData)
router.get("/list", ensureAuth, getDebtsList)

module.exports = router;
