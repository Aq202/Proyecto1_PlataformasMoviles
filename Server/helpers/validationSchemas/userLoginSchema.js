const yup = require("yup");

module.exports = yup.object().shape({
	user: yup.string().trim().nullable().required("La propiedad 'user' es obligatoria."),
    password: yup.string().trim().nullable().required("La propiedad 'password' es requerida."),
});
