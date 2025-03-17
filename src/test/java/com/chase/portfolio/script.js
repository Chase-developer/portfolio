document.addEventListener("DOMContentLoaded", () => {
    const slides = document.querySelectorAll(".slide");
    let currentSlide = 0;

    function showSlide(index) {
        slides.forEach((slide, i) => {
            slide.style.display = i === index ? "block" : "none";
        });
    }

    document.getElementById("prev").addEventListener("click", () => {
        currentSlide = (currentSlide > 0) ? currentSlide - 1 : slides.length - 1;
        showSlide(currentSlide);
    });

    document.getElementById("next").addEventListener("click", () => {
        currentSlide = (currentSlide < slides.length - 1) ? currentSlide + 1 : 0;
        showSlide(currentSlide);
    });
	
	


    showSlide(currentSlide);
	
	document.addEventListener("DOMContentLoaded", async () => {
		    const fontFiles = ["CeraPro-Regular.woff2", "HKGrotesk-Regular.woff2"];

		    fontFiles.forEach(async (font) => {
		        try {
		            const response = await fetch(`/font/${font}`);
		            const fontUrl = await response.text();
		            
		            const fontFace = new FontFace("CeraPro", `url(${fontUrl}) format('woff2')`);
		            await fontFace.load();
		            document.fonts.add(fontFace);
		        } catch (error) {
		            console.error(`Failed to load font ${font}:`, error);
		        }
		    });
		});
});
