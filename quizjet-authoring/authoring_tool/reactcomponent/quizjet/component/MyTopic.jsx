'use strict';

var React = require('react');
var TopicEditor = require('./TopicEditor');
var $ = require('jquery');

var TopicRow = React.createClass({
	displayName: 'TopicRow',
	render: function(){
		return (
			<li className="list-group-item">
				<div className="row">
					<div className="col-sm-6">
						<div className="row">
							<label className="col-sm-3">Title:</label>
							<div className="col-sm-9">{this.props.data.title}</div>
						</div>
						<div className="row">
							<label className="col-sm-3">Description:</label>
							<div className="col-sm-9">{this.props.data.decp}</div>
						</div>
					</div>
					<div className="col-sm-6">
						<button className="btn btn-default pull-right" 
						 onClick={this.props.handleEditClick().bind(this, this.props.data.topicId)} >Edit</button>
					</div>
				</div>
			</li>
			);
	}
});

module.exports = React.createClass({
	displayName: 'MyTopic',
	handleChange: function(e){
		var keyword = e.target.value.trim();
		if(keyword.length < 1)
			return false;

		$.ajax({
			url: this.props.url.SEARCH_QUIZ + keyword,
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
	componentDidMount: function(){
		$.ajax({
			url: this.props.url.MY_TOPIC,
			dataType: 'json',
			cache: true,
			success: function(data){
				console.log(data);
				this.setState({"resultSet": data});
			}.bind(this),
			error: function(xhr, status, err) {
				console.error(this.props.url.MY_TOPIC, status, err.toString());
			}.bind(this)
		});
	},
	getInitialState: function(){
		return {
			"resultSet" : []
		}
	},
	render: function(){
		var self = this;
		return (
			<div>
				<h1 className="page-header">My Topics</h1>
				<ol className="list-group">
					{self.state.resultSet.map(function(result){
						return <TopicRow key={result.topicId} 
						handleEditClick={self.props.handleEditClick} data={result} />;
					})}
				</ol>
			</div>
			);
	}
});