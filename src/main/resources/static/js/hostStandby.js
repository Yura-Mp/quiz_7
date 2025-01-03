document.addEventListener("DOMContentLoaded", function () {
  // リンククリック時のイベントハンドラ
  document.querySelector("#triggerLink").addEventListener("click", function () {
    const roomID = new URLSearchParams(window.location.search).get('room');
    const event = "transitionToWait";

    fetch(`/sse/trigger?room=${roomID}&event=${event}`, {
      method: "POST",
    })
      .then(response => {
        if (response.ok) {
          window.location.href = "/playing/wait?room=" + roomID; // waitページへ遷移
        } else {
          console.error("Failed to trigger event");
        }
      })
      .catch(error => console.error("Error triggering event:", error));
  });
});
