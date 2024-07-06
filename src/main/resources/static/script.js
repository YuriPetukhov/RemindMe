$(document).ready(function() {
    var urlParams = new URLSearchParams(window.location.search);
    var userId = urlParams.get('userId');
    var currentCard;

    if (!userId) {
        alert('userId is missing in URL. Please provide a valid userId.');
        return;
    }

    function loadCards() {
        $.ajax({
            url: '/cards/' + userId + '/random-set',
            type: 'GET',
            dataType: 'json',
            success: function(cards) {
                var cardsHtml = '';
                cards.forEach(function(card) {
                    cardsHtml += '<div class="card">' +
                                  '<h2>' + card.title + '</h2>' +
                                  '<p>' + card.content + '</p>' +
                                  '</div>';
                });
                $('#cardsContainer').html(cardsHtml).show();
            },
            error: function(error) {
                console.error('Ошибка загрузки карточек:', error);
            }
        });
    }

    function loadStatistics() {
        $.ajax({
            url: '/cards/' + userId + '/intervals/stats',
            type: 'GET',
            dataType: 'json',
            success: function(data) {
                handleProgressData(data, "#statsTableBody01");
            },
            error: function(error) {
                console.error('Ошибка загрузки статистики:', error);
            }
        });

        $.ajax({
            url: '/monitoring/' + userId + '/report',
            type: 'GET',
            dataType: 'json',
            success: function(data) {
                handleReportData(data, "#statsTableBody02");
            },
            error: function(error) {
                console.error('Ошибка загрузки отчета по ошибкам:', error);
            }
        });
    }

    function loadRandomCard() {
            $.ajax({
                url: '/cards/' + userId + '/random-card',
                type: 'GET',
                dataType: 'json',
                success: function(card) {
                    if (card) {
                        currentCard = card;
                        $('#randomCardContent').text(currentCard.content);
                        $('#resultMessage').html('');
                        $('#userAnswer').val('');
                    } else {
                        $('#randomCardContent').text('No cards available.');
                    }
                },
                error: function(error) {
                    console.error('Error loading card:', error);
                }
            });
        }

    $('.menu__link').click(function(e) {
    e.preventDefault();
    closeAllContainers();

    var target = $(this).attr('href');
    if (target === '/cards') {
        $('#manageCardsContainer').show();
    } else if (target === '#add') {
        $('#addWordFormContainer').show();
    } else if (target === '/view-statistics') {
        $('#statsContainer').show();
        loadStatistics();
    } else if (target === '#update') {
        $('#updateWordFormContainer').show();
    } else if (target === '#delete') {
        $('#deleteWordFormContainer').show();
    } else if (target === '/practise') {
        $('#practiseContainer').show();
    } else if (target === '/info') {
        $('#infoContainer').show();
    }
});

    $('.tablinks').click(function(e) {
        e.preventDefault();
        var tabName = $(this).attr('href').substring(1);
        $('.tabcontent').hide();
        $('#' + tabName).show();
    });

    $('#addWordForm').submit(function(e) {
        e.preventDefault();
        var cardData = {
            title: $('#cardTitleAdd').val(),
            content: $('#cardContentAdd').val()
        };

        $.ajax({
            url: '/cards/' + userId,
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(cardData),
            success: function(response) {
                loadCards();
                $('#addWordForm')[0].reset();
            },
            error: function(error) {
                console.error('Ошибка при добавлении карточки:', error);
            }
        });
    });

    $('#findWordFormUpdate').submit(function(e) {
    e.preventDefault();
    var cardName = $('#cardTitleToFindUpdate').val().trim();

    $.ajax({
        url: '/cards/' + userId + '/card-name',
        type: 'GET',
        data: { cardName: cardName },
        success: function(cards) {
            if (cards.length > 0) {
                var foundCardsHtml = '';
                cards.forEach(function(card) {
                    foundCardsHtml += '<div class="card found-card" data-id="' + card.id + '">' +
                                      '<h2>' + card.title + '</h2>' +
                                      '<p>' + card.content + '</p>' +
                                      '</div>';
                });
                $('#foundCardsListUpdate').html(foundCardsHtml);
                $('#foundCardsContainerUpdate').show();

                $('.found-card').click(function() {
                    var cardId = $(this).data('id');
                    var card = cards.find(c => c.id == cardId);

                    $('#editCardForm #cardIdUpdate').val(cardId);
                    $('#editCardForm #editCardTitle').val(card.title);
                    $('#editCardForm #editCardContent').val(card.content);
                    $('#editCardContainer').show();
                });
            } else {
                alert('Card not found');
            }
        },
        error: function(error) {
            console.error('Error finding card:', error);
        }
    });
});

    $('#findWordFormDelete').submit(function(e) {
    e.preventDefault();
    var cardName = $('#cardTitleToFindDelete').val().trim();

    $.ajax({
        url: '/cards/' + userId + '/card-name',
        type: 'GET',
        data: { cardName: cardName },
        success: function(cards) {
            if (cards.length > 0) {
                var foundCardsHtml = '';
                cards.forEach(function(card) {
                    foundCardsHtml += '<div class="card found-card" data-id="' + card.id + '">' +
                                      '<h2>' + card.title + '</h2>' +
                                      '<p>' + card.content + '</p>' +
                                      '</div>';
                });
                $('#foundCardsListDelete').html(foundCardsHtml);
                $('#foundCardsContainerDelete').show();

                $('.found-card').click(function() {
                    var cardId = $(this).data('id');
                    var card = cards.find(c => c.id == cardId);

                    $('#deleteCardForm #cardIdDelete').val(cardId);
                    $('#deleteCardForm #deleteCardTitle').val(card.title);
                    $('#deleteCardContainer').show();
                });
            } else {
                alert('Card not found');
            }
        },
        error: function(error) {
            console.error('Error finding card:', error);
        }
    });
});

    $('#editCardForm').submit(function(e) {
        e.preventDefault();
        var cardId = $('#cardIdUpdate').val();
        var updatedCard = {
            title: $('#editCardTitle').val(),
            content: $('#editCardContent').val()
        };

        $.ajax({
            url: '/cards/' + userId + '/' + cardId,
            type: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify(updatedCard),
            success: function(response) {
                alert('Card updated successfully');
                $('#editCardForm')[0].reset();
                $('#editCardContainerUpdate').hide();
                $('#foundCardsContainerUpdate').hide();
            },
            error: function(error) {
                console.error('Error updating card:', error);
            }
        });
    });

    $('#deleteCardForm').submit(function(e) {
        e.preventDefault();
        var cardId = $('#cardIdDelete').val();

        $.ajax({
            url: '/cards/' + userId + '/' + cardId,
            type: 'DELETE',
            success: function(response) {
                alert('Card deleted successfully');
                $('#deleteCardForm')[0].reset();
                $('#deleteCardContainer').hide();
                $('#foundCardsContainer').hide();
            },
            error: function(error) {
                console.error('Error deleting card:', error);
            }
        });
    });

    $('#practiseForm').submit(function(e) {
        e.preventDefault();
        var userAnswer = $('#userAnswer').val().trim();

        if (userAnswer.toLowerCase() === currentCard.title.toLowerCase()) {
            $('#resultMessage').html('<p style="color: green;">Correct!</p>');
        } else {
            $('#resultMessage').html('<p style="color: red;">Incorrect! The correct answer is: ' + currentCard.title + '</p>');
        }
    });

    $('#nextCardButton').click(function() {
        loadRandomCard();
    });

    loadRandomCard();

    loadCards();
});

