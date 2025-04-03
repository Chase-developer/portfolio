
document.addEventListener("DOMContentLoaded", () => {
	
	document.querySelectorAll(".badge-item").forEach(badgeItem => {
	    badgeItem.addEventListener("click", function() {
	        let badgeImg = this.querySelector(".small-badge"); // Select the image inside the badge item

	        document.getElementById("bigBadge").src = badgeImg.src;
	        document.getElementById("badgeTitle").innerText = badgeImg.dataset.name || "Badge Name";
	        document.getElementById("badgeDescription").innerText = badgeImg.dataset.desc || "No description available.";

	        let verifyLink = document.getElementById("badgeVerify");
	        if (badgeImg.dataset.verify) {
	            verifyLink.href = badgeImg.dataset.verify;
	            verifyLink.innerText = "VERIFY";
	            verifyLink.style.display = "block";  // Show the link if available
	        } else {
	            verifyLink.href = "#";
	            verifyLink.innerText = "No Link Available";
	            verifyLink.style.display = "none";  // Hide if no link is provided
	        }
	    });
	});
});


