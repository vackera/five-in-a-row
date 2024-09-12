const MESSAGES = {
    NEW_GAME: 'New game started!',
    WAITING_FOR_FIRST_STEP: 'Waiting for the first step...',
    GAME_STARTED: 'Game time started!',
    RESULT_PLAYER_WON: 'Congratulations! You won!',
    RESULT_AI_WON: 'AI player won... try again!',
    RESULT_DRAW: 'This game is a draw... try again!',
    INVALID_PLAYER_NAME: 'Player name must be between 3 and 20 characters and can only include English letters, numbers, and spaces.',
    RESULTS_REFRESHED: 'Top results refreshed',
    ERROR_AND_RELOAD: 'An error occurred. Page will reload.',
    RESPONSE_ERROR: 'Network response was not ok.',
    RESULTS_ERROR: 'Top results are not available at the moment.',
    PLAYER_NAME_CHANGED: 'Player name changed',
};

const ALERT_TYPE = {
    SUCCESS: 'success',
    ERROR: 'error'
};

const TABLE_HEIGHT = 15;
const TABLE_WIDTH = 15;
const DEFAULT_PLAYER_NAME = 'Anonymous';

let lastResponseCell;
let playerResultId;
let playerEarnedRankedResult;
let timePenalty = 0.0;
let timePenaltyElement;
let timePenaltyTimer = null;
let firstStepAlertTimer = null;
let hideInfoBarTimer = null;
let aiVersion = null;
let rankingIsUp = false;
let playerNameChangeInProgress = false;
let playerNameChangeButtonIsClicked = false;
let playerName = getCookie('player-name');
let infoBarElement;
let playerNameElement;

const CSRF_TOKEN = $('meta[name="_csrf"]').attr('content');
const CSRF_HEADER = $('meta[name="_csrf_header"]').attr('content');

function addCsrfHeader(xhr) {
    if (CSRF_TOKEN && CSRF_HEADER) {
        xhr.setRequestHeader(CSRF_HEADER, CSRF_TOKEN);
    } else {
        console.error('CSRF token or header not found!');
    }
}

$(document).ready(function () {

    timePenaltyElement = document.getElementById('time-penalty-info');
    infoBarElement = document.getElementById('info-bar');
    playerNameElement = document.getElementById('player-name');

    getAIVersion();
    refreshResults();
    startNewGame(true);
    $("#ranking-header").on("click", function () {
        rankingToggle();
    });

    $("#ranking-refresh-icon").on("click", function (event) {
        rankingDown();
        rankingUp();
        event.stopPropagation();
        displayMessage(MESSAGES.RESULTS_REFRESHED, ALERT_TYPE.SUCCESS);
    });

    $(playerNameElement)
        .on("focusout", function () {
            setTimeout(() => {
                if (!playerNameChangeButtonIsClicked) {
                    $("#player-name").val(playerName);
                    playerNameChangeInProgress = false;
                }
            }, 200);
        });

    $(playerNameElement)
        .on("focusin", function () {
            $("#player-name").val("");
        });

    $('#github-link').click(function () {
        logClick('github');
    });

    $('#linkedin-link').click(function () {
        logClick('linkedin');
    });

    setTimeout(() => {
        rankingUp();
    }, 500);
});

function startNewGame(firstLoad) {

    $.ajax({
        url: '/api/game/new',
        type: 'POST',
        beforeSend: addCsrfHeader,
        success: function () {
            const table = $('#game-board')[0];
            $(table).html("");
            createGameBoard();

            stopTimePenalty();
            $("#game-score-info").text("Score: 0.00");
            $("#game-step-info").text("Step: 0");
            $("#game-time-info").text("Time: 0.00s");
            $("#time-penalty-info").text("Time penalty: +0.00");

            playerResultId = null;
            playerEarnedRankedResult = false;

            if (firstLoad === true) {
                checkPlayerNameCookie();
            } else {
                rankingDown();
            }

            displayMessage(MESSAGES.NEW_GAME, ALERT_TYPE.SUCCESS);

            firstStepAlertTimer = setTimeout(() => {
                waitingForFirstStep();
            }, 6000);
        },
        error: function (error) {
            console.error(MESSAGES.RESPONSE_ERROR, error);
            reloadPage();
        }
    });

}

function startTimePenalty() {

    timePenalty = -0.01;
    timePenaltyTimer = setInterval(() => {
        timePenalty += 0.01;
        timePenaltyElement.innerText = `Time penalty: +${timePenalty.toFixed(2)}`;
    }, 100);
}

function stopTimePenalty() {

    clearInterval(timePenaltyTimer);
}

function waitingForFirstStep() {

    if (!isGameStarted()) {
        firstStepAlertTimer = setTimeout(() => {
            waitingForFirstStep();
        }, 10000);
        displayMessage(MESSAGES.WAITING_FOR_FIRST_STEP, ALERT_TYPE.SUCCESS);
    }
}

