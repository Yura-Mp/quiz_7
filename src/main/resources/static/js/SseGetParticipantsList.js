function showModal() {
  var modal = document.getElementById("modal");
  modal.style.display = "flex";
}

class SSEparticipantsList {
  sse = undefined;

  run() {
    console.log("SSEparticipantsList.run() is called. ");

    try { this.sse.close(); } catch(e) { console.warn("SSEparticipantsList.run(): ", e); }

    var roomID = new URLSearchParams(window.location.search).get('room');
    // 参加者リスト更新用イベントソース
    this.sse = new EventSource('/sse/participantsList?room=' + roomID);
    console.log("Room ID:", roomID);
    console.log("EventSource created with URL: /sse/participantsList?room=" + roomID);
    this.sse.onopen = function () {
      console.log("SSE connection opened");
    };
    this.sse.onmessage = function (event) {
      console.log("SSEparticipantsList received:" + event.data);
    };
    this.sse.addEventListener('participantsList', function (event) {
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
    this.sse.addEventListener('init', function (event) {
      console.log("Init event received: " + event.data);
    });
    this.sse.onerror = function (error) {
      console.log("SSE error:", error);
    };
  }

  close() {
    console.log("SSEparticipantsList.close() is called. ");
    try { this.sse.close(); } catch { console.error("SSEparticipantsList.close(): ", e); }
  }
}

class SSEpageTransition {
  sse = undefined;

  run() {
    console.log("SSEpageTransition.run() is called. ");

    try { this.sse.close(); } catch(e) { console.warn("SSEpageTransition.run(): ", e); }

    var roomID = new URLSearchParams(window.location.search).get('room');
    // ページ遷移用イベントソース
    this.sse = new EventSource('/sse/pageTransition?room=' + roomID);
    console.log("EventSource created with URL: /sse/pageTransition?room=" + roomID);
    this.sse.onopen = function () {
      console.log("SSE connection opened");
    };
    this.sse.onmessage = function (event) {
      console.log("SSEpageTransition received:" + event.data);
    };
    this.sse.addEventListener('pageTransition', function (event) {
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
    this.sse.addEventListener('init', function (event) {
      console.log("Init event received: " + event.data);
    });
    this.sse.onerror = function (error) {
      console.log("SSE error:", error);
    };

    // SSEイベントを受け取ったときにモーダルを表示
    this.sse.addEventListener('gameCancelled', function (event) {
      if (event.data == "gameCancelled") {
        showModal();
      }
    });
  }

  close() {
    console.log("SSEpageTransition.close() is called. ");
    try { this.sse.close(); } catch { console.error("SSEpageTransition.close(): ", e); }
  }
}
