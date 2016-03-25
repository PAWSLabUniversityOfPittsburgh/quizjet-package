# ADL Content Server

## Installation

To install acos server, clone the repository and install necessary dependencies
by running `npm install` in the acos-server directory.  


## Running

Easiest way to run the server is to navigate to 'acos-server' directory:

    node bin/www

By default the server can be accessed from `http://localhost:3000`.

### Events

Different smart content can emit events, which can be handled either in the
content or the protocol used. Examples of supported events:

    grade: {
      "points": 1, 
      "max_points": 2, 
      "status": "graded", 
      "feedback": "Problem solved, you got half of the points."
    }

    log: {
      "log": [
        [1.996,"step",1],
        [2.427,"step",2],
        [2.788,"step",3]
      ],
      "logId":57648,
      "animationId": "ae_adl_hello"
    }

## Required Metadata for Packages

All content/protocol/utility packages starting with 'acos-' must have the
following metadata included in them:

    Package.meta = {
      'name': 'package-name',
      'shortDescription': 'One sentence describing the package.',
      'description': 'Longer description for the package.',
      'author': 'Name',
      'license': 'MIT/Public Domain/whatever',
      'version': '0.0.1',
      'url': 'http://somesite.com'
    }