window.onload = function () {
  var roomID = new URLSearchParams(window.location.search).get('room');
  var sse = new EventSource('/sse/participantsList?room=' + roomID);
  console.log("Room ID:", roomID);
  console.log("EventSource created with URL: /sse/participantsList?room=" + roomID);
  sse.onopen = function () {
    console.log("SSE connection opened");
  }
  sse.onmessage = function (event) {
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
  }
  sse.addEventListener('init', function (event) {
    console.log("Init event received: " + event.data);
  });
  sse.onerror = function (error) {
    console.log("SSE error:", error);
  }
}
