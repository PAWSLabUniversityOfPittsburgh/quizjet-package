'use strict';

var React = require('react');
var $ = require('jquery');
var brace  = require('brace');
var AceEditor  = require('react-ace');

require('brace/mode/python');
require('brace/theme/github');

var TopicSelector = require('./form/TopicSelector');
var ExternalClassSelector = require('./form/ExternalClassSelector');
var Notifier = require('./form/Notifier');

var notNullParametersMap = {
	"topicId": "Topic",
	"title": "Title", 
	"rdfId": "RdfID", 
	"code": "Code", 
	"minVar": "Minimum Value", 
	"maxVar": "Maximum Value"
};

var pageHeaderMap = {
	"allNewQuiz": "Create New Quiz",
	"modifyQuiz": "Edit Quiz",
	"cloneQuiz": "Clone Quiz",
	"browseQuiz":"Browse Quiz",
	"editConcept2":"Edit Concept"
};

var SearchResultRow = React.createClass({
	displayName: 'SearchResultRow',
	render: function(){
		return (
			<li className="list-group-item">
				<div className="row">
					<div className="col-sm-5">
						<div className="row">
							<label className="col-sm-2">Title:</label>
							<div className="col-sm-10">{this.props.data.title}</div>
						</div>
					</div>
					<div className="col-sm-5">
						<div className="row">
							<label className="col-sm-2">RdfId:</label>
							<div className="col-sm-10">{this.props.data.rdfId}</div>
						</div>
						<div className="row">
							<label className="col-sm-2">Version:</label>
							<div className="col-sm-10">{this.props.data.version}</div>
						</div>
					</div>
					<div className="col-sm-2">
						<div className="btn-group" role="group">
							<button className="btn btn-default" 
							 onClick={this.props.handleConceptClick().bind(this, this.props.data.quizId, this.props.data.title)} >Edit Concept</button>
							
						</div>
					</div>
				</div>

			</li>
			);
	}
});


