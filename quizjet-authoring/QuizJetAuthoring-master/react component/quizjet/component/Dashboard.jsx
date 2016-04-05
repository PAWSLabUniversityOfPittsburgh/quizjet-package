'use strict';

var React = require('react');
var $ = require('jquery');
var Navigation = require('react-router').Navigation;

var QuizEditor = require('./QuizEditor');
var QuizSearcher = require('./QuizSearcher');
var TopicEditor = require('./TopicEditor');
var MyTopic = require('./MyTopic');
var ConceptEditor = require('./ConceptEdit');
var QuizBrowser = require('./QuizBrowser');
var QuizBrowser2 = require('./QuizBrowser2');
var topicGroupOptions = [
	{
		"id" : "createTopic",
		"text" : "Create Topic"
	},
	{
		"id" : "myTopic",
		"text" : "My Topics"
	}
];

var quizGroupOptions = [
	{
		"id" : "createQuiz",
		"text" : "Create Question"
	},
	//  {
	// 	"id" : "searchQuiz",
	// 	"text" : "Search Question"
	// },
	{
		"id" : "browseQuiz",
		"text" : "Browse Question"
	}
	// {
	// 	"id" : "editConcept2",
	// 	"text" : "Edit Concept"
	// }
];

var mapItems = function(option){
	return (
		<li key={option.id} id={option.id} className={this.getSidebarClassName(option.id)} 
		role={"button"} onClick={this.handleSidebarItemClick}>{option.text}</li>
		);
}

module.exports = React.createClass({
	mixins: [Navigation],
	displayName: 'Dashboard',
	mainAreaContentMap: {
		"createQuiz": function(){
			return <QuizEditor url={this.props.url} goal={"allNewQuiz"} />;
		},
		"searchQuiz": function(){
			return (
				<QuizSearcher url={this.props.url} handleEditClick={this.getEditClickHandler}
				 handleBrowseClick={this.getBrowseClickHandler} handleCloneClick={this.getCloneClickHandler}/>
				);
		},
		"browseQuiz": function(){
			return (
				<QuizBrowser  goal={"browseQuiz"} url={this.props.url} handleBrowseClick={this.getBrowseClickHandler} handleEditConcept={this.getConceptClickHandler} handleConceptClick={this.getConceptClickHandler}/>
				);
		},
		"editQuiz": function(){
			return <QuizEditor url={this.props.url} goal={"modifyQuiz"} quizId={this.state.data.quizId} />
		},
		"browseSingleQuiz": function(){
			return <QuizEditor url={this.props.url} goal={"browseSingleQuiz"} quizId={this.state.data.quizId} />
		},
		"cloneQuiz": function(){
			return <QuizEditor url={this.props.url} goal={"cloneQuiz"} quizId={this.state.data.quizId} />
		},
		"createTopic": function(){
			return <TopicEditor url={this.props.url} goal={"allNewTopic"} />;
		},
		"myTopic": function(){
			return <MyTopic url={this.props.url} handleEditClick={this.getTopicEditClickHandler} />;
		},
		"editTopic": function(){
			return <TopicEditor url={this.props.url} goal={"modifyTopic"} topicId={this.state.data.topicId} />;
		},
		'editConcept': function(){
			return <ConceptEditor url={this.props.url} quizId={this.state.data.quizId} title ={this.state.data.title} rdfId={this.state.data.rdfId}/>
		},
		"editConcept2": function(){
			return (
				<QuizBrowser2  goal={"editConcept2"} url={this.props.url} handleBrowseClick={this.getBrowseClickHandler} handleConceptClick={this.getConceptClickHandler}/>
				);
		},
		
	},
	getSidebarClassName: function(id){
		var active = "";
		if(id === this.state.currentId)
			active = " active";

		return "list-group-item" + active;
	},
	handleSidebarItemClick: function(e){
		if(e.target.id === this.state.currentId)
			return false;
		
		this.setState({
			"currentId" : e.target.id,
			"data" : {}
		},function(){
			window.location.hash = this.state.currentId;
		});
		
		//triggerRender[e.target.id].bind(this)();
	},
	getEditClickHandler: function(){
		var parent = this;
		return function(quizId){
			parent.setState({
				"currentId" : "editQuiz",
				"data" : {
					"quizId" : quizId
				}
			});
		}
	},
	getCloneClickHandler: function(){
		var parent = this;
		return function(quizId){
			parent.setState({
				"currentId" : "cloneQuiz",
				"data" : {
					"quizId" : quizId
				}
			});
		}
	},
	getBrowseClickHandler: function(){
		var parent = this;
		return function(quizId){
			parent.setState({
				"currentId" : "browseSingleQuiz",
				"data" : {
					"quizId" : quizId
				}
			});
		}
	},
	getTopicEditClickHandler: function(){
		var parent = this;
		return function(topicId){
			parent.setState({
				"currentId" : "editTopic",
				"data" : {
					"topicId" : topicId
				}
			});
		}
	},
	getConceptClickHandler: function(){
		var parent = this;
		return function(quizId,title,rdfId){
			parent.setState({
				"currentId" : "editConcept",
				"data" : {
					"quizId" : quizId,
					"title":title,
					"rdfId":rdfId
				}
			});
		}
	},
	getInitialState: function(){
		var self = this;
		jQuery(document).ready(function($) {

		  if (window.history && window.history.pushState) {

		    $(window).on('popstate', function() {
		      var hashLocation = location.hash;
		      var hashSplit = hashLocation.split("#!/");
		      var hashName = hashSplit[1];

		      if (hashName !== '') {
		        var hash = window.location.hash;
		       
				self.setState({
					"currentId" : hash.substring(1),
					
				});
		      }
		    });

		    //window.history.pushState('forward', null, './#forward');
		  }

		});
		
		return {
			"currentId": "myTopic",
			"data": {}
		}
	},
	render: function(){
		var component = this;
		return (
			<div className="row">
	            {/*<!-- Sidebar -->*/}
	            <div id="sidebar" className="col-sm-3 col-md-2 sidebar">
	            	<ul id="quizGroup" className="list-group">
					    <li className="list-group-item"><h4>Topics</h4></li>
					    {topicGroupOptions.map(mapItems.bind(component))}
					</ul>
	            	<ul id="quizGroup" className="list-group">
					    <li className="list-group-item"><h4>Questions</h4></li>
					    {quizGroupOptions.map(mapItems.bind(component))}
					</ul>
	            </div>

	            {/*<!-- Main Area -->*/}
	            <div id="content" className="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
	            	{this.mainAreaContentMap[this.state.currentId].bind(this)()}
	            </div>
	        </div>
			);
	}
});

