/**
 * 관리자 공통 모듈 설정
 */
var app = angular.module('app', []);

app.config(function ($httpProvider) {
    $httpProvider.defaults.headers.common['ajaxHeader'] = true;
});

/**
 * 사다리 controller
 */
app.controller('LadderCtrl', function ($q) {

    var listener = $q.defer(), socket = {
        client: null,
        stomp: null
    };

    var startListener = function () {
        socket.stomp.subscribe('/ladder/chat', getMessage);
        socket.stomp.subscribe('/ladder/gameInfo', getGameInfo);
    };


    var getMessage = function(message) {
        console.log('메시지 : ' + message.body.toString());

    };

    var getGameInfo = function (gameInfo) {
        console.log('게임정보 : ' + gameInfo.body.toString());
    }

    function open() {
        socket.client = new SockJS('/stomp');
        socket.stomp = Stomp.over(socket.client);
        socket.stomp.connect({}, startListener);
    }

    open();

    setInterval(function() {
        //socket.stomp.send('/app/ladder/chat', {}, "음햐햐햐햐햐");
    }, 10000);

});
