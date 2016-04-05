'use strict';

var React = require('react');
var $ = require('jquery');

module.exports = React.createClass({
	displayName: 'ConceptSelector',
	render: function(){
		return (
			<div className="form-group">
				
				<div className="col-sm-12">
					<select onChange={this.props.handleOntologySelect} id="conceptselector" className="form-control" >
						{this.props.data.map(function(option){
							return <option key={option.key} value={option.value}>{option.text}</option>
						})}
					</select>
				</div>
			</div>
			);
	}
});