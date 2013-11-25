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
    self.updateTask = function(task, newTask) {
        var i = self.tasks.indexOf(task);
        self.tasks()[i].uri(newTask.uri);
        self.tasks()[i].title(newTask.title);
       	self.tasks()[i].description(newTask.description);
        self.tasks()[i].done(newTask.is_live);
		self.tasks()[i].private(newTask.is_private);
    }

   self.beginAdd = function() {
       $('#addBucket').modal('show');
   }
   self.add = function(task) {
       self.ajax(self.tasksURI, 'POST', task).done(function(data) {
           self.tasks.push({
               uri: ko.observable(data.bucket.uri),
               title: ko.observable(data.bucket.title),
               description: ko.observable(data.bucket.description),
               private: ko.observable(data.bucket.is_private),
			   done: ko.observable(data.bucket.is_live),
			   deadline: ko.observable(data.bucket.deadline)
           });
       });
   }
   // self.beginEdit = function(task) {
   //     editTaskViewModel.setTask(task);
   //     $('#edit').modal('show');
   // }
//    self.edit = function(task, data) {
//        self.ajax(task.uri(), 'PUT', data).done(function(res) {
//            self.updateTask(task, res.task);
//        });
//    }
    // self.remove = function(task) {
    //     self.ajax(task.uri(), 'DELETE').done(function() {
    //         self.tasks.remove(task);
    //     });
    // }
    self.delFromList = function(task) {
    	self.ajax(task.uri(), 'PUT', { is_live: 9 }).done(function(res) {
    		self.updateTask(task. res.bucket);
    	});
    }
    self.markInProgress = function(task) {
        self.ajax(task.uri(), 'PUT', { is_live: 0 }).done(function(res) {
            self.updateTask(task, res.bucket);
        });
    }
    self.markDone = function(task) {
        self.ajax(task.uri(), 'PUT', { is_live: 1 }).done(function(res) {
            self.updateTask(task, res.bucket);
        });
    }
//    self.beginLogin = function() {
//        $('#login').modal('show');
//    }
    
    self.do = function() {
        self.ajax(self.tasksURI, 'GET').done(function(data) {
        	
            for (var i = 0; i < data.buckets.length; i++) {
                self.tasks.push({
                    uri: ko.observable(data.buckets[i].uri),
                    title: ko.observable(data.buckets[i].title),
                   	description: ko.observable(data.buckets[i].description),
                    done: ko.observable(data.buckets[i].is_live),
					private: ko.observable(data.buckets[i].is_private),
					deadline: ko.observable(data.buckets[i].deadline)
                });
            }
        });
    }

    self.do();
}

function AddTaskViewModel() {
    var self = this;
    self.title = ko.observable();
    self.description = ko.observable();
	self.private = ko.observable();
	self.done = ko.observable();
	self.due = ko.observable();
    self.addTask = function() {
    	alert(self.title());
    	alert(self.due());
    	$('#addBucket').modal('hide');
    	tasksViewModel.add({
    		title: self.title(),
    		description: self.description(),
    		is_private: self.private(),
    		is_live: self.done(),
    		deadline: self.due()
        });
        self.title("");
        self.description("");
		self.private("");
		self.done("");
		self.due("");
    }
}
//function EditTaskViewModel() {
//    var self = this;
//    self.title = ko.observable();
//    self.description = ko.observable();
//    self.private
//    self.done = ko.observable();
//
//    self.setTask = function(task) {
//        self.task = task;
//        self.title(task.title());
//        self.description(task.description());
//        self.done(task.done());
//        $('edit').modal('show');
//    }
//
//    self.editTask = function() {
//        $('#edit').modal('hide');
//        tasksViewModel.edit(self.task, {
//            title: self.title(),
//            description: self.description() ,
//            done: self.done()
//        });
//    }
//}
$(function () {
    $(document).on('click', 'a.disconnect', function (e) {
        e.preventDefault();
        $('form#disconnect-form')
            .attr('action', $(this).attr('href'))
            .submit();
    });
    $("button[data-href]").click( function() {
        location.href = $(this).attr("data-href");
    });
});