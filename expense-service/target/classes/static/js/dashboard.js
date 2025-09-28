// Simple JS for category click (if not using Thymeleaf's th:onclick)
document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('.category-box').forEach(function(div) {
        div.addEventListener('click', function() {
            window.location = '/category/' + div.textContent.trim();
        });
    });
});
