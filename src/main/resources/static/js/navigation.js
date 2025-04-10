document.addEventListener("DOMContentLoaded", function() {
    // Get the current chapter (you can set this from your backend or JavaScript)
    const currentUrl = window.location.pathname;

    // Get all anchor tags (<a>) within the #toggle-nav-content element
    const links = document.querySelectorAll('#toggle-nav-content .link');

    // Loop through each link
    links.forEach(link => {
        // Get the href attribute of the link
        const href = link.getAttribute('href');

        // If the href matches the current URL, add the 'active' class
        if (href === currentUrl) {
            link.classList.add('active');
            link.style.pointerEvents = 'none';  // Disable click
        }
    });
});