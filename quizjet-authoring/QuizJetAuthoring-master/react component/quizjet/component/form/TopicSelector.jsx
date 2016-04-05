'use strict';

var React = require('react');
var $ = require('jquery');

module.exports = React.createClass({
	displayName: 'TopicSelector',
	render: function(){
		return (
			<div className="form-group">
				<label htmlFor="topicselector" className="col-sm-2 control-label">Topic</label>
				<div className="col-sm-4">
					<select value={this.props.value} onChange={this.props.handleSelect} id="topicselector" className="form-control" >
						{this.props.data.map(function(option){
							return <option key={option.key} value={option.value}>{option.text}</option>
						})}
					</select>
				</div>
			</div>
			);
	}
});