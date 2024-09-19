$(document).ready(function() {
    let cachedLogData = null;

    $.get("/api/server/status", function(data) {
        const container = $("#status-container");
        container.append("<div class='status-item'>Server start time: " + new Date(data.serverStartTime).toLocaleString() + "</div>");
        container.append("<div class='status-item'>Server uptime: " + data.serverUptime + "</div>");
        container.append("<div class='status-item'>Game created: " + data.gameCreated + "</div>");
        container.append("<div class='status-item'>Game started: " + data.gameStarted + "</div>");
        container.append("<div class='status-item'>Game finished: " + data.gameFinished + "</div>");
        container.append("<div class='status-item'>Active games: " + data.activeGames + "</div>");
        container.append("<div class='status-item'>Player won: " + data.playerWon + "</div>");
        container.append("<div class='status-item'>AI won: " + data.aiWon + "</div>");
        container.append("<div class='status-item'>Draw: " + data.draw + "</div>");
    });

    $.get("/api/server/log", function(data) {
        cachedLogData = data;
        $("#log-content").html(highlightLog(data, "ALL"));
    }).fail(function() {
        $("#log-content").text("Failed to load log file.");
    });

    $("#log-filter").change(function() {
        const filter = $(this).val();
        if (cachedLogData) {
            $("#log-content").html(highlightLog(cachedLogData, filter));
        }
    });
});

function highlightLog(logData, filter) {
    const lines = logData.split("\n");
    return lines.map(line => {
        if (filter === "ALL" || line.includes(filter)) {

            const timestampMatch = line.match(/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}(?:\.\d{3})?(?:[+-]\d{2}:\d{2}|Z)/);
            const logLevelMatch = line.match(/\s(INFO|WARN|ERROR|DEBUG)\s/);
            const messageMatch = line.split(": ").pop();

            if (timestampMatch && logLevelMatch && messageMatch) {
                const originalTimestamp = timestampMatch[0];
                const logLevel = logLevelMatch[1];
                let className = logLevel.toLowerCase();

                const date = new Date(originalTimestamp);
                const localTimestamp = new Intl.DateTimeFormat("hu-HU", {
                    year: "numeric",
                    month: "2-digit",
                    day: "2-digit",
                    hour: "2-digit",
                    minute: "2-digit",
                    second: "2-digit"
                }).format(date).replace(/\s/g, "");

                return `<div class="log-line ${className}">${localTimestamp} ${logLevel}: ${messageMatch}</div>`;
            }
        }
        return "";
    }).join("");
}


