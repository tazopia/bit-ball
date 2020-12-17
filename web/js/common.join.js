var showMessage = errorMessage = function (message) {
    alert(message);
};
var path = '/';
try {
    path = '/' + location.pathname.split('/')[1];
} catch (e) {

}

var scene = document.getElementById('background');
try {
    var parallax = new Parallax(scene);
} catch (e) {

}

var login;
var checkUserid = false, checkNickname = false, checkPhone = false, checkAccount = false, checkRecommend = false;
$(function () {
    login = {
        goLogin: function () {
            if ($('#username').val() === '') {
                alert('아이디를 입력 하세요.');
                $('#username').focus();
                return false;
            }
            if ($('#password').val() === '') {
                alert('비밀번호를 입력 하세요.');
                $('#password').focus();
                return false;
            }
            $.ajax({
                url: '/login',
                data: {
                    username: $('#username').val(),
                    password: $('#password').val()
                }
            }).done(function (data) {
                if (data.success) {
                    location.href = data.url;
                } else {
                    $('#username').val('');
                    $('#password').val('');
                    $('#username').focus();
                }
            });
            return false;
        },
        checkCode: function () {
            if ($('#joinCode').val() === '') {
                alert('가입 코드를 입력하세요.');
                $('#joinCode').focus();
                return false;
            }
            $.ajax({
                url: path + '/code',
                data: {
                    joinCode: $('#joinCode').val()
                }
            }).done(function (data) {
                if (data.success) {
                    location.href = data.url;
                } else {
                    joinCode.val('');
                    joinCode.focus();
                }
            });
            return false;
        },
        goJoin: function () {
            if (clicked) return false;
            clicked = true;

            if (!checkUserid) {
                alert('아이디가 올바르지 않습니다.');
                $('#userid').focus();
                clicked = false;
                return false;
            }
            if (!checkNickname) {
                alert('닉네임이 올바르지 않습니다.');
                $('#nickname').focus();
                clicked = false;
                return false;
            }
            if ($('#password').val().length < 4) {
                alert('비밀번호는 최소 4자리 이상입니다.')
                $('#password').focus();
                clicked = false;
                return false;
            }
            if ($('#password').val() != $('#passwordConfirm').val()) {
                alert('비밀번호와 비밀번호 확인이 일치하지 않습니다.');
                $('#passwordConfirm').focus();
                clicked = false;
                return false;
            }
            if (!checkPhone) {
                alert('사용가능한 휴대폰이 아닙니다.');
                $('#phone').focus();
                clicked = false;
                return false;
            }
            if ($('#bank').val() == '') {
                alert('은행을 선택하여 주세요.');
                clicked = false;
                return false;
            }
            if ($('#depositor').val() == '') {
                alert('예금주를 입력하여 주세요.');
                $('#depositor').focus();
                clicked = false;
                return false;
            }
            if (config.duplicateAccount && !checkAccount) {
                alert('사용가능한 계좌번호가 아닙니다.');
                $('#account').focus();
                clicked = false;
                return false;
            }
            if ($('#account').val().length < 6) {
                alert('계좌번호를 입력 하세요.');
                $('#account').focus();
                clicked = false;
                return false;
            }
            if ($('#bankPassword').val().length < 4) {
                alert('충환전 비밀번호는 4자리 이상입니다.');
                $('#bankPassword').focus();
                clicked = false;
                return false;
            }
            if ($('#bankPassword').val() != $('#bankPasswordConfirm').val()) {
                alert('충환전 비밀번호와  비밀번호 확인이 유효하지 않습니다.');
                $('#bankPasswordConfirm').focus();
                clicked = false;
                return false;
            }
            if (config.requiredRecommend && !checkRecommend) {
                alert('추천인 아이디를 확인할 수 없습니다.');
                $('#recommender').focus();
                clicked = false;
                return false;
            }

            $.ajax({
                url: path + '/join',
                data: {
                    userid: $('#userid').val(),
                    nickname: $('#nickname').val(),
                    password: $('#password').val(),
                    phone: $('#phone').val(),
                    bank: $('#bank').val(),
                    depositor: $('#depositor').val(),
                    account: $('#account').val(),
                    bankPassword: $('#bankPassword').val(),
                    code: $('#code').val(),
                    recommender: $('#recommender').val()
                }
            }).done(function (data) {
                if (data.success) {
                    location.href = data.url;
                } else {
                    clicked = false;
                }
            });
            return false;

        },
        checkFocus: function (bul) {
            bul.removeClass('bul-ready bul-ok bul-error');
            bul.addClass('bul-ready');
        },
        checkEnabled: function (bul) {
            bul.removeClass('bul-ready bul-ok bul-error');
            bul.addClass('bul-ok');
        },
        checkDisabled: function (bul) {
            bul.removeClass('bul-ready bul-ok bul-error');
            bul.addClass('bul-error');
        }
    };

    $('#frm-join #userid').on('blur', function () {
        $.post(path + '/userid', {userid: $('#userid').val()}).done(function (data) {
            if (data.success && $('#userid').val().length > 3) {
                login.checkEnabled($('#bul-userid'));
                checkUserid = true;
            } else {
                login.checkDisabled($('#bul-userid'));
                checkUserid = false;
            }
        });
    }).on('focus', function () {
        checkUserid = false;
        login.checkFocus($('#bul-userid'));
    }).on('keyup', function () {
        $('#userid').val($('#userid').val().userid());
    });

    $('#frm-join #nickname').on('blur', function () {
        $.post(path + '/nickname', {nickname: $('#nickname').val()}).done(function (data) {
            if (data.success && $('#nickname').val().length > 1) {
                login.checkEnabled($('#bul-nickname'));
                checkNickname = true;
            } else {
                login.checkDisabled($('#bul-nickname'));
                checkNickname = false;
            }
        });
    }).on('focus', function () {
        checkNickname = false;
        login.checkFocus($('#bul-nickname'));
    }).on('keyup', function () {
        $('#nickname').val($('#nickname').val().nickname());
    });

    $('#frm-join #phone').on('blur', function () {
        if (config.duplicatePhone) { // 중복체크
            $.post(path + '/phone', {phone: $('#phone').val()}).done(function (data) {
                if (data.success && $('#phone').val().num().isMobile()) {
                    checkPhone = true;
                    login.checkEnabled($('#bul-phone'));
                } else {
                    checkPhone = false;
                    login.checkDisabled($('#bul-phone'));
                }
            });
        } else {
            if ($('#phone').val().num().isMobile()) {
                checkPhone = true;
            } else {
                checkPhone = false;
            }
        }
    }).on('focus', function () {
        checkPhone = false;
        login.checkFocus($('#bul-phone'));
    });

    if (config.duplicateAccount) {
        $('#frm-join #account').on('blur', function () {
            $.post(path + '/account', {account: $('#account').val()}).done(function (data) {
                if (data.success && $('#account').val().length > 6) {
                    checkAccount = true;
                    login.checkEnabled($('#bul-account'));
                } else {
                    checkAccount = false;
                    login.checkDisabled($('#bul-account'));
                }
            });
        }).on('focus', function () {
            checkAccount = false;
            login.checkFocus($('#bul-account'));
        });
    }

    if (config.requiredRecommend) {
        $('#frm-join #recommender').on('blur', function () {
            $.post(path + '/recommender', {recommender: $('#recommender').val()}).done(function (data) {
                if (data.success) {
                    checkRecommend = true;
                    login.checkEnabled($('#bul-recommend'));
                } else {
                    checkRecommend = false;
                    login.checkDisabled($('#bul-recommend'));
                }
            });
        }).on('focus', function () {
            checkRecommend = false;
            login.checkFocus($('#bul-recommend'));
        });
    }

});
