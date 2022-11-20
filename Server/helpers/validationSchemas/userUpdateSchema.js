const yup = require("yup");

module.exports = yup.object().shape({
	name: yup.string().trim().nullable().typeError("La propiedad 'name' debe ser un texto."),
	lastName: yup.string().trim().nullable().typeError("La propiedad 'lastName' debe ser un texto."),
	email: yup
		.string()
		.nullable()
		.email("El valor de 'email' es inválido."),
    birthDate: yup.date().nullable().typeError("La propiedad 'birthDate' debe ser una fecha valida."),
	alias: yup.string().nullable().typeError("La propiedad 'alias' debe ser un texto.."),
    password: yup.string().trim().nullable().typeError("La propiedad 'password' debe ser un texto."),
});
