'use strict';

var React = require('react');
var $ = require('jquery');
var Notifier = require('./form/Notifier');
var brace  = require('brace');
var AceEditor  = require('react-ace');

require('brace/mode/python');
require('brace/theme/github');

var Concept = React.createClass({
	displayName: 'Concept',
	render: function(){
		return (
			<div className="form-horizontal">
				<div className="form-group">
					<div className="col-sm-4">Concept Name:</div>
					<div className="col-sm-8">
						<input onChange={this.props.handleSimpleValueChange().bind(this, "name")}
						 className="form-control" value={this.props.data.name}/>
					</div>
				</div>
				<div className="form-group">
					<div className="col-sm-4">Concept Direction:</div>
					<div className="col-sm-8">
						<input onChange={this.props.handleSimpleValueChange().bind(this, "direction")} 
						 className="form-control" value={this.props.data.direction}/>
					</div>
				</div>
				<div className="form-group">
					<div className="col-sm-2">Start Line:</div>
					<div className="col-sm-2">
						<input onChange={this.props.handleSimpleValueChange().bind(this, "startLine")} 
						 className="form-control" value={this.props.data.startLine}/>
					</div>
					<div className="col-sm-2">End Line:</div>
					<div className="col-sm-2">
						<input onChange={this.props.handleSimpleValueChange().bind(this, "endLine")} 
						 className="form-control" value={this.props.data.endLine}/>
					</div>
					<div className="col-sm-2">Weight:</div>
					<div className="col-sm-2">
						<input onChange={this.props.handleSimpleValueChange().bind(this, "weight")} 
						 className="form-control" value={this.props.data.weight}/>
					</div>
				</div>
				<div className="form-group">
					<div className="col-sm-12">
						<button onClick={this.props.handleRemoveConcept().bind(this)} className="btn btn-danger btn-sm">
							<span className="glyphicon glyphicon-remove-circle" aria-hidden="true"></span> Delete Concept
						</button>
					</div>
				</div>
				<hr/>
			</div>
			);
	}
});

var ConceptPane = React.createClass({
	displayName: 'ConceptPane',
	componentDidMount: function(){
	},
	render: function(){
		return (
			<div className="panel panel-default">
				<div className="panel-heading">{this.props.classFileName}</div>
				<div className="panel-body">
				    <div className="row">
				    	<div className="col-sm-5">
				    		<AceEditor mode="python" theme="github" readOnly={true} width="100%"
							name={this.props.classFileName + "-code"} value={this.props.data[0]} />
				    	</div>
				    	<div className="col-sm-7">
				    		{this.props.data[1].map(function(concept, i){
				    			return (
				    				<Concept key={this.props.classFileName + "-concept-" + i} index={i} data={concept} 
				    				handleRemoveConcept={this.props.handleRemoveConcept} handleSimpleValueChange={this.props.handleSimpleValueChange}/>
				    			);
				    		}.bind(this))}
				    		<div className="row">
					    		<div className="col-sm-12">
					    			<button className="btn btn-success pull-right" onClick={this.props.handleNewConcept().bind(this)}>
						    			<span className="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> Add New Concept
					    			</button>
					    		</div>
					    	</div>
				    	</div>
				    </div>
				</div>
			</div>
			);
	}
});

