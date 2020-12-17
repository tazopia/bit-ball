var path = '/';
try {
    path = location.pathname.match(/^(\/.*?)\//)[1];
} catch (e) {
}

$(function () {
    // flash message 보이기
    var flashMessage = $('#flashMessage');
    if (flashMessage.text().trim() !== '') {
        setTimeout(function () {
            showMessage(flashMessage.text());
        }, 10);
    }

    var sel = $('.table-game input, .sel');
    sel.on('focus', function () {
        $(this).select();
    });

    var cal = $('.cal');
    cal.calendar();

    var calReset = $('#sp-reset-date');
    calReset.on('click', function () {
        cal.val('');
    });

    // $(document).tooltip({
    //     position: {
    //         my: 'center'
    //     }
    // });
});


var popup = {
    close: function () {
        try {
            opener.focus();
        } catch (e) {
        }
        self.close();
    },
    reloadClose: function () {
        try {
            opener.location.reload();
            opener.focus();
        } catch (e) {
        }
        self.close();
    },
    sports: { // 종목관리
        add: function () {
            window.open(path + '/config/sports/add', 'popup-sports-add', 'width=800, height=300');
        },
        update: function (id) {
            window.open(path + '/config/sports/update/' + id, 'popup-sports-update', 'width=800, height=340');
        }
    },
    league: { // 리그관리
        add: function () {
            window.open(path + '/config/league/add', 'popup-league-add', 'width=800, height=310');
        },
        update: function (id) {
            window.open(path + '/config/league/update/' + id, 'popup-league-update', 'width=800, height=380');
        }
    },
    team: {
        update: function (id) {
            window.open(path + '/config/team/update/' + id, 'popup-team-update', 'width=800, height=300');
        }
    },
    game: {
        logger: function (id) {
            window.open(path + '/game/logger/' + id, 'popup-logger-' + id, 'width=1200, height=600');
        },
        score: function (id) {
            var obj = $('#' + id);
            $('tr').removeClass('result');

            if (obj.next().attr('id') == 'result-box' && $('#result-box').is(':visible')) {
                $('#result-box').hide();
            } else {
                obj.addClass('result');
                $('#result-box').insertAfter(obj).show();
                var score = obj.find('.sp-score').text().replace('-', ':').split(':');
                $('#score-away').val(score[1].trim());
                $('#score-cancel').prop('checked', false);
                $('#score-home').val(score[0].trim()).focus();
                $('#score-id').val(id);
            }
        },
        add: function () {
            window.open(path + '/game/popup/add', 'popup-game-add', 'width=1024, height=700');
        }
    },
    member: {
        add: function () {
            window.open(path + '/member/add', 'popup-member-add', 'width=1024, height=640');
        },
        dummy: function () {
            window.open(path + '/member/dummy', 'popup-member-dummy', 'width=800, height=440');
        },
        info: function (userid) {
            window.open(path + '/member/info/' + userid, 'popup-member-' + userid, 'width=1024, height=640');
        },
        login: function (userid) { // 접속현황
            alert('아이피');
        },
        currentUser: function () {
            window.open(path + '/member/currentUser', 'popup-current-user', 'width=1024, height=640');
        }
    },
    agency: {
        add: function (userid) {
            window.open(path + '/member/add?userid=' + userid, 'popup-member-add', 'width=1024, height=640');
        }
    },
    customer: {
        memo: function (userid) {
            window.open(path + '/customer/memo/add/' + userid, 'popup-customer-memo', 'width=800, height=440');
        }
    },
    payment: {
        money: function (userid) {
            window.open(path + '/payment/money/' + userid, 'popup-money-' + userid, 'width=1200, height=740');
        },
        point: function (userid) {
            window.open(path + '/payment/point/' + userid, 'popup-point-' + userid, 'width=1200, height=740');
        },
        banking: function (userid) {
            window.open(path + '/payment/banking/' + userid, 'popup-banking-' + userid, 'width=1200, height=740');
        }
    },
    betting: {
        list: function (userid) {
            window.open(path + '/betting/popup/' + userid, 'popup-betting-' + userid, 'width=1400, height=740');
        },
        game: function (id, betTeam) {
            window.open(path + '/betting/' + betTeam + '/' + id, betTeam + '-' + id, 'width=1400, height=740');
        },
        zone: function (zoneName, sdate) {
            window.open(path + '/betting/zone/' + zoneName + '/' + sdate, zoneName + '-' + sdate, 'width=1400, height=740');
        },
        board: function () {
            window.open(path + '/betting/popup/board', 'popup-betting-board', 'width=1400, height=740');
        }
    },
    popup: {
        add: function () {
            window.open(path + '/customer/popup/add', 'popup-sports-add', 'width=800, height=340');
        },
        update: function (id) {
            window.open(path + '/customer/popup/update/' + id, 'popup-sports-update', 'width=800, height=800');
        }
    },
    notice: {
        add: function () {
            window.open(path + '/notice/add', 'popup-notice-add', 'width=800, height=400');
        },
        update: function (id) {
            window.open(path + '/notice/update/' + id, 'popup-notice-update', 'width=800, height=400');
        }
    }
};

var search = {
    payment: {
        userid: function (userid) {
            $('#username').val(userid);
            $('#page').val();
            $('#frm-search').submit();
        }
    }
};

var payment = {
    deposit: {
        submit: function (id) {
            if (!confirm('입금을 확인하였습니다.\n\n입금 처리를 완료 합니다.')) {
                return false;
            }
            $.post(path + '/payment/deposit/submit', {
                id: id
            }).done(function (data) {
                if (data.success) {
                    location.reload();
                }
            });
        },
        cancel: function (id) {
            if (!confirm('충전 신청을 취소합니다.')) {
                return false;
            }
            $.post(path + '/payment/deposit/cancel', {
                id: id
            }).done(function (data) {
                if (data.success) {
                    location.reload();
                }
            });
        },
        stop: function (id) {
            $.post(path + '/payment/deposit/stop', {
                id: id
            }).done(function (data) {
                if (data.success) {
                    location.reload();
                }
            });
        },
        rollback: function (id) {
            if (!confirm('충전완료 내역을 취소합니다.\n\n충전된 머니를 되돌립니다.')) {
                return false;
            }
            $.post(path + '/payment/deposit/rollback', {
                id: id
            }).done(function (data) {
                if (data.success) {
                    location.reload();
                }
            });
        }
    },
    withdraw: {
        submit: function (id) {
            if (!confirm('지급을 완료하였습니다.\n\n환전 처리를 완료 합니다.')) {
                return false;
            }
            $.post(path + '/payment/withdraw/submit', {
                id: id,
                fees: $('#fees' + id).val()
            }).done(function (data) {
                if (data.success) {
                    location.reload();
                }
            });
        },
        rollback: function (id) {
            if (!confirm('환전을 취소합니다.\n\n사용자 머니를 복구 합니다.')) {
                return false;
            }
            $.post(path + '/payment/withdraw/rollback', {
                id: id
            }).done(function (data) {
                if (data.success) {
                    location.reload();
                }
            });
        },
        stop: function (id) {
            $.post(path + '/payment/withdraw/stop', {
                id: id
            }).done(function (data) {
                if (data.success) {
                    location.reload();
                }
            });
        }
    }
};

var game = {
    betEnabled: function (id, betType) {
        $.post(path + '/game/betEnabled', {
            gameId: id, betType: betType
        }).done(function (data) {
            $('#' + id + ' [data-sports]').addClass('popup');
            var btn = $('#' + id + ' [data-bet="' + betType + '"]');
            if (data.value === 'Y') {
                btn.addClass('btn01').text('베팅가능');
            } else {
                btn.removeClass('btn01').text('베팅불가');
            }
            $('#' + id + ' [data-auto]').removeClass('btn01').text('OFF');
        });
    },
    autoUpdate: function (id) {
        $.post(path + '/game/autoUpdate', {
            gameId: id
        }).done(function (data) {
            $('#' + id + ' [data-sports]').addClass('popup');
            var btn = $('#' + id + ' [data-auto]');
            if (data.value === 'Y') {
                btn.addClass('btn01').text('ON');
            } else {
                btn.removeClass('btn01').text('OFF');
            }
        });
    },
    update: function (id) {
        if (!confirm('경기 시간 및 배당을 업데이트 하시겠습니까?')) return;

        var date = $('#' + id + ' [name="date"]').val();
        var hour = $('#' + id + ' [name="hour"]').val();
        var minute = $('#' + id + ' [name="minute"]').val();
        var oddsHome = $('#' + id + ' [name="oddsHome"]').val();
        var oddsDraw = $('#' + id + ' [name="oddsDraw"]').val();
        var oddsAway = $('#' + id + ' [name="oddsAway"]').val();
        var handicap = $('#' + id + ' [name="handicap"]').val();

        $.post(path + '/game/update', {
            gameId: id,
            date: date,
            hour: hour,
            minute: minute,
            oddsHome: oddsHome,
            oddsDraw: oddsDraw,
            oddsAway: oddsAway,
            handicap: handicap
        }).done(function (data) {
            if (data.success) {
                $('#' + id + ' [data-sports]').addClass('popup');
                $('#' + id + ' [data-auto]').removeClass('btn01').text('OFF');
            }
        });
    },
    score: function () {
        var id = $('#score-id').val();
        var scoreHome = $('#score-home').val();
        var scoreAway = $('#score-away').val();
        var cancel = $('#score-cancel').is(':checked');
        if (!cancel && (scoreHome == '' || scoreAway == '')) {
            alert('결과를 입력 하세요');
            scoreHome == '' ? $('#score-home').focus() : $('#score-away').focus();
            return false;
        }

        $.post(path + '/game/score', {
            id: id,
            scoreHome: scoreHome,
            scoreAway: scoreAway,
            cancel: cancel
        }).done(function (data) {
            if (data.success) {
                $('#result-box').hide();
                $('#' + id).hide();
            }
        });
    },
    oddsUp: function (id) {
        // 홈 up, 원정 down
        var oddsHome = $('#' + id + ' [name="oddsHome"]');
        var oddsAway = $('#' + id + ' [name="oddsAway"]');
        var home = (parseFloat(oddsHome.val()) || config.oddsDefault) + config.oddsUp;
        var away = (parseFloat(oddsAway.val()) || config.oddsDefault) - config.oddsDown;

        if (home < 1 || away < 1) {
            alert('배당은 1이하로 적용시킬 수 없습니다.');
            return;
        }

        oddsHome.val(home.toFixed(2));
        oddsAway.val(away.toFixed(2));
    },
    oddsDown: function (id) {
        // 홈 down, 원정 up
        var oddsHome = $('#' + id + ' [name="oddsHome"]');
        var oddsAway = $('#' + id + ' [name="oddsAway"]');
        var home = (parseFloat(oddsHome.val()) || config.oddsDefault) - config.oddsUp;
        var away = (parseFloat(oddsAway.val()) || config.oddsDefault) + config.oddsDown;

        if (home < 1 || away < 1) {
            alert('배당은 1이하로 적용시킬 수 없습니다.');
            return;
        }

        oddsHome.val(home.toFixed(2));
        oddsAway.val(away.toFixed(2));
    },
    select: function () {
        var selected = $('#sp-select').data().selected;
        if (selected) {
            $('input[name="gameId"]').each(function () {
                $(this).prop('checked', '');
            });
            $('#sp-select').data().selected = false;
        } else {
            $('input[name="gameId"]').each(function () {
                $(this).prop('checked', 'checked');
            });
            $('#sp-select').data().selected = true;
        }
    },
    enabled: function (enabled) {
        var obj = $('input[name="gameId"]:checked');
        var cnt = obj.length, gameIds = [];
        var msg = enabled ? '등록' : '등록취소';
        if (cnt == 0) {
            alert(String.format('{0} 하실 게임을 선택하여 주세요', msg));
            return false;
        }

        if (!confirm(String.format('선택하신 [{0}]경기를 {1} 하시겠습니까?', cnt, msg))) {
            return false;
        }

        obj.each(function () {
            gameIds.push($(this).val());
        });

        $.post(path + '/game/enabled', {
            gameIds: gameIds,
            enabled: enabled
        }).done(function (data) {
            if (data.success) {
                location.reload();
            }
        });
    },
    deleted: function (enabled) {
        var obj = $('input[name="gameId"]:checked');
        var cnt = obj.length, gameIds = [];
        var msg = enabled ? '삭제' : '복원';
        if (cnt == 0) {
            alert(String.format('{0} 하실 게임을 선택하여 주세요', msg));
            return false;
        }

        if (!confirm(String.format('선택하신 [{0}]경기를 {1} 하시겠습니까?', cnt, msg))) {
            return false;
        }

        obj.each(function () {
            gameIds.push($(this).val());
        });

        $.post(path + '/game/deleted', {
            gameIds: gameIds,
            enabled: enabled
        }).done(function (data) {
            if (data.success) {
                location.reload();
            }
        });
    },
    reload: function () {
        $.post(path + '/game/reload');
    },
    reset: function (id) {
        if (!confirm('정산 취소를 하시겠습니까?')) {
            return false;
        }

        $.post(path + '/game/reset', {
            id: id
        }).done(function (data) {
            if (data.success) {
                location.reload();
            }
        });
    }
};

var sports = {
    sortUp: function (id) {
        $.ajax({
            url: path + '/config/sports/up',
            method: 'post',
            data: {id: id}
        }).done(function () {
            location.reload();
        });
    },
    sortDown: function (id) {
        $.ajax({
            url: path + '/config/sports/down',
            method: 'post',
            data: {id: id}
        }).done(function () {
            location.reload();
        });
    },
    delete: function (id) {
        if (!confirm('삭제를 하시면 되살릴 수 없습니다.\n\n삭제를 하시겠습니까?')) {
            return;
        }
        $.ajax({
            url: path + '/config/sports/delete',
            method: 'post',
            data: {
                id: id
            }
        }).done(function () {
            location.reload();
        });
    }
};

var league = {
    delete: function (id) {
        if (!confirm('삭제를 하시면 되살릴 수 없습니다.\n\n삭제를 하시겠습니까?')) {
            return;
        }
        $.ajax({
            url: path + '/config/league/delete',
            method: 'post',
            data: {
                id: id
            }
        }).done(function () {
            location.reload();
        });
    }
};

var member = {
    enabled: function (userid, obj) {
        var obj = $(obj);
        $.ajax({
            url: path + '/member/update/enabled',
            method: 'post',
            data: {
                userid: userid
            }
        }).done(function (data) {
            if (data.value === 'Y') {
                obj.removeClass('color03').addClass('color04');
                obj.find('i').removeClass('fa-thumbs-o-down').addClass('fa-thumbs-o-up');
            } else {
                obj.removeClass('color04').addClass('color03');
                obj.find('i').removeClass('fa-thumbs-o-up').addClass('fa-thumbs-o-down');
            }
        });
    },
    black: function (userid, obj) {
        var obj = $(obj);
        $.ajax({
            url: path + '/member/update/black',
            method: 'post',
            data: {
                userid: userid
            }
        }).done(function (data) {
            if (data.value === 'Y') {
                obj.addClass('color03');
            } else {
                obj.removeClass('color03');
            }
        });
    }
};

var betting = {
    win: function (betId) {
        if (!confirm('베팅을 적중처리 하시겠습니까?')) {
            return false;
        }
        $.ajax({
            url: path + '/betting/win',
            method: 'post',
            data: {
                betId: betId
            }
        }).done(function (data) {
            if (data.success) {
                setTimeout(function () {
                    location.reload();
                }, 1000);
            }
        });
    },
    hit: function (betId) {
        if (!confirm('베팅을 적특처리 하시겠습니까?')) {
            return false;
        }
        $.ajax({
            url: path + '/betting/hit',
            method: 'post',
            data: {
                betId: betId
            }
        }).done(function (data) {
            if (data.success) {
                setTimeout(function () {
                    location.reload();
                }, 1000);
            }
        });
    },
    lose: function (betId) {
        if (!confirm('베팅을 미적처리 하시겠습니까?')) {
            return false;
        }
        $.ajax({
            url: path + '/betting/lose',
            method: 'post',
            data: {
                betId: betId
            }
        }).done(function (data) {
            if (data.success) {
                setTimeout(function () {
                    location.reload();
                }, 1000);
            }
        });
    },
    cancel: {
        bet: function (betId) {
            if (!confirm('베팅을 취소처리 하시겠습니까?')) {
                return false;
            }
            $.ajax({
                url: path + '/betting/cancel',
                method: 'post',
                data: {
                    betId: betId
                }
            }).done(function (data) {
                if (data.success) {
                    setTimeout(function () {
                        location.reload();
                    }, 1000);
                }
            });
        },
        item: function (betId, itemId) {
            if (!confirm('베팅을 취소하면 되살릴 수 없습니다.\n\n취소 하시겠습니까?')) {
                return false;
            }
            $.ajax({
                url: path + '/betting/cancel/item',
                method: 'post',
                data: {
                    betId: betId,
                    itemId: itemId
                }
            }).done(function (data) {
                if (data.success) {
                    setTimeout(function () {
                        location.reload();
                    }, 1000);
                }
            });
        }
    }
};

var auto = {
    add: function () {
        if (!confirm('고객 응대를 등록하시겠습니까?')) {
            return false;
        }

        $.ajax({
            url: path + '/customer/auto/add',
            method: 'post',
            data: {
                name: $('#name').val(),
                title: $('#title').val(),
                contents: $('#contents').val()
            }
        }).done(function (data) {
            if (data.success) {
                setTimeout(function () {
                    location.reload();
                }, 1000);
            }
        });
    },
    enabled: function (id) {
        $.ajax({
            url: path + '/customer/auto/enabled',
            method: 'post',
            data: {
                id: id
            }
        }).done(function (data) {
            if (data.success) {
                if (data.value === 'Y') {
                    $('#' + id + '-name').parent().addClass('color03');
                    $('#' + id + '-btn').addClass('btn01').text('ON');
                } else {
                    $('#' + id + '-name').parent().removeClass('color03');
                    $('#' + id + '-btn').removeClass('btn01').text('OFF');
                }
            }
        });
    },
    update: function (id) {
        $.ajax({
            url: path + '/customer/auto/update',
            method: 'post',
            data: {
                id: id,
                name: $('#' + id + '-name').val(),
                title: $('#' + id + '-title').val(),
                contents: $('#' + id + '-contents').val()
            }
        });
    },
    delete: function (id) {
        $.ajax({
            url: path + '/customer/auto/delete',
            method: 'post',
            data: {
                id: id
            }
        }).done(function (data) {
            if (data.success) {
                setTimeout(function () {
                    location.reload();
                }, 1000);
            }
        });
    }
};

var showMessage = function (msg) {
    $('#flashMessage div').html(msg);
    $('#flashMessage').removeClass('error').stop(true, true).slideDown().delay(1000).slideUp();
};

var errorMessage = function (msg) {
    $('#flashMessage div').html(msg);
    $('#flashMessage').addClass('error').stop(true, true).slideDown().delay(1000).slideUp();
};

var goPage = function (page) {
    $('#page').val(page);
    $('#frm-search').submit();
};

var goSearch = function () {
    $('#page').val(1);
    $('#frm-search').submit();
};