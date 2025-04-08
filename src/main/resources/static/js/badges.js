
document.addEventListener("DOMContentLoaded", () => {
	
	document.querySelectorAll(".item-list .item").forEach(badgeItem => {
	    badgeItem.addEventListener("click", function() {
	        let badgeEl = this.querySelector(".badgeEl"); // Select the image inside the badge item

	        document.getElementById("badgeImage").src = badgeEl.src;
	        document.getElementById("badgeTitle").innerText = badgeEl.dataset.name || "Badge Name";
	        document.getElementById("badgeDesc").innerText = badgeEl.dataset.desc || "No description available.";

	        let verifyLink = document.getElementById("badgeVerify");
	        if (badgeEl.dataset.verify) {
	            verifyLink.href = badgeEl.dataset.verify;
	        }
	    });
	});
});


