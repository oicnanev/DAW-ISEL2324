import React, {useEffect, useState} from 'react';
import {Navigate, useParams, useLocation} from 'react-router-dom';
import {GameProps} from './Game';

async function GetGame({gid}: { gid: number }) {
    const response = await fetch(`/api/game/${gid}`);
    const data = await response.json();
    console.log(data);
    return data;
}

// CSS styles -----------------------------------------------------------------
const gamesStyle = {padding: '70px'};
const textAreaStyle = {padding: '10px'};
const fontStyle = {color: 'darkgray'};

const cellStyle = {
    width: '40px',
    height: '40px',
    border: '1px solid bisque',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    cursor: 'pointer',
    backgroundImage: 'url(/res/crossWithBackground.png)',
    backgroundSize: 'cover',
};

const pieceStyleWhite = {
    width: '95%',
    height: '95%',
    borderRadius: '100%',
    backgroundColor: 'dimgray',
    backgroundImage: 'url(/res/whitePiece.png)',
    backgroundSize: 'cover',
};

const pieceStyleBlack = {
    width: '95%',
    height: '95%',
    borderRadius: '50%',
    backgroundImage: 'url(/res/blackPiece.png)',
    backgroundSize: 'cover',
};

type PlayProps = {
    gid: number;
    x: number;
    y: number;
}

type State =
    | { tag: 'loading' }
    | { tag: 'loaded'; game: GameProps }
    | { tag: 'error'; error: string }
    | { tag: 'reload'; game: GameProps }
    | { tag: 'finished'; game: GameProps }
    | { tag: 'redirect'; }
    | { tag: 'loop redirect'; };

type Action =
    | { type: 'reload' }
    | { type: 'played' }
    | { type: 'success'; game: GameProps }
    | { type: 'error'; message: string }
    | { type: 'cancel' }
    | { type: 'loop cancel' };

async function Play({gid, x, y}: PlayProps) {
    await fetch(`/api/game/${gid}`, {
        method: 'POST',
        headers: {'Content-Type': 'application/json',},
        body: JSON.stringify({x: x, y: y}),
    })
        .then((response) => {
            response.json()
        })
        .then((data) => {
            return data;
        })
        .catch((error) => {
            return error;
        });
}

function reduce(state: State, action: Action) {
    switch (state.tag) {
        case 'loading':
            switch (action.type) {
                case 'success':
                    return {tag: 'loaded', game: action.game};
                case 'error':
                    return {tag: 'error', error: action.message};
                default:
                    return state;
            }
        case 'loaded':
            switch (action.type) {
                case 'reload':
                    return {tag: 'loading'};
                case 'played':
                    return {tag: 'loading'};
                /*case 'played':
                    return {tag: 'to-reload', game: state.game};*/
                case 'cancel':
                    return {tag: 'redirect'};
                case 'loop cancel':
                    return {tag: 'loop redirect'};
                default:
                    return state;
            }
        case 'error':
            switch (action.type) {
                case 'reload':
                    return {tag: 'loading'};
                default:
                    return state;
            }
    }
}

async function cancelGame(): Promise<string | undefined> {
    const response = await fetch('/api/game/giveup', {
        method: 'POST'
    });
    if (response.ok) {  // response.status >= 200 && response.status < 300
        return response.status.toString();
    } else {
        return undefined;
    }
}

