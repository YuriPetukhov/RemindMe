$(document).ready(function() {
        var urlParams = new URLSearchParams(window.location.search);
        var userId = urlParams.get('userId');

    function loadCards() {
        $.ajax({
            url: '/cards/' + userId + '/random-set',
            type: 'GET',
            dataType: 'json',
            success: function(cards) {
                var cardsHtml = '';
                cards.forEach(function(card) {
                    cardsHtml += '<div class="card">' +
                                      '<h2>' + card.content + '</h2>' +
                                      '<p>' + card.title + '</p>' +
                                      '</div>';
                });
                $('#cardsContainer').html(cardsHtml);
            },
            error: function(error) {
                console.error('Ошибка загрузки карточек:', error);
            }
        });
    }

    function showAddCardForm() {
        $('#cardsContainer').hide();
        $('#addCardFormContainer').show();
    }

    $('.menu__link').click(function(e) {
        e.preventDefault();
        var target = $(this).attr('href');
        if (target === '/cards') {
            showAddCardForm();
        } else {

        }
    });

    $('#addCardForm').submit(function(e) {
        e.preventDefault();
        var cardData = {
            title: $('#cardTitle').val(),
            content: $('#cardContent').val()
        };
        $.ajax({
             url: '/cards/' + userId,
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(cardData),
            success: function(response) {
                $('#addCardFormContainer').hide();
                loadCards();
                $('#cardsContainer').show();
            },
            error: function(error) {
                console.error('Ошибка при добавлении карточки:', error);
            }
        });
    });

    loadCards();
});
