function SseAutoRedirectToAnswerPage() {
  console.log("SseAutoRedirectToAnswerPage function is called. ")

  let sse = new EventSource("/sse/transitToAnswer");
  sse.onopen = function() {
    console.info("SseAutoRedirectToAnserPage: SSE connection opened. ");
  };
  sse.onerror = function (error) {
    console.error("SseAutoRedirectToAnserPage: SSE error: ", error);
  };
  sse.addEventListener('init', function (event) {
    console.log("SseAutoRedirectToAnserPage: Init event received: " + event.data);
  });
  sse.onmessage = function(event) {
    console.log("SseAutoRedirectToAnserPage: sse.onmessage Event received: " + event.data);

    try {
      if(!isNaN(event.data)) {
        const loc = "/playing/answer?room=" + event.data;
        console.log("Redirect to " + loc);
        window.location.href = loc;
      }

    } catch(e) {
      console.error("Error by " + e);
    }
  };
}
