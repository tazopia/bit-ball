
angular.module('app', []).controller('AlarmCtrl', ['$scope', '$http', '$interval', '$timeout', function ($scope, $http, $interval, $timeout) {
    $scope.alarm = {alarmDeposit:false, alarmWithdraw:false, alarmQna:false};

    $scope.monitor = function () {
        $http({
            url: path + '/api/monitor',
            method: 'POST',
            headers: {
                'Content-type': 'application/json',
                'Ajax': true
            }
        }).then(function success(response) {
            $scope.alarm = response.data;
        }, function error(response) {
            if (response.status === 401 || response.status === 403) {
                top.location.href = '/logout';
            }
        });
    };

    $interval(function () {
        $scope.monitor();
    }, 5000);
    $scope.monitor();

}]);
