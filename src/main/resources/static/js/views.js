// views.js
document.addEventListener("DOMContentLoaded", function () {
    const viewsElement = document.getElementById("views-counter");
    if (!viewsElement) return;

    fetch("/views")
        .then(response => response.text())
        .then(data => {
            viewsElement.textContent = data;
        })
        .catch(err => {
            console.error("Failed to fetch view count", err);
        });
});
