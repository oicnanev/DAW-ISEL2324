import * as React from 'react';
import { useEffect, useState } from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useCurrentUser } from './Authn';


export type GameProps = {
  id: number,
  board: Board,
  opening: string,
  variant: string,
  currentPlayer: number,
  player1: string,
  player2: string,
  gameState: string,
  startTime: number,
  lastUpdated: number,
}


type Board = {
  size: number,
  player1: PlayerBoard,
  player2: PlayerBoard
}

type PlayerBoard = {
  stone: string,
  coords: Coords | null,
  userId: number,
}

export type Coords = Play[] | null;


export type Play = {
  timestamp: number,
  x: number,
  y: number,
}

type GameLobbyProps = {
  boardSize: number,
  id: number,
  opening: string,
  rank: number,
  username: string,
  variant: string,
}

type ListLobbyProps =
  | { outputLobbyGameList: Array<GameLobbyProps> | undefined }

const gamesStyle = { padding: '70px' };
const textAreaStyle = { padding: '10px' };

type State =
  | { tag: 'editing'; error?: string; inputs: { boardSize: number } }
  | { tag: 'submitting'; boardSize: number; }
  | { tag: 'redirect'; }
  | { tag: 'preloading'; result?: Array<GameLobbyProps> }
  | { tag: 'waiting'; };

type Action =
  | { type: 'edit'; inputName: string; inputValue: string }
  | { type: 'submit' }
  | { type: 'error'; message: string }
  | { type: 'success'; result?: ListLobbyProps }
  | { type: 'wait'; }
  | { type: 'cancel'; };

function logUnexpectedAction(state: State, action: Action) {
  console.log(`Unexpected action ${action.type} on state ${state.tag}`);
}


function reduce(state: State, action: Action): State {
  switch (state.tag) {
    case 'editing':
      if (action.type === 'edit') {
        return {
          tag: 'editing',
          error: undefined,
          inputs: { ...state.inputs, [action.inputName]: action.inputValue },
        };
      } else if (action.type === 'submit') {
        return { tag: 'submitting', boardSize: state.inputs.boardSize };
      } else {
        logUnexpectedAction(state, action);
        return state;
      }
    case 'submitting':
      if (action.type === 'success') {
        return { tag: 'redirect' };
      } else if (action.type === 'error') {
        return { tag: 'editing', error: action.message, inputs: { boardSize: state.boardSize } };
      } else if (action.type === 'wait') {
        return { tag: 'waiting' };
      } else if (action.type === 'cancel') {
        return { tag: 'preloading' };
      } else {
        logUnexpectedAction(state, action);
        return state;
      }
    case 'preloading':
      if (action.type === 'success') {
        return {
          tag: 'editing',
          error: undefined,
          inputs: { boardSize: 15 },
        };
      }
      if (action.type === 'wait') {
        return {
          tag: 'waiting',
        };
      } else {
        logUnexpectedAction(state, action);
        return state;
      }
    case 'waiting':
      if (action.type === 'wait') {
        return {
          tag: 'waiting',
        };
      } if (action.type === 'success') {
        return { tag: 'redirect' };
      } if (action.type === 'cancel') {
        return { tag: 'preloading' };
      } else {
        logUnexpectedAction(state, action);
        return state;
      }
    case 'redirect':
      logUnexpectedAction(state, action);
      return state;
  }
}

async function createGame(boardSize: number): Promise<string | undefined> {
  const response = await fetch('/api/lobby', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      'variant': 'freestyle simple',
      'opening': 'open',
      'boardSize': boardSize,
    }),
  });
  if (response.ok) {  // response.status >= 200 && response.status < 300
    return response.status.toString();
  } else {
    return undefined;
  }
}



type joinBody = { id: number }

async function joinGame(body:joinBody): Promise<number | undefined> {
  const response = await fetch(`/api/joingame/${body.id}` , {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(body),
  });
  if (response.ok) {  // response.status >= 200 && response.status < 300
    const gameId = await response.json()
    console.log(`game = ${Object.values(gameId.value)}`);
    return gameId.value //response.status.toString();
  } else {
    return undefined;
  }
}

