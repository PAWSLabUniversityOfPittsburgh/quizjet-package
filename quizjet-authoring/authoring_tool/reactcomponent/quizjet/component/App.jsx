'use strict';

var React = require('react');
var $ = require('jquery');
var URL = require('../json/ApiUrl');

var LoginForm = require('./LoginForm');
var Dashboard = require('./Dashboard');

function getAuthenticationStatus(callback){
	$.ajax({
		url: URL.GET_USER_INFO,
		method: "GET",
		success: function(data){
			console.log(data);
			//callback(data);
		},
		error: function(xhr, status, err) {
			callback(xhr);
		}
	});
}

function getStatusComponent(){
	if(this.state.authenticated){
		/*<li><a href="#" onClick={this.logOut}>Please log in</a></li>*/
		return <li><a href="#" onClick={this.handleLogout}>Log out</a></li>;
	}else{
		return <li><a href="#">Please log in</a></li>
	}
}

function getDashboard(){
	if(this.state.authenticated){
		/*<li><a href="#" onClick={this.logOut}>Please log in</a></li>*/
		return <Dashboard url={URL} />;
	}else{
		return <LoginForm handleSuccess={this.handleSuccess} />
	}
}

module.exports = React.createClass({
	displayName: 'App',
	getInitialState: function(){
		return {
			'authenticated' : false,
			'userInfo' : {}
		}
	},
	handleSuccess: function(){
		this.setState({'authenticated' : true});
	},
	handleLogout: function(e){
		e.preventDefault();

	},
	componentDidMount: function(){
		getAuthenticationStatus(function(data){
			if(data['status'] === undefined){
				this.setState({'authenticated' : true});
			}else{
				this.setState({'authenticated' : false});
			}
		}.bind(this));
	},
	render: function(){
		return (
			<div>
				<nav className="navbar navbar-inverse navbar-fixed-top">
			        <div className="container-fluid">
			            <div className="navbar-header">
			                <button type="button" className="navbar-toggle collapsed" 
			                data-toggle="collapse" data-target="#navbar" aria-expanded="false" 
			                aria-controls="navbar">
			                    <span className="sr-only">Toggle navigation</span>
			                    <span className="icon-bar"></span>
			                    <span className="icon-bar"></span>
			                    <span className="icon-bar"></span>
			                </button>
			                <a className="navbar-brand" href="#">QuizPet</a>
			            </div>
			            <div id="navbar" className="navbar-collapse collapse">
			                <ul className="nav navbar-nav navbar-right">
			                	{getStatusComponent.bind(this)()}
			                </ul>
			            </div>
			        </div>
			    </nav>
			    <div id="container" className="container-fluid">
			    	{getDashboard.bind(this)()}
			    </div>
		    </div>
			);
	}
});