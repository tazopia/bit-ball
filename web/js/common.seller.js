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