module.exports = React.createClass({
	displayName: 'ConceptEditor',
	componentDidMount: function(){
		this.loadConceptsFromServer();
	},
	componentWillReceiveProps: function(props){
		this.loadConceptsFromServer();
	},
	getInitialState: function(){
		return {
			"isSubmitting": false,
			"notifications": [],
			"concepts": {}
		}
	},
	handleSubmit: function(event){
		event.preventDefault();
		this.setState({"isSubmitting": true});
		this.updateConcepts();

		this.setState({
			"notifications": [{
					"key": 0,
					"type": "info",
					"text": "Submitting the changes. Please DO NOT CLOSE the window."
				}]
		});
	},
	updateConcepts: function(){
		var concepts = this.state.concepts;
		var newConcepts = [];
		for(var classFile in concepts){
			var conceptList = concepts[classFile][1];
			conceptList.forEach(function(concept){
				newConcepts.push(concept);
			});
		}
		//console.log(newConcepts, this.props.quizId);
		$.ajax({
			url: this.props.url.UPDATE_CONCEPTS + this.props.quizId,
			method: "PUT",
			dataType: 'json',
			contentType: "application/json; charset=utf-8",
			data: JSON.stringify(newConcepts),
			success: function(data){
				if(data.success){
					this.setState({
						"isSubmitting": false,
						"notifications": [{
							"key": 0,
							"type": "success",
							"text": data.message
						}]
					});
				}else{
					this.setState({
						"isSubmitting": false,
						"notifications": [{
							"key": 0,
							"type": "danger",
							"text": "Update failed. " + data.message
						}]
					});
					return false;
				}
			}.bind(this),
			error: function(xhr, status, err) {
				console.error(this.props.url.UPDATE_CONCEPTS, status, err.toString());
			}.bind(this)
		});
	},
	handleSimpleValueChange: function(){
		var self = this;
		return function(name, event){
			event.preventDefault();
			//console.log(this.props, name, event.target.value);
			var classFile = this.props.data.classFile;
			var index = this.props.index;
			var state = self.state;
			var concept = state.concepts[classFile][1][index];
			concept[name] = event.target.value;
			self.setState(state);
		}
	},
	handleNewConcept: function(e){
		var self = this;
		return function(e){
			e.preventDefault();
			var state = self.state;
			var conceptArray = state.concepts[this.props.classFileName][1];
			conceptArray.push({
				'children': null,
				'classFile': this.props.classFileName,
				'direction': 'outcome',
				'endLine': 1,
				'id': null,
				'name': "New Concept",
				'startLine': 1,
				'weight': "1.0"
			});
			self.setState(state);
		}
	},
	handleRemoveConcept: function(e){
		var self = this;
		return function(e){
			e.preventDefault();
			//console.log(self, this.props);
			var classFile = this.props.data.classFile;
			var index = this.props.index;
			var state = self.state;
			var conceptArray = state.concepts[classFile][1];
			conceptArray.splice(index, 1);
			self.setState(state);
		}
	},
	loadConceptsFromServer: function(){
		$.ajax({
			url: this.props.url.GET_CONCEPTS + this.props.quizId,
			dataType: 'json',
			cache: true,
			success: function(data){
				var state = this.state;
				state.concepts = data;
				this.setState(state);
			}.bind(this),
			error: function(xhr, status, err) {
				console.error(this.props.url.GET_CONCEPTS, status, err.toString());
			}.bind(this)
		});
	},
	getClassList: function(){
		var classes = Object.keys(this.state.concepts);
		var rs = [];
		rs.push("Tester.py");
		classes.forEach(function(className){
			if(className === "Tester.py")
				return false;
			rs.push(className);
		});
		return rs;
	},
	render: function(){
		return (
			<div>
				<h1 className="page-header">Edit Concepts</h1>
				<form className="form-horizontal">
					{Object.keys(this.state.concepts).map(function(classFileName){
						return <ConceptPane key={classFileName + "-pane"} 
						classFileName={classFileName} data={this.state.concepts[classFileName]} 
						handleNewConcept={this.handleNewConcept} handleRemoveConcept={this.handleRemoveConcept} 
						handleSimpleValueChange={this.handleSimpleValueChange}/>
					}.bind(this))}
					{/* Notifier*/}
					<Notifier data={this.state.notifications} offset={0}/>
					<div className="form-group">
						<div className="col-sm-12">
							<button type="submit" disabled={this.state.isSubmitting} 
							className="btn btn-primary" onClick={this.handleSubmit}>Submit</button>
						</div>
					</div>
				</form>
			</div>
			);
	}
});