var express = require('express');
var path = require('path');
//var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');
var compression = require('compression');
var fs = require('fs');
var colors = require('colors');
var nunjucks = require('nunjucks');

var app = express();


if (app.get('env') != 'development') {
    // In production use, Nginx or similar should be used as a proxy
    app.enable('trust proxy');
}

// Views
nunjucks.configure(path.join(__dirname, 'views'), {
    autoescape: true,
    express   : app
});

//app.set('views', path.join(__dirname, 'views'));
//app.set('view engine', 'jade');
//app.locals.pretty = true;
//app.use(favicon(__dirname + '/public/favicon.ico'));

if (process.env.NODE_ENV !== 'test') {
    app.use(logger('dev'));
}
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(compression());
app.use(express.static(path.join(__dirname, 'public')));

// ********************************************************************************
// ACOS code begins

var config = require('./config');

// Make sure that the directory for log files exist
config.logDirectory = config.logDirectory || 'logs';
if (config.logDirectory[0] != '/') {
    config.logDirectory = __dirname + '/' + config.logDirectory;
}
fs.mkdir(config.logDirectory, 0775, function(err) {});

// Register all installed packages
var handlers = {'protocols' : {}, 'contentTypes' : {}, 'contentPackages' : {}, 'tools' : {}, 'loggers' : {}};

for (var i = 0; i < config.installedPackages.length; i++) {
    try {
        var package = require(config.installedPackages[i]);
        package.register(handlers, app, config);
        // Serve static content that is inside the installed package
        app.use('/static/' +
            package.namespace, express.static(__dirname + '/node_modules/' + config.installedPackages[i] +
            '/static'));
        console.log('[ACOS Server] ' + 'INFO:'.green +
            ' Package ' + config.installedPackages[i].yellow + ' is installed.');
    } catch (e) { //Loading failed, perhaps a configuration error
        console.log('[ACOS Server]' + ' ERROR:'.red +
            ' Package '.red + config.installedPackages[i].yellow + ' could not be loaded.'.red);
    }
}

// URLs are, for example, http://myserver.com/html/puzzles/advanced/ex5
var urlPrefix = '/:protocol([a-z]+)/:contentType([a-z]+)/:contentPackage([a-zA-Z0-9_-]+)/:name([a-zA-Z0-9_.-]+)';
app.all(urlPrefix, function(req, res) {

    if (handlers.protocols[req.params.protocol] &&
        handlers.contentTypes[req.params.contentType] &&
        handlers.contentPackages[req.params.contentPackage]) {

        var params = {
            'name' : req.params.name || '',  // name of the requested exercise
            'headContent' : '',             // required additions to HTML head section
            'bodyContent' : ''             // required additions to HTML body section
        };

        var sendResponse = function() {

            if (!params.error) {

                if (app.get('env') === 'development') {
                    console.log('[ACOS Server] ' + 'INFO:'.green + ' Content requested => protocol: ' + req.params.protocol.yellow +
                        ', content type: ' + req.params.contentType.yellow +
                        ', content package: ' + req.params.contentPackage.yellow +
                        ', name: ' + params.name.yellow);
                }
                res.render('content.html', params);

            } else {

                if (app.get('env') === 'development') {
                    console.log('[ACOS Server] ' + 'ERROR:'.red + ' Initialization failed => protocol: ' + req.params.protocol.yellow +
                        ', content type: ' + req.params.contentType.yellow +
                        ', content package: ' + req.params.contentPackage.yellow +
                        ', name: ' + params.name.yellow);
                }
                res.status(404).send('Initialization failed!');
            }

        };

        // Initialize the protocol (which initializes the content type and content package)
        handlers.protocols[req.params.protocol].initialize(req, params, handlers, sendResponse);

    } else {
        res.status(404).send('Unsupported request!');
    }

});

// ********************************************************************************
// Handle events
app.post(urlPrefix + '/event', function(req, res) {

    if (handlers.protocols[req.params.protocol] && handlers.contentTypes[req.params.contentType]) {

        var event = req.body.event;
        var payload = JSON.parse(req.body.payload);
        var protocolData = JSON.parse(req.body.protocolData);

        for (var logger in handlers.loggers) {
            handlers.loggers[logger].handleEvent(event, payload, req, res, protocolData);
        }

        handlers.contentTypes[req.params.contentType].handleEvent(event, payload, req, res, protocolData);

        // Protocol is responsible for sending the response
        handlers.protocols[req.params.protocol].handleEvent(event, payload, req, res, protocolData);

    } else {
        res.status(404).send('Unsupported request!');
    }

});

