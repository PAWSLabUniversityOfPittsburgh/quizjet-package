var CONFIG = {
    // The address for the server
    serverAddress: 'http://localhost:3000/',

    // Location for log files. The path will be relative to this directory.
    // If the path starts with a slash, it will be considered as an absolute path.
    logDirectory: 'logs',    
    
    // Path for the Python 3 executable. 
    // This is used, for example, by acos-python-parser
    pythonPath: '/usr/bin/python3',
    

    // Insert all the installed package names here to use them
    installedPackages: [
        // Installed protocols
        //'acos-aplus',
        //'acos-html',
        'acos-pitt',
        //'acos-lti',
        // Installed content types
       // 'acos-annotated',
        'acos-quiz',
        // Installed content packages
        //'acos-annotated-demo',
        'acos-quiz-quizjet'
        // Tools
    ]
    
};

module.exports = CONFIG;
