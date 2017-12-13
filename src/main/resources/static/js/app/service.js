var servicesModule = angular.module('vacanciesAppServices', []);

servicesModule.service('VacanciesService', function($http) {
    this.getVacancies = function() {
        return $http.get(window.location.href + '/api/vacancies').then(function(response){
            return response.data;
        }).catch(function(err) {
            return [];
        });
    }

    this.getKeywords = function() {
        return $http.get(window.location.href + '/api/vacancies/keywords').then(function(response){
            return response.data;
        }).catch(function(err) {
            return [];
        });
    }
    
    this.startFeed = function(searchParameters) {
        console.log("service start feed....");
        $http.post(window.location.href + '/api/vacancies/start', JSON.stringify(searchParameters)).then(function() {
            console.log('service start feed success');
        }).catch(function(err) {
            console.log('service start feed failure');
        });
    }

    this.stopFeed = function(searchParameters) {
        console.log("service stop feed....");
        $http.post(window.location.href + '/api/vacancies/stop', '').then(function() {
            console.log('service stop feed success');
        }).catch(function(err) {
            console.log('service start feed failure');
        });
    }

});
