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

var _ = require('lodash');

// These fields will be in the response, if the field is available
var fields = ['name', 'title', 'description', 'author', 'license', 'version', 'created', 'modified', 'keywords'];

var contentTypesAsJSON = function(filters, expand) {
    var contentTypes = [];
    var allContentTypes = handlers.contentTypes;

    Object.keys(allContentTypes).forEach(function(key) {

        if (filters.keyword && allContentTypes[key].meta.keywords.indexOf(filters.keyword) < 0) {
            return;
        }

        if (filters.author && (allContentTypes[key].meta.author || '') !== filters.author) {
            return;
        }

        var currentContentType = {};

        fields.forEach(function(field) {
            if (allContentTypes[key].meta[field]) {
                currentContentType[field] = allContentTypes[key].meta[field];
            }
        });

        currentContentType.name = allContentTypes[key].meta.name;
        //console.log(currentContentType.name);
        currentContentType.url = config.serverAddress +'api/v1/content/' + allContentTypes[key].meta.name;
        currentContentType.title = allContentTypes[key].meta.title;
        currentContentType.description = allContentTypes[key].meta.shortDescription;
        currentContentType.author = allContentTypes[key].meta.author;
        currentContentType.license = allContentTypes[key].meta.license;
        currentContentType.version = allContentTypes[key].meta.version;

        if (expand) {
            currentContentType.subitems = contentPackagesAsJSON(key, filters, true);
        }

        contentTypes.push(currentContentType);

    });

    return contentTypes;

};

var contentPackagesAsJSON = function(contentType, filters, expand) {
    if (handlers.contentTypes[contentType]) {
        var contentPackages = [];
        var packages = handlers.contentTypes[contentType].installedContentPackages;
        var names = ['java','python','sql','c','cpp','vb','name'];
        for (var i = 0; i < packages.length; i++) {

            if (filters.keyword && package.meta.keywords.indexOf(filters.keyword) < 0) {
                return;
            }

            if (filters.author && package.meta.author !== filters.author) {
                return;
            }

            var currentPackage = {};

            fields.forEach(function(field) {
                if (package.meta[field]) {
                    currentPackage[field] = package.meta[field];
                }
            });

            currentPackage.url = config.serverAddress + 'api/v1/content/' + contentType + '/' + package.meta.name;
            currentPackage.name = packages[i].meta[names[i]];
            currentPackage.url = config.serverAddress + 'api/v1/content/' + contentType + '/' + packages[i].meta[names[i]];
            currentPackage.title = packages[i].meta.title;
            currentPackage.description = packages[i].meta.shortDescription;
            currentPackage.author = packages[i].meta.author;
            currentPackage.license = packages[i].meta.license;
            currentPackage.version = packages[i].meta.version;
            currentPackage.created = packages[i].meta.created;
            currentPackage.modified = packages[i].meta.modified;
            currentPackage.keywords = packages[i].meta.keywords;

            if (expand) {
                currentPackage.subitems = contentPackageAsJSON(contentType, package.meta.name, filters, true, false);
            }

            contentPackages.push(currentPackage);
        };
        return contentPackages;
    } else {
        return null;
    }
};

var contentPackageAsJSON = function(contentType, contentPackage, filters, expand, expandProtocolUrls) {
    if (handlers.contentPackages[contentPackage]) {

        var namee = (contentPackage).replace('-','_');
        console.log(namee);
        var allContent = handlers.contentPackages[contentPackage].meta[namee];

        //var allContent = handlers.contentPackages[contentPackage].meta.contents || [];
        var content = [];

        Object.keys(allContent).forEach(function(key) {

            if (filters.keyword && (allContent[key].keywords || []).indexOf(filters.keyword) < 0) {
                return;
            }

            if (filters.author && (allContent[key].author || handlers.contentPackages[contentPackage].meta.author) !== filters.author) {
                return;
            }

            var currentContent = {};

            fields.forEach(function(field) {
                if (allContent[key][field]) {
                    currentContent[field] = allContent[key][field];
                }
            });

            currentContent.name = key;
            currentContent.title = allContent[key].title;
            currentContent.description = allContent[key].description;
            currentContent.url = config.serverAddress + 'api/v1/content/' + contentType + '/' + contentPackage + '/' + currentContent.name;
            if (contentType == 'annotated') {
                currentContent.html_url = config.serverAddress + 'pitt/' + contentType + '/' + contentPackage + '/' + 'Dissection2?act='+ key;
            }
            else{
                currentContent.html_url = 'http://adapt2.sis.pitt.edu/quizjet/' + 'quiz1.jsp?rdfID='+ key;
            }
            currentContent.author = allContent[key].author;
            currentContent.version = allContent[key].version;
            currentContent.created = allContent[key].created;
            currentContent.modified = allContent[key].modified;
            currentContent.author_id = allContent[key].author_id;
            currentContent.keywords = allContent[key].keywords;

            if (expandProtocolUrls) {
                currentContent.protocol_urls = {};
                for (var protocol in handlers.protocols) {
                    currentContent.protocol_urls[protocol] = config.serverAddress + protocol + '/' + contentType + '/' + contentPackage + '/' + currentContent.name;
                }
            }

            /*if (expand) {
             currentContent.subitems = contentAsJSON(contentType, contentPackage, currentContent.name, filters, true, false);
             }*/

            content.push(currentContent);
        });

        return content;

    } else {
        return null;
    }
};

