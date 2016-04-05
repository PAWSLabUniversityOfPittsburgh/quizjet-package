'use strict';

var React = require('react');
var QuizEditor = require('./QuizEditor');
var $ = require('jquery');

var SearchResultRow = React.createClass({
	displayName: 'SearchResultRow',
	render: function(){
		return (
			<li className="list-group-item">
				<div className="row">
					<div className="col-sm-6">
						<div className="row">
							<label className="col-sm-2">Title:</label>
							<div className="col-sm-10">{this.props.data.title}</div>
						</div>
					</div>
					<div className="col-sm-6">
						<div className="row">
							<label className="col-sm-2">RdfId:</label>
							<div className="col-sm-10">{this.props.data.rdfId}</div>
						</div>
						<div className="row">
							<label className="col-sm-2">Version:</label>
							<div className="col-sm-10">{this.props.data.version}</div>
						</div>
					</div>
				</div>
				<div className="row">
					<div className="col-sm-12">
						<div className="btn-group" role="group">
							<button className="btn btn-default" 
							 onClick={this.props.handleCloneClick().bind(this, this.props.data.quizId)} >Clone Quiz</button>
							<button className="btn btn-default" 
							 onClick={this.props.handleEditClick().bind(this, this.props.data.quizId)} >Edit Quiz</button>
							<button className="btn btn-default" 
							 onClick={this.props.handleBrowseClick().bind(this, this.props.data.quizId)} >Browse Quiz</button>
						</div>
					</div>
				</div>
			</li>
			);
	}
});

module.exports = React.createClass({
	displayName: 'QuizSearcher',
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
	getInitialState: function(){
		return {
			"resultSet" : []
		}
	},
	render: function(){
		var self = this;
		return (
			<div>
				<h1 className="page-header">Search Quiz by Title</h1>
				<form>
					<div className="form-group">
					    <input type="input" className="form-control" 
					    id="searchBox" onChange={this.handleChange} placeholder="Enter quiz title here." />
					</div>
				</form>
				<ol className="list-group">
					{self.state.resultSet.map(function(result){
						return <SearchResultRow url={self.props.url} 
						handleEditClick={self.props.handleEditClick} 
						handleBrowseClick={self.props.handleBrowseClick}
						handleCloneClick={self.props.handleCloneClick}
						key={result.quizId} data={result} />;
					})}
				</ol>
			</div>
			);
	}
});