// ********************************************************************************
// Server content listing
app.get('/', function(req, res) {

    var params = {'protocols' : [], 'contentTypes' : [], 'contentPackages' : [], 'tools' : []};

    for (var protocol in handlers.protocols) {
        params.protocols.push(protocol);
    }

    for (var contentType in handlers.contentTypes) {
        params.contentTypes.push(contentType);
    }

    for (var contentPackage in handlers.contentPackages) {
        params.contentPackages.push(handlers.contentPackages[contentPackage]);
    }

    for (var tool in handlers.tools) {
        params.tools.push(tool);
    }

    res.render('index.html', params);

});



















// ********************************************************************************
// API

var api = express.Router();
app.use('/api/v1', api);

api.get('/', function(req, res) {
    console.log('started!');
    var urls = {
        content: { url: config.serverAddress + 'api/v1/content', description: 'Lists all available content types'}
        // search: {url: config.serverAddress + 'api/v1/search', description: 'Search'}
    };

    res.json(urls);

});

//Has all content types
api.get('/content', function(req, res) {
    console.log('again');
    var contentTypes = [];
    var allContentTypes = handlers.contentTypes;

    Object.keys(allContentTypes).forEach(function(key) {
        var currentContentType = {};
        currentContentType.name = allContentTypes[key].meta.name;
        console.log(currentContentType.name);
        currentContentType.url = config.serverAddress +'api/v1/content/' + allContentTypes[key].meta.name;
        currentContentType.title = allContentTypes[key].meta.title;
        currentContentType.description = allContentTypes[key].meta.shortDescription;
        currentContentType.author = allContentTypes[key].meta.author;
        currentContentType.license = allContentTypes[key].meta.license;
        currentContentType.version = allContentTypes[key].meta.version;
        contentTypes.push(currentContentType);
    });

    res.json({contentTypes: contentTypes});

});

// Has all content packages in the given content type
api.get('/content/:contentType([a-z]+)', function(req, res) {

    if (req.params.contentType in handlers.contentTypes) {

        var packages = handlers.contentTypes[req.params.contentType].installedContentPackages;
        //console.log(packages);
        var contentPackages = [];
        var names = ['java','python','sql','c','cpp','vb','name'];
        for (var i = 0; i < packages.length; i++) {
            var currentContent = {};
            currentContent.name = packages[i].meta[names[i]];
            currentContent.url = config.serverAddress + 'api/v1/content/' + req.params.contentType + '/' + packages[i].meta[names[i]];
            currentContent.title = packages[i].meta.title;
            currentContent.description = packages[i].meta.shortDescription;
            currentContent.author = packages[i].meta.author;
            currentContent.license = packages[i].meta.license;
            currentContent.version = packages[i].meta.version;
            currentContent.created = packages[i].meta.created;
            currentContent.modified = packages[i].meta.modified;
            currentContent.keywords = packages[i].meta.keywords;
            contentPackages.push(currentContent);
        }
        //console.log(contentPackages);
        res.json({contentPackages: contentPackages});

    } else {
        res.status(404).json({'error':'Unknown content type.'});
    }

});

