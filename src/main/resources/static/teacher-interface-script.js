$(document).ready(function () {
  $("#addCardSetForm").submit(function (e) {
    e.preventDefault();
    let cardSetData = {
      setName: $("#cardSetName").val(),
      setDescription: $("#cardSetDescription").val(),
    };

    $.ajax({
      url: "/card-sets",
      type: "POST",
      contentType: "application/json",
      data: JSON.stringify(cardSetData),
      success: function (response) {
        let cardSetId = response.cardSetId;
        uploadCardsTeacher(cardSetId);
      },
      error: function (error) {
        console.error("Ошибка при добавлении набора карточек:", error);
      },
    });
  });

  function uploadCardsTeacher(cardSetId) {
    let fileInput = document.getElementById("uploadFileTeacher");
    let file = fileInput.files[0];

    if (!file) {
      alert("Please select a file to upload.");
      return;
    }

    let formData = new FormData();
    formData.append("file", file);
    formData.append("cardSetId", cardSetId);

    fetch("/api/cards/uploadSetCards", {
      method: "POST",
      body: formData,
    })
      .then((response) => response.json())
      .then((data) => {
        alert("Cards successfully uploaded");
        $("#addCardSetForm")[0].reset();
      })
      .catch((error) => {
        console.error("Error uploading cards:", error);
      });
  }

  $("#addStudyGroupForm").submit(function (e) {
    e.preventDefault();

    let groupData = {
      groupName: $("#groupName").val(),
      groupDescription: $("#groupDescription").val(),
    };

    $.ajax({
      url: "/study-group",
      type: "POST",
      contentType: "application/json",
      data: JSON.stringify(groupData),
      success: function (response) {
        alert("Group successfully added.");
        $("#addStudyGroupForm")[0].reset();
      },
      error: function (error) {
        console.error("Ошибка при добавлении новой группы:", error);
        alert("Error adding group. Please try again.");
      },
    });
  });

  $("#createFolderForm").submit(function (e) {
    e.preventDefault();
    let folderName = $("#folderName").val();
    let parentFolderId = $("#parentFolder").val();
    createFolder(folderName, parentFolderId);
  });

  function loadStudentStatistics() {
    $.ajax({
      url: "/teacher/student/intervals/stats",
      type: "GET",
      dataType: "json",
      success: function (data) {
        handleProgressData(data, "#statsTableBody05");
      },
      error: function (error) {
        console.error("Ошибка загрузки статистики:", error);
      },
    });

    $.ajax({
      url: "/teacher/students/reports",
      type: "GET",
      dataType: "json",
      success: function (data) {
        handleReportData(data, "#statsTableBody06");
      },
      error: function (error) {
        console.error("Ошибка загрузки отчета по ошибкам:", error);
      },
    });
  }

  $(".menu__link").click(function (e) {
    e.preventDefault();
    closeAllTeacherContainers();

    let target = $(this).attr("href");
    if (target === "/card-sets") {
      $("#manageCardSetsContainer").show();
      loadTeacherCardSets();
    } else if (target === "/studying-groups") {
      $("#addStudyGroupTeacherContainer").show();
      loadTeacherStudyingGroups();
    } else if (target === "/students-statistics") {
      $("#").show();
    } else if (target === "#addCardSet") {
      $("#addCardSetFormContainer").show();
      $("#addCardSetForm").show();
    }
  });

  $(".tablinks").click(function (e) {
    e.preventDefault();
    let tabName = $(this).attr("href").substring(1);
    $(".tabcontent").hide();
    $("#" + tabName).show();
  });

  loadTeacherCardSets();
  loadTeacherStudyingGroups();
});

function closeAllTeacherContainers() {
  $("#manageCardSetsContainer").hide();
  $("#teacherCardSetsContainer").hide();
  $("#addCardSet").hide();
  $("#addCardSetForm").hide();
  $("#addStudyGroupTeacherContainer").hide();
  $("#teacherStudyingGroups").hide();
  $("#addCardSetFormContainer").hide();
  $("#updateCardSetFormContainer").hide();
  $("#deleteCardSetFormContainer").hide();
  $("#cardSetsContainer").hide();
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

function loadTeacherCardSets() {
  $.ajax({
    url: "/card-sets/all",
    type: "GET",
    dataType: "json",
    success: function (cardSets) {
      let cardSetsHtml = "";
      cardSets.forEach(function (cardSet) {
        cardSetsHtml +=
          '<div class="card">' +
          "<h2>" +
          cardSet.setName +
          "</h2>" +
          "<p>" +
          cardSet.setDescription +
          "</p>" +
          "<p>Cards number: " +
          (cardSet.setSize || 0) +
          "</p>" +
          "</div>";
      });
      $("#teacherCardSetsContainer").html(cardSetsHtml).show();
    },
    error: function (error) {
      console.error("Ошибка загрузки наборов карточек:", error);
    },
  });
}

function loadTeacherStudyingGroups() {
  $.ajax({
    url: "/study-group/all",
    type: "GET",
    dataType: "json",
    success: function (studyGroups) {
      let studyGroupsHtml = "";
      studyGroups.forEach(function (studyGroup) {
        studyGroupsHtml +=
          '<div class="card">' +
          "<h2>" +
          studyGroup.groupName +
          "</h2>" +
          "<p>" +
          studyGroup.groupDescription +
          "</p>" +
          "<p>Students number: " +
          (studyGroup.size || 0) +
          "</p>" +
          "</div>";
      });
      $("#teacherStudyingGroups").html(studyGroupsHtml).show();
    },
    error: function (error) {
      console.error("Ошибка загрузки учебных групп:", error);
    },
  });
}
