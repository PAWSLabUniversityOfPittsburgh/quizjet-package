'use strict';

var React = require('react');
var $ = require('jquery');
var brace  = require('brace');
var AceEditor  = require('react-ace');

require('brace/mode/python');
require('brace/theme/github');

var ConceptEditor = require('./ConceptEdit');
var TopicSelector = require('./form/TopicSelector');
var ExternalClassSelector = require('./form/ExternalClassSelector');
var Notifier = require('./form/Notifier');

var QuizEditor = require('./QuizEditor');
var QuizPreview = require('./QuizPreview');

var notNullParametersMap = {
	"topicId": "Topic",
	"title": "Title", 
	"rdfId": "RdfID", 
	"code": "Code", 
	"minVar": "Minimum Value", 
	"maxVar": "Maximum Value"
};

var pageHeaderMap = {
	"allNewQuiz": "Create New Question",
	"modifyQuiz": "Edit Question",
	"cloneQuiz": "Clone Question",
	"browseQuiz":"Questions",
	"editConcept2":"Edit Concept"
};

var SearchResultRow = React.createClass({
	displayName: 'SearchResultRow',
	update:function(id){
		alert(id);
		this.setState({"quizid":id});
		this.setState({"selectquizId":id});
		
	
		
			$('#myModal2').modal('show');	
	
			React.render(
			     QuizEditor({ url: this.props.url ,
					goal :"browseSingleQuiz",
					quizId:id}),
			     document.getElementById('tobe')
			);
		
	        this.props.refresh(id);
	    },
		getInitialState: function(){
			return {"quizid":0}
		},
	render: function(){
		var imgStyle ={
			width:'30px',
			height:'30px'
		};
		
		if(this.props.index != 0)
		return (
      <tr>
        <td className="text-center"><img   onClick={this.update.bind(this,this.props.data.quizId)}  src="resources/images/preview.png" data-pin-nopin="true" style={imgStyle}/></td>
			<td>{this.props.data.title}</td>
			<td>{this.props.data.version}</td>
        <td className="text-center">
	
							<button className="btn btn-default" 
							 >Cone</button>
					
			</td>
		<td className="text-center">
	
							<button className="btn btn-default" 
							 >Edit Quiz</button>
						
			</td>
		<td className="text-center">					
							<button className="btn btn-default" 
							 >Edit Concept</button>
					
			</td>
      </tr>
			
		
			);
			
			
			else return (
				
	
				 <tr>

				
        <td className="text-center"><img   onClick={this.update.bind(this,this.props.data.quizId)}  src="resources/images/preview.png" data-pin-nopin="true" style={imgStyle}/></td>
			<td>{this.props.data.title}</td>
			<td>{this.props.data.version}</td>
        <td className="text-center">
	
							<button className="btn btn-default" 
							 >Cone</button>
					
			</td>
		<td className="text-center">
	
							<button className="btn btn-default" 
							 >Edit Quiz</button>
						
			</td>
		<td className="text-center">					
							<button className="btn btn-default" 
							 >Edit Concept</button>

					
			</td>
      </tr>
			
		
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
		this.setState({"filterstr":""});
		this.setState({"filterStrAuthor":""});
		var k = (event.target.value);
		$.ajax({
			url: this.props.url.SEARCH_QUIZ_TOPIC + k,
			dataType: 'json',
			cache: true,
			success: function(data){
				this.setState({"resultSet": data});
				this.setState({"resultSetFilter": data});
				
				$('#searchBoxFilter').show();
				
				$('#searchBoxFilter3').show();
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
	handleChange: function(value){
		var filterstr = value.target.value;
		this.setState({"filterstr":filterstr});
		var quizresult = this.state.resultSet;
		var filterstrauthor = this.state.filterStrAuthor;
		var quizs = [];
		for (var i = 0; i < quizresult.length; i++) {
			if((quizresult[i].title.toLowerCase().indexOf(filterstr.toLowerCase()) > -1 || quizresult[i].decp.toLowerCase().indexOf(filterstr.toLowerCase()) > -1) && quizresult[i].author.toLowerCase().indexOf(filterstrauthor.toLowerCase()) > -1)
				quizs.push(quizresult[i]);
		}
		this.setState({"resultSetFilter": quizs});
	},
	handleAuthorChange:function(value){
		var filterstrauthor = value.target.value;
		this.setState({"filterStrAuthor":filterstrauthor});
		var filterstr = this.state.filterstr;
		var quizresult = this.state.resultSet;
		var quizs = [];
		for (var i = 0; i < quizresult.length; i++) {
			if((quizresult[i].title.toLowerCase().indexOf(filterstr.toLowerCase()) > -1 || quizresult[i].decp.toLowerCase().indexOf(filterstr.toLowerCase()) > -1) && quizresult[i].author.toLowerCase().indexOf(filterstrauthor.toLowerCase()) > -1)
				
				quizs.push(quizresult[i]);
		}
		this.setState({"resultSetFilter": quizs});
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
	getBrowseClickHandler: function(quiz){
		
		React.unmountComponentAtNode(document.getElementById('divdiv'));
		ReactDOM.render(<QuizPreview  url={this.props.url} goal={"browseSingleQuiz"} quizId={quiz} />, document.getElementById("divdiv"));
		$("#myModal2").modal("show");
	},
	
	getCloneClickHandler: function(quiz){
		
		React.unmountComponentAtNode(document.getElementById('divdiv'));
		ReactDOM.render(<QuizEditor  url={this.props.url} goal={"cloneQuiz"} quizId={quiz} />, document.getElementById("divdiv"));
		$("#myModal2").modal("show");
	},
	getEditClickHandler: function(quiz){
		
		React.unmountComponentAtNode(document.getElementById('divdiv'));
		ReactDOM.render(<QuizEditor  url={this.props.url} goal={"modifyQuiz"} quizId={quiz} />, document.getElementById("divdiv"));
		$("#myModal2").modal("show");
	},
	
	handleEditConcept: function(quiz,title,rdfId){
		
		React.unmountComponentAtNode(document.getElementById('divdiv'));
		ReactDOM.render(<ConceptEditor url={this.props.url} quizId={quiz} rdfId = {rdfId} title ={title}/>, document.getElementById("divdiv"));
		$("#myModal2").modal("show");
	},
	handleRefresh:function(qid){
		// var selectquizId = this.state.selectquizId;
	// 	selectquizId = qid;
	// 	this.setState({selectquizId:selectquizId});
	//
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
	loadAllQuestions:function(){
		var k = '-1';
		$.ajax({
			url: this.props.url.SEARCH_QUIZ_TOPIC + k,
			dataType: 'json',
			cache: true,
			success: function(data){
				this.setState({"resultSet": data});
				this.setState({"resultSetFilter": data});
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
			"resultSetFilter" : [],
			"selectquizId":300,
			"filterstr":"",
			"filterStrAuthor":"",
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
		this.loadAllQuestions();
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
		var inputStyle = {
			display: 'none'
		};
		var modalStyle = {
			width: '80%'
			//margin-left:-40%;
		};
		var imgStyle ={
			width:'20px',
			height:'20px'
		}
		return (
			
			
			
			<div>
			
					
<div className="modal fade" id="myModal2" tabindex="-1" role="dialog" aria-labelledby="myModalLabel2" aria-hidden="true">
  <div className="modal-dialog modal-lg" style={modalStyle}>
    <div className="modal-content">
      <div className="modal-header">
        <button type="button" className="close" data-dismiss="modal"><span aria-hidden="true">x</span><span className="sr-only">Close</span></button>
        <h4 className="modal-title" id="myModalLabel2"></h4>
      </div>
      <div className="modal-body">
							<div id = "divdiv"></div>	
			
      </div>
      
    </div>
  </div>
</div>  
			
			
			
				<h1 className="page-header">{pageHeaderMap[this.props.goal]}</h1>
				<form className="form-horizontal">
					{/* topic selector*/}
					<TopicSelector value={this.state.quiz.topicId} data={this.state.topics} handleSelect={this.handleTopicSelect} />
					
					<div className="form-group" id="searchBoxFilter" >
			

			
					<label htmlFor="topicselector" className="col-sm-2 control-label"><img  src="resources/images/mg.png" data-pin-nopin="true" style={imgStyle}/></label>
					
					<div className="col-sm-4">
					
					<input type="input" className="form-control" value = {this.state.filterstr}
					     onChange={this.handleChange} placeholder="Filter by quiz title or description here." />
					</div>
					
					
					</div>
					
					<div className="form-group" id="searchBoxFilter" >
			

			
					<label htmlFor="topicselector" className="col-sm-2 control-label"><img  src="resources/images/author.png" data-pin-nopin="true" style={imgStyle}/></label>
					
					<div className="col-sm-4">
					
					<input type="input" className="form-control" value = {this.state.filterStrAuthor}
					     onChange={this.handleAuthorChange} placeholder="Filter by author here." />
					</div>
					
					
					</div>
				</form>
					{/*
				<ol className="list-group">
					{self.state.resultSetFilter.map(function(result){
						return <SearchResultRow url={self.props.url} 
						handleBrowseClick={self.props.handleBrowseClick}
						key={result.quizId} data={result} />;
					})}
				</ol>*/}

						<div className="container" id="searchBoxFilter3" >         
  <table className="table table-bordered">
    <thead>
      <tr className="info">
        <th>Preview</th>
        <th>Question</th>
						<th>Author</th>
		<th>Version</th>
        <th>Clone</th>
		<th>Edit</th>
		<th>Edit Concept</th>
      </tr>
    </thead>
    <tbody>
					{/*  {self.state.resultSetFilter.map(function(result,i){
							return <SearchResultRow url={self.props.url}  index = {i}
							handleBrowseClick={self.getBrowseClickHandler}
							key={result.quizId}  refresh={self.handleRefresh}  data={result} />;
						}.bind(this))}  */}
						{self.state.resultSetFilter.map(function(result,i){
							var imgStyle ={
								width:'30px',
								height:'30px'
							};
							var inputStyle = {
								display: 'none'
							};
							var inputStyleshow = {
								display: 'block'
							};
							{/*return <tr> <td className="text-center"><img   onClick={self.getBrowseClickHandler.bind(this,result.quizId)}  src="resources/images/preview.png" data-pin-nopin="true" style={imgStyle}/></td> <td>{result.title}</td><td>{result.version}</td><td><button onClick={self.getCloneClickHandler.bind(this,result.quizId)} className="btn btn-default" >Clone</button></td><td>	<button className="btn btn-default" disabled={!result.canModify}  onClick={self.getEditClickHandler.bind(this,result.quizId)} >Edit Quiz</button></td><td><button className="btn btn-default"  disabled={!result.canModify}  onClick={self.handleEditConcept.bind(this,result.quizId,result.title,result.rdfId)} >Edit Concept</button></td></tr>
							 */}
							return <tr> <td className="text-center"><img   onClick={self.getBrowseClickHandler.bind(this,result.quizId)}  src="resources/images/preview.png" data-pin-nopin="true" style={imgStyle}/></td> <td>{result.title}</td><td>{result.author}</td><td>{result.version}</td><td><button onClick={self.getCloneClickHandler.bind(this,result.quizId)} className="btn btn-default" >Clone</button></td><td>	<button className="btn btn-default" style={(!result.canModify)?inputStyle:inputStyleshow} onClick={self.getEditClickHandler.bind(this,result.quizId)} >Edit Question</button></td><td><button className="btn btn-default"  style={(!result.canModify)?inputStyle:inputStyleshow} onClick={self.props.handleEditConcept().bind(this,result.quizId,result.title,result.rdfId)} >Edit Concept</button></td></tr>
					
					
			
		})}
						
    </tbody>
  </table>
</div> 
						
				
				
						</div>
			);
	}
});