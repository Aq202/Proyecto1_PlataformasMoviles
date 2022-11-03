const yup = require("yup");

module.exports = yup.object().shape({
	localId: yup.number().nullable().typeError("La propiedad 'localId' debe ser un número.").required("La propiedad 'localId' es obligatoria."),
	title: yup.string().trim().nullable().required("La propiedad 'title' es obligatoria."),
	account: yup.string().trim().nullable().required("La propiedad 'account' es obligatoria."),
    amount: yup.number().nullable().typeError("La propiedad 'amount' debe ser un numero").min(0, "El valor minimo es 0.").required("La propiedad 'amount' es requerida."),   
    active: yup
		.bool()
		.typeError("La propiedad 'active' debe ser un  valor booleano.")
		.required("La propiedad 'active' es requerida."),
    date: yup.date().nullable().typeError("La propiedad 'date' debe ser una fecha valida.").required("La propiedad 'date' es obligatoria."),
	category: yup.string().trim().nullable().required("La propiedad 'category' es obligatoria."),
    
});
