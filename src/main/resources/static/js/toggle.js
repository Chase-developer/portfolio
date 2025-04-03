document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll(".toggle-action").forEach(button => {
        button.addEventListener("click", function () {
            let toggleId = this.getAttribute("data-toggle-id");
            let content = document.getElementById(`${toggleId}-content`);
            let switchContainer = document.getElementById(`${toggleId}-switch`);
            
            if (content) {
                content.classList.toggle("hidden");
            }

            if (switchContainer) {
                let children = switchContainer.children;
                for (let i = 0; i < children.length; i++) {
                    children[i].classList.toggle("hidden");
                }
            }
        });
    });
});