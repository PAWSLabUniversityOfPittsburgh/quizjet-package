'use strict';

var React = require('react');

module.exports = React.createClass({
	displayName: 'Notifier',
	render: function(){
		return (
			<div className="form-group">
				<div className={"col-sm-offset-" + (this.props.offset !== undefined ? this.props.offset : 2) + " col-sm-6"}>
					{this.props.data.map(function(row){
						return <div className={"alert alert-"+row.type} role="alert" key={row.key}>{row.text}</div>
					})}
				</div>
			</div>
			);
	}
});