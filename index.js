var express = require('express');
var cool = require('cool-ascii-faces');
var app = express();
var MongoClient = require('mongodb').MongoClient, assert = require('assert');
var dburl = 'mongodb://localhost:27017/contents'
var assert = require('assert');

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
      console.log("Found the following records");
      console.log(results)
      response.render('pages/db',{results : results});
    });
    // response.render('pages/db', {results : results});

  // db.query("db.stories.find()", function(err, result) {
  //   done();
  //   if (err)
  //     {console.error(err); response.send("Error " + err); }
  //   else
  //     { response.render('pages/data', { results : result.rows}); }
  // });
    console.log("Connected successfully to server");
    db.close();
  });
});

app.get('/cool', function(request, response) {
  response.send(cool());
});

app.listen(app.get('port'), function() {
  console.log('Node app is running on port', app.get('port'));
});