function playerDoStep(coordinateY, coordinateX, cell) {

    stopTimePenalty();
    $("#game-board-overlay").show();

    if (firstStepAlertTimer != null) {
        clearTimeout(firstStepAlertTimer);
        firstStepAlertTimer = null;
        rankingDown();
    } else {
    }

    const dto = {
        coordinateY: coordinateY,
        coordinateX: coordinateX
    };

    $.ajax({
        url: '/api/game/player-step',
        type: 'POST',
        beforeSend: addCsrfHeader,
        contentType: 'application/json',
        data: JSON.stringify(dto),
        success: function (data) {
            $(cell).text("X");
            $(cell).addClass("clicked-player");
            $(cell).off('click');

            const table = $('#game-board')[0];

            $("#game-step-info").text(`Step: ${data.stepCount}`);
            $("#game-time-info").text(`Time: ${formatTime(data.gameTimeInMillis)}s`);
            $("#game-score-info").text(`Score: ${formatScore(data.score)}`);

            if (data.gameStatus === "DRAW") {
                displayMessage(MESSAGES.RESULT_DRAW, ALERT_TYPE.SUCCESS, 6000);
            } else if (data.gameStatus === "IN_PROGRESS" || data.gameStatus === "AI_WON") {
                const responseCell = table.rows[data.step.coordinateY].cells[data.step.coordinateX];

                $(responseCell).text("O");
                $(responseCell).addClass("clicked-ai");
                $(responseCell).off('click');

                if (isGameStarted()) {
                    $(lastResponseCell).removeClass("last-step-ai");
                } else {
                    displayMessage(MESSAGES.GAME_STARTED, ALERT_TYPE.SUCCESS);
                }

                $(responseCell).addClass("last-step-ai");
                lastResponseCell = responseCell;
            }

            if (data.gameStatus !== "IN_PROGRESS") {
                let cells = document.querySelectorAll('#game-board td, #myTable th');
                cells.forEach(function (cell) {
                    $(cell).off('click');
                });

                if (data.gameStatus !== "DRAW") {
                    data.winnerCoordinates.forEach(coordinate => {
                        const winnerCell = table.rows[coordinate.coordinateY].cells[coordinate.coordinateX];
                        $(winnerCell).addClass("winner-cell");
                    });

                    if (data.gameStatus === "PLAYER_WON") {
                        playerResultId = data.ownResultId;
                        displayMessage(MESSAGES.RESULT_PLAYER_WON, ALERT_TYPE.SUCCESS, 6000);
                    } else if (data.gameStatus === "AI_WON") {
                        displayMessage(MESSAGES.RESULT_AI_WON, ALERT_TYPE.SUCCESS, 6000);
                    }
                }
                refreshResults();
            } else {
                startTimePenalty();
            }
            $("#game-board-overlay").hide();
        },
        error: function (error) {
            console.error(MESSAGES.RESPONSE_ERROR, error);
            reloadPage();
        }
    });
}

function reloadPage() {

    alert(MESSAGES.ERROR_AND_RELOAD);
    location.reload();
}

function createGameBoard() {

    const table = $('#game-board')[0];
    lastResponseCell = null;
    for (let i = 0; i < TABLE_HEIGHT; i++) {
        const row = document.createElement('tr');
        for (let j = 0; j < TABLE_WIDTH; j++) {
            const cell = document.createElement('td');

            $(cell).on('click', function () {
                playerDoStep(i, j, cell);
            });

            row.appendChild(cell);
        }
        table.appendChild(row);
    }
}

function setPlayerName() {

    playerNameChangeButtonIsClicked = true;
    playerNameChangeInProgress = true;

    $("#new-name-button").focus();
    let newPlayerName = $(playerNameElement).val().trim();

    const regex = /^[a-zA-Z0-9 ]*$/;
    if (!regex.test(newPlayerName) || newPlayerName.length < 3 || newPlayerName.length > 20) {
        displayMessage(MESSAGES.INVALID_PLAYER_NAME, ALERT_TYPE.ERROR);
        $("#player-name").addClass("shake");
        setTimeout(() => {
            playerNameChangeButtonIsClicked = false;
            $(playerNameElement).focus();
            $(playerNameElement).removeClass("shake");
        }, 500);
        return;
    }
    $.ajax({
        url: '/api/game/player-name',
        type: 'POST',
        beforeSend: addCsrfHeader,
        data: JSON.stringify({name: newPlayerName}),
        contentType: 'application/json',
        dataType: 'text',

        success: function (text) {
            let data;
            try {
                data = JSON.parse(text);
            } catch (e) {
                data = {};
            }

            if (data.fieldErrors) {
                throw new Error(data.fieldErrors.name);
            }

            playerNameChangeInProgress = false;
            playerNameChangeButtonIsClicked = false;
            displayMessage(MESSAGES.PLAYER_NAME_CHANGED, ALERT_TYPE.SUCCESS);
            playerName = newPlayerName;
            $(playerNameElement).val(playerName);
        },
        error: function (error) {
            console.error(MESSAGES.RESPONSE_ERROR, error);
            reloadPage();
        }
    });

}

