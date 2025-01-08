window.onload = function () {
  var roomID = new URLSearchParams(window.location.search).get('room');
  var quizID = new URLSearchParams(window.location.search).get('quiz');
  var sse = new EventSource('/sse/answerList?room=' + roomID + '&quiz=' + quizID);
  console.log("EventSource created with URL: /sse/answerList?room=" + roomID + "&quiz=" + quizID);
  sse.onopen = function () {
    console.log("SSE connection opened");
  }
  // 1秒ごとに届く
  sse.addEventListener('answerData', function (event) {
    console.log("Answer Data received: " + event.data);
    let answerDataList = JSON.parse(event.data);
    if (answerDataList.length === 0) {
      return;
    } else {
      let answerList = "<tbody>";
      answerDataList.forEach((answerData) => {
        if (answerData.answerContent === null) {
          answerList += "<tr><td>" + answerData.userName + "</td><td>未解答</td><td align='right'>----</td></tr>";
        } else {
          answerList += "<tr><td>" + answerData.userName + "</td><td align='right'>" + answerData.answerContent + "</td><td align='right'>" + (answerData.answerTime_ms/1000) + "秒</td></tr>";
        }
      });
      answerList += "</tbody>";
      document.getElementById("answerList").innerHTML = answerList;
    }
  });
  sse.addEventListener('countdown', function (event) {
    console.log("Countdown event received: " + event.data);
    let countdown = Math.round(event.data);
    let remainTime = "<h3>残り解答時間: " + countdown + " 秒</h3>";
    if (countdown <= 0) {
      remainTime = "<h3>残り解答時間: 終了</h3>";
    }
    document.getElementById("remainTime").innerHTML = remainTime;
  });
  sse.addEventListener('transition', function (event) {
    console.log("Transition event received: " + event.data);
    let data = JSON.parse(event.data);
    if (data === "toRanking") {
      window.location.href = "/playing/quiz_result?room=" + roomID + "&quiz=" + quizID;
    } else if (data === "toOverall") {
      window.location.href = "/playing/overall?room=" + roomID;
    }
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
