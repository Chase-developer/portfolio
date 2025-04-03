document.addEventListener("DOMContentLoaded", async function () {
    //document.querySelectorAll(".tab-content").forEach(element => {
           // element.innerHTML = element.innerHTML
               // .replace(/(^|\s)-/g, '$1<span class="highlight-dash">-</span>') // Match `-`
               // .replace(/(^|\s)&gt;/g, '$1<span class="highlight-arrow">&gt;</span>'); // Match `>` (HTML entity)
        //});
    var converter = new showdown.Converter();
	const textArea = document.getElementById("raw-text");
    const fileUrl = textArea.getAttribute("data-src"); // Get the Thymeleaf-resolved URL
    //var markdownText = document.getElementById("raw-text").value;
	try {
        const response = await fetch(fileUrl);
        if (!response.ok) throw new Error("Failed to load the markdown file");

        const markdownText = await response.text();
		var html = converter.makeHtml(markdownText);

	    var outputDiv = document.getElementById("output");
	    outputDiv.innerHTML = html;

	    // Apply syntax highlighting
		outputDiv.querySelectorAll("pre code").forEach((block) => {
			//block.classList.add("language-ruby"); // Change to "language-yaml" or "language-json" if needed
	        hljs.highlightElement(block);
	    });
	    
    } catch (error) {
        console.error("Error loading markdown:", error);
    }
});
/*document.addEventListener("DOMContentLoaded", function () {
    var converter = new showdown.Converter();
    var markdownText = document.getElementById("raw-text").value;
    var html = converter.makeHtml(markdownText);

    var outputDiv = document.getElementById("output");
    outputDiv.innerHTML = html;

    // Apply syntax highlighting
    outputDiv.querySelectorAll("pre code").forEach((block) => {
        hljs.highlightElement(block);
    });
});*/

