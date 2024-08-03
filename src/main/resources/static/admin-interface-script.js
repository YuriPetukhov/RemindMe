$(document).ready(function () {
  function makeAjaxRequest(url, type, successCallback, errorCallback) {
    $.ajax({
      url: url,
      type: type,
      dataType: "json",
      success: successCallback,
      error: errorCallback,
    });
  }

  function loadStatistics() {
    makeAjaxRequest(
      "/admin/users/intervals/stats",
      "GET",
      function (data) {
        handleProgressData(data, "#statsTableBody03");
      },
      function (error) {
        console.error("Ошибка загрузки статистики:", error);
      },
    );

    makeAjaxRequest(
      "/admin/users/report",
      "GET",
      function (data) {
        handleReportData(data, "#statsTableBody04");
      },
      function (error) {
        console.error("Ошибка загрузки отчета по ошибкам:", error);
      },
    );
  }

  function loadMessages() {
    makeAjaxRequest(
      "/metrics/new-user",
      "GET",
      function (messages) {
        if (messages && messages.length > 0) {
          let messagesHtml = "";
          messages.forEach(function (message, index) {
            messagesHtml += `
                            <tr data-id="${message.id}">
                                <td><input type="checkbox" class="messageCheckbox" data-id="${message.id}"></td>
                                <td>${index + 1}</td>
                                <td>${message.message}</td>
                                <td>${message.timestamp}</td>
                                <td><button class="deleteButton" data-id="${message.id}">Delete</button></td>
                            </tr>
                        `;
          });
          $("#newUserMessage").html(messagesHtml).show();
        } else {
          $("#newUserMessage").empty().show();
        }
      },
      function (xhr, status, error) {
        console.error("Ошибка загрузки сообщений:", error);
        console.log("Status:", status);
        console.log("Response:", xhr.responseText);
        $("#newUserMessage").empty().show();
      },
    );
  }

  function deleteMessage(messageId) {
    makeAjaxRequest(
      "/metrics/new-user/" + messageId,
      "DELETE",
      function () {
        loadMessages();
      },
      function (error) {
        console.error("Ошибка удаления сообщения:", error);
      },
    );
  }

  function setupEventHandlers() {
    $("#newUserMessagesContainer").on("click", ".deleteButton", function () {
      let messageId = $(this).data("id");
      deleteMessage(messageId);
    });

    $("#deleteSelected").click(function () {
      let selectedIds = $(".messageCheckbox:checked")
        .map(function () {
          return $(this).data("id");
        })
        .get();
      selectedIds.forEach(function (id) {
        deleteMessage(id);
      });
    });

    $(".menu__link").click(function (e) {
      e.preventDefault();
      closeAllAdminContainers();

      let target = $(this).attr("href");
      if (target === "/statistics") {
        $("#usersStatsContainer").show();
        loadStatistics();
      } else if (target === "/messages") {
        $("#newUserMessagesContainer").show();
        loadMessages();
      } else if (target === "/users") {
        $("#manageUsersContainer").show();
      } else if (target === "#add-user") {
        $("#addUserFormContainer").show();
      } else if (target === "#update-user") {
        $("#updateUserFormContainer").show();
      } else if (target === "#delete-user") {
        $("#deleteUserFormContainer").show();
      } else if (target === "#get-user") {
        $("#getUserFormContainer").show();
      } else if (target === "#get-users") {
        $("#getUsersFormContainer").show();
      }
    });

    $(".tablinks").click(function (e) {
      e.preventDefault();
      let tabName = $(this).attr("href").substring(1);
      $(".tabcontent").hide();
      $("#" + tabName).show();
    });
  }

  function closeAllAdminContainers() {
    $("#adminContainer").hide();
    $("#usersStatsContainer").hide();
    $("#newUserMessagesContainer").hide();
    $("#manageUsersContainer").hide();
    $("#addUserFormContainer").hide();
    $("#updateUserFormContainer").hide();
    $("#deleteUserFormContainer").hide();
    $("#getUserFormContainer").hide();
    $("#getUsersFormContainer").hide();
  }

  function handleProgressData(progressData, tableBodyId) {
    const intervals = [
      "20 minutes",
      "1 hour",
      "4 hours",
      "8 hours",
      "24 hours",
      "48 hours",
      "96 hours",
      "14 days",
      "30 days",
      "60 days",
    ];

    let statsHtml = intervals
      .map((interval, index) => {
        const completedWords = progressData[index];
        const totalWords = progressData[progressData.length - 2];
        const percentage =
          totalWords > 0 ? (completedWords / totalWords) * 100 : 0;
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
      })
      .join("");

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
                        <div class="progress-bar" style="width: ${(finishedWords / totalWords) * 100}%; background-color: ${getProgressBarColor((finishedWords / totalWords) * 100)};">
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
      "20 minutes",
      "1 hour",
      "4 hours",
      "8 hours",
      "24 hours",
      "48 hours",
      "96 hours",
      "14 days",
      "30 days",
      "60 days",
    ];

    let tableHtml = "";
    let totalAttempts = 0;
    let totalErrors = 0;

    reportData.forEach(function (report, index) {
      const errorPercentage =
        report.attemptsCount > 0
          ? (report.errorCount / report.attemptsCount) * 100
          : 0;
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

    const totalErrorPercentage =
      totalAttempts > 0 ? (totalErrors / totalAttempts) * 100 : 0;
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
      return "green";
    } else if (percentage >= 50) {
      return "orange";
    } else if (percentage > 0) {
      return "red";
    } else {
      return "grey";
    }
  }

  function getErrorIndicatorColor(errorPercentage) {
    if (errorPercentage === 0) {
      return "green";
    } else if (errorPercentage <= 5) {
      return "orange";
    } else {
      return "red";
    }
  }

  setupEventHandlers();
  loadMessages();
});
