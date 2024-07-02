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
        $('#statsContainer').hide();
        $('#addWordFormContainer').show();
    }

    $('.menu__link').click(function(e) {
        e.preventDefault();
        var target = $(this).attr('href');
        if (target === '/cards') {
            showAddCardForm();
        }
    });

    $('#addWordForm').submit(function(e) {
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
                $('#addWordFormContainer').hide();
                loadCards();
                $('#cardsContainer').show();
                document.getElementById('addWordForm').reset();
            },
            error: function(error) {
                console.error('Ошибка при добавлении карточки:', error);
            }
        });
    });

   // Функция для управления отображением элементов интерфейса
   function manageUI(showStats, tableBodyId) {
     if (showStats) {
       $('.form-style').hide();
       $('#cardsContainer').hide();

       $('#statsContainer').show();

     } else {
       loadCards();
       $('#statsContainer').hide();
       $('#cardsContainer').show();
     }
   }

   // Функция для получения цвета индикатора прогресса
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

   // Функция для обработки данных прогресса
   function handleProgressData(progressData, tableBodyId) {
     const totalWords = progressData[progressData.length - 2];
     const finishedWords = progressData[progressData.length - 1];
     const intervals = [
       '20 minutes', '1 hour', '4 hours', '8 hours',
       '24 hours', '48 hours', '96 hours', '14 days',
       '30 days', '60 days'
     ];

     let statsHtml = intervals.map((interval, index) => {
       const completedWords = progressData[index];
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

     // Добавляем данные для 'Total' и 'Finished'
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
      manageUI(true, tableBodyId);
   }

   // Функция для обработки данных отчета по ошибкам
   function handleReportData(reportData, tableBodyId) {
       var tableHtml = '';
       const intervals = [
           '20 minutes', '1 hour', '4 hours', '8 hours',
           '24 hours', '48 hours', '96 hours', '14 days',
           '30 days', '60 days'
       ];
       var totalAttempts = 0;
       var totalErrors = 0;

       reportData.forEach(function(report, index) {
           // Проверка, чтобы избежать деления на ноль
           var errorPercentage = report.attemptsCount > 0 ? (report.errorCount / report.attemptsCount) * 100 : 0;
           var errorIndicatorColor = getErrorIndicatorColor(errorPercentage);
           tableHtml += '<tr>' +
                        '<td>' + intervals[index] + '</td>' +
                        '<td>' + report.attemptsCount + '</td>' +
                        '<td>' +
                           '<div class="error-indicator" style="width: ' + errorPercentage + '%; background-color: ' + errorIndicatorColor + ';">' +
                               report.errorCount + ' (' + (report.attemptsCount > 0 ? Math.round(errorPercentage) : '0') + '%)' +
                           '</div>' +
                        '</td>' +
                        '</tr>';
           // Суммирование общего количества попыток и ошибок
           totalAttempts += report.attemptsCount;
           totalErrors += report.errorCount;
       });

       // Добавление строки с общим количеством попыток и ошибок
       var totalErrorPercentage = totalAttempts > 0 ? (totalErrors / totalAttempts) * 100 : 0;
       tableHtml += '<tr>' +
                    '<td>Total</td>' +
                    '<td>' + totalAttempts + '</td>' +
                    '<td>' +
                       '<div class="error-indicator" style="width: ' + totalErrorPercentage + '%; background-color: ' + getErrorIndicatorColor(totalErrorPercentage) + ';">' +
                           totalErrors + ' (' + (totalAttempts > 0 ? Math.round(totalErrorPercentage) : '0') + '%)' +
                       '</div>' +
                    '</td>' +
                    '</tr>';

       // Добавление пустой строки для выравнивания
       tableHtml += '<tr><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>';
       $(tableBodyId).html(tableHtml);
       manageUI(true, tableBodyId);
   }


   // Функция для обработки данных stats3Data
//   function handleStats3Data (stats3Data, tableBodyId) {
//
//      manageUI(true, tableBodyId);
//   }

   // Функция для обработки данных stats4Data
//   function handleStats4Data (stats4Data, tableBodyId) {
//
//      manageUI(true, tableBodyId);
//   }

   // Функция для загрузки данных и вызова соответствующего обработчика
   function loadData(url, tableBodyId, dataHandler) {
     $.ajax({
       url: url,
       type: "GET",
       dataType: "json",
       success: function (data) {
         dataHandler(data, tableBodyId);
       },
       error: function (xhr, status, error) {
         console.error('Произошла ошибка при запросе:', status, error);
       }
     });
   }

   // Вызов функций для разных эндпойнтов с соответствующими обработчиками

$('.menu__link').click(function(e) {
    e.preventDefault();
    var target = $(this).attr('href');
    if (target === '/stats') {
        // Загрузка данных статистики
        loadData("/cards/" + userId + "/intervals/stats", "#statsTableBody01", handleProgressData);
        loadData("/monitoring/" + userId + "/report", "#statsTableBody02", handleReportData);
        //   loadData("/cards/" + userId + "/intervals/stats3", "#statsTableBody03", handleStats3Data);
        //   loadData("/cards/" + userId + "/intervals/stats4", "#statsTableBody04", handleStats4Data);
    }
    });


    loadCards();

});
