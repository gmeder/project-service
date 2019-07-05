db.createUser({
	user : "mongo",
	pwd : "mongo",
	roles : [ {
		role : "readWrite",
		db : "projectdb"
	} ]
});

db.projects.insert({
	"projectId" : "11111",
	"ownerFirstName" : "John",
	"ownerLastName" : "Doe",
	"ownerEmailAddress" : "jdoe@example.com",
	"projectTitle":"Microservices Project",
	"projectDescription":"Vertx Microservice for Projects",
	"status":"open"
});
db.projects.insert({
	"projectId" : "22222",
	"ownerFirstName" : "Penny",
	"ownerLastName" : "Wise",
	"ownerEmailAddress" : "pennywise@it.com",
	"projectTitle":"Clown Party",
	"projectDescription":"",
	"status":"cancelled"
});
db.projects.insert({
	"projectId" : "33333",
	"ownerFirstName" : "Thomas",
	"ownerLastName" : "Anderson",
	"ownerEmailAddress" : "neo@matrix.io",
	"projectTitle":"Matrix",
	"projectDescription":"What is the Matrix ?",
	"status":"completed"
})
