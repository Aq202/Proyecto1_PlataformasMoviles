const express = require("express");
const { createOperation, editOperation, deleteOperation } = require("../controllers/operation.controller");
const { operationImagePath } = require("../middlewares/defineImagePath");
//const validateBody = require("../middlewares/validateBody");
const multer = require("../services/multer");

const router = express.Router();

router.post(
    "/createOperation",
    operationImagePath,
    multer.single("image"),
    createOperation
)

router.post(
    "/editOperation",
    editOperation
)

router.delete(
    "/deleteOperation",
    deleteOperation
)

module.exports = router;
