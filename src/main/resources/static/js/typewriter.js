document.addEventListener("DOMContentLoaded", () => {
    const quoteEl = document.getElementById("quote");
    const finalQuote = "Continuously mastering the ever-evolving world of technology.";
    
    quoteEl.innerHTML = `<span id="text"></span><span class="cursor">|</span>`;
    const textEl = document.getElementById("text");
    let index = 0;
	
	function typeWriter() {
        if (index < finalQuote.length) {
    		textEl.innerHTML += finalQuote.charAt(index); // Use innerHTML instead of textContent
            index++;
    
            // Delay before continuing after the line break
            setTimeout(typeWriter, 50);
        }
	}

    // Observer to start effect when in view
    const observer = new IntersectionObserver(entries => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                typeWriter();
                observer.disconnect(); // Stop observing after animation
            }
        });
    }, { threshold: 0.5 });
	observer.observe(document.querySelector(".quote"));
    //observer.observe(quote1El);
});