// All exercises in the given content package
api.get('/content/:contentType([a-z]+)/:contentPackage([a-zA-Z0-9_-]+)', function(req, res) {

    if (req.params.contentPackage in handlers.contentPackages) {
        //console.log(handlers.contentPackages);
        var namee = (req.params.contentPackage).replace('-','_');
        console.log(namee);
        var allContent = handlers.contentPackages[req.params.contentPackage].meta[namee];
        //console.log(allContent);
        var content = [];

        Object.keys(allContent).forEach(function(key) {
            var currentContent = {};
            currentContent.name = key;
            currentContent.title = allContent[key].title;
            currentContent.description = allContent[key].description;
            currentContent.url = config.serverAddress + 'api/v1/content/' + req.params.contentType + '/' + req.params.contentPackage + '/' + currentContent.name;
            if (req.params.contentType == 'annotated') {
                currentContent.html_url = config.serverAddress + 'pitt/' + req.params.contentType + '/' + req.params.contentPackage + '/' + 'Dissection2?act='+ key;
            }
            else{
                currentContent.html_url = 'http://adapt2.sis.pitt.edu/quizjet/' + 'quiz1.jsp?rdfID='+ key;
            }
            currentContent.author = allContent[key].author;
            currentContent.version = allContent[key].version;
            currentContent.created = allContent[key].created;
            currentContent.modified = allContent[key].modified;
            currentContent.keywords = allContent[key].keywords;
            //currentContent.subitems = allContent[key].subitems;

            if (req.query.expand && req.query.expand.split(',').indexOf('protocol_urls') > -1) {
                currentContent.protocol_urls = {};
                for (var protocol in handlers.protocols) {
                    currentContent.protocol_urls[protocol] = config.serverAddress + protocol + '/' + req.params.contentType + '/' + req.params.contentPackage + '/' + currentContent.name;
                }
            }

            content.push(currentContent);
        });

        res.json({content: content});

    } else {
        res.status(404).json({'error':'Unknown content package.'});
    }

});

// Information about the given exercise
api.get('/content/:contentType([a-z]+)/:contentPackage([a-zA-Z0-9_-]+)/:name([a-zA-Z0-9_.-]+)', function(req, res) {
    var nameee = (req.params.contentPackage).replace('-','_');
    if (req.params.contentPackage in handlers.contentPackages && req.params.name in handlers.contentPackages[req.params.contentPackage].meta[nameee]) {
        console.log(true);
        var exercise = handlers.contentPackages[req.params.contentPackage].meta[nameee][req.params.name];

        var exerciseInfo = {};
        exerciseInfo.name = req.params.name;
        exerciseInfo.url = config.serverAddress + 'api/v1/content/' + req.params.contentType + '/' + req.params.contentPackage + '/' + req.params.name;

        if (req.params.contentType == 'annotated') {
            exerciseInfo.html_url = config.serverAddress + 'pitt/' + req.params.contentType + '/' + req.params.contentPackage + '/' + 'Dissection2?act=' + req.params.name;
        }
        else{
            exerciseInfo.html_url = 'http://adapt2.sis.pitt.edu/quizjet/' + 'quiz1.jsp?rdfID='+ req.params.name;
        }

        exerciseInfo.title = exercise.title;
        exerciseInfo.description = exercise.description;
        exerciseInfo.version = exercise.version;
        exerciseInfo.keywords = exercise.keywords;
        exerciseInfo.subitems = exercise.subitems;

        if (req.query.expand && req.query.expand.split(',').indexOf('protocol_urls') > -1) {
            exerciseInfo.protocol_urls = {};
            for (var protocol in handlers.protocols) {
                exerciseInfo.protocol_urls[protocol] = config.serverAddress + protocol + '/' + req.params.contentType + '/' + req.params.contentPackage + '/' + req.params.name;
            }
        }

        res.json({exercise: exerciseInfo});

    } else {
        console.log(false);
        res.status(404).json({'error':'Unknown exercise.'});
    }

});

// ********************************************************************************
// Dummy logger for debugging purposes
if (app.get('env') === 'development') {

    var logger = function() {};
    logger.handleEvent = function(event, payload, req, res, protocolData) {
        console.log('[ACOS Server] ' + 'INFO:'.green + ' Event received => event: %s, payload: %j , protocol data: %j',
            event.yellow, payload, protocolData);
    };

    handlers.loggers.dummy = logger;

}

console.log('[ACOS Server] ' + 'INFO: Server is running.'.green);

// ********************************************************************************
// ACOS code ends

// catch 404 and forward to error handler
app.use(function(req, res, next) {
    var err = new Error('Not Found');
    err.status = 404;
    next(err);
});

// error handlers

// development error handler
// will print stacktrace
if (app.get('env') === 'development') {
    app.use(function(err, req, res, next) {
        res.status(err.status || 500);
        res.render('error.html', {
            message: err.message,
            error: err
        });
    });
}

// production error handler
// no stack traces leaked to user
app.use(function(err, req, res, next) {
    res.status(err.status || 500);
    res.render('error.html', {
        message: err.message,
        error: {}
    });
});


module.exports = app;