export function Game() {

  const [listLobby, setListLobby] =
    useState<ListLobbyProps | undefined>(undefined);
  const [state, dispatch] = React.useReducer(reduce, { tag: 'preloading' });
  const [error, setError] = useState(null);
  const [lobbyGameId, setLobbyGameId] = useState<number | undefined>(undefined);
  const [gameId, setGameId] = useState<number | undefined>(undefined);
  const location = useLocation();
  const user = useCurrentUser();

  async function cancelGame(): Promise<string | undefined> {
    const response = await fetch(`/api/lobby/${lobbyGameId}`, {
      method: 'DELETE'
    });
    if (response.ok) {  // response.status >= 200 && response.status < 300
      return response.status.toString();
    } else {
      return undefined;
    }
  }

  if (state.tag === 'preloading') {
    fetchLobby().then(it => {
      console.log(it.outputLobbyGameList);
      console.log('Current user:', user);
      setListLobby(it);
      const found = it.outputLobbyGameList.find((game) => game.username === user);
      if (found) {
        console.log('Found active game for current user');
        console.log("AQUI "+found.id)
        setLobbyGameId(found.id)
        dispatch({ type: 'wait' });
      } else {
        console.log('No active game found for current user');
        dispatch({ type: 'success', result: it });
      }
    });
  }

  async function fetchLobby(): Promise<ListLobbyProps | undefined> {
    try {
      const response = await fetch('/api/lobby');
      const temp = await response.json();
      return temp;
    } catch (error) {
      console.log(error);
      setError(error.message);
    }
  }

  async function checkPartner(): Promise<number | undefined> {
    try {
      const response = await fetch('/api/checkmatch');
      const game: GameProps = await response.json();
      console.log(game.id)
      console.log(`function checkPartner() ${Object.values(game)}`);
      if (game.id) {
        return game.id;
      } else
        return -1;
    } catch (error) {
      console.log(error);
      setError(error.message);
    }
  }

  useEffect(() => {
    const keepItMoving = async () => {      
      if (state.tag === 'waiting') {
        console.log('Currently Waiting in UseEffect');
        checkPartner().then(it => {
          if (it == -1) {
            console.log('No partner found')
          } else {
            console.log('Partner found');
            setGameId(it);
            dispatch({ type: 'success' });
          }
        });
      }
    };
    const iid =  setInterval( () => {
     keepItMoving()
  },2000);

  return( () => {
    clearInterval(iid)
  })
    
  }, [state.tag, error]);


  if (state.tag === 'redirect') {
    console.log(gameId);
    return <Navigate to={location.state?.source.pathname || `/games/${gameId}`} replace={true} />;
  }

    function handleGiveUp(ev: React.FormEvent<HTMLFormElement>): void {
      ev.preventDefault();
      cancelGame().then(() => dispatch({ type: 'cancel' }));
    }

    function GiveUpFromGameForm() {
      if (state.tag === 'waiting') {
        return (
          <form onSubmit={handleGiveUp}>
            <div>
              <button className="bg-dark btn btn-outline-light btn-lg px-5" type="submit">Give Up</button>
            </div>
          </form>
        );
      }
    }

  if (state.tag === 'waiting') {
    return (
      <section className='vh-100 gradient-custom'>
        <main style={gamesStyle} role='main' className='container'>
          <div className='card bg-dark text-white-50' style={textAreaStyle}>
            <h1>Waiting for Partner</h1>
            <GiveUpFromGameForm />
          </div>
        </main>
      </section>
    );
  }

  function handleChange(ev: React.ChangeEvent<HTMLInputElement>) {
    dispatch({
      type: 'edit',
      inputName: ev.target.name,
      inputValue: ev.target.value,
    });
  }

  function handleSubmit(ev: React.FormEvent<HTMLFormElement>) {
    ev.preventDefault();

    if (state.tag !== 'editing') {
      return;
    }
    dispatch({ type: 'submit' });
    const boardSize = state.inputs.boardSize;
    createGame(boardSize)
      .then(res => {
        if (res) {
          console.log(`setUser(${res})`);
          dispatch({ type: 'cancel' });
        } else {
          dispatch({ type: 'error', message: 'Invalid board size' });
        }
      })
      .catch(error => {
        console.error(error);
        dispatch({ type: 'error', message: error.message });
      });
  }

  function handleSubmitJoin(id: number, ev: React.FormEvent<HTMLFormElement>) {
    ev.preventDefault();
    if (state.tag !== 'editing') {
      return;
    }
    dispatch({ type: 'submit' });
    joinGame({id})
      .then(res => {
        if (res) {
          setGameId(res)
          console.log(`gameId: => (${res})`);
          dispatch({ type: 'success' });
          console.log('Joining game');
        } else {
          dispatch({ type: 'error', message: 'Invalid game id' });
          console.log('Not joining game');
        }
      });
  }

  function ShowListLobby() {
    if (state.tag === 'editing') {
      return (
        <ul>
          {listLobby.outputLobbyGameList.map((lobby, index) => (
              <li key={index}>
                <form onSubmit={(event) => handleSubmitJoin(lobby.id, event)}>
                  <div><b><i className='bi bi-person'></i> User: {lobby.username}</b></div>
                  <div><i className='bi bi-award'></i> Rank: {lobby.rank}</div>
                  <div><i className='bi bi-signpost-split'></i> Variant: {lobby.variant}</div>
                  <div><i className='bi bi-play'></i> Opening: {lobby.opening}</div>
                  <div><i className='bi bi-grid-3x3'></i> Size: {lobby.boardSize} x {lobby.boardSize}</div>
                  <br />
                  <div>
                    <button type={'submit'} className='btn btn-outline-light btn-lg px-5'>
                      Join
                    </button>
                  </div>
                </form>
              </li>
            ),
          )}
        </ul>
      );
    }
  }

  return (
    <section className='vh-100 gradient-custom'>
      <main style={gamesStyle} role='main' className='container'>
        <div className='card bg-dark text-white-50' style={textAreaStyle}>
          <h2><i className='bi bi-grid-3x3'></i> Games:</h2>
          <ShowListLobby />
          <br />
          <form onSubmit={handleSubmit}>
            <h4>Create a new Game</h4>
            <ul>
              <li>
                <div className='form-check-label form-check-inline'>Board Size:</div>
                <div className='form-check form-check-inline'>
                  <input className='form-check-input' type='radio' name='boardSize' id='boardSize15'
                         value='15' onChange={handleChange} />
                  <label className='form-check-label' htmlFor='boardSize'>15 x 15</label>
                </div>
                <div className='form-check form-check-inline'>
                  <input className='form-check-input' type='radio' name='boardSize' id='boardSize19'
                         value='19' onChange={handleChange} />
                  <label className='form-check-label' htmlFor='boardSize'>19 x 19</label>
                </div>
              </li>
              <li>
                <div className='form-check-label form-check-inline'>Opening:</div>
                <div className='form-check form-check-inline'>
                  <input className='form-check-input selected' type='radio' name='opening' id='openingopen'
                         value='open' checked onChange={handleChange} />
                  <label className='form-check-label' htmlFor='opening'>Open</label>
                </div>
              </li>
              <li>
                <div className='form-check-label form-check-inline'>Variant:</div>
                <div className='form-check form-check-inline'>
                  <input className='form-check-input' type='radio' name='variant' id='varianFreestyleSimple'
                         value='freestyleSimple' checked onChange={handleChange} />
                  <label className='form-check-label' htmlFor='variant'>Freestyle Simple</label>
                </div>
                <button type={'submit'} className='btn btn-outline-light btn-lg px-5'>Create</button>
              </li>
            </ul>
          </form>
          {error && (
            <div className='alert alert-danger' role='alert'>
              <p>Failed to fetch list lobby games</p>
              <p>{error}</p>
            </div>
          )}
        </div>
      </main>
    </section>
  );
}
