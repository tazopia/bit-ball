/**
 * 관리자 공통 모듈 설정
 */
angular.module('app', [])
    .config(function ($httpProvider) {
        $httpProvider.headers.common['ajaxHeader'] = true;
    });

angular.module('app', [])
    .controller('loginCtrl', function ($scope, $http, $httpParamSerializer) {
        $scope.user = {
            role: 'admin'
        };
        $scope.goLogin = function () {
            if (!$scope.user.username) {
                alert('아이디를 입력 하세요.');
                $('#username').focus();
                return;
            }

            if (!$scope.user.password) {
                alert('비밀번호를 입력 하세요.');
                $('#password').focus();
                return;
            }

            $http.post('/login', $httpParamSerializer($scope.user), {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }).success(function (data) {
                if (data.success) {
                    location.href = data.url;
                } else {
                    alert(data.message);
                    $scope.user.username = $scope.user.password = '';
                    $('#username').focus();
                }
            }).error(function () {
                alert('아이디와 비밀번호를 확인하세요.');
                $scope.user.username = $scope.user.password = '';
                $('#username').focus();
            });
        }
    });

angular.module('app', [])
    .controller('memberAddCtrl', function ($scope) {
        $scope.role = 'USER';
    });
