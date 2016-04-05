'use strict';

var React = require('react');
var ReactDOM = require('reactDom');

var Dashboard = require('./component/Dashboard');
var URL = require('./json/ApiUrl');

ReactDOM.render(<Dashboard url={URL} />, document.getElementById('app'));