export function Board() {
    const {gid} = useParams();
    const [state, dispatch] = React.useReducer(reduce, {tag: 'loading'});
    useState<number | undefined>(undefined);
    const location = useLocation();

    useEffect(() => {
        async function checkTurn() {
            const response = await fetch(`/api/game/${gid}`);
            const data = await response.json()
            if (data && data?.gameState != "in-progress"){
                dispatch({type: 'loop cancel'})
            }
            if (data && data?.currentPlayer !== state.game.currentPlayer) {
                dispatch({type: 'reload'})
            }
            return data;
        }

        const setIntervalId = setInterval(() => {
            checkTurn()
        }, 1000);
        return () => clearInterval(setIntervalId);
    }, [gid, state?.game?.currentPlayer]);

    const extractRowAndCol = (cell: number) => {
        const row = (cell - 1) % state.game.board.size;
        const col = Math.floor((cell - 1) / state.game.board.size);
        return {row, col};
    };

    const handleCellClick = (cell: number) => {
        const {row, col} = extractRowAndCol(cell);
        Play({gid: parseInt(gid), x: row, y: col}).then(() => {
            dispatch({type: 'reload'})
            dispatch({type: 'played'});
        }).catch((error) => {
            console.log('error: ', error);
        });
    };

    if (state.tag === 'loading') {
        GetGame({gid: parseInt(gid)}).then((data) => {
            dispatch({type: 'success', game: data});
        }).catch((error) => {
            console.error(error);
        });
        return (<div>Loading... Please Wait </div>);
    }

    if (state.tag === 'error') {
        return (
            <section className='vh-100 gradient-custom'>
                <main style={gamesStyle} role='main' className='container'>
                    <div className='card bg-dark text-white-50' style={textAreaStyle}>
                        <div>Error: {state.error}</div>
                    </div>
                </main>
            </section>);
    }

    if (state.tag === 'redirect') {
        alert('Game Successfully Cancelled');
        return <Navigate to={location.state?.source?.pathname || '/games'} replace={true}/>;
    }

    if (state.tag === 'loop redirect') {
            alert('Your opponent left the game.');
            return <Navigate to={location.state?.source?.pathname || '/games'} replace={true}/>;
        }


    function haveCellPiece(cell: number, xCoordinates: number[], yCoordinates: number[]) {
        if (xCoordinates === null || yCoordinates === null) {
            return false;
        }
        const {row, col} = extractRowAndCol(cell);
        for (const key in xCoordinates) {
            if (xCoordinates[key] === row && yCoordinates[key] === col) {
                return true;
            }
        }
        return false;
    }

    const boardStyle = {
        display: 'grid',
        gridTemplateColumns: `repeat(${state.game.board.size}, 40px)`,
        gridTemplateRows: `repeat(${state.game.board.size}, 40px)`,
        gridGap: '5px',
        backgroundColor: 'darkgray',
        padding: '5px',
    };

    const createBoard = () => {
        // Build arrays of x and y coordinates for each player
        const player1X = [];
        const player1Y = [];
        const player2X = [];
        const player2Y = [];

        if (state.game.board.player1.coords !== null) {
            for (const key in state.game.board.player1.coords) {
                player1X.push(state.game.board.player1.coords[key].x)
                player1Y.push(state.game.board.player1.coords[key].y)
            }
        }

        if (state.game.board.player2.coords !== null) {
            for (const key in state.game.board.player2.coords) {
                player2X.push(state.game.board.player2.coords[key].x)
                player2Y.push(state.game.board.player2.coords[key].y)
            }
        }

        function handleGiveUp(ev: React.FormEvent<HTMLFormElement>): void {
            ev.preventDefault();
            cancelGame().then(() => dispatch({type: 'cancel'}));
        }

        function GiveUpFromGameForm() {
            if (state.tag === 'loaded') {
                return (
                    <form onSubmit={handleGiveUp}>
                        <div>
                            <button className="bg-dark btn btn-outline-light btn-lg px-5" type="submit">Give Up</button>
                        </div>
                    </form>
                );
            }
        }

        const board = [];

        // TODO: Make this less ugly
        const uglyHeader = [];
        for (let i = 0; i < state.game.board.size; i++) {
            uglyHeader.push(<div key={i*1000}><p style={fontStyle}>{i}</p></div>)
        }

        for (let row = 0; row < state.game.board.size; row++) {
            for (let column = 0; column < state.game.board.size; column++) {
                const cell = column * state.game.board.size + row + 1;

                if (state.game.board.player1.coords === null && state.game.board.player2.coords === null) {
                    board.push(
                        <div className='cell' key={cell} style={cellStyle} onClick={() => handleCellClick(cell)}/>,
                    );
                } else if (state.game.board.player1.coords !== null && state.game.board.player2.coords === null) {
                    board.push(
                        <div className='cell' key={cell} style={cellStyle} onClick={() => handleCellClick(cell)}>
                            {
                                state.tag === 'loaded' &&
                                haveCellPiece(cell, player1X, player1Y) &&
                                state.game.board.player1.stone === 'w' &&
                                <div style={pieceStyleWhite}/>
                            }
                            {
                                state.tag === 'loaded' &&
                                haveCellPiece(cell, player1X, player1Y) &&
                                state.game.board.player1.stone === 'b' &&
                                <div style={pieceStyleBlack}/>
                            }
                        </div>,
                    );
                } else if (state.game.board.player2.coords !== null && state.game.board.player1.coords === null) {
                    board.push(
                        <div className='cell'
                             key={cell} style={cellStyle} onClick={() =>
                            handleCellClick(cell)}>
                            {
                                state.tag === 'loaded' &&
                                haveCellPiece(cell, player2X, player2Y) &&
                                state.game.board.player2.stone === 'b' &&
                                <div style={pieceStyleBlack}/>
                            }
                            {
                                state.tag === 'loaded' &&
                                haveCellPiece(cell, player2X, player2Y) &&
                                state.game.board.player2.stone === 'w' &&
                                <div style={pieceStyleWhite}/>
                            }
                        </div>,
                    );
                } else {
                    board.push(
                        <div className='cell'
                             key={cell} style={cellStyle} onClick={() =>
                            handleCellClick(cell)}>
                            {
                                state.tag === 'loaded' &&
                                haveCellPiece(cell, player1X, player1Y) &&
                                state.game.board.player1.stone == 'w' &&
                                <div style={pieceStyleWhite}/>
                            }
                            {
                                state.tag === 'loaded' &&
                                haveCellPiece(cell, player1X, player1Y) &&
                                state.game.board.player1.stone == 'b' &&
                                <div style={pieceStyleBlack}/>
                            }
                            {
                                state.tag === 'loaded' &&
                                haveCellPiece(cell, player2X, player2Y) &&
                                state.game.board.player2.stone == 'b' &&
                                <div style={pieceStyleBlack}/>
                            }
                            {
                                state.tag === 'loaded' &&
                                haveCellPiece(cell, player2X, player2Y) &&
                                state.game.board.player2.stone == 'w' &&
                                <div style={pieceStyleWhite}/>
                            }
                        </div>,
                    );
                }
            }
        }
        return (
            <>
                {uglyHeader}
                {uglyHeader}
                {board}
                <GiveUpFromGameForm/>
            </>
        );
    };

    if (state.tag === 'loaded') {
        return (
            <div>
                <div style={boardStyle}>
                    {createBoard()}
                </div>
            </div>
        );
    }

    return (
        <div>
            <div style={boardStyle}>
                {createBoard()}
            </div>
        </div>
    );
}


