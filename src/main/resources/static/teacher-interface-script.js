$(document).ready(function() {
        loadFoldersAndCardSets();

        $('#createFolderForm').submit(function(e) {
                    e.preventDefault();
                    let folderName = $('#folderName').val();
                    let parentFolderId = $('#parentFolder').val();
                    createFolder(folderName, parentFolderId);
                });

        function loadFoldersAndCardSets() {
                    loadFolders();
                    loadCardSetsWithoutFolder();
                }

        function loadFolders() {
                    $.ajax({
                        url: '/folders/all',
                        type: 'GET',
                        dataType: 'json',
                        success: function(folders) {
                            let foldersHtml = generateFolderHtml(folders);
                            $('#foldersContainer').html(foldersHtml);
                            populateParentFolderOptions(folders);
                        },
                        error: function(error) {
                            console.error('Ошибка загрузки папок:', error);
                        }
                    });
                }

        function generateFolderHtml(folders, parentFolderId = null) {
                    let html = '';
                    folders.filter(folder => (folder.parentFolder ? folder.parentFolder.id : null) === parentFolderId)
                           .forEach(folder => {
                                html += '<div class="folder">' +
                                        '<h2>' + folder.folderName + '</h2>' +
                                        '<div id="cardSetsContainer_' + folder.id + '"></div>' +
                                        generateFolderHtml(folders, folder.id) +
                                        '</div>';
                                loadCardSets(folder.id);
                           });
                    return html;
                }

        function loadCardSets(folderId) {
            $.ajax({
                url: '/card-sets/all?folderId=' + folderId,
                type: 'GET',
                dataType: 'json',
                success: function(cardSets) {
                    let cardSetsHtml = '';
                    if (cardSets && cardSets.length > 0) {
                        cardSets.forEach(function(cardSet) {
                            cardSetsHtml += '<div class="card">' +
                                            '<h2>' + cardSet.setName + '</h2>' +
                                            '<p>' + cardSet.setDescription + '</p>' +
                                            '<p>Cards number: ' + cardSet.setSize + '</p>' +
                                            '</div>';
                        });
                    } else {
                        cardSetsHtml = '';
                    }
                    $('#cardSetsContainer_' + folderId).html(cardSetsHtml).show();
                },
                error: function(error) {
                    console.error('Ошибка загрузки наборов карточек:', error);
                    $('#cardSetsContainer_' + folderId).empty().show();
                }
            });
        }

        function loadCardSetsWithoutFolder() {
                    $.ajax({
                        url: '/card-sets/all',
                        type: 'GET',
                        dataType: 'json',
                        success: function(cardSets) {
                            let cardSetsHtml = '<div class="no-folder"><h2>Card Sets without Folder</h2>';
                            cardSets.forEach(function(cardSet) {
                                cardSetsHtml += '<div class="card">' +
                                                '<h2>' + cardSet.setName + '</h2>' +
                                                '<p>' + cardSet.setDescription + '</p>' +
                                                '<p>Cards number: ' + cardSet.setSize + '</p>' +
                                                '</div>';
                            });
                            cardSetsHtml += '</div>';
                            $('#noFolderCardSetsContainer').html(cardSetsHtml).show();
                        },
                        error: function(error) {
                            console.error('Ошибка загрузки наборов карточек без папки:', error);
                        }
                    });
                }

        function createFolder(folderName, parentFolderId) {
                    $.ajax({
                        url: '/folders/create' + (parentFolderId ? '?parentFolderId=' + parentFolderId : ''),
                        type: 'POST',
                        contentType: 'application/json',
                        data: JSON.stringify({ folderName: folderName }),
                        success: function(folder) {
                            loadFoldersAndCardSets();
                        },
                        error: function(error) {
                            console.error('Ошибка создания папки:', error);
                        }
                    });
                }

        function populateParentFolderOptions(folders) {
                    let optionsHtml = '<option value="">None</option>';
                    folders.forEach(folder => {
                        optionsHtml += '<option value="' + folder.id + '">' + folder.folderName + '</option>';
                    });
                    $('#parentFolder').html(optionsHtml);
                }

        function loadStudentStatistics() {
                $.ajax({
                    url: '/teacher/student/intervals/stats',
                    type: 'GET',
                    dataType: 'json',
                    success: function(data) {
                        handleProgressData(data, "#statsTableBody05");
                    },
                    error: function(error) {
                        console.error('Ошибка загрузки статистики:', error);
                    }
                });

                $.ajax({
                    url: '/teacher/students/reports',
                    type: 'GET',
                    dataType: 'json',
                    success: function(data) {
                        handleReportData(data, "#statsTableBody06");
                    },
                    error: function(error) {
                        console.error('Ошибка загрузки отчета по ошибкам:', error);
                    }
                });
            }

        $('.menu__link').click(function(e) {
                e.preventDefault();
                closeAllTeacherContainers();

                let target = $(this).attr('href');
                if (target === '/card-sets') {
                    $('#manageCardSetsContainer').show();
                    loadCardSets();
                } else if (target === '/studying-groups') {
                    $('#').show();
                } else if (target === '/students-statistics') {
                    $('#').show();
                } else if (target === '#addCardSet') {
                    $('#addCardSetFormContainer').show();
                } else if (target === '#updateCardSet') {
                    $('#updateCardSetFormContainer').show();
                } else if (target === '#deleteCardSet') {
                    $('#deleteCardSetFormContainer').show();
                }
            });

        $('.tablinks').click(function(e) {
                e.preventDefault();
                let tabName = $(this).attr('href').substring(1);
                $('.tabcontent').hide();
                $('#' + tabName).show();
            });

        $('#addCardSetForm').submit(function(e) {
                    e.preventDefault();
                    let cardSetData = {
                        setName: $('#cardSetName').val(),
                        setDescription: $('#cardSetDescription').val()
                    };

                    $.ajax({
                        url: '/card-sets',
                        type: 'POST',
                        contentType: 'application/json',
                        data: JSON.stringify(cardSetData),
                        success: function(response) {
                            $('#addCardSetForm')[0].reset();
                        },
                        error: function(error) {
                            console.error('Ошибка при добавлении набора картоек:', error);
                        }
                    });
                });

        function closeAllTeacherContainers() {
                $('#manageCardSetsContainer').hide();
                $('#addCardSetFormContainer').hide();
                $('#updateCardSetFormContainer').hide();
                $('#deleteCardSetFormContainer').hide();
                $('#cardSetsContainer').hide();
            }

        function uploadCardsTeacher() {
                        let fileInput = document.getElementById('uploadFileTeacher');
                        let file = fileInput.files[0];

                        if (!file) {
                            alert('Please select a file to upload.');
                            return;
                        }

                        let formData = new FormData();
                        formData.append('file', file);

                        fetch('/api/cards/upload', {
                            method: 'POST',
                            body: formData
                        })
                        .then(response => response.json())
                        .then(data => {
                            alert('Cards successfully uploaded');
                        })
                        .catch(error => {
                            console.error('Error uploading cards:', error);
                        });
                    }

        function loadTeacherCardSets() {
                            $.ajax({
                                url: '/card-sets/all',
                                type: 'GET',
                                dataType: 'json',
                                success: function(cardSets) {
                                    let cardSetsHtml = '';
                                    cardSets.forEach(function(cardSet) {
                                        cardSetsHtml += '<div class="card">' +
                                                        '<h2>' + cardSet.setName + '</h2>' +
                                                        '<p>' + cardSet.setDescription + '</p>' +
                                                        '<p>Cards number: ' + cardSet.setSize + '</p>' +
                                                        '</div>';
                                    });
                                    $('#teacherCardSetsContainer').html(cardSetsHtml).show();
                                },
                                error: function(error) {
                                    console.error('Ошибка загрузки наборов карточек:', error);
                                }
                            });
                        }

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

});
