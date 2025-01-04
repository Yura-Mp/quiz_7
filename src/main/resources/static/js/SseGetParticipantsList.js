window.onload = function () {
  var roomID = new URLSearchParams(window.location.search).get('room');
  // 参加者リスト更新用イベントソース
  var SSEparticipantsList = new EventSource('/sse/participantsList?room=' + roomID);
  console.log("Room ID:", roomID);
  console.log("EventSource created with URL: /sse/participantsList?room=" + roomID);
  SSEparticipantsList.onopen = function () {
    console.log("SSE connection opened");
  }
  SSEparticipantsList.onmessage = function (event) {
    console.log("SSEparticipantsList received:" + event.data);
  }
  SSEparticipantsList.addEventListener('participantsList', function (event) {
    console.log("Event received:" + event.data);
    try {
      var participants = JSON.parse(event.data);
      console.log("Parsed participants:", participants);
      var participantList = "<ul>";
      participants.forEach(function (participant) {
        participantList += "<li>" + participant.userName + "</li>";
      });
      participantList += "</ul>";
      document.getElementById("participantsList").innerHTML = participantList;
      let participantNum = participants.length;
      document.getElementById("participantNum").textContent = participantNum;
      if (participantNum === 0) {
        document.getElementById("noParticipant").textContent = "現在参加者はいません。";
        document.getElementById("noParticipant").style.display = "block";
      } else {
        document.getElementById("noParticipant").style.display = "none";
      }
    } catch (e) {
      console.log("Error parsing event data:", e);
    }
  });
  SSEparticipantsList.addEventListener('init', function (event) {
    console.log("Init event received: " + event.data);
  });
  SSEparticipantsList.onerror = function (error) {
    console.log("SSE error:", error);
  }

  // ページ遷移用イベントソース
  var SSEpageTransition = new EventSource('/sse/pageTransition?room=' + roomID);
  console.log("EventSource created with URL: /sse/pageTransition?room=" + roomID);
  SSEpageTransition.onopen = function () {
    console.log("SSE connection opened");
  }
  SSEpageTransition.onmessage = function (event) {
    console.log("SSEpageTransition received:" + event.data);
  }
  SSEpageTransition.addEventListener('pageTransition', function (event) {
    console.log("Event received:" + event.data);
    try {
      if (event.data == "pageTransition") {
        console.log("Page transition detected");
        window.location.href = "/playing/wait?room=" + roomID;
      }
    } catch (e) {
      console.log("Error parsing event data:", e);
    }
  });
  // SSEpageTransition.addEventListener('gameCancelled', function (event) {
  //   console.log("Event received:" + event.data);
  //   try {
  //     if (event.data == "gameCancelled") {
  //       var isHost = document.body.getAttribute('data-is-host');
  //       console.log("isHost value:", isHost);
  //       if (isHost === 'false') {
  //         console.log("Confirm dialog will appear.");
  //         if (confirm("ホストによりゲームが中止されました。")) {
  //           console.log("Page transition detected");
  //           window.location.href = "/playgame";
  //         }
  //       }
  //     }
  //   } catch (e) {
  //     console.log("Error parsing event data:", e);
  //   }
  // });
  SSEpageTransition.addEventListener('init', function (event) {
    console.log("Init event received: " + event.data);
  });
  SSEpageTransition.onerror = function (error) {
    console.log("SSE error:", error);
  }

  function showModal() {
    var modal = document.getElementById("modal");
    modal.style.display = "flex";
  }

  // モーダルを閉じる処理
  document.getElementById("confirmButton").addEventListener("click", function () {
    window.location.href = "/playgame";
  });

  // SSEイベントを受け取ったときにモーダルを表示
  SSEpageTransition.addEventListener('gameCancelled', function (event) {
    if (event.data == "gameCancelled") {
      showModal();
    }
  });
}
