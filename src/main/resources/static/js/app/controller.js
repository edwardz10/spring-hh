var controllerModule = angular.module('vacanciesAppControllers', []);

controllerModule.controller('VacanciesController', function($scope, $http, $interval, VacanciesService) {

    $scope.vacancies = [];
    $scope.searchParameters = {};

    startFeed = function() {
        console.log('Start feed');
        VacanciesService.startFeed($scope.searchParameters);
        $interval(feedVacanciesAtInterval, 2000);
    }

    $scope.submit = function () {
        console.log('submit');
        startFeed();
    }

    $scope.isFormInvalid = function() {
        return $scope.searchParameters.keyword == undefined
                || $scope.searchParameters.salary == undefined;
    }

    $scope.init = function () {
        console.log('AAA');
        $scope.buttonLabel = "Start feed";
        $scope.searchParameters.salary= 200000;
        $scope.searchParameters.keywordLimit = 10;
        $scope.searchParameters.itemsOnPage= 100;
    }

    feedVacanciesAtInterval = function() {
        VacanciesService.getVacancies().then(function(vacancies){
            $scope.vacancies = vacancies;
        });
    }


});
