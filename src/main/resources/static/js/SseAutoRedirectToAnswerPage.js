class SseAutoRedirectToAnswerPage {
  sse = undefined;

  run() {
    console.log("SseAutoRedirectToAnswerPage.run() is called. ");

    try { this.sse.close(); } catch(e) { console.warn("SseAutoRedirectToAnswerPage.run(): ", e); }

    this.sse = new EventSource("/sse/transitToAnswer");
    this.sse.onopen = function() {
      console.info("SseAutoRedirectToAnswerPage.run(): SSE connection opened. ");
    };
    this.sse.onerror = function (error) {
      console.error("SseAutoRedirectToAnswerPage.run(): SSE error: ", error);
    };
    this.sse.addEventListener('init', function (event) {
      console.log("SseAutoRedirectToAnswerPage.run(): Init event received: " + event.data);
    });
    this.sse.onmessage = function(event) {
      console.log("SseAutoRedirectToAnswerPage.run(): sse.onmessage Event received: " + event.data);

      try {
        const loc = "/playing/answer?room=" + event.data;
        console.log("Redirect to " + loc);
        window.location.href = loc;

      } catch(e) {
        console.error("Error by " + e);
      }
    };
  }

  close() {
    console.log("SseAutoRedirectToAnswerPage.close() is called. ");
    try { this.sse.close(); } catch { console.error("SseAutoRedirectToAnswerPage.close(): ", e); }
  }
}
