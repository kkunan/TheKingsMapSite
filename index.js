var express = require('express');
var cool = require('cool-ascii-faces');
var app = express();
var MongoClient = require('mongodb').MongoClient;
var assert = require('assert');
var databaseuri = require('./databaseuri.js')
var dburl = databaseuri.database;//dev side
// var dburl = 'mongodb://site:9ramaking@ds045031.mlab.com:45031/heroku_2khfgvtb'

app.set('port', (process.env.PORT || 5000));
app.use(express.static(__dirname + '/public'));

// views is directory for all template files
app.set('views', __dirname + '/views');
app.set('view engine', 'ejs');

app.get('/', function(request, response) {
  response.render('pages/index');
});

app.get('/data', function(request, response) {
  MongoClient.connect(dburl, function(err, db) {
    assert.equal(null, err);
    collection = db.collection('stories');
    collection.find().toArray(function(err,results) {
      assert.equal(err, null);
      console.log("Retrieve Stories");
      response.setHeader('Content-type','application/json')
      response.send(JSON.stringify(results));
    });
    db.close();
  });
});

app.get('/map', function(request, response) {
  response.render('pages/db');
});

app.get('/stories_display', function(request, response) {
  MongoClient.connect(dburl, function(err, db) {
    assert.equal(null, err);
    collection = db.collection('stories');
    collection.find().toArray(function(err,results) {
      assert.equal(err, null);
      response.render('pages/data-display',{results : results});
    });
    db.close();
  });
});

app.listen(app.get('port'), function() {
  console.log('Node app is running on port', app.get('port'));
});
