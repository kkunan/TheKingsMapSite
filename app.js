var MongoClient = require('mongodb').MongoClient, assert = require('assert');

//connect
var url = 'mongodb://localhost:27017/contents'

// Use connect method to connect to the server
MongoClient.connect(url, function(err, db) {
  assert.equal(null, err);
  console.log("Connected successfully to server");
  collection = db.collection('stories');
  result = collection.find({});
  console.log(result);
  db.close();
});


