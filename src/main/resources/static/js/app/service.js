var servicesModule = angular.module('vacanciesAppServices', []);

servicesModule.service('VacanciesService', function($http) {
    this.getUsers = function() {
        return $http.get('http://localhost:8080/spring-angular-feed-0.0.1-SNAPSHOT/api/users').then(function(response){
            return response.data;
        }).catch(function(err) {
            return [];
        });
    }

    this.startFeed = function() {
        console.log("service start feed....");
        $http.post('http://localhost:8080/spring-angular-feed-0.0.1-SNAPSHOT/api/users/start', '').then(function() {
            console.log('service start feed success');
        }).catch(function(err) {
            console.log('service start feed failure');
        });
    }

    this.stopFeed = function() {
        console.log("service stop feed....");
        $http.post('http://localhost:8080/spring-angular-feed-0.0.1-SNAPSHOT/api/users/stop', '').then(function() {
            console.log('service stop feed success');
        }).catch(function(err) {
            console.log('service stop feed failure');
        });
    
    }
});
