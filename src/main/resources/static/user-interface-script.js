$(document).ready(function() {
        let currentCard;

        function loadCards() {
            $.ajax({
                url: '/cards/random-set',
                type: 'GET',
                dataType: 'json',
                success: function(cards) {

                if (cards && cards.length > 0) {
                    let cardsHtml = '';
                    cards.forEach(function(card) {
                        cardsHtml += '<div class="card" data-id="' + card.id + '">' +
                                      '<h2>' + card.content + '</h2>' +
                                      '<p>' + card.title + '</p>' +
                                      '</div>';
                    });
                    $('#cardsContainer').html(cardsHtml).show();
                } else {
                    $('#cardsContainer').empty();
                }


                },
                error: function(error) {
                    console.error('Ошибка загрузки карточек:', error);
                }
            });
        }

//        function loadCardSet(cardSetId) {
//                $.ajax({
//                    url: '/card-sets/' + cardSetId,
//                    type: 'GET',
//                    dataType: 'json',
//                    success: function(cardSet) {
//                        currentCardSet = cardSet;
//                        $('#editCardSetName').val(cardSet.setName);
//                        $('#editCardSetDescription').val(cardSet.setDescription);
//                        $('#editCardSetSize').text(cardSet.setSize);
//                        closeAllContainers();
//                        $('#addWordFormContainer').show();
//                        $('#editCardSetContainer').show();
//                        $('#userCardSetCardsContainer').show();
//                        $('#editCardSetForm').show();
//                    },
//                    error: function(error) {
//                        console.error('Ошибка загрузки набора карточек:', error);
//                    }
//                });
//            }

        function loadStatistics() {
            $.ajax({
                url: '/cards/intervals/stats',
                type: 'GET',
                dataType: 'json',
                success: function(data) {
                    if (data && data.length > 0) {
                        handleProgressData(data, "#statsTableBody01");
                    } else {
                        $('#statsTableBody01').empty();
                    }
                },
                error: function(error) {
                    console.error('Ошибка загрузки статистики:', error);
                }
            });

            $.ajax({
                url: '/monitoring/report',
                type: 'GET',
                dataType: 'json',
                success: function(data) {
                    if (data && data.length > 0) {
                        handleReportData(data, "#statsTableBody02");
                    } else {
                        $('#statsTableBody02').empty();
                    }
                },
                error: function(error) {
                    console.error('Ошибка загрузки отчета по ошибкам:', error);
                }
            });
        }

        function loadRandomCard() {
        $.ajax({
            url: '/cards/random-card',
            type: 'GET',
            dataType: 'json',
            success: function(card) {
                if (card && Object.keys(card).length > 0) {
                    currentCard = card;
                    $('#randomCardContent').text(currentCard.content);
                    $('#resultMessage').html('');
                    $('#userAnswer').val('');
                } else {
                    $('#randomCardContent').text('');
                    $('#resultMessage').html('');
                    $('#userAnswer').val('');
                }
            },
            error: function(jqXHR, textStatus, errorThrown) {
                console.error('Error loading card:', errorThrown);
            }
        });
    }

        $('.menu__link').click(function(e) {
        e.preventDefault();
        closeAllContainers();

        let target = $(this).attr('href');
        if (target === '/cards') {
            $('#manageCardsContainer').show();
        } else if (target === '#add') {
            $('#addWordFormContainer').show();
            $('#addWordForm').show();
            $('#uploadFileForm').show();
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
        } else if (target === '/settings') {
            $('#settingsContainer').show();
        }
    });

        $('.tablinks').click(function(e) {
            e.preventDefault();
            let tabName = $(this).attr('href').substring(1);
            $('.tabcontent').hide();
            $('#' + tabName).show();
        });


//        $(document).on('click', '.card-set', function() {
//            const cardSetId = $(this).data('set-id');
//            loadCardSet(cardSetId);
//            loadUserCardSetCards(cardSetId);
//        });

        $(document).on('click', '.card', function() {
            const cardId = $(this).data('id');
            loadCard(cardId);
        });

        function loadCard(cardId) {
            $.ajax({
                url: '/cards/' + cardId,
                type: 'GET',
                dataType: 'json',
                success: function(card) {
                    currentCard = card;
                    $('#cardIdUpdateUser').val(card.id);
                    $('#editCardSetCardTitle').val(card.title);
                    $('#editCardSetCardContent').val(card.content);
                    $('#activationStartCard').val(card.reminderDateTime);
                    $('#userCardSetCardsContainer').hide();
                    $('#editCardSetForm').hide();
                    $('#editCardSetContainer').show();
                    $('#editCardSetCardContainer').show();
                },
                error: function(error) {
                    console.error('Ошибка загрузки карточки:', error);
                }
            });
        }

        $('#addWordForm').submit(function(e) {
            e.preventDefault();
            let cardData = {
                title: $('#cardTitleAdd').val(),
                content: $('#cardContentAdd').val()
            };

            $.ajax({
                url: '/cards',
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
        let cardName = $('#cardTitleToFindUpdate').val().trim();

        $.ajax({
            url: '/cards/card-name',
            type: 'GET',
            data: { cardName: cardName },
            success: function(cards) {
                if (cards.length > 0) {
                    let foundCardsHtml = '';
                    cards.forEach(function(card) {
                        foundCardsHtml += '<div class="card found-card" data-id="' + card.id + '">' +
                                          '<h2>' + card.title + '</h2>' +
                                          '<p>' + card.content + '</p>' +
                                          '</div>';
                    });
                    $('#foundCardsListUpdate').html(foundCardsHtml);
                    $('#foundCardsContainerUpdate').show();

                    $('.found-card').click(function() {
                        let cardId = $(this).data('id');
                        let card = cards.find(c => c.id == cardId);

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
        let cardName = $('#cardTitleToFindDelete').val().trim();

        $.ajax({
            url: '/cards/card-name',
            type: 'GET',
            data: { cardName: cardName },
            success: function(cards) {
                if (cards.length > 0) {
                    let foundCardsHtml = '';
                    cards.forEach(function(card) {
                        foundCardsHtml += '<div class="card found-card" data-id="' + card.id + '">' +
                                          '<h2>' + card.title + '</h2>' +
                                          '<p>' + card.content + '</p>' +
                                          '</div>';
                    });
                    $('#foundCardsListDelete').html(foundCardsHtml);
                    $('#foundCardsContainerDelete').show();

                    $('.found-card').click(function() {
                        let cardId = $(this).data('id');
                        let card = cards.find(c => c.id == cardId);

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
            let cardId = $('#cardIdUpdate').val();
            let updatedCard = {
                title: $('#editCardTitle').val(),
                content: $('#editCardContent').val()
            };

            $.ajax({
                url: '/cards/' + cardId,
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
            let cardId = $('#cardIdDelete').val();

            $.ajax({
                url: '/cards/' + cardId,
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
            let userAnswer = $('#userAnswer').val().trim();

            if (userAnswer.toLowerCase() === currentCard.title.toLowerCase()) {
                $('#resultMessage').html('<p style="color: green;">Correct!</p>');
            } else {
                $('#resultMessage').html('<p style="color: red;">Incorrect! The correct answer is: ' + currentCard.title + '</p>');
            }
        });

        $('#nextCardButton').click(function() {
            loadRandomCard();
        });

//        $('#editCardSetForm').submit(function(e) {
//        e.preventDefault();
//        const updatedCardSet = {
//            setName: $('#editCardSetName').val(),
//            setDescription: $('#editCardSetDescription').val()
//        };

//        $.ajax({
//            url: '/card-sets/' + currentCardSet.id,
//            type: 'PUT',
//            contentType: 'application/json',
//            data: JSON.stringify(updatedCardSet),
//            success: function(response) {
//                alert('Набор карточек успешно обновлен');
//                closeAllContainers()
//            },
//            error: function(error) {
//                console.error('Ошибка обновления набора карточек:', error);
//            }
//        });
//    });

        applyTheme();

        document.querySelectorAll('.theme-button').forEach(button => {
        button.addEventListener('click', function() {
            let theme = this.getAttribute('data-theme');
            changeTheme(theme);
        });
    });

        loadRandomCard();

        loadCards();

//        loadUserCardSets();

        function changeTheme(theme) {
                localStorage.setItem('theme', theme);
                applyTheme();
            }

        function applyTheme() {
                let theme = localStorage.getItem('theme') || 'light';
                document.body.setAttribute('data-theme', theme);
                updateButtonState(theme);
            }

        function updateButtonState(theme) {
                let buttons = document.querySelectorAll('.theme-button');
                buttons.forEach(button => {
                    if (button.getAttribute('data-theme') === theme) {
                        button.classList.add('active');
                    } else {
                        button.classList.remove('active');
                    }
                });
            }

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

//function loadUserCardSets() {
//                $.ajax({
//                    url: '/card-sets/all',
//                    type: 'GET',
//                    dataType: 'json',
//                    success: function(cardSets) {
//                        let cardSetsHtml = '';
//                        cardSets.forEach(function(cardSet) {
//                            const cardClass = cardSet.active ? 'active-card-set' : 'inactive-card-set';
//                            cardSetsHtml += '<div class="card-set ' + cardClass + '" data-set-id="' + cardSet.id + '">' +
//                                            '<h2>' + cardSet.setName + '</h2>' +
//                                            '<p>' + cardSet.setDescription + '</p>' +
//                                            '<p>Cards number: ' + cardSet.setSize + '</p>' +
//                                            '</div>';
//                        });
//                        $('#userCardSetsContainer').html(cardSetsHtml).show();
//                        $('#addWordFormContainer').show();
//                        $('#addWordForm').show();
//                        $('#uploadFileForm').show();
//                    },
//                    error: function(error) {
//                        console.error('Ошибка загрузки наборов карточек:', error);
//                    }
//                });
//            }

//function loadUserCardSetCards(cardSetId) {
//        $.ajax({
//            url: '/card-sets/' + cardSetId + '/cards',
//            type: 'GET',
//            dataType: 'json',
//            success: function(cards) {
//                if (cards && cards.length > 0) {
//                    let cardsHtml = '';
//                    cards.forEach(function(card) {
//                        cardsHtml += '<div class="card" data-id="' + card.id + '">' +
//                                      '<h2>' + card.content + '</h2>' +
//                                      '<p>' + card.title + '</p>' +
//                                      '</div>';
//                    });
//                    $('#userCardSetCardsContainer').html(cardsHtml).show();
//                } else {
//                    $('#userCardSetCardsContainer').empty();
//                }
//            },
//            error: function(error) {
//                console.error('Ошибка загрузки карточек:', error);
//            }
//        });
//    }

function uploadCards() {
            const formData = new FormData();
            const fileInput = document.getElementById('uploadFile');
//            const cardSetName = document.getElementById('cardSetName').value;
//            const setDescription = document.getElementById('setDescription').value;
            const activationStart = document.getElementById('activationStart').value;
            const cardsPerBatch = document.getElementById('cardsPerBatch').value;
            const activationInterval = document.getElementById('activationInterval').value;
            const intervalUnit = document.getElementById('intervalUnit').value;

            formData.append('file', fileInput.files[0]);
//            formData.append('cardSetName', cardSetName);
//            formData.append('setDescription', setDescription);

            formData.append('activationStart', activationStart);
            formData.append('cardsPerBatch', cardsPerBatch);
            formData.append('activationInterval', activationInterval);
            formData.append('intervalUnit', intervalUnit);


            fetch('/api/cards/upload', {
                method: 'POST',
                body: formData
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                console.log('Success:', data);
                alert('Cards successfully uploaded: ' + data.message);
            })
            .catch(error => {
                console.error('Error uploading cards:', error);
                alert('Error uploading cards: ' + error.message);
            });
        }

function closeAllContainers() {
                    $('#cardsContainer').hide();
                    $('#manageCardsContainer').hide();
                    $('#statsContainer').hide();
                    $('#userCardSetsContainer').hide();
                    $('#updateWordFormContainer').hide();
                    $('#deleteWordFormContainer').hide();
                    $('#foundCardsContainerUpdate').hide();
                    $('#editCardContainer').hide();
                    $('#foundCardsContainerDelete').hide();
                    $('#deleteCardContainer').hide();
                    $('#practiseContainer').hide();
                    $('#infoContainer').hide();
                    $('#settingsContainer').hide();
                    $('#editCardSetForm').hide();
                    $('#editCardSetCardContainer').hide();
                    $('#loadUserCardSetCards').hide();
                    $('#userCardSetCardsContainer').hide();
                    $('#addWordFormContainer').hide();
                    $('#addWordForm').hide();
                    $('#uploadFileForm').hide();

                }

let socket2 = new SockJS('/ws/recall');
let stompClient2 = Stomp.over(socket2);

stompClient2.connect({}, function (frame) {
    console.log('Connected: ' + frame);

    stompClient2.subscribe('/topic/recall', function (message) {
        console.log("New message received:", message.body);
        displayMessage(message.body);
    });
});

function displayMessage(message) {
    localStorage.setItem('lastMessage', message);

    let messageButton = document.createElement('button');
    messageButton.textContent = message;
    messageButton.addEventListener('click', function() {
        showResponseModal(message);
    });

    document.getElementById('chat').innerHTML = '';
    document.getElementById('chat').appendChild(messageButton);
}

function showResponseModal(message) {
    document.getElementById('responseMessage').innerHTML = message;
    document.getElementById('responseModal').style.display = 'block';
}

document.getElementById('submitResponse').addEventListener('click', function() {
    const response = document.getElementById('responseInput').value;
    // Отправка ответа на сервер
    console.log('Отправлен ответ:', response);
    document.getElementById('responseModal').style.display = 'none';
    document.getElementById('responseInput').value = '';
});

document.getElementById('cancelResponse').addEventListener('click', function() {
    document.getElementById('responseModal').style.display = 'none';
    document.getElementById('responseInput').value = '';
});

window.onload = function() {
    const lastMessage = localStorage.getItem('lastMessage');
    if (lastMessage) {
        displayMessage(lastMessage);
    }
};
