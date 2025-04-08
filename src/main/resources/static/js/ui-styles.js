document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll('.sync-btn').forEach(btn => {
      const text = btn.getAttribute('data-text');
      btn.querySelectorAll('.sync-layer').forEach(layer => {
        layer.textContent = text;
      });
    });

});