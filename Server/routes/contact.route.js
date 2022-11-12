const express = require("express");
const { getContactData } = require("../controllers/contact.controller");
const { ensureAuth } = require("../middlewares/auth");

const router = express.Router();

router.get("/data/:contactId", ensureAuth, getContactData)

module.exports = router;
