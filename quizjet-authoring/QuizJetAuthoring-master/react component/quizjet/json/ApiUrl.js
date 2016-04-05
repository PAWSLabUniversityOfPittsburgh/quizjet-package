
var TEST_SERVER = "http://localhost:8080/quizjet";
var PRODUCTION_SERVER = "http://columbus.exp.sis.pitt.edu/quizjet_authoring";
var SERVER = TEST_SERVER;

module.exports = {
	"GET_USER_INFO" : SERVER + "/api/login.json",
	"LOGIN" : SERVER + "/api/login.json",

	"GET_ALL_TOPICS" : SERVER + "/topic/getAll",
	"GET_ALL_TOPICS_USER" : SERVER + "/topic/getUserAll",
	"GET_ALL_ONTOLOGY":SERVER + "/concept/getAll/",
	"GET_CONCEPTS" : SERVER + "/concept/get/",
	"UPDATE_CONCEPTS" : SERVER + "/concept/update/",
	"DELETE_CONCEPTS" : SERVER + "/concept/delete/",
	"EDIT_CONCEPTS_LINE":SERVER + "/concept/addline/",
	"ADD_CONCEPTS" : SERVER + "/concept/add/",
	"ADD_CONCEPTS_MUL" :SERVER + "/concept/addMul/",
	"DELETE_QUIZ_CONCEPTS":SERVER + "/concept/deletebyrdf/",
	"GET_ALL_CLASSES" : SERVER + "/class/all",
	"IS_RdfId_Available" : SERVER + "/quiz/isRdfIdAvailable/",
	"GET_CLASS_BY_ID" : SERVER + "/class/get/",
	"SEARCH_QUIZ" : SERVER + "/quiz/search/",
	"SEARCH_QUIZ_TOPIC":SERVER + "/quiz/searchbytopic/",

	"NEW_QUIZ" : SERVER + "/quiz/new",
	"GET_QUIZ" : SERVER + "/quiz/id/",
	"UPDATE_QUIZ" : SERVER + "/quiz/update",
	"UPDATE_QUIZ_CLASS_REL" : SERVER + "/quiz/linkClasses",
	"UPDATE_QUIZ_TOPIC_REL" : SERVER + "/topic/linkQuiz",
	"UPDATE_QUIZ_CONCEPTS" : SERVER + "/quiz/injectConcept/",
	"UPLOAD_CLASS_FILE" : SERVER + "/class/new",
	"NEW_TOPIC" : SERVER + "/topic/new",
	"MY_TOPIC" : SERVER + "/topic/myTopics",
	"GET_TOPIC_BY_ID" : SERVER + "/topic/id/",
	"UPDATE_TOPIC" : SERVER + "/topic/update/"
}