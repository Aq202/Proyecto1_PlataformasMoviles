const express = require("express");
const {
	createOperation,
	editOperation,
	deleteOperation,
	getGeneralBallance,
	getAllOperations,
} = require("../controllers/operation.controller");
const newOperationSchema = require("../helpers/validationSchemas/newOperationSchema");
const { ensureAuth } = require("../middlewares/auth");
const { operationImagePath } = require("../middlewares/defineImagePath");
const validateBody = require("../middlewares/validateBody");
const multer = require("../services/multer");

const router = express.Router();

router.post("/create", ensureAuth, validateBody(newOperationSchema), createOperation);

router.post("/editOperation", editOperation);

router.delete("/deleteOperation", deleteOperation);

router.get("/getAll", ensureAuth, getAllOperations);

module.exports = router;
