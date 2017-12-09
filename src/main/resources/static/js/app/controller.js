var controllerModule = angular.module('vacanciesAppControllers', []);

controllerModule.controller('VacanciesController', function($scope, $http, $interval, VacanciesService) {

    $scope.vacancies = [];
    $scope.searchParameters = {};

    $scope.submit = function () {
        console.log('submit');
    }
	
    $scope.init = function () {
        console.log('AAA');
        $scope.buttonLabel = "Start feed";
        $scope.searchParameters.salary= 200000;
        $scope.searchParameters.keywordLimit = 10;
        $scope.searchParameters.itemsOnPage= 100;
    }

    feedUsersAtInterval = function() {
        UserService.getUsers().then(function(users){
            $scope.users = users;
        });
    }

    $scope.controlFeed = function() {
        if ($scope.buttonLabel === 'Start feed') {
            console.log('Start feed');
            UserService.startFeed();
            $scope.buttonLabel = 'Stop feed';

            $interval(feedUsersAtInterval, 2000);

        } else if ($scope.buttonLabel === 'Stop feed'){
            console.log('Stop feed');
            UserService.stopFeed();
            $scope.buttonLabel = 'Start feed';
        } else {
            console.log('Not implemented');
        }
    }

});
