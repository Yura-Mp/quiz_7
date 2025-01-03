window.onload = function () {
  var roomID = new URLSearchParams(window.location.search).get('room');
  var sse = new EventSource('/sse/events?room=' + roomID);
  console.log("Room ID:", roomID);
  console.log("EventSource created with URL: /sse/events?room=" + roomID);
  sse.onopen = function () {
    console.log("SSE connection opened");
  }
  sse.onmessage = function (event) {
    console.log("Event received:" + event.data);
    try {
      if (event.data === "transitionToWait") {
        console.log("Transition to wait page");
        window.location.href = "/playing/wait?room=" + roomID;
      }
    } catch (e) {
      console.log("Error with event data:", e);
    }
  }
  sse.addEventListener('init', function (event) {
    console.log("Init event received: " + event.data);
  });
  sse.onerror = function (error) {
    console.log("SSE error:", error);
  }
}
