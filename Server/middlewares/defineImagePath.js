const profilePicPath = (req, res, next) => {
	req.imagesPath = "/resources/images/profilePictures/";
	next();
};

module.exports = {
	profilePicPath,
};
