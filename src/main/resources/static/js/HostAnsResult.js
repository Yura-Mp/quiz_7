window.onload = function () {
  var roomID = new URLSearchParams(window.location.search).get('room');
  var sse = new EventSource('/sse/answerList?room=' + roomID);
  console.log("EventSource created with URL: /sse/answerList?room=" + roomID);
  sse.onopen = function () {
    console.log("SSE connection opened");
  }
  sse.addEventListener('refresh', function (event) {
    console.log("Refresh event received: " + event.data);
    let participantsList = JSON.parse(event.data);
    let answerList = "<ul>";
    participantsList.forEach((participant) => {
      if (participant.answerContents === null) {
        answerList += "<li>" + participant.userName + ": 未回答</li>";
      } else {
        answerList += "<li>" + participant.userName + ": " + participant.answerContents + " " + participant.answerTime + "</li>";
      }
    });
    answerList += "</ul>";
    document.getElementById("answerList").innerHTML = answerList;
  });
  sse.onmessage = function (event) {
    console.log("Event received:" + event.data);
  }
  sse.addEventListener('init', function (event) {
    console.log("Init event received: " + event.data);
  });
  sse.onerror = function (error) {
    console.log("SSE error:", error);
  }
}
