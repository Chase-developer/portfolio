
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

	/*
    let bigBadge = {
        img: document.getElementById("bigBadge").src,
        name: document.getElementById("badgeTitle").innerText,
        desc: document.getElementById("badgeDescription").innerText
    };

    let badges = Array.from(document.querySelectorAll(".small-badge")).map(img => ({
        element: img,
        img: img.src,
        name: img.dataset.name,
        desc: img.dataset.desc
    }));

    badges.forEach(badge => {
        badge.element.addEventListener("click", function() {
            // Swap the big badge with the clicked badge
            //let temp = { ...bigBadge };
            bigBadge.img = badge.img;
            bigBadge.name = badge.name;
            bigBadge.desc = badge.desc;

            // Update DOM
            document.getElementById("bigBadge").src = bigBadge.img;
            document.getElementById("badgeTitle").innerText = bigBadge.name;
            document.getElementById("badgeDescription").innerText = bigBadge.desc;

        });
    });
	*/
});


