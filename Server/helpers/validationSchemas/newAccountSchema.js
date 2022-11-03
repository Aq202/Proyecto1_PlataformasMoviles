const yup = require("yup");

module.exports = yup.object().shape({
	localId: yup.string().trim().nullable().required("La propiedad 'localId' es obligatoria."),
	title: yup.string().trim().nullable().required("La propiedad 'title' es obligatoria."),
	defaultAccount: yup
		.bool()
		.typeError("La propiedad 'defaultAccount' debe ser un  valor booleano.")
		.required("La propiedad 'defaultAccount' es requerida."),
    total: yup.number().nullable().typeError("La propiedad 'total' debe ser un numero").min(0, "El valor minimo es 0.").required("La propiedad 'total' es requerida.")

});
