/*
 * /lib/controller.js
 */
var view = require('./view');
var controller = function() {};

controller.prototype = {
  'view' : function(user, callback) {
    var callback = (typeof callback === 'function') ? callback : function() {};
    var data = {
      'user' : user ? user : 'nobody'
    };
    view.renderView('view', data, function(data) {
      callback(data);
    });
    return '<h1> TODOs for me';
  },
  'home' : function(arg, callback) {
    var callback = (typeof callback === 'function') ? callback : function () {};
    var data = { 'users' : {
      'name' : 'James',
      'viewLink' : '/view/james'
    }};
    view.renderView('home', data, function(data) { callback(data);});
  },

};
module.exports = new controller();