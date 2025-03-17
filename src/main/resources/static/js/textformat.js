function toggleIntro() {
    let fullIntro = document.getElementById("fullIntro");
    let btn = document.getElementById("readMoreBtn");

    if (fullIntro.style.display === "none") {
        fullIntro.style.display = "block";
        btn.innerText = "Read Less";
    } else {
        fullIntro.style.display = "none";
        btn.innerText = "Read More";
    }
}
