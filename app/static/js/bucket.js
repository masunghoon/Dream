/**
 * @author Ma Sunghoon
 */
var baseUrl = 'http://masunghoon.iptime.org:5000/'

function TasksViewModel() {
    var self = this;
    self.tasksURI = 'api/buckets';
    self.tasks = ko.observableArray();

    self.ajax = function(uri, method, data) {
        var request = {
            url: baseUrl + uri,
            type: method,
            contentType: "application/json",
            accepts: "application/json",
            cache: false,
            dataType: 'json',
            data: JSON.stringify(data),
            error: function(jqXHR) {
                console.log("ajax error " + jqXHR.status);
            }
        };
        return $.ajax(request);
    }
//    self.updateTask = function(task, newTask) {
//        var i = self.tasks.indexOf(task);
//        self.tasks()[i].uri(newTask.uri);
//        self.tasks()[i].title(newTask.title);
//        self.tasks()[i].description(newTask.description);
//        self.tasks()[i].done(newTask.done);
//    }
//
//    self.beginAdd = function() {
//        $('#add').modal('show');
//    }
//    self.add = function(task) {
//        self.ajax(self.tasksURI, 'POST', task).done(function(data) {
//            self.tasks.push({
//                uri: ko.observable(data.task.uri),
//                title: ko.observable(data.task.title),
//                description: ko.observable(data.task.description),
//                done: ko.observable(data.task.done)
//            });
//        });
//    }
//    self.beginEdit = function(task) {
//        editTaskViewModel.setTask(task);
//        $('#edit').modal('show');
//    }
//    self.edit = function(task, data) {
//        self.ajax(task.uri(), 'PUT', data).done(function(res) {
//            self.updateTask(task, res.task);
//        });
//    }
//    self.remove = function(task) {
//        self.ajax(task.uri(), 'DELETE').done(function() {
//            self.tasks.remove(task);
//        });
//    }
//    self.markInProgress = function(task) {
//        self.ajax(task.uri(), 'PUT', { done: false }).done(function(res) {
//            self.updateTask(task, res.task);
//        });
//    }
//    self.markDone = function(task) {
//        self.ajax(task.uri(), 'PUT', { done: true }).done(function(res) {
//            self.updateTask(task, res.task);
//        });
//    }
//    self.beginLogin = function() {
//        $('#login').modal('show');
//    }
    
    self.do = function() {
        self.ajax(self.tasksURI, 'GET').done(function(data) {
        	
            for (var i = 0; i < data.buckets.length; i++) {
                self.tasks.push({
//                    uri: ko.observable(data.buckets[i].uri),
                    title: ko.observable(data.buckets[i].title),
                    description: ko.observable(data.buckets[i].description),
                    done: ko.observable(data.buckets[i].done)
                });
            }
        });
    }

    self.do();
}