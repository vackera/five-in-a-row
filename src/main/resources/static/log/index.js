$(document).ready(function() {
    let cachedLogData = null;

    $.get('/api/server/status', function(data) {
        const container = $('#status-container');
        container.append('<div class="status-item">Server start time: ' + new Date(data.serverStartTime).toLocaleString() + '</div>');
        container.append('<div class="status-item">Server uptime: ' + data.serverUptime + '</div>');
        container.append('<div class="status-item">Game created: ' + data.gameCreated + '</div>');
        container.append('<div class="status-item">Game started: ' + data.gameStarted + '</div>');
        container.append('<div class="status-item">Game finished: ' + data.gameFinished + '</div>');
        container.append('<div class="status-item">Active games: ' + data.activeGames + '</div>');
        container.append('<div class="status-item">Player won: ' + data.playerWon + '</div>');
        container.append('<div class="status-item">AI won: ' + data.aiWon + '</div>');
        container.append('<div class="status-item">Draw: ' + data.draw + '</div>');
    });


    $.get('/api/server/log', function(data) {
        cachedLogData = data;
        $('#log-content').html(highlightLog(data, 'ALL'));
    }).fail(function() {
        $('#log-content').text('Failed to load log file.');
    });

    $('#log-filter').change(function() {
        const filter = $(this).val();
        if (cachedLogData) {
            $('#log-content').html(highlightLog(cachedLogData, filter));
        }
    });
});

function highlightLog(logData, filter) {
    const lines = logData.split('\n');
    return lines.map(line => {
        if (filter === 'ALL' || line.includes(filter)) {
            let className = '';
            if (line.includes('ERROR')) {
                className = 'error';
            } else if (line.includes('WARN')) {
                className = 'warn';
            } else if (line.includes('INFO')) {
                className = 'info';
            } else if (line.includes('DEBUG')) {
                className = 'debug';
            }

            return `<div class="log-line ${className}">${line}</div>`;
        } else {
            return '';
        }
    }).join('');
}
