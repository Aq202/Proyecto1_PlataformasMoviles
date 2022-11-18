const yup = require("yup");

module.exports = yup.object().shape({
	localId: yup.string().trim().nullable().required("La propiedad 'localId' es obligatoria."),
	title: yup.string().trim().nullable().required("La propiedad 'title' es obligatoria."),
	defaultAccount: yup
		.bool()
		.typeError("La propiedad 'defaultAccount' debe ser un  valor booleano.")
		.required("La propiedad 'defaultAccount' es requerida."),

});
