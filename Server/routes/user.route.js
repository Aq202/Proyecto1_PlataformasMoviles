const express = require("express")
const { registerUser } = require("../controllers/user.controller")
const userRegistrationSchema = require("../helpers/validationSchemas/userRegistrationSchema")
const validateBody = require("../middlewares/validateBody")

const router = express.Router()


router.post("/register",validateBody(userRegistrationSchema), registerUser)


module.exports = router;