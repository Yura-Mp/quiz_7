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
      var content = "<ul>";
      participants.forEach(function (participant) {
        content += "<li>" + participant.userName + "</li>";
      });
      content += "</ul>";
      document.getElementById("participantsList").innerHTML = content;
    } catch (e) {
      console.log("Error parsing event data:", e);
    }
  }
  sse.onerror = function (error) {
    console.log("SSE error:", error);
  }
}
