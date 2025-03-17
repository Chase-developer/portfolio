
document.querySelectorAll(".small-badge").forEach(badge => {
    badge.addEventListener("click", function() {
        document.getElementById("bigBadge").src = this.src;
        document.getElementById("badgeTitle").innerText = this.dataset.name;
        document.getElementById("badgeDescription").innerText = this.dataset.desc;
    });
});

