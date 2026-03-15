function fetchGreeting() {
    const name = document.getElementById('nameInput').value || 'World';
    fetch('/greeting?name=' + encodeURIComponent(name))
        .then(r => r.text())
        .then(text => { document.getElementById('result').textContent = text; })
        .catch(() => { document.getElementById('result').textContent = 'Error connecting to server.'; });
}
