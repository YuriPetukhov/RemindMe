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

    $('.menu__link').click(function(e) {
        e.preventDefault();
        var target = $(this).attr('href');
        $('#cardsContainer').hide();
        $('#manageCardsContainer').hide();
        $('#statsContainer').hide();
        if (target === '/cards') {
            $('#addWordFormContainer').hide();
            $('#manageCardsContainer').show();
        }
        if (target === '#add') {
            $('#addWordFormContainer').show();
        } else if (target === '/view-statistics') {
            $('#addWordFormContainer').hide();
            $('#statsContainer').show();
            loadStatistics();
        } else if (target === '#update') {
            $('#updateWordFormContainer').show();
            $('#foundCardsContainer').hide();
            $('#editCardContainer').hide();
        }
    });

    $('.tablinks').click(function(e) {
        e.preventDefault();
        var tabName = $(this).attr('href').substring(1); // Убираем '#' из href
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

    $('#updateWordForm').submit(function(e) {
        e.preventDefault();
        var cardName = $('#cardTitleToFind').val().trim();

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
                    $('#foundCardsList').html(foundCardsHtml);
                    $('#foundCardsContainer').show();

                    console.log('Found cards:', cards);

                        // Обработчик клика по найденной карточке
                        $('#foundCardsList').on('click', '.found-card', function() {
                            var cardId = $(this).data('id');
                            var card = cards.find(c => c.id == cardId);

                            $('#cardId').val(card.id);
                            $('#editCardTitle').val(card.title);
                            $('#editCardContent').val(card.content);
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


    $('#editCardForm').submit(function(e) {
        e.preventDefault();
        var cardId = $('#cardId').val();
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
                $('#editCardContainer').hide();
                $('#updateWordForm')[0].reset();
            },
            error: function(error) {
                console.error('Error updating card:', error);
            }
        });
    });

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
