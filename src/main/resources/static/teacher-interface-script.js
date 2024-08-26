$(document).ready(function () {

let currentGroupId;

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
      $("#studentStatsTable").show();
//      loadStudyGroupsNamesAndIds();
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

  $(document).on("click", ".teacher-study-group", function() {
        console.log("Clicked on .teacher-study-group");
        closeAllTeacherContainers();
        let groupId = $(this).data("group-id");
        if (groupId) {
            console.log("groupId:", groupId);
            loadGroupDetailsNew(groupId);
        } else {
            console.error("groupId is undefined or null 1.");
        }
    });

  $("#editGroupButton").on("click", function() {
      $("#editGroupForm").show();
      $("#detailGroupName").hide();
      $("#detailGroupDescription").hide();
    });

  $("#saveGroupButton").on("click", function() {
      let groupId = $("#editGroupForm").data("group-id");
      saveGroupDetails(groupId);
    });

  $(document).on("click", ".select-activate-btn", function() {
        const cardSetName = $(this).data("card-set-name");

        $("#selectedCardSetName").val(cardSetName);

        $("#groupDetailsContainer").hide();
        $("#activateFormContainer").show();
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
  $("#groupDetailsContainer").hide();
  $("#activateFormContainer").hide();
  $("#studentStatsTable").hide();
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
          '<div class="teacher-card-set">' +
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
                    '<div class="teacher-study-group" data-group-id="' +
                    studyGroup.groupId +
                    '">' +
                    "<h2>" + studyGroup.groupName + "</h2>" +
                    "<p>" + studyGroup.groupDescription + "</p>" +
                    "<p>Students number: " + (studyGroup.groupSize || 0) + "</p>" +
                    "<h2>" + studyGroup.joinCode + "</h2>" +
                    "</div>";
            });
            $("#teacherStudyingGroups").html(studyGroupsHtml).show();
        },
        error: function (error) {
            console.error("Ошибка загрузки учебных групп:", error);
        },
    });
}

function loadGroupDetailsNew(groupId) {
    if (!groupId) {
        console.error("groupId is undefined or null.");
        return;
    }

    currentGroupId = groupId;

    $.ajax({
        url: "/study-group/" + groupId,
        type: "GET",
        dataType: "json",
        success: function (studyGroup) {
            $("#groupDetailsContainer").show();
            $("#detailGroupName").text(studyGroup.groupName);
            $("#detailGroupDescription").text(studyGroup.groupDescription);
            $("#detailGroupSize").text(studyGroup.groupSize || 0);
            $("#detailJoinCode").text(studyGroup.joinCode);

            const studentList = $("#studentList");
            studentList.empty();

            if (studyGroup.students && studyGroup.students.length > 0) {
                studyGroup.students.forEach((student, index) => {
                    studentList.append(`<li>${index + 1}. ${student} <button class="edit-btn">Edit</button></li>`);
                });
            } else {
                studentList.append("<li>No students available.</li>");
            }

            const cardSetList = $("#cardSetList");
            cardSetList.empty();

            if (studyGroup.cardSets && studyGroup.cardSets.length > 0) {
                studyGroup.cardSets.forEach((cardSet, index) => {
                    cardSetList.append(`
                    <li>
                         ${index + 1}. ${cardSet}
                         <button class="select-activate-btn" data-card-set-name="${cardSet}">Select to activate</button>
                         </li>`);
                });
            } else {
                cardSetList.append("<li>No card sets available.</li>");
            }

            $("#editGroupForm").hide();
            $("#editGroupName").val(studyGroup.groupName);
            $("#editGroupDescription").val(studyGroup.groupDescription);
            $("#editGroupForm").data("group-id", groupId);

            $("#addCardSetButton").off("click").on("click", function() {
                loadAvailableCardSets(groupId);
                $("#cardSetModal").show();
            });

            $("#closeModalButton").off("click").on("click", function() {
                $("#cardSetModal").hide();
            });

            $("#availableCardSets").off("click", "li").on("click", "li", function() {
                const cardSetName = $(this).text();
                addCardSetToGroup(cardSetName, groupId);
            });
        },
        error: function (error) {
            console.error("Ошибка загрузки деталей группы:", error);
        }
    });
}

