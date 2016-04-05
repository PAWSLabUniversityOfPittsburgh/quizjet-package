'use strict';

var React = require('react');
var $ = require('jquery');
var Notifier = require('./form/Notifier');
var brace  = require('brace');
var AceEditor  = require('react-ace');
var ConceptSelector = require('./form/ConceptGridSelector');

require('brace/mode/python');
require('brace/theme/github');


var Concept = React.createClass({
	displayName: 'Concept',
	render: function(){
		return (
			<div className="form-horizontal">
				<div className="form-group">
					<div className="col-sm-8"><span className="text-primary">{this.props.index+1}:{this.props.data.name}</span></div>
				<div className="col-sm-2">
<img onClick={this.props.handleEditConcept().bind(this)}  src="resources/images/edit.png" data-pin-nopin="true"/>
			</div>
			
				<div className="col-sm-1">

			<img onClick={this.props.handleRemoveConcept().bind(this)}  src="resources/images/delete.png" data-pin-nopin="true"/>

			
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
				    				handleRemoveConcept={this.props.handleRemoveConcept} handleEditConcept={this.props.handleEditConcept} handleSimpleValueChange={this.props.handleSimpleValueChange}/>
				    			);
				    		}.bind(this))}
				    		<div className="row">
					    		<div className="col-sm-12">
					    			<button className="btn btn-success pull-right" onClick={this.props.handleNewConcept().bind(this,this.props.classFileName,this.props.quizId,this.props.title)}>
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
		this.loadOntologyFromServer();
	},
	componentWillReceiveProps: function(props){
		this.loadConceptsFromServer();
	},
	getInitialState: function(){
		return {
			"isSubmitting": false,
			"notifications": [],
			"conceptstoadd": [],
			"concepts": {},
			"conceptdelete":0,
			"conceptindexdelete":0,
			"conceptontologyadd":"",
			"conceptfiledelete":"",
			"line":"",
			"lineedit":"",
			"ontology":[
				{
								"key":0,
								"value":"default",
								"text":"Please choose a concept"
							}]
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
	handleSimpleValueChange: function(name, event){
		var self = this;
		
			event.preventDefault();
			//console.log(this.props, name, event.target.value);
			var state = self.state;
		
				state.line = event.target.value;
				
				self.setState(state);

		
	},
	handleSimpleValueChangeline: function(name, event){
		var self = this;
		
			event.preventDefault();
			//console.log(this.props, name, event.target.value);
			var state = self.state;
		
				state.lineedit = event.target.value;
				
				self.setState(state);

		
	},
	handleOntologySelect: function(event){
		var self = this;
		// return function(name, event){
			event.preventDefault();
			console.log(this.props, event.target.value,this.props.quizId);
			// var classFile = this.props.data.classFile;
			// var index = this.props.index;
			 var state = self.state;
			 state.conceptontologyadd = event.target.value;
			// concept[name] = event.target.value;
			//alert(event.target.value);
			 self.setState(state);
			//}
	},
	
	
	
	
	addConcept: function(){
		var self = this;
	
	 var state = self.state;
	 if(state.line.match(/^[0-9]+(\-)+[0-9]+$/) === null){
		 alert("format not correct");
		 return null;
	 } 
			var concetp = {
							"name": self.state.conceptontologyadd,
							"classFile":  self.state.concepaddfile,
							"direction": 'outcome',
							"startLine":self.state.line.split("-")[0],
							"weight": 1.0,
							"title":self.state.conceptaddtitle ,
						"endLine": self.state.line.split("-")[1]
						};
						
						$.ajax({
							url: this.props.url.ADD_CONCEPTS+ this.props.rdfId,
							method: "PUT",
							dataType: 'json',
							contentType: "application/json; charset=utf-8",
							data: JSON.stringify(concetp),
							success: function(data){
								if(data.success){
									var state = self.state;
									var conceptArray = state.concepts[self.state.concepaddfile][1];
									conceptArray.push({
										'children': null,
										'classFile': self.state.concepaddfile,
										'direction': 'outcome',
										'endLine': self.state.line.split("-")[1],
										'id': data.content[0],
										'name':self.state.conceptontologyadd,
										'startLine': self.state.line.split("-")[0],
										'weight': "1.0"
									});
									self.setState(state);
								$('#myModal').modal('hide');		
								}
				
							}.bind(this),
							error: function(xhr, status, err) {
								console.error(this.props.url.ADD_CONCEPTS, status, err.toString());
							}.bind(this)
						});
			


			
	
	},
	
	addConceptMultiple:function(){
		var self = this;
		
		var cs =this.state.conceptstoadd;
		var filestr = this.state.concepaddfile;
		var con = filestr+":"+cs.join(':');
		$.ajax({
			url: this.props.url.ADD_CONCEPTS_MUL+ this.props.rdfId,
			method: "PUT",
			dataType: 'json',
			contentType: "application/json; charset=utf-8",
			data: con,
			success: function(data){
				if(data.success){
					var state = self.state;
					var conceptArray = state.concepts[self.state.concepaddfile][1];
					for (var item in data.content[0]){
						conceptArray.push({
							'children': null,
							'classFile': self.state.concepaddfile,
							'direction': 'outcome',
							'endLine': 0,
							'id': data.content[0][item],
							'name':item,
							'startLine': 0,
							'weight': "1.0"
						});
					}
					 self.setState(state);
				$('#myModal').modal('hide');		
				}

			}.bind(this),
			error: function(xhr, status, err) {
				console.error(this.props.url.ADD_CONCEPTS_MUL, status, err.toString());
			}.bind(this)
		});
		
		
	},
	
	
	handleNewConcept: function(){
		
		var self = this;
		
		return function(c,q,t,event){
			
			event.preventDefault();
			this.setState({"conceptstoadd":[]});
			$('#myModal').modal('show');	
			//console.log(self, this.props);
			// var classFile = this.props.data.classFile;
			// var index = this.props.index;
			 var state = self.state;
		 // 			//
		 // 			// var conceptArray = state.concepts[classFile][1];
		 state.concepaddfile = c;
		 state.conceptaddquizid = q;
		 state.conceptaddtitle = t;
			//
			 self.setState(state);
	 		React.unmountComponentAtNode(document.getElementById('conceptsel'));
	 		ReactDOM.render(<ConceptSelector data={self.state.ontology} handleOntologySelect={self.handleOntologySelect.bind(self)} onCheckChange={self.onCheckChange} />, document.getElementById("conceptsel"));
		}
		
		
		
			
	
	},
	loadOntologyFromServer: function() {
		$.ajax({
			url: this.props.url.GET_ALL_ONTOLOGY,
			dataType: 'json',
			cache: true,
			success: function(data){
				var ontology = [];
				var i = 1;
				for(var d in data){
					ontology.push({
						"key": d,
						"value": data[d],
						"text": data[d]
					});
					i++;
				}
				ontology.unshift({"key": 0, "value": " ", "text": "Please Select One Concept"});
				this.setState({"ontology": ontology});
			}.bind(this),
			error: function(xhr, status, err) {
				console.error(this.props.url.GET_ALL_ONTOLOGY, status, err.toString());
			}.bind(this)
		});
	},
	handleRemoveConcept: function(e){
		var self = this;
		return function(e){
			e.preventDefault();
			
			$('#myModal3').modal('show');	
			//console.log(self, this.props);
			var classFile = this.props.data.classFile;
			var index = this.props.index;
			var state = self.state;
			
			var conceptArray = state.concepts[classFile][1];
			state.conceptdelete = conceptArray[index].id;
			state.conceptindexdelete = index
			state.conceptfiledelete = classFile

			self.setState(state);
		}
	},
	
	handleEditConcept: function(e){
		var self = this;
		return function(e){
			e.preventDefault();
		
			$('#myModalmyModal').modal('show');	
			//console.log(self, this.props);
			var classFile = this.props.data.classFile;
			var index = this.props.index;
			var state = self.state;
			
			var conceptArray = state.concepts[classFile][1];
			state.conceptedit = conceptArray[index].id;
			state.conceptindexedit = index
			state.conceptfileedit = classFile
			state.lineedit = conceptArray[index].startLine+'-'+conceptArray[index].endLine

			self.setState(state);
		}
	},
	
	editConcept:function(q,i,c){
		var self = this;
   	 var state = self.state;
   	 if(state.lineedit.match(/^[0-9]+(\-)+[0-9]+$/) === null){
   		 alert("format not correct");
   		 return null;
   	 } 
		$.ajax({
			url: this.props.url.EDIT_CONCEPTS_LINE + q,
			method: "PUT",
			dataType: 'json',
			data: self.state.lineedit,
			success: function(data){
				var state = self.state;
				var conceptArray = state.concepts[c][1];
				conceptArray[i].startLine = self.state.lineedit.split('-')[0];
				conceptArray[i].endLine = self.state.lineedit.split('-')[1];
				self.setState(state);
				$('#myModalmyModal').modal('hide');	
				
				
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
							"text": data.message
						}]
					});
					return false;
				}
			}.bind(this),
			error: function(xhr, status, err) {
				$('#myModalmyModal').modal('hide');	
				console.error(this.props.url.EDIT_CONCEPTS_LINE, status, err.toString());
			}.bind(this)
		});		
		
	},
	
	deleteConcept:function(q,i,c){
		var self = this;
		
		$.ajax({
			url: this.props.url.DELETE_CONCEPTS + q,
			method: "PUT",
			dataType: 'json',
			success: function(data){
				var state = self.state;
				var conceptArray = state.concepts[c][1];
				conceptArray.splice(i, 1);
				self.setState(state);
				$('#myModal3').modal('hide');	
				
				
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
							"text": data.message
						}]
					});
					return false;
				}
			}.bind(this),
			error: function(xhr, status, err) {
				$('#myModal3').modal('hide');	
				console.error(this.props.url.DELETE_CONCEPTS, status, err.toString());
			}.bind(this)
		});		
		
	},
	reparseConcept:function(q,i){
		var _ = this;
		$.ajax({
			url: _.props.url.DELETE_QUIZ_CONCEPTS + i,
			method: "PUT",
			success: function(data){
				if(data.success){
					var newState = this.state;
					newState.notifications.text = "Deleting the current concepts. Please DO NOT CLOSE the window.";
					this.setState(newState);
					this.reparseNewConcept(i);
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
				this.setState({
					"isSubmitting": false,
					"notifications": [{
						"key": 0,
						"type": "danger",
						"text": "Error occured." 
					}]
				});
				console.error(this.props.url.DELETE_QUIZ_CONCEPTS, status, err.toString());
			}.bind(_)
		});
		
		
	},
	reparseNewConcept:function(rdfid){
		var _ = this;
		$.ajax({
			url: _.props.url.UPDATE_QUIZ_CONCEPTS + rdfid,
			method: "GET",
			success: function(data){
				if(data.success){
					var newState = this.state;
					newState.notifications.text = "Repasing concepts. Please DO NOT CLOSE the window.";
					this.setState(newState);
					$('#myModal4').modal('hide');
					this.loadConceptsFromServer();
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
				this.setState({
					"isSubmitting": false,
					"notifications": [{
						"key": 0,
						"type": "danger",
						"text": "Error occured. "
					}]
				});
				console.error(this.props.url.UPDATE_QUIZ_CONCEPTS, status, err.toString());
			}.bind(_)
		});
		
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
				this.setState({
					"notifications": [{
							"key": 0,
							"type": "info",
							"text": "Load concepts finished."
						}]
				});
				
			}.bind(this),
			error: function(xhr, status, err) {
				console.error(this.props.url.GET_CONCEPTS, status, err.toString());
			}.bind(this)
		});
	},
	onCheckChange:function(event){
		console.error(event.target.value);
		console.error(event.target.checked);
		var state = this.state;
		var ca = state.conceptstoadd;
		if(event.target.checked){
			ca.push(event.target.value);
		}
		else {
			for(var i = ca.length - 1; i >= 0; i--) {
			    if(ca[i] === event.target.value) {
			       ca.splice(i, 1);
			    }
			}
		}
		this.setState({"conceptstoadd":ca});
		//this.setState({"conceptstoadd":[]});
		// var conceptArray = state.concepts[this.state.concepaddfile][1];
		// conceptArray.push({
		// 	'children': null,
		// 	'classFile': this.state.concepaddfile,
		// 	'direction': 'outcome',
		// 	'endLine': 0,
		// 	'id': -1,
		// 	'name':event.target.value,
		// 	'startLine': 0,
		// 	'weight': "1.0"
		// });
		//this.setState(state);
		
	},
	reparse:function(){
		$('#myModal4').modal('show');
	},
	closeModal:function(){
		$('#myModal3').modal('hide');
	},
	closeModal3:function(){
		$('#myModalmyModal').modal('hide');
	},
	closeModal4:function(){
		$('#myModal4').modal('hide');
	},
	closeModal2:function(){
		$('#myModal').modal('hide');
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
		var imgStyle ={
			width:'30px',
			height:'30px'
		};
		var modalStyle = {
			width: '95%'
		};
		return (
			<div>
<div className="modal fade" id="myModal3" tabindex="-1" role="dialog" aria-labelledby="myModalLabel2" aria-hidden="true">
  <div className="modal-dialog modal-sm">
    <div className="modal-content">
      <div className="modal-header">
        <button type="button" className="close" data-dismiss="modal"><span aria-hidden="true">X</span><span className="sr-only">Close</span></button>
        <h4 className="modal-title" id="myModalLabel2">Delete Concept</h4>
      </div>
      <div className="modal-body">
			Are you sure that you will delete this concept?
      </div>
      <div className="modal-footer">
<button type="button" className="btn btn-default" onClick={this.deleteConcept.bind(this, this.state.conceptdelete,this.state.conceptindexdelete,this.state.conceptfiledelete)}>Ok</button><button type="button" className="btn btn-default" onClick ={this.closeModal}>Cancel</button>
				
      </div>
    </div>
  </div>
</div>    
		
  

			
				
		

<div className="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div className="modal-dialog modal-lg" >
    <div className="modal-content">
      <div className="modal-header">
        <button type="button" className="close" data-dismiss="modal"><span aria-hidden="true">X</span><span className="sr-only">Close</span></button>
        <h4 className="modal-title" id="myModalLabel">Add Concept</h4>
      </div>
      <div className="modal-body">
		<div className="form-horizontal">
			<div className="form-group">
	    		<div className="col-sm-12">
			
		{/* topic selector*/}
		
		<div id ="conceptsel"></div>
				</div>
    		</div>
		{/* topic selector*
<div className="form-group">
					<label className="col-sm-3 control-label"  id = "x" name = "">Start-end lines:</label><div className="col-sm-9">
					
					    	<input type="input" className="form-control" id="line" placeholder=""  value={this.state.line}
					    	 onChange={this.handleSimpleValueChange.bind(this, "line")}/>
					<span className="help-block">start-end lines example: 1-3</span></div></div>
			
*/}

	    	<input type="hidden" value="" />	      

	  		<input type="hidden" id='questionClassCount' value=''/>	      
	    	<input type="hidden" id='question' value=''/>
		</div>
		 </div>
      <div className="modal-footer">
			<button type="button" className="btn btn-default" onClick={this.addConceptMultiple.bind(this)}>Ok</button>
   	{/*     <button type="button" className="btn btn-default" onClick={this.addConcept.bind(this)}>Ok</button>*/}
		<button type="button" className="btn btn-default" onClick ={this.closeModal2}>Cancel</button>
      </div>
    </div>
  </div>
</div>   
			
			
			
					
<div id="myModalmyModal" className="modal fade" role="dialog">
  <div div className="modal-dialog modal-lg" >

    
    <div className="modal-content">
      <div className="modal-header">
        <button type="button" className="close" data-dismiss="modal">X</button>
        <h4 className="modal-title">Edit line</h4>
      </div>
      <div className="modal-body"><div className="form-horizontal">
<div className="form-group">
					<label className="col-sm-3 control-label"  id = "x" name = "">Start-end lines:</label><div className="col-sm-9">
					
					    	<input type="input" className="form-control" id="lineedit" placeholder=""  value={this.state.lineedit}
					    	 onChange={this.handleSimpleValueChangeline.bind(this, "lineedit")}/>
					<span className="help-block">start-end lines example: 1-3</span></div></div>
      </div> </div>
      <div className="modal-footer">
<button type="button" className="btn btn-default" onClick={this.editConcept.bind(this, this.state.conceptedit,this.state.conceptindexedit,this.state.conceptfileedit)}>Ok</button><button type="button" className="btn btn-default" onClick ={this.closeModal3}>Cancel</button>
				
 
      </div>
    </div>

  </div>
</div>
							
							
<div className="modal fade" id="myModal4" tabindex="-1" role="dialog" aria-labelledby="myModalLabel4" aria-hidden="true">
  <div className="modal-dialog modal-sm">
    <div className="modal-content">
      <div className="modal-header">
        <button type="button" className="close" data-dismiss="modal"><span aria-hidden="true">X</span><span className="sr-only">Close</span></button>
        <h4 className="modal-title" id="myModalLabel4">Reparse</h4>
      </div>
      <div className="modal-body">
			Are you sure that you will reparse this question?
      </div>
      <div className="modal-footer">
<button type="button" className="btn btn-default" onClick={this.reparseConcept.bind(this, this.props.quizId,this.props.rdfId)}>Ok</button><button type="button" className="btn btn-default" onClick ={this.closeModal4}>Cancel</button>
				
      </div>
    </div>
  </div>
</div>    
		
			
			
			
				<h1 className="page-header">Edit Concepts</h1><div>
							<button className="btn btn-success pull-right" onClick={this.reparse}>
						    			<span className="glyphicon" aria-hidden="true">	<img style={imgStyle} src="resources/images/reset2.png" data-pin-nopin="true"/>
</span> Reparse
					    			</button><br/><br/><br/>
							</div>
				<form className="form-horizontal">
					{Object.keys(this.state.concepts).map(function(classFileName){
						return <ConceptPane key={classFileName + "-pane"} quizId = {this.props.quizId} rdfId = {this.props.rdfId} title = {this.props.title}
						classFileName={classFileName} data={this.state.concepts[classFileName]} 
						handleNewConcept={this.handleNewConcept} handleRemoveConcept={this.handleRemoveConcept} handleEditConcept={this.handleEditConcept} 
						handleSimpleValueChange={this.handleSimpleValueChange}/>
					}.bind(this))}
					{/* Notifier*/}
					<Notifier data={this.state.notifications} offset={0}/>
				
				</form>
			</div>
			);
	}
});