module.exports = React.createClass({
	displayName: 'QuizEditor',
	handleSimpleValueChange: function(name, event){
		var quizState = this.state.quiz;
		quizState[name] = event.target.value;
		this.setState({"quiz": quizState});
	},
	handleTopicSelect: function(event){
		var quizState = this.state.quiz;
		quizState['topicId'] = event.target.value;
		this.setState({"quiz": quizState});
		var k = (event.target.value);
		$.ajax({
			url: this.props.url.SEARCH_QUIZ_TOPIC + k,
			dataType: 'json',
			cache: true,
			success: function(data){
				this.setState({"resultSet": data});
			}.bind(this),
			error: function(xhr, status, err) {
				console.error(this.props.url.GET_ALL_TOPICS, status, err.toString());
			}.bind(this)
		});
	},
	handleCodeChange: function(value){
		var quizState = this.state.quiz;
		quizState['code'] = value;
		this.setState({"quiz": quizState});
	},
	handlePrivacyChange: function(event){
		var quizState = this.state.quiz;
		quizState.privacy = event.target.value === "private" ? true : false;
		this.setState({"quiz": quizState});
	},
	handleExternalClassesChange: function(event){
		var quizState = this.state.quiz;
		var incomingVal = event.target.value;
		var index = quizState.linkedClasses.indexOf(incomingVal);
		if(index < 0){
			quizState.linkedClasses.push(incomingVal);
		}else{
			quizState.linkedClasses.splice(index, 1);
		}
		this.setState({"quiz": quizState});
	},
	handleSubmit: function(event){
		event.preventDefault();
		var quizState = this.state.quiz;
		//console.log(quizState);

		//check for validity
		//check not null
		var nullParameters = [];
		for(var key in notNullParametersMap){
			if(quizState[key].trim().length < 1){
				nullParameters.push(notNullParametersMap[key]);
			}
		}
		if(nullParameters.length > 0){
			this.setState({
				"notifications": [{
						"key": 0,
						"type": "danger",
						"text": nullParameters.join(", ") + " should not be empty."
					}]
			});
			return null;
		}

		//check for numbers
		if(quizState.maxVar.match(/^\d+$/) === null || quizState.minVar.match(/^\d+$/) === null){
			this.setState({
				"notifications": [{
						"key": 0,
						"type": "danger",
						"text": "Please enter valid integer for minimum and maximum values."
					}]
			});
			return null;
		}

		this.setState({"isSubmitting": true});
		if(this.props.goal === "allNewQuiz" || this.props.goal === "cloneQuiz")
			this.newQuiz();
		else if(this.props.goal === "modifyQuiz")
			this.updateQuiz();
	},
	newQuiz: function(){
		var _ = this;

		this.setState({
			"notifications": [{
					"key": 0,
					"type": "info",
					"text": "Submitting the quiz. Please DO NOT CLOSE the window."
				}]
		});

		$.ajax({
			url: _.props.url.NEW_QUIZ,
			method: "PUT",
			dataType: 'json',
			contentType: "application/json; charset=utf-8",
			data: JSON.stringify(_.state.quiz),
			success: function(data){
				if(data.success){
					var newState = this.state;
					newState.quiz.quizId = data.content[0].quizId;
					newState.notifications[0].text = "Building relations to external classes. Please DO NOT CLOSE the window.";
					newState.notifications.push({
								"key": 1,
								"type": "success",
								"text": data.message
							});
					this.setState(newState);
					//console.log(data.content);
					this.updateQuizClassRel();
				}else{
					this.setState({
						"isSubmitting": false,
						"notifications": [{
							"key": 0,
							"type": "danger",
							"text": "Create failed. " + data.message
						}]
					});
					return null;
				}
			}.bind(_),
			error: function(xhr, status, err) {
				console.error(this.props.url.NEW_QUIZ, status, err.toString());
			}.bind(_)
		});
	},
	updateQuiz: function(){
		this.setState({
			"notifications": [{
					"key": 0,
					"type": "info",
					"text": "Submitting the quiz. Please DO NOT CLOSE the window."
				}]
		});

		$.ajax({
			url: this.props.url.UPDATE_QUIZ,
			method: "PUT",
			dataType: 'json',
			contentType: "application/json; charset=utf-8",
			data: JSON.stringify(this.state.quiz),
			success: function(data){
				if(data.success){
					if(data.content === null){
						//plain update, the task list is finished
						//update the topic relationship
						var newState = this.state;
						newState.notifications[0].text = "Building relations to the topic. Please DO NOT CLOSE the window.";
						newState.notifications.push({
								"key": 1,
								"type": "success",
								"text": data.message
							});
						this.setState(newState);
						this.updateQuizTopicRel();
					}else{
						//console.log(data.content[0]);
						var newState = this.state;
						newState.notifications[0].text = "Building relations to external classes. Please DO NOT CLOSE the window.";
						newState.notifications.push({
								"key": 1,
								"type": "success",
								"text": data.message
							});
						//new version update brings new quiz id
						newState.quiz.quizId = data.content[0].quizId.toString();
						this.setState(newState);
						//trigger the chain
						this.updateQuizClassRel();
					}
				}else{
					this.setState({
						"isSubmitting": false,
						"notifications": [{
							"key": 0,
							"type": "danger",
							"text": "Error occured. " + data.message
						}]
					});
					return null;
				}
			}.bind(this),
			error: function(xhr, status, err) {
				console.error(this.props.url.UPDATE_QUIZ, status, err.toString());
			}.bind(this)
		});
	},
	updateQuizClassRel: function(){
		var _ = this;
		$.ajax({
			url: _.props.url.UPDATE_QUIZ_CLASS_REL,
			method: "PUT",
			dataType: 'json',
			contentType: "application/json; charset=utf-8",
			data: JSON.stringify(_.state.quiz),
			success: function(data){
				if(data.success){
					var newState = this.state;
					newState.notifications[0].text = "Injecting concepts related to this quiz. Please DO NOT CLOSE the window.";
					newState.notifications[1].text = data.message
					this.setState(newState);
					this.updateQuizConcepts();
				}else{
					this.setState({
						"isSubmitting": false,
						"notifications": [{
							"key": 0,
							"type": "danger",
							"text": "External classes linking failed. " + data.message
						}]
					});
					return null;
				}
			}.bind(_),
			error: function(xhr, status, err) {
				console.error(this.props.url.UPDATE_QUIZ_CLASS_REL, status, err.toString());
			}.bind(_)
		});
	},
	updateQuizConcepts: function(){
		var _ = this;
		$.ajax({
			url: _.props.url.UPDATE_QUIZ_CONCEPTS + this.state.quiz.quizId,
			method: "GET",
			success: function(data){
				if(data.success){
					var newState = this.state;
					newState.notifications[0].text = "Building relations to the topic. Please DO NOT CLOSE the window.";
					newState.notifications[1].text = data.message
					this.setState(newState);
					this.updateQuizTopicRel();
				}else{
					this.setState({
						"isSubmitting": false,
						"notifications": [{
							"key": 0,
							"type": "danger",
							"text": "Error occured. " + data.message
						}]
					});
					return null;
				}
			}.bind(_),
			error: function(xhr, status, err) {
				console.error(this.props.url.UPDATE_QUIZ_CONCEPTS, status, err.toString());
			}.bind(_)
		});
	},
	updateQuizTopicRel: function(){
		var _ = this;
		$.ajax({
			url: _.props.url.UPDATE_QUIZ_TOPIC_REL,
			method: "PUT",
			dataType: 'json',
			contentType: "application/json; charset=utf-8",
			data: JSON.stringify(_.state.quiz),
			success: function(data){
				if(data.success){
					this.setState({
						"notifications": [{
							"key": 0,
							"type": "success",
							"text": "All the modificaitons have been saved. "
						}]
					});
				}else{
					this.setState({
						"isSubmitting": false,
						"notifications": [{
							"key": 0,
							"type": "danger",
							"text": "External classes linking failed. " + data.message
						}]
					});
					return null;
				}
			}.bind(_),
			error: function(xhr, status, err) {
				console.error(this.props.url.UPDATE_QUIZ_TOPIC_REL, status, err.toString());
			}.bind(_)
		});
	},
	loadTopicsFromServer: function() {
		$.ajax({
			url: this.props.url.GET_ALL_TOPICS,
			dataType: 'json',
			cache: true,
			success: function(data){
				var topics = [];
				var i = 1;
				for(var key in data){
					topics.push({
						"key": i,
						"value": key,
						"text": data[key]
					});
					i++;
				}
				topics.unshift({"key": -1, "value": "-1", "text": "All topics"});
				topics.unshift({"key": 0, "value": " ", "text": "Please Select One Topic"});
				
				this.setState({"topics": topics});
			}.bind(this),
			error: function(xhr, status, err) {
				console.error(this.props.url.GET_ALL_TOPICS, status, err.toString());
			}.bind(this)
		});
	},
	loadQuizFromServer: function(){
		$.ajax({
			url: this.props.url.GET_QUIZ + this.props.quizId,
			dataType: 'json',
			cache: true,
			success: function(data){
				if(data.success){
					var quiz = data.content[0];
					//console.log(quiz);

					this.setState({
						"quiz": {
							"quizId": quiz.quizId.toString(),
							"topicId": quiz.topicId.toString(),
							"title": quiz.title,
							"rdfId": quiz.rdfId,
							"decp": quiz.decp,
							"questionTypeId": quiz.questionTypeId.toString(),
							"code": quiz.code,
							"linkedClasses": quiz.linkedClasses.map(function(ele){return ele.toString()}),
							"minVar": quiz.minVar.toString(),
							"maxVar": quiz.maxVar.toString(),
							"awsTypeId": quiz.awsTypeId.toString(),
							"privacy": quiz.privacy
						}
					});
				}else{
					this.setState({
						"notifications": [{
								"key": 0,
								"type": "danger",
								"text": "Loading error." + data.message
							}]
					});
					return false;
				}
			}.bind(this),
			error: function(xhr, status, err) {
				console.error(this.props.url.GET_QUIZ, status, err.toString());
			}.bind(this)
		});
	},
	getInitialState: function(){
		return {
			"isSubmitting": false,
			"notifications": [],
			"topics": [],
			"resultSet" : [],
			"awsTypes": [
				{"key": 0, "value": 3, "text": "Integer"},
				{"key": 1, "value": 5, "text": "Float"},
				{"key": 2, "value": 7, "text": "String"},
				{"key": 3, "value": 8, "text": "Double"}
			],
			"quiz": {
				"quizId": "",
				"topicId": "",
				"title": "",
				"rdfId": "",
				"decp": "",
				"questionTypeId": "2",
				"code": "",
				"linkedClasses": [],
				"minVar": "",
				"maxVar": "",
				"awsTypeId": "7",
				"privacy": true
			}
		}
	},
	componentDidMount: function() {
		this.loadTopicsFromServer();
		if(this.props.goal === "modifyQuiz" || this.props.goal === "cloneQuiz")
			this.loadQuizFromServer();
	},
	componentWillReceiveProps: function(props){
		if(props.goal === "modifyQuiz" || this.props.goal === "cloneQuiz")
			this.loadQuizFromServer();
		else if(props.goal === "allNewQuiz"){
			var state = this.state;
			state.quiz = {
				"quizId": "",
				"topicId": "",
				"title": "",
				"rdfId": "",
				"decp": "",
				"questionTypeId": "2",
				"code": "",
				"linkedClasses": [],
				"minVar": "",
				"maxVar": "",
				"awsTypeId": "7",
				"privacy": true
			};
			this.setState(state);
		}
	},
	render: function(){
		var self = this;
		return (
			<div>
				<h1 className="page-header">{pageHeaderMap[this.props.goal]}</h1>
				<form className="form-horizontal">
					{/* topic selector*/}
					<TopicSelector value={this.state.quiz.topicId} data={this.state.topics} handleSelect={this.handleTopicSelect} />
					
				</form>
				<ol className="list-group">
					{self.state.resultSet.map(function(result){
						return <SearchResultRow url={self.props.url} 
						handleBrowseClick={self.props.handleBrowseClick} handleConceptClick={self.props.handleConceptClick}
						key={result.quizId} data={result} />;
					})}
				</ol>
			</div>
			);
	}
});