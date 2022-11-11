const yup = require("yup");

module.exports = yup.object().shape({
	localId: yup.string().trim().nullable().required("La propiedad 'localId' es obligatoria."),
	targetUser: yup.string().trim().nullable().required("La propiedad 'targetUser' es obligatoria."),
});
