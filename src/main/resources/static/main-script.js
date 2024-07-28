document.addEventListener('DOMContentLoaded', function() {
    currentInterface = localStorage.getItem('currentInterface') || 'userInterface';
    loadUserRoles();
});

let userRoles = [];
let currentInterface = 'userInterface';

function loadUserRoles() {
    $.ajax({
        url: '/api/user-roles',
        method: 'GET',
        success: function(data) {
            userRoles = data;
            console.log('Roles loaded:', userRoles);
            updateInterface();
        },
        error: function(error) {
            if (error.status === 401) {
                window.location.href = '/auto-login';
            } else {
                console.error('Error fetching user roles:', error);
            }
        }
    });
}

function updateInterface() {
    hideAllInterfaces();
    showCurrentInterface();
    loadMenuLinks();
}

function hideAllInterfaces() {
    var interfaces = document.getElementsByClassName('interface');
    for (var i = 0; i < interfaces.length; i++) {
        interfaces[i].style.display = 'none';
    }
}

function showCurrentInterface() {
    if (document.getElementById(currentInterface)) {
        $('#' + currentInterface).show();
    } else {
        currentInterface = 'userInterface';
        $('#userInterface').show();
    }
}

function loadInterfaces() {
    if (userRoles.includes('ROLE_ADMIN')) {
        $('#adminInterface').show();
        currentInterface = 'adminInterface';
    } else if (userRoles.includes('ROLE_TEACHER')) {
        $('#teacherInterface').show();
        currentInterface = 'teacherInterface';
    } else if (userRoles.includes('ROLE_STUDENT')) {
        $('#studentInterface').show();
        currentInterface = 'studentInterface';
    } else if (userRoles.includes('ROLE_USER')) {
        $('#userInterface').show();
        currentInterface = 'userInterface';
    } else {
        $('#userInterface').show();
        currentInterface = 'userInterface';
    }

    localStorage.setItem('currentInterface', currentInterface);
}

var socket = new SockJS('/ws');
var stompClient = Stomp.over(socket);

stompClient.connect({}, function (frame) {
    console.log('Connected: ' + frame);

    var userId = /*[[${userId}]]*/ 'default';

    stompClient.subscribe('/topic/roles/' + userId, function (message) {
        console.log('Role update received:', message.body);
        fetchRoles();
    });
}, function (error) {
    console.error('STOMP error: ', error);
    if (error.headers && error.headers.message === '401 Unauthorized') {
        window.location.href = '/auto-login';
    }
});

function fetchRoles() {
    $.ajax({
        url: '/api/user-roles',
        method: 'GET',
        success: function(data) {
            userRoles = data;
            console.log('Roles updated:', userRoles);
            updateInterface();
        },
        error: function(error) {
            console.error('Error fetching user roles:', error);
        }
    });
}

function loadMenuLinks() {
    $('.menuList').empty();

    if (userRoles.includes('ROLE_ADMIN') && currentInterface !== 'adminInterface') {
        $('.menuList').append('<li class="menu__list"><a href="#adminInterface" class="interface__link">Admin Interface</a></li>');
    }
    if (userRoles.includes('ROLE_TEACHER') && currentInterface !== 'teacherInterface') {
        $('.menuList').append('<li class="menu__list"><a href="#teacherInterface" class="interface__link">Teacher Interface</a></li>');
    }
    if (userRoles.includes('ROLE_STUDENT') && currentInterface !== 'studentInterface') {
        $('.menuList').append('<li class="menu__list"><a href="#studentInterface" class="interface__link">Student Interface</a></li>');
    }
    if (userRoles.includes('ROLE_USER') && currentInterface !== 'userInterface') {
        $('.menuList').append('<li class="menu__list"><a href="#userInterface" class="interface__link">User Interface</a></li>');
    }
}

$(document).on('click', '.interface__link', function(e) {
    e.preventDefault();
    let target = $(this).attr('href').substring(1);
    if (document.getElementById(target)) {
        hideAllInterfaces();
        $('#' + target).show();
        currentInterface = target;
        loadMenuLinks();

        localStorage.setItem('currentInterface', currentInterface);
    }
});