function displayMessage(message, type, duration = 3000) {

    if (hideInfoBarTimer != null) {
        clearTimeout(hideInfoBarTimer);
    }

    if (firstStepAlertTimer != null) {
        clearTimeout(firstStepAlertTimer);
        firstStepAlertTimer = null;
        if (!isGameStarted()) {
            firstStepAlertTimer = setTimeout(() => {
                waitingForFirstStep();
            }, 8000);
        }
    }

    if (type === ALERT_TYPE.ERROR) {
        $(infoBarElement).addClass("message-error");
    } else if (type === ALERT_TYPE.SUCCESS) {
        $(infoBarElement).removeClass("message-error");
    }

    $("#infoMessage").text(message);
    $(infoBarElement).show();

    hideInfoBarTimer = setTimeout(() => {
        hideInfoBarTimer = null;
        $(infoBarElement).fadeOut("slow");
    }, duration);
}

function refreshResults() {

    $.ajax({
        url: '/api/result',
        type: 'GET',
        dataType: 'json',
        success: function (data) {
            displayResults(data);
        },
        error: function (error) {
            console.error(MESSAGES.RESPONSE_ERROR, error);
            displayResults(null);
        }
    });
}

function displayResults(results) {

    const rankingList = document.getElementById('ranking-list');
    $(rankingList).html("");

    if (results === null) {
        const headerRow = document.createElement('div');
        rankingList.appendChild(headerRow);
        $(headerRow).addClass("results-error");
        $(headerRow).html(MESSAGES.RESULTS_ERROR);

        return false;
    }

    const headerRow = document.createElement('div');
    $(headerRow).addClass("result-row result-header");
    headerRow.innerHTML = `
        <div class="result-date">Date</div>
        <div class="result-name">Name</div>
        <div class="result-score">Score</div>
        <div class="result-steps">Step</div>
        <div class="result-time">Time</div>
    `;
    rankingList.appendChild(headerRow);

    results.forEach((result, index) => {
        const resultRow = document.createElement('div');
        $(resultRow).addClass("result-row");
        if (index % 2 === 0) {
            $(resultRow).addClass("even-row");
        } else {
            $(resultRow).addClass("odd-row");
        }
        resultRow.innerHTML = `
            <div class="result-date" title="${formatFullDate(result.datePlayed)}">${formatDate(result.datePlayed)}</div>
            <div class="result-name">${result.playerName}</div>
            <div class="result-score">${formatScore(result.score)}</div>
            <div class="result-steps">${result.winnerStepCount}</div>
            <div class="result-time">${formatTime(result.winnerTime)}s</div>
        `;
        rankingList.appendChild(resultRow);
        if (result.id === playerResultId) {
            playerEarnedRankedResult = true;
            $(resultRow).addClass("ranked-result");
        }
    });
    if (playerEarnedRankedResult) {
        rankingUp();
    }
    return true;
}

function rankingToggle() {

    if (rankingIsUp) {
        rankingDown();
    } else {
        rankingUp();
    }
}

function rankingDown() {

    $("#ranking-list").slideUp("slow", function () {
        rankingIsUp = false;
        $(".ranking-arrow").text("▲");
        $("#ranking-refresh-icon").hide();
    });
}

function rankingUp() {

    $("#ranking-list").slideDown("slow", function () {
        rankingIsUp = true;
        $(".ranking-arrow").text("▼");
        $("#ranking-refresh-icon").show();
    });
}

function checkPlayerNameCookie() {

    if (playerName != null) {
        $("#player-name").val(playerName.replace(/_/g, ' '));
        setPlayerName();
        playerNameChangeButtonIsClicked = false;
    } else {
        playerName = DEFAULT_PLAYER_NAME;
        $("#player-name").val(DEFAULT_PLAYER_NAME);
    }
}

function getCookie(name) {

    const cookieString = document.cookie;
    const cookies = cookieString.split('; ');

    for (let cookie of cookies) {
        const cookiePair = cookie.split('=');
        const cookieName = cookiePair[0];
        const cookieValue = cookiePair[1];

        if (cookieName === name) {
            return cookieValue;
        }
    }

    return null;
}

function getAIVersion() {

    $.ajax({
        url: '/api/game/ai-version',
        type: 'GET',
        dataType: 'text',
        success: function (data) {
            aiVersion = data;
            $("#ai-version").text(`(AI version: ${data})`);
        },
        error: function (error) {
            console.error(MESSAGES.RESPONSE_ERROR, error);
        }
    });
}

function isGameStarted() {

    return lastResponseCell != null;
}

function formatDate(dateTime) {

    return new Date(dateTime).toLocaleDateString();
}

function formatFullDate(date) {

    return new Date(date).toString();
}

function formatScore(score) {

    return score.toFixed(2);
}

function formatTime(timeInMillis) {

    return (timeInMillis / 1000).toFixed(2);
}

function logClick(link) {

    $.ajax({
        url: 'api/server/log-click',
        type: 'POST',
        beforeSend: addCsrfHeader,
        contentType: 'application/json',
        data: JSON.stringify({
            link: link,
            screenWidth: window.innerWidth,
            screenHeight: window.innerHeight
        }),
        error: function (error) {
            console.error(MESSAGES.RESPONSE_ERROR, error);
        }
    });
}