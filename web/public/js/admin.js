var app = angular.module('app', ['ngMessages']);

var app = angular.module('app', []);

/**
 * 로그인 컨트롤러
 */
app.controller('loginController', function ($scope, $http) {
    $scope.login = function () {
        if (!$scope.username) {
            alert('아이디를 입력하세요.');
            angular.element('#username').focus();
            return;
        }
        if (!$scope.password) {
            alert('비밀번호를 입력하세요.');
            angular.element('#password').focus();
            return;
        }

        $http({
            method: 'post',
            url: '/login',
            data: $.param({
                username: $scope.username,
                password: $scope.password,
                role: $scope.role
            }),
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
            }
        }).then(function (response) {
            var result = response.data;
            if (result.success) {
                location.href = result.url;
            } else {
                alert(result.message);
                $scope.username = $scope.password = null;
                angular.element('#username').focus();
            }
        }, function () {
            alert('아이디와 비밀번호를 확인하세요.');
            $scope.username = $scope.password = null;
            angular.element('#username').focus();
        });
    }
});

/**
 * 관리자 회원가입
 */
app.controller('memberController', function ($scope, $http) {
    $scope.addMember = function () {
        if (!/^[0-9a-zA-Z]{4,12}$/.test($scope.userid || '')) {
            alert('아이디는 4~12자 숫자 영문으로 입력하세요.');
            $scope.userid = null;
            angular.element('#userid').focus();
            return;
        }
        if (($scope.password || '').length < 4) {
            alert('비밀번호는 최소 4자리 입니다.');
            $scope.password = null;
            angular.element('#password').focus();
            return;
        }
        if (!/^[0-9a-zA-Z가-힣]{2,12}/.test($scope.nickname || '')) {
            alert('닉네임은 한글 영문 숫자로 2~12자 이내로 입력하세요.');
            $scope.nickname = null;
            angular.element('#nickname').focus();
            return;
        }

        $http({
            method: 'POST',
            url: config.path + '/member/add',
            data: {
                'userid': $scope.userid,
                'password': $scope.password,
                'nickname': $scope.nickname,
                'agency.role': $scope.role,
                'money': $scope.money || '0',
                'point': $scope.point || '0',
                'agency.agency1': $scope.agency1 || '',
                'agency.agency2': $scope.agency2 || '',
                'agency.agency3': $scope.agency3 || '',
                'agency.agency4': $scope.agency4 || '',
                'phone': ($scope.phone || '').num(),
                'bank': $scope.bank,
                'account': $scope.account || '',
                'deposit': $scope.deposit || '',
                'bankPassword': $scope.bankPassword || '',
                'memo': $scope.memo || ''
            },
            headers: {
                'ajaxHeader': true
            }
        }).then(function (response) {
            var result = response.data;
            if (result.success) {
                alert($scope.userid + '님을 등록하였습니다.');
            } else {
                alert(result.message);
            }
        }, function () {
            alert('회원등록에 실패하였습니다.');
        });
    }
});
