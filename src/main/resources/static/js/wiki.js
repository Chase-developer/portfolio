document.addEventListener("DOMContentLoaded", function () {
    document.querySelector(".toggle-btn").addEventListener("click", function () {
        let content = document.getElementById("rollDownSection");
        let downArrow = document.getElementById("arrow-down");
        let rightArrow = document.getElementById("arrow-right");

        // Toggle visibility
        downArrow.classList.toggle("hidden");
        rightArrow.classList.toggle("hidden");
        content.classList.toggle("show");
    });
});