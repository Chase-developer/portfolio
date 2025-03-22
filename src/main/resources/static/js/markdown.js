document.addEventListener("DOMContentLoaded", function () {
    var converter = new showdown.Converter({
        tables: true,    // Enables tables
        strikethrough: true,
        simplifiedAutoLink: true,
        ghCodeBlocks: true
    });

    var markdownText = document.getElementById("raw-text").value;
    var html = converter.makeHtml(markdownText);

    var outputDiv = document.getElementById("output");
    outputDiv.innerHTML = html;

    // Only apply syntax highlighting to actual code blocks
    outputDiv.querySelectorAll("pre code").forEach((block) => {
        hljs.highlightElement(block);
    });
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

