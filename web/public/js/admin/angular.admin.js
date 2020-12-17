/**
 * 관리자 공통 모듈 설정
 */
var app = angular.module('app', []);

app.config(function ($httpProvider) {
    $httpProvider.defaults.headers.common['ajaxHeader'] = true;
});

/**
 * 로그인 컨트롤러
 */
app.controller('loginCtrl', function ($scope, $http, $httpParamSerializer) {
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

/**
 * 공통 알람 콘트롤러
 */
app.controller('AlarmCtrl', function ($q) {

    var listener = $q.defer(), socket = {
        client: null,
        stomp: null
    };

    var startListener = function () {
        socket.stomp.subscribe('/queue/chat', getMessage);
    };

    var getMessage = function(message) {
        console.log('메시지 : ' + message.body.toString());

    };

    function open() {
        socket.client = new SockJS('/stomp');
        socket.stomp = Stomp.over(socket.client);
        socket.stomp.connect({}, startListener);
    }

    open();

    // setInterval(function() {
    //     socket.stomp.send('/app/chat', {}, "음햐햐햐햐햐");
    // }, 10000);

});
