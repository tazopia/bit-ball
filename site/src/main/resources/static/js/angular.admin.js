angular.module("app",[]).controller("AlarmCtrl",["$scope","$http","$interval","$timeout",function(t,a,o,n){t.alarm={alarmDeposit:!1,alarmWithdraw:!1,alarmQna:!1},t.monitor=function(){a({url:path+"/api/monitor",method:"POST",headers:{"Content-type":"application/json",Ajax:!0}}).then((function(a){t.alarm=a.data}),(function(t){401!==t.status&&403!==t.status||(top.location.href="/logout")}))},o((function(){t.monitor()}),5e3),t.monitor()}]);