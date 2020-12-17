var path = '/';
try {
    path = location.pathname.match(/^(\/.*?)\//)[1];
} catch (e) {
}

var showMessage = function (message) {
    alert(message);
};

var errorMessage = function (message) {
    alert(message);
};

$(window).on('scroll load', function(){
    if ($(window).scrollTop() >= 42) {
        $('#user-info').addClass('fixed');
    } else {
        $('#user-info').removeClass('fixed');
    }

    if ($(window).scrollTop() > 120) {
        $('#btn-top').addClass('show');
    } else {
        $('#btn-top').removeClass('show');
    }
});

$('#btn-top').on('click', function(e) {
    e.preventDefault();
    e.stopPropagation();

    $('html, body').stop().animate({
        scrollTop: 0
    }, 300);
});

var clock = $('.clock');
var now_date = parseInt($('#milliseconds').html());

function setClock() {
    now_date += 1000;
    var time = new Date(now_date);
    console.log(time);
    var hour = time.getHours();
    var minutes = time.getMinutes();
    var seconds = time.getSeconds();

    clock.html(`${setTime(hour)}<em>:</em>${setTime(minutes)}<em>:</em>${setTime(seconds)}`);
}

function setTime(m) {
    if (m < 10) m = '0' + m;
    var ms = m.toString().split('');

    return `<span>${ms[0]}</span><span>${ms[1]}</span>`
}

if (clock.length > 0) {
    function clockInit() {
        setInterval(setClock, 1000);
    }
    clockInit();
}


$(function() {
    if ($('.tab-menu-wrap').length > 0) {
        var width = ($('.tab-menu-wrap').find('a').length * 25);
        $('.tab-menu-wrap').css({
            width: width + '%'
        }).find('a').css({
            width: (100 / $('.tab-menu-wrap').find('a').length) + '%'
        });
        $('.tab-menu').scrollLeft($('.tab-menu-wrap').find('a.on').offset().left);
    }
});

$('#btn-menu').on('click', function () {
    $('#gnb').addClass('on');
});

$('#btn-close').on('click', function () {
    $('#gnb').removeClass('on');
});

$('#btn-cart').on('click', function () {
    $('#cart-wrap').addClass('on');
});

$('.cart-down').on('click', function () {
    $('#cart-wrap').removeClass('on');
});

var goPage = function (page) {
    $('#page').val(page);
    $('#frm-search').submit();
};

var goSearch = function () {
    $('#page').val(1);
    $('#frm-search').submit();
};

var iframeScale = function(x, y) {
    var w = $('main').width();
    var scale = (w / x);
    var height = y * scale;
    $('#zoneBox > div').css({
        transform: 'scale(' + scale + ')',
        height: height
    });
};


var point = {
    exchange: function () {
        if (!confirm('포인트를 머니로 전환하겠습니까?')) {
            return false;
        }

        $.post(path + '/payment/exchange').done(function (data) {
            if (data.success) {
                var money = parseInt(data.value);
                $('#user-money').text((parseInt($('#user-money').text().num()) + money).toString().money());
                $('#user-point').text(0);
            }
        });
    }
};

var payment = {
    account: function () {
        if (!confirm('계좌문의를 하시겠습니까?')) {
            return false;
        }
        $.post(path + '/customer/account').done(function (data) {
            if (data.success) {
                location.href = path + '/customer/qna';
            }
        });
    }
};

$('#lotto, #lotto2').on('click', function () {
    $(this).attr('src', '/images/lotto.gif');
    $.ajax({
        url: path + '/event/lotto',
        success: function (data) {
            setTimeout(function () {
                alert(data.message);
                if (data.success) {
                    var point = parseInt(data.value);
                    $('#user-point').text((parseInt($('#user-point').text().num(), 10) + point).toString().money());
                }
                $('#lotto').attr('src', '/images/lotto.png');
                $('#lotto2').attr('src', '/images/lotto.png');
            }, 2500)
        }
    });
});