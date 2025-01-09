class SseAutoRedirectToAnsResultPage {
  sse = undefined;

  run() {
    console.log("SseAutoRedirectToAnsResultPage.run() is called. ");

    try { this.sse.close(); } catch(e) { console.warn("SseAutoRedirectToAnsResultPage.run(): ", e); }

    this.sse = new EventSource("/sse/transitToAnsResult");
    this.sse.onopen = function() {
      console.info("SseAutoRedirectToAnsResultPage.run(): SSE connection opened. ");
    };
    this.sse.onerror = function (error) {
      console.error("SseAutoRedirectToAnsResultPage.run(): SSE error: ", error);
    };
    this.sse.addEventListener('init', function (event) {
      console.log("SseAutoRedirectToAnsResultPage.run(): Init event received: " + event.data);
    });
    this.sse.onmessage = function(event) {
      console.log("SseAutoRedirectToAnsResultPage.run(): sse.onmessage Event received: " + event.data);

      try {
        const loc = "/playing/ans_result?" + event.data;
        console.log("Redirect to " + loc);
        window.location.href = loc;

      } catch(e) {
        console.error("Error by " + e);
      }
    };
  }

  close() {
    console.log("SseAutoRedirectToAnsResultPage.close() is called. ");
    try { this.sse.close(); } catch { console.error("SseAutoRedirectToAnsResultPage.close(): ", e); }
  }
}
