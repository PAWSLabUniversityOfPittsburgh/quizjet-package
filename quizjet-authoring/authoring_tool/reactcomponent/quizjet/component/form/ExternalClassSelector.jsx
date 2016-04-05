 'use strict';

var React = require('react');
var $ = require('jquery');
var brace  = require('brace');
var AceEditor  = require('react-ace');

require('brace/mode/python');
require('brace/theme/github');

var ClassFileItem = React.createClass({
	displayName: 'ClassFileItem',
	handleClick: function(){
		var id = "#externalClassId-" + this.props.value;
		if(this.state.displayStatus === "hidden"){
			if(this.state.loadStatus === "unloaded"){
				//first load
				this.setState({
					"buttonText" : "Loading",
					"buttonDisabled" : true,
				});
				this.loadCode(id);
			}else{
				$(id).collapse("show");
				this.setState({
					"buttonText" : "Hide",
					"displayStatus" : "shown"
				});
			}
		}else{
			$(id).collapse("hide");
			this.setState({
				"buttonText" : "Show code",
				"displayStatus" : "hidden"
			});
		}
	},
	loadCode: function(id){
		$.ajax({
			url: this.props.url.GET_CLASS_BY_ID + this.props.value,
			method: "GET",
			dataType: 'json',
			success: function(data){
				if(data.success){
					$(id).collapse("show");
					this.setState({
						"loadStatus" : "loaded",
						"buttonText" : "Hide",
						"displayStatus" : "shown",
						"buttonDisabled" : false,
						"code": data.content[0]
					});
				}else{
					this.setState({
						"buttonText" : "Show code",
						"buttonDisabled" : false,
					});
				}
			}.bind(this),
			error: function(xhr, status, err) {
				console.error(this.props.url.NEW_QUIZ, status, err.toString());
				this.setState({
					"buttonText" : "Show code",
					"buttonDisabled" : false,
				});
			}.bind(this)
		});
	},
	getInitialState: function(){
		return {
			"loadStatus" : "unloaded",
			"buttonText" : "Show code",
			"buttonDisabled" : false,
			"displayStatus" : "hidden",
			"code": null
		};
	},
	render: function(){
		return (
			<li className="list-group-item">
			<div className="clearfix">
				<label className="checkbox-inline">
					<input type="checkbox" checked={this.props.data.checked}
					 value={this.props.value} onChange={this.props.onCheckChange} /> {this.props.data.className}
				</label>
				<button className="btn btn-info btn-sm pull-right" disabled={this.state.buttonDisabled} type="button" onClick={this.handleClick} >
					{this.state.buttonText}
				</button>
			</div>
			<div className="content collapse" id={"externalClassId-" + this.props.value} >
				<br/>
				<AceEditor mode="python" theme="github" readOnly={true}
				name={"externalClassId-code-" + this.props.value} width="100%" height="100px" value={this.state.code} />
			</div>
			</li>
			);
	}
});

var ClassFileUploader = React.createClass({
	displayName: 'ClassFileUploader',
	handleSubmit: function(e){
		e.stopPropagation();
		e.preventDefault();
		var file = this.state.file;

		//check if there is a file
		if(file === null){
			alert("Please select a file first.");
			return false;
		}

		//check file type
		if(file.name.match(/.java$/i) === null){
			alert("Invalid File Type.");
			return false;
		}

		//create form data
		var data = new FormData();
		data.append("file", this.state.file);
		//console.log(data.get("file"));
		$.ajax({
		    url: this.props.url.UPLOAD_CLASS_FILE,
		    type: 'POST',
		    data: data,
		    cache: false,
		    dataType: 'json',
		    processData: false,
		    contentType: false,
		    success: function(data){
		        if(data.success){
		        	alert("File uploaded.");
		        	this.props.onUploaded(data.content[0]);
		        }else{
		        	alert("Upload Failed. " + data.message);
		        }
		    }.bind(this),
		    error: function(xhr, status, err) {
				console.error(this.props.url.UPLOAD_CLASS_FILE, status, err.toString());
			}.bind(this)
		});
	},
	handleFileChange: function(e){
		this.setState({"file" : e.target.files[0]});
	},
	getInitialState: function(){
		return {
			"file" : null
		};
	},
	render: function(){
		return (
			<li className="list-group-item clearfix" key={"uploader"}>
				<input width="300px" type="file" name="file" onChange={this.handleFileChange} />
				<button onClick={this.handleSubmit} className="btn btn-default btn-sm pull-right">Upload</button>
			</li>
		);
	}
});

module.exports = React.createClass({
	displayName: 'ExternalClassSelector',
	onUploaded: function(newFile){
		var newState = this.state;
		for(var key in newFile){
			newState[key] = {
				"className" : newFile[key],
				"checked" : false
			}
		}
		this.setState(newState);
	},
	loadClassesFromServer: function(){
		$.ajax({
			url: this.props.url.GET_ALL_CLASSES,
			dataType: 'json',
			cache: true,
			success: function(data){
				var newState = {};
				for(var key in data){
					newState[key] = {
						"className" : data[key],
						"checked" : false
					}
				}
				this.setState(newState);
			}.bind(this),
			error: function(xhr, status, err) {
				console.error(this.props.url.GET_ALL_CLASSES, status, err.toString());
			}.bind(this)
		});
	},
	componentDidMount: function() {
		this.loadClassesFromServer();
	},
	componentWillReceiveProps: function(newProps){
		//console.log(newProps);
		var newState = this.state;
		for(var key in newState){
			newState[key].checked = false;
		}
		newProps.selectedClasses.forEach(function(curr){
			if(curr in newState)
				newState[curr].checked = true;
		});
		this.setState(newState);
	},
	getInitialState: function(){
		return {};
	},
	render: function(){
		var state = this.state;
		var props = this.props;
		return (
			<div className="col-sm-6">
				<div className="panel panel-default">
					<div className="panel-heading">
						Selected {props.selectedClasses.length} external classes.
						<a className="btn btn-default btn-xs pull-right" role="button" 
							data-toggle="collapse" href="#externalClassList" 
							aria-expanded="true" aria-controls="externalClassList">Toggle</a>
					</div>
					<ul id="externalClassList" className="list-group collapse">
						{Object.keys(state).map(function(key){
							return (
								<ClassFileItem onCheckChange={props.onCheckChange} 
								 value={key} key={key} data={state[key]} url={props.url}/>
								);
						})}
						<ClassFileUploader url={props.url} onUploaded={this.onUploaded} />
					</ul>
				</div>
			</div>
			);
	}
});