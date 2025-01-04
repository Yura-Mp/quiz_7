window.onload = function () {
  var roomID = new URLSearchParams(window.location.search).get('room');
  var quizID = new URLSearchParams(window.location.search).get('quiz');
  var sse = new EventSource('/sse/answerList?room=' + roomID + '&quiz=' + quizID);
  console.log("EventSource created with URL: /sse/answerList?room=" + roomID + "&quiz=" + quizID);
  sse.onopen = function () {
    console.log("SSE connection opened");
  }
  // 1秒ごとに届く
  sse.addEventListener('refresh', function (event) {
    console.log("Refresh event received: " + event.data);
    let participantsList = JSON.parse(event.data);
    if (participantsList.length === 0) {
      return;
    } else {
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
    }
  });
  sse.addEventListener('countdown', function (event) {
    console.log("Countdown event received: " + event.data);
    let countdown = Math.round(event.data);
    let remainTime = "<h3>制限時間: " + countdown + " 秒</h3>";
    if (countdown <= 0) {
      remainTime = "<h3>制限時間: 終了</h3>";
    }
    document.getElementById("remainTime").innerHTML = remainTime;
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
