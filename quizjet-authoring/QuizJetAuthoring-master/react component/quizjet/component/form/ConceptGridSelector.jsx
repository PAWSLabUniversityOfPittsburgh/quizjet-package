'use strict';

var React = require('react');
var $ = require('jquery');

module.exports = React.createClass({
	displayName: 'ConceptSelector',
	render: function(){
		var self = this;
		return (

<div className="container">
  <div className="row">
    <div className="col-sm-3">
			{this.props.data.map(function(option,i){
				if (i%3===0)
				return    <label className="checkbox"><input type="checkbox" onChange={self.props.onCheckChange} value={option.value} id="inlineCheckbox1"/> {option.text}</label>
				
			})}
    </div>
    <div className="col-sm-3">
			{this.props.data.map(function(option,i){
				if (i%3===1)
				return    <label className="checkbox"><input type="checkbox" onChange={self.props.onCheckChange} value={option.value} id="inlineCheckbox1"/> {option.text}</label>
				
			})}
    </div>
    <div className="col-sm-3">
			{this.props.data.map(function(option,i){
				if (i%3===2)
				return    <label className="checkbox"><input type="checkbox" onChange={self.props.onCheckChange} value={option.value} id="inlineCheckbox1"/> {option.text}</label>
				
			})}
    </div>
  </div>
</div>		
       
    
			);
	}
});