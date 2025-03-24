document.addEventListener("DOMContentLoaded", () => {
    const quote1El = document.getElementById("quote-one");
	const cursor1El = document.getElementById("cursor-one");
	const quote2El = document.getElementById("quote-two");
	const cursor2El = document.getElementById("cursor-two");
	
	//const quoteEl = document.getElementById("quote");
		//const cursorEl = document.querySelector(".text-cursor");
    const finalQuote = "Continuously mastering the ever-evolving world of technology.";
    let index = 0;
	
	function typeWriter1() {
		quote1El.innerHTML += finalQuote.charAt(index); // Use innerHTML instead of textContent
        index++;

        // Delay before continuing after the line break
		if (index == 26)
		{
			
			setTimeout(typeWriter2, 50);
		}
		else
		{
			setTimeout(typeWriter1, 50);
		}
        //let delay = (index === 32) ? 1000 : 50;
        
	}
	
	function typeWriter2() {
		    if (index < finalQuote.length) {
		      	if (index == 26)
				{
					cursor1El.style.display = "none";
					cursor2El.style.display = "inline-block";
				}
		        quote2El.innerHTML += finalQuote.charAt(index); // Use innerHTML instead of textContent
		        index++;
		        setTimeout(typeWriter2, 50);
		    } else {
		        //cursor2El.style.display = "none"; // Hide cursor when done
		        //setTimeout(() => {
		           // authorEl.style.opacity = "1"; // Fade in author name
		        //}, 500);
		    }
		}

    

    // Observer to start effect when in view
    const observer = new IntersectionObserver(entries => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                typeWriter1();
                observer.disconnect(); // Stop observing after animation
            }
        });
    }, { threshold: 0.5 });
	observer.observe(document.querySelector(".quote"));
    //observer.observe(quote1El);
});
