document.addEventListener("DOMContentLoaded", () => {
    const quoteEl = document.getElementById("quote");

    const observer = new IntersectionObserver(entries => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                quoteEl.classList.add("show");
                observer.disconnect();
            }
        });
    }, { threshold: 0.5 });

    observer.observe(quoteEl);
});
