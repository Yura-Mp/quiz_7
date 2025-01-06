function SseAutoRedirectToAnsResultPage() {
  console.log("SseAutoRedirectToAnsResultPage function is called. ")

  let sse = new EventSource("/sse/transitToAnsResult");
  sse.onopen = function() {
    console.info("SseAutoRedirectToAnsResultPage: SSE connection opened. ");
  };
  sse.onerror = function (error) {
    console.error("SseAutoRedirectToAnsResultPage: SSE error: ", error);
  };
  sse.addEventListener('init', function (event) {
    console.log("SseAutoRedirectToAnsResultPage: Init event received: " + event.data);
  });
  sse.onmessage = function(event) {
    console.log("SseAutoRedirectToAnsResultPage: sse.onmessage Event received: " + event.data);

    try {
      const loc = "/playing/ans_result?" + event.data;
      console.log("Redirect to " + loc);
      window.location.href = loc;

    } catch(e) {
      console.error("Error by " + e);
    }
  };
}
