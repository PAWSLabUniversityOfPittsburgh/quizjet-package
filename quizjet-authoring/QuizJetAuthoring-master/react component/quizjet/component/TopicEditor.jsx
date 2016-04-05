'use strict';

var React = require('react');
var $ = require('jquery');
var Notifier = require('./form/Notifier');

var notNullParametersMap = {
	"title": "Title",
	"decp": "Description",
};

var pageHeaderMap = {
	"allNewTopic": "Create New Topic",
	"modifyTopic": "Edit Topic"
}

module.exports = React.createClass({
	displayName: 'TopicEditor',
	handleSimpleValueChange: function(name, event){
		var topicState = this.state.topic;
		topicState[name] = event.target.value;
		this.setState({"topic": topicState});
	},
	handlePrivacyChange: function(event){
		var topicState = this.state.topic;
		topicState.privacy = event.target.value === "private" ? true : false;
		this.setState({"topic": topicState});
	},
	handleSubmit: function(event){
		event.preventDefault();
		var topicState = this.state.topic;

		//check for validity
		//check not null
		var nullParameters = [];
		for(var key in notNullParametersMap){
			if(topicState[key].trim().length < 1){
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

		this.setState({"isSubmitting": true});
		if(this.props.goal === "allNewTopic")
			this.newTopic();
		else if(this.props.goal === "modifyTopic")
			this.updateTopic();

		this.setState({
			"notifications": [{
					"key": 0,
					"type": "info",
					"text": "Submitting the quiz. Please DO NOT CLOSE the window."
				}]
		});
	},
	newTopic: function(){
		$.ajax({
			url: this.props.url.NEW_TOPIC,
			method: "PUT",
			dataType: 'json',
			contentType: "application/json; charset=utf-8",
			data: JSON.stringify(this.state.topic),
			success: function(data){
				
				this.setState({"isSubmitting": false});
				if(data.success){
					this.setState({
						"notifications": [{
							"key": 0,
							"type": "success",
							"text": data.message
						}]
					});
				}else{
					this.setState({
						"notifications": [{
							"key": 0,
							"type": "danger",
							"text": data.message
						}]
					});
				}
			}.bind(this),
			error: function(xhr, status, err) {
				console.error(this.props.url.NEW_TOPIC, status, err.toString());
			}.bind(this)
		});
	},
	updateTopic: function(){
		$.ajax({
			url: this.props.url.UPDATE_TOPIC,
			method: "PUT",
			dataType: 'json',
			contentType: "application/json; charset=utf-8",
			data: JSON.stringify(this.state.topic),
			success: function(data){
				this.setState({"isSubmitting": false});
				if(data.success){
					this.setState({
						"notifications": [{
							"key": 0,
							"type": "success",
							"text": data.message
						}]
					});
				}else{
					this.setState({
						"notifications": [{
							"key": 0,
							"type": "danger",
							"text": data.message
						}]
					});
				}
			}.bind(this),
			error: function(xhr, status, err) {
				console.error(this.props.url.UPDATE_TOPIC, status, err.toString());
			}.bind(this)
		});
	},
	loadTopicFromServer: function(){
		$.ajax({
			url: this.props.url.GET_TOPIC_BY_ID + this.props.topicId,
			dataType: 'json',
			cache: true,
			success: function(data){
				var state = this.state;
				state.topic = {
					"topicId": data.topicId.toString(),
					"title": data.title,
					"decp": data.decp,
					"privacy": data.privacy
				};
				this.setState(state);
			}.bind(this),
			error: function(xhr, status, err) {
				console.error(this.props.url.GET_TOPIC_BY_ID, status, err.toString());
			}.bind(this)
		});
	},
	componentDidMount: function() {
		if(this.props.goal === "modifyTopic")
			this.loadTopicFromServer();
	},
	componentWillReceiveProps: function(props){
		if(props.goal === "allNewTopic"){
			var state = this.state;
			state.notifications = [];
			state.topic = {
				"topicId": "",
				"title": "",
				"decp": "",
				"privacy": true
			};
			this.setState(state);
		}else if(props.goal === "modifyTopic"){
			this.loadTopicFromServer();
		}
	},
	getInitialState: function(){
		return {
			"isSubmitting": false,
			"notifications": [],
			"topic": {
				"topicId": "",
				"title": "",
				"decp": "",
				"privacy": true
			}
		}
	},
	render: function(){
		return (
			<div>
				<h1 className="page-header">{pageHeaderMap[this.props.goal]}</h1>
				<form className="form-horizontal">
					{/* title */}
					<div className="form-group">
						<label htmlFor="title" className="col-sm-2 control-label">Title</label>
					    <div className="col-sm-4">
					    	<input type="input" className="form-control" id="title" placeholder="Title for this topic" 
					    	value={this.state.topic.title} onChange={this.handleSimpleValueChange.bind(this, "title")}/>
					    </div>
					</div>
					{/* Description */}
					<div className="form-group">
						<label htmlFor="description" className="col-sm-2 control-label">Description</label>
					    <div className="col-sm-6">
					    	<textarea rows="5" className="form-control" id="description" placeholder="Description for this topic" 
					    	value={this.state.topic.decp} onChange={this.handleSimpleValueChange.bind(this, "decp")}>
					    	</textarea>
					    </div>
					</div>
					{/* Privacy */}
					<div className="form-group">
						<label className="col-sm-2 control-label">Privacy</label>
					    <div className="col-sm-4">
							<label className="radio-inline">
								<input type="radio" name="privacy" id="privacyPrivate" value="private" 
								checked={this.state.topic.privacy} onChange={this.handlePrivacyChange} /> Private
							</label>
							<label className="radio-inline">
								<input type="radio" name="privacy" id="privacyPublic" value="public" 
								checked={!this.state.topic.privacy} onChange={this.handlePrivacyChange} /> Public
							</label>
						</div>
					</div>
					{/* Notifier*/}
					<Notifier data={this.state.notifications} />
					{/* Submit */}
					<div className="form-group">
						<div className="col-sm-offset-2 col-sm-10">
							<button type="submit" disabled={this.state.isSubmitting} 
							className="btn btn-primary" onClick={this.handleSubmit}>Submit</button>
						</div>
					</div>
				</form>
			</div>
			);
	}
});