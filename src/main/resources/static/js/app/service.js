var servicesModule = angular.module('vacanciesAppServices', []);

servicesModule.service('VacanciesService', function($http) {
    this.getVacancies = function() {
        return $http.get('http://localhost:8080/spring-hh-0.0.1-SNAPSHOT/api/vacancies').then(function(response){
            return response.data;
        }).catch(function(err) {
            return [];
        });
    }

    this.startFeed = function(searchParameters) {
        console.log("service start feed....");
        $http.post('http://localhost:8080/spring-hh-0.0.1-SNAPSHOT/api/vacancies/start', JSON.stringify(searchParameters)).then(function() {
            console.log('service start feed success');
        }).catch(function(err) {
            console.log('service start feed failure');
        });
    }

});
