const yup = require("yup");

module.exports = yup.object().shape({
	name: yup.string().trim().nullable().required("La propiedad 'name' es obligatoria."),
	lastName: yup.string().trim().nullable().required("La propiedad 'lastName' es obligatoria."),
	email: yup
		.string()
		.nullable()
		.email("El valor de 'email' es inválido.")
		.required("La propiedad 'email' es obligatoria."),
    birthDate: yup.date().nullable().typeError("La propiedad 'birthDate' debe ser una fecha valida.").required("La propiedad 'birthDate' es obligatoria."),
	alias: yup.string().nullable().required("La propiedad 'alias' es obligatoria."),
    password: yup.string().trim().nullable().required("La propiedad 'password' es requerida."),
});