function loadAvailableCardSets(groupId) {
    $.ajax({
        url: "/card-sets/all",
        type: "GET",
        dataType: "json",
        success: function (availableCardSets) {
            const availableCardSetsList = $("#availableCardSets");
            availableCardSetsList.empty();

            if (availableCardSets && availableCardSets.length > 0) {
                availableCardSets.forEach(cardSet => {
                    availableCardSetsList.append(`<li>${cardSet.setName}</li>`);
                });
            } else {
                availableCardSetsList.append(`<li>No card sets available.</li>`);
            }
        },
        error: function (error) {
            console.error("Ошибка загрузки доступных наборов карточек:", error);
        }
    });
}

function addCardSetToGroup(cardSetName, groupId) {
    $.ajax({
        url: "/study-group/add-card-set-to-group",
        type: "POST",
        dataType: "json",
        data: {
            cardSetName: cardSetName,
            groupId: groupId
        },
        success: function (response) {
            if (response.success) {
                console.log(`Card set ${cardSetName} added to group ${groupId}`);
                loadGroupDetailsNew(groupId);
            } else {
                console.error("Ошибка добавления набора карточек.");
            }
        },
        error: function (error) {
            console.error("Ошибка при добавлении набора карточек:", error);
        }
    });
}

function saveGroupDetails(groupId) {
    let updatedGroup = {
      groupName: $("#editGroupName").val(),
      groupDescription: $("#editGroupDescription").val()
    };

    $.ajax({
      url: "/study-group/" + groupId,
      type: "PUT",
      contentType: "application/json",
      data: JSON.stringify(updatedGroup),
      success: function () {
        loadTeacherStudyingGroups();
        loadGroupDetailsNew(groupId);
      },
      error: function (error) {
        console.error("Ошибка сохранения деталей группы:", error);
      },
    });
}

function activateCardSet() {
    const activationDetails = {
        cardSetName: $("#selectedCardSetName").val(),
        activationStart: $("#activationStartTeacherForm").val(),
        cardsPerBatch: $("#cardsPerBatchTeacherForm").val(),
        activationInterval: $("#activationIntervalTeacherForm").val(),
        intervalUnit: $("#intervalUnitTeacherForm").val(),
    };

    $.ajax({
        url: "/student/" + currentGroupId + "/activate-card-set",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(activationDetails),
        success: function(response) {
            console.log("Card set activated successfully.");
            $("#activateFormContainer").hide();
            $("#manageCardSetsContainer").show();
            loadTeacherCardSets()
        },
        error: function(error) {
            console.error("Ошибка при активации набора карточек:", error);
        }
    });
}

//function loadStudentsStatistics(groupId) {
//    $.ajax({
//      url: "/student/" + groupId + "/statistics",
//      type: "GET",
//      dataType: "json",
//      success: function (data) {
//        handleProgressData(data, "#statsTableBody09");
//      },
//      error: function (error) {
//        console.error("Ошибка загрузки статистики:", error);
//      },
//    });
//}

//function loadStudyGroupsNamesAndIds() {
//    $.ajax({
//        url: "/study-group/all",
//        type: "GET",
//        dataType: "json",
//        success: function(groups) {
//            const container = $("#studyGroupsButtonsContainer");
//            container.empty(); // Очистить контейнер перед добавлением новых кнопок
//
//            // Создаем кнопку для каждой группы
//            groups.forEach(function(group) {
//                const button = $('<button>')
//                    .addClass('group-button')
//                    .text(group.groupName) // Текст кнопки — название группы
//                    .attr('data-group-id', group.groupId) // Присваиваем ID группы в data атрибут
//                    .on('click', function() {
//                        const groupId = $(this).data('group-id'); // Получаем ID группы при нажатии
//                        loadGroupStatistics(groupId); // Загружаем статистику для выбранной группы
//                    });
//                container.append(button); // Добавляем кнопку в контейнер
//            });
//        },
//        error: function(error) {
//            console.error("Ошибка при загрузке групп:", error);
//        }
//    });
//}