function handleProgressData(progressData, tableBodyId) {
    const intervals = [
        '20 minutes', '1 hour', '4 hours', '8 hours',
        '24 hours', '48 hours', '96 hours', '14 days',
        '30 days', '60 days'
    ];

    let statsHtml = intervals.map((interval, index) => {
        const completedWords = progressData[index];
        const totalWords = progressData[progressData.length - 2];
        const percentage = totalWords > 0 ? (completedWords / totalWords) * 100 : 0;
        const progressBarColor = getProgressBarColor(percentage);
        return `
            <tr>
                <td>${interval}</td>
                <td>
                    <div class="progress-bar-container">
                        <div class="progress-bar" style="width: ${percentage}%; background-color: ${progressBarColor};">
                            ${Math.round(percentage)}% (${completedWords}/${totalWords})
                        </div>
                    </div>
                </td>
            </tr>
        `;
    }).join('');

    const totalWords = progressData[progressData.length - 2];
    const finishedWords = progressData[progressData.length - 1];
    statsHtml += `
        <tr>
            <td>Total</td>
            <td>
                <div class="progress-bar-container">
                    <div class="progress-bar" style="width: 100%; background-color: green;">${totalWords}</div>
                </div>
            </td>
        </tr>
        <tr>
            <td>Finished</td>
            <td>
                <div class="progress-bar-container">
                    <div class="progress-bar" style="width: ${finishedWords / totalWords * 100}%; background-color: ${getProgressBarColor(finishedWords / totalWords * 100)};">
                        ${finishedWords}
                    </div>
                </div>
            </td>
        </tr>
    `;

    $(tableBodyId).html(statsHtml);
}

