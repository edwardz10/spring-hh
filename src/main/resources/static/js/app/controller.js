var controllerModule = angular.module('vacanciesAppControllers', []);

controllerModule.controller('VacanciesController', function($scope, $http, $interval, VacanciesService) {

    $scope.vacancies = [];
    $scope.searchParameters = {};
    
    startFeed = function() {
        VacanciesService.startFeed($scope.searchParameters);
        $interval(feedVacanciesAtInterval, 2000);
    }

    $scope.submit = function () {
        if ($scope.buttonLabel == "Start feed") {
            console.log("Start feed");
            $scope.buttonLabel = "Stop feed & reset";
            startFeed();
        } else {
            $scope.buttonLabel = "Start feed";
            VacanciesService.stopFeed();
        }
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
        VacanciesService.getKeywords().then(function(keywords){
            $scope.keywords = keywords;
        });
    }

    $scope.setOrderProperty = function(propertyName) {
        if ($scope.orderProperty === propertyName) {
            $scope.orderProperty = '-' + propertyName;
        } else if ($scope.orderProperty === '-' + propertyName) {
            $scope.orderProperty = propertyName;
        } else {
            $scope.orderProperty = propertyName;
    }
}

});
