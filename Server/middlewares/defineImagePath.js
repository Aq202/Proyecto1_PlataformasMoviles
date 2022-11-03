const profilePicPath = (req, res, next) => {
	req.imagesPath = "/resources/images/profilePictures/";
	next();
};

const operationImagePath = (req, res, next)=>{
	req.imagesPath = "/resources/images/operationImages/";
	next();
}

module.exports = {
	profilePicPath,
	operationImagePath
};