function handleReportData(reportData, tableBodyId) {
    const intervals = [
        '20 minutes', '1 hour', '4 hours', '8 hours',
        '24 hours', '48 hours', '96 hours', '14 days',
        '30 days', '60 days'
    ];

    let tableHtml = '';
    let totalAttempts = 0;
    let totalErrors = 0;

    reportData.forEach(function(report, index) {
        const errorPercentage = report.attemptsCount > 0 ? (report.errorCount / report.attemptsCount) * 100 : 0;
        const errorIndicatorColor = getErrorIndicatorColor(errorPercentage);
        tableHtml += `
            <tr>
                <td>${intervals[index]}</td>
                <td>${report.attemptsCount}</td>
                <td>
                    <div class="error-indicator" style="width: ${errorPercentage}%; background-color: ${errorIndicatorColor};">
                        ${report.errorCount} (${Math.round(errorPercentage)}%)
                    </div>
                </td>
            </tr>
        `;
        totalAttempts += report.attemptsCount;
        totalErrors += report.errorCount;
    });

    const totalErrorPercentage = totalAttempts > 0 ? (totalErrors / totalAttempts) * 100 : 0;
    tableHtml += `
        <tr>
            <td>Total</td>
            <td>${totalAttempts}</td>
            <td>
                <div class="error-indicator" style="width: ${totalErrorPercentage}%; background-color: ${getErrorIndicatorColor(totalErrorPercentage)};">
                    ${totalErrors} (${Math.round(totalErrorPercentage)}%)
                </div>
            </td>
        </tr>
    `;

    $(tableBodyId).html(tableHtml);
}

function getProgressBarColor(percentage) {
    if (percentage >= 75) {
        return 'green';
    } else if (percentage >= 50) {
        return 'orange';
    } else if (percentage > 0) {
        return 'red';
    } else {
        return 'grey';
    }
}

function getErrorIndicatorColor(errorPercentage) {
    if (errorPercentage === 0) {
        return 'green';
    } else if (errorPercentage <= 5) {
        return 'orange';
    } else {
        return 'red';
    }
}

function closeAllContainers() {
    $('#cardsContainer').hide();
    $('#manageCardsContainer').hide();
    $('#statsContainer').hide();
    $('#addWordFormContainer').hide();
    $('#updateWordFormContainer').hide();
    $('#deleteWordFormContainer').hide();
    $('#foundCardsContainerUpdate').hide();
    $('#editCardContainer').hide();
    $('#foundCardsContainerDelete').hide();
    $('#deleteCardContainer').hide();
    $('#practiseContainer').hide();
    $('#infoContainer').hide();
}
