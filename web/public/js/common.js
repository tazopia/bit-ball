(function ($) {
    $.cookieOptions = {
        expires: 1,
        path: '/'
    };

    $.cookie = function (key, value, options) {
        if (arguments.length > 1) {
            var o = $.extend({}, $.cookieOptions, options);
            if (value === null || value === undefined) {
                value = '';
                o.expires = -1;
            }
            if (o.expires.constructor != Date) {
                var today = new Date();
                today.setDate(today.getDate() + o.expires);
                o.expires = today;
            }
            // Create the cookie string
            document.cookie =
                key + '=' + value +
                '; expires=' + o.expires.toUTCString() +
                (o.path ? '; path=' + (o.path) : '') +
                (o.domain ? '; domain=' + (o.domain) : '') +
                (o.secure ? '; secure' : '');
        } else {
            if (result = new RegExp(key + '=(.*?)(?:;|$)').exec(document.cookie))
                return decodeURIComponent(result[1]);
            return '';
        }
    };

    /**
     * 숫자만 입력 받기
     */
    $.fn.onlyNum = function () {
        return $(this).each(function () {
            $(this).on('keyup', function () {
                $(this).val($(this).val().num());
            });
        });
    };

    /**
     * 숫자만 입력 받기
     */
    $.fn.onlyFloat = function () {
        return $(this).each(function () {
            $(this).on('keyup', function () {
                $(this).val($(this).val().float());
            });
        });
    };

    /**
     * calendar
     */
    $.fn.calendar = function () {
        $(this).prop({readonly: 'readonly'}).datepicker({
            dateFormat: 'yy.mm.dd',
            monthNames: ['01.', '02.', '03.', '04.', '05.', '06.', '07.', '08.', '09.', '10.', '11.', '12.'],
            dayNamesMin: ['일', '월', '화', '수', '목', '금', '토']
        });
    };

    $.num2han = function (num) {
        var num = $.trim(num.toString());
        var n = ['일', '이', '삼', '사', '오', '육', '칠', '팔', '구', '십'];
        var p = ['', '십', '백', '천'];
        var u = ['', '만', '억', '조', '경'];
        var pos = num.length % 4;
        var unit = (pos == 0) ? num.length / 4 - 1 : parseInt(num.length / 4);
        var han = '', op = 0;

        for (var i = 0; i < num.length; i++) {
            if (pos == 0) pos = 4;
            var j = parseInt(num.substring(i, i + 1));
            if (j != 0) {
                han += ' ' + n[j - 1];
                han += p[pos - 1];
                op = 1;
            }
            if (pos == 1) {
                if (op == 1) han += u[unit];
                unit--;
                op = 0;
            }
            pos--;
        }
        return han;
    };

    String.prototype.trim = function () {
        return this.replace(/(^\s+)|(\s+$)/g, '');
    };

    // 숫자만 가져오기
    String.prototype.num = function () {
        return this.trim().replace(/[^0-9]/g, '');
    };

    // 숫자형 (더블, 플랫)만 가져 오기
    String.prototype.float = function () {
        return this.trim().replace(/[^0-9\\.\\-]/g, '');
    };

    String.prototype.userid = function () {
        return this.trim().replace(/[^0-9a-zA-Z]/g, '');
    };

    String.prototype.nickname = function () {
        return this.trim().replace(/[^0-9a-zA-Zㄱ-힣]/g, '');
    };

    // 화폐형으로 3자리 마다 , 찍기
    String.prototype.money = function () {
        var num = this.trim();
        while (/(-?[0-9]+)([0-9]{3})/.test(num)) {
            num = num.replace(/(-?[0-9]+)([0-9]{3})/, '$1,$2');
        }
        return num;
    };

    // 앞자리 채우기
    String.prototype.prefix = function (value, size) {
        var size = size || 2;
        var value = typeof(value) === 'string' ? value : '0';
        while (value.length < size) {
            value = +'0';
        }
        return value;
    };

    // 숫자형인지 확인
    String.prototype.isNum = function () {
        return (/[0-9]/g).test(this);
    };

    // 휴대폰
    String.prototype.isMobile = function () {
        return /01[016789][1-9][0-9]{2,3}[0-9]{4}$/.test(this.num());
    };

    // ID 첫글자는 영문이고 나머지는 영문과 숫자
    String.prototype.isId = function () {
        return /^[0-9a-zA-Z]{4,}$/.test(this);
    };

    if (!String.format) {
        String.format = function (format) {
            var args = Array.prototype.slice.call(arguments, 1);
            return format.replace(/{(\d+)}/g, function (match, number) {
                return typeof args[number] != 'undefined'
                    ? args[number]
                    : match;
            });
        };
    }

})(jQuery);


(function ($) {
    $('body').on('click', 'a[href="#"]', function (e) {
        e.preventDefault();
        e.stopPropagation();
    });

    $.ajaxSetup({
        method: 'post',
        dataType: 'json',
        async: false,
        traditional: true,
        cache: false,
        beforeSend: function (xhr) {
            xhr.setRequestHeader('AJAX', true);
        },
        success: function (data) {
            if (data.message !== undefined && data.message !== '') {
                data.success ? showMessage(data.message) : errorMessage(data.message);
            }
        },
        error: function (xhr) {
            if (xhr.status === 401 || xhr.status === 403) {
                top.location.href = '/logout';
            } else if (xhr.status === 404) {
                alert('요청한 페이지를 찾을 수 없습니다.');
            } else {
                alert('현재요청을 수행할 수 없습니다.\n\n잠시 후 다시 이용하세요.')
            }
        }
    });
})(jQuery);
var clicked = false;