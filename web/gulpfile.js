'use strict';

let gulp = require('gulp'),
    stylus = require('gulp-stylus'),
    uglify = require('gulp-uglify-es').default,
    uglifycss = require('gulp-uglifycss'),
    include = require('gulp-include'),
    ngmin = require('gulp-ngmin');


let path = '../site/src/main/resources/static/';

function js() {
    return gulp.src('js/*.js')
        .pipe(include())
        .pipe(gulp.dest('public/js'))
        .pipe(uglify())
        .pipe(gulp.dest(path + 'js'));
}

function ng() {
    return gulp.src('ng/*.js')
        .pipe(gulp.dest('public/js'))
        .pipe(ngmin({dynamic: true}))
        .pipe(uglify())
        .pipe(gulp.dest(path + 'js'));
}

function css() {
    return gulp.src('css/style.styl')
        .pipe(stylus())
        .pipe(gulp.dest('public/css'))
        .pipe(uglifycss())
        .pipe(gulp.dest(path + 'css'));
}

function cssMobile() {
    return gulp.src('css/style.mobile.styl')
        .pipe(stylus())
        .pipe(gulp.dest('public/css'))
        .pipe(uglifycss())
        .pipe(gulp.dest(path + 'css'));
}

function cssAdmin() {
    return gulp.src('css/style.admin.styl')
        .pipe(stylus())
        .pipe(gulp.dest('public/css'))
        .pipe(uglifycss())
        .pipe(gulp.dest(path + 'css'));
}

gulp.task('dev', function () {
    gulp.watch('js/**/*.js', gulp.series(js));
    gulp.watch('ng/**/*.js', gulp.series(ng));
    gulp.watch('css/**/*.styl', {usePolling: true}, gulp.series(css));
    gulp.watch('css/**/*.styl', {usePolling: true}, gulp.series(cssMobile));
    gulp.watch('css/**/*.styl', {usePolling: true}, gulp.series(cssAdmin));
});