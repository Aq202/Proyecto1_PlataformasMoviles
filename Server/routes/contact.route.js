const express = require("express");
const { getContactData, deleteContact } = require("../controllers/contact.controller");
const { ensureAuth } = require("../middlewares/auth");

const router = express.Router();

router.get("/data/:contactId", ensureAuth, getContactData);
router.delete("/:contactId", ensureAuth, deleteContact);

module.exports = router;