var contentAsJSON = function(contentType, contentPackage, content, filters, expand, expandProtocolUrls) {
    var nameee = (contentPackage).replace('-','_');
    if (handlers.contentPackages[contentPackage] && handlers.contentPackages[contentPackage].meta[nameee]) {

        var exercise = handlers.contentPackages[contentPackage].meta[nameee][content];
        console.log(exercise);

        var exerciseInfo = {};
        exerciseInfo.name = content;
        //exerciseInfo.name = req.params.name;
        exerciseInfo.url = config.serverAddress + 'api/v1/content/' + contentType + '/' + contentPackage + '/' + content;

        if (contentType == 'annotated') {
            exerciseInfo.html_url = config.serverAddress + 'pitt/' + contentType + '/' + contentPackage + '/' + 'Dissection2?act=' + content;
        }
        else{
            exerciseInfo.html_url = 'http://adapt2.sis.pitt.edu/quizjet/' + 'quiz1.jsp?rdfID='+ content;
        }

        exerciseInfo.title = exercise.title;
        exerciseInfo.description = exercise.description;
        exerciseInfo.version = exercise.version;
        exerciseInfo.keywords = exercise.keywords;


        fields.forEach(function(field) {
            if (exercise[field]) {
                exerciseInfo[field] = exercise[field];
            }
        });

        if (expandProtocolUrls) {
            exerciseInfo.subitems = exercise.subitems;
        }

        return exerciseInfo;

    } else {
        return null;
    }
};


var createFilters = function(req) {
    var filters = {};
    var allowed = ['keyword', 'author'];
    allowed.forEach(function(item) {
        if (req.query[item]) {
            filters[item] = req.query[item];
        }
    });
    return filters;
};

// **********************************************

var api = express.Router();
app.use('/api/v1', api);

api.get('/', function(req, res) {

    var endpoints = {
        content: { url: config.serverAddress + 'api/v1/content', description: 'Lists all available content types'},
    };

    res.json(endpoints);

});

// All content types
api.get('/content', function(req, res) {
    var expand = req.query.expand && req.query.expand.split(',').indexOf('subitems') > -1;
    var filters = createFilters(req);
    var response = {contentTypes: contentTypesAsJSON(filters, expand)};
    res.json(response);
});

// All content packages in the given content type
api.get('/content/:contentType([a-zA-Z0-9_-]+)', function(req, res) {
    var expand = req.query.expand && req.query.expand.split(',').indexOf('subitems') > -1;
    var filters = createFilters(req);
    var response = contentPackagesAsJSON(req.params.contentType, filters, expand);
    if (response) {
        res.json({contentPackages: response});
    } else {
        res.status(404).json({'error':'Unknown content type.'});
    }
});

// All exercises in the given content package
api.get('/content/:contentType([a-zA-Z0-9_.-]+)/:contentPackage([a-zA-Z0-9_.-]+)', function(req, res) {
    var expand = req.query.expand && req.query.expand.split(',').indexOf('subitems') > -1;
    var expandProtocolUrls = req.query.expand && req.query.expand.split(',').indexOf('protocol_urls') > -1;
    var filters = createFilters(req);
    var response = contentPackageAsJSON(req.params.contentType, req.params.contentPackage, filters, expand, expandProtocolUrls);
    if (response) {
        res.json({content: response});
    } else {
        res.status(404).json({'error':'Unknown content package.'});
    }
});

// Information about the given exercise
api.get('/content/:contentType([a-zA-Z0-9_-]+)/:contentPackage([a-zA-Z0-9_-]+)/:name([a-zA-Z0-9_.-]+)', function(req, res) {
    var expand = req.query.expand && req.query.expand.split(',').indexOf('subitems') > -1;
    var expandProtocolUrls = req.query.expand && req.query.expand.split(',').indexOf('subitems') > -1;
    var filters = createFilters(req);
    var response = contentAsJSON(req.params.contentType, req.params.contentPackage, req.params.name, filters, expand, expandProtocolUrls);
    if (response) {
        res.json({item: response});
    } else {
        res.status(404).json({'error':'Unknown content item.'});
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
