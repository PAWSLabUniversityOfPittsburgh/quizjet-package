'use strict';

var React = require('react');
var $ = require('jquery');
var URL = require('../json/ApiUrl');

module.exports = React.createClass({
	displayName: 'LoginForm',
	handleSimpleValueChange: function(name, event){
		var formState = this.state;
		formState[name] = event.target.value;
		this.setState(formState);
	},
	handleLogin: function(e){
		e.preventDefault();

		if(this.state.username.trim().length < 1 || this.state.password.trim().length < 1){
			alert('Please enter valid username and password.');
			return false;
		}

		$.ajax({
			url: URL.LOGIN,
			method: "POST",
			data: this.state,
			success: function(data){
				//call the delegator
				console.log(data);
				this.props.handleSuccess();
			}.bind(this),
			error: function(xhr, status, err) {
				alert('Wrong credentials.');
			}.bind(this)
		});
	},
	getInitialState: function(){
		return {
			'username': "",
			"password": ""
		}
	},
	render: function(){
		return (
			<form className="form-signin">
				<h2>Please log in</h2>
				<label htmlFor="username" className="sr-only">Username</label>
				<input type="input" id="username" className="form-control" onChange={this.handleSimpleValueChange.bind(this, 'username')}
					placeholder="Username" value={this.state.username} required autofocus />
		        <label htmlFor="inputPassword" className="sr-only">Password</label>
		        <input type="password" id="inputPassword" className="form-control" onChange={this.handleSimpleValueChange.bind(this, 'password')}
		        	placeholder="Password" value={this.state.password} required />
		        <br/>
		        <button className="btn btn-la btn-primary btn-block" 
		        onClick={this.handleLogin} type="submit">Log in</button>
			</form>
			);
	}
});