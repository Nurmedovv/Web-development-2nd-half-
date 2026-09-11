const express = require('express');
const path = require('path');
const app = express();
const PORT = 3000;

app.use(express.static('dist'));
app.use(express.static('public'));

app.get('/', function(req, res) {
    res.sendFile(path.join(__dirname, 'public', 'Getting-started.html'));
});

app.get('/p2', function(req, res) {
    res.sendFile(path.join(__dirname, 'public', 'p2.html'));
});

app.get('/p4', function(req, res) {
    res.sendFile(path.join(__dirname, 'public', 'p4.html'));
});

app.listen(PORT, function() {
    console.log('Server running on http://localhost:' + PORT);
    console.log('Example: http://localhost:' + PORT + '/');
    console.log('Regions: http://localhost:' + PORT + '/p2');
    console.log('SPA: http://localhost:' + PORT + '/p4');
});