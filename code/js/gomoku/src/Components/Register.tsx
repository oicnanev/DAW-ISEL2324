import * as React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
//import { useSetUser } from './Authn';

type State =
  | { tag: 'editing'; error?: string; inputs: { username: string; password: string } }
  | { tag: 'submitting'; username: string }
  | { tag: 'redirect' };

type Action =
  | { type: 'edit'; inputName: string; inputValue: string }
  | { type: 'submit' }
  | { type: 'error'; message: string }
  | { type: 'success' };

function logUnexpectedAction(state: State, action: Action) {
  console.log(`Unexpected action '${action.type} on state '${state.tag}'`);
}

function reduce(state: State, action: Action): State {
  switch (state.tag) {
    case 'editing':
      if (action.type === 'edit') {
        return { tag: 'editing', error: undefined, inputs: { ...state.inputs, [action.inputName]: action.inputValue } };
      } else if (action.type === 'submit') {
        return { tag: 'submitting', username: state.inputs.username };
      } else {
        logUnexpectedAction(state, action);
        return state;
      }

    case 'submitting':
      if (action.type === 'success') {
        return { tag: 'redirect' };
      } else if (action.type === 'error') {
        return { tag: 'editing', error: action.message, inputs: { username: state.username, password: '' } };
      } else {
        logUnexpectedAction(state, action);
        return state;
      }

    case 'redirect':
      logUnexpectedAction(state, action);
      return state;
  }
}

async function fetchRegister(username: string, password: string): Promise<number | undefined> {
  const response =
    await fetch('/api/users', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        username: username,
        password: password,
      }),
    });
  if (response.ok) {  // response.status >= 200 && response.status < 300
    return response.status;
  } else {
    return undefined;
  }
}


export function Register() {
  console.log('Login');
  const [state, dispatch] = React.useReducer(reduce, { tag: 'editing', inputs: { username: '', password: '' } });
  //const setUser = useSetUser();
  const location = useLocation();
  if (state.tag === 'redirect') {
    alert('User Successfully Created, Please Login.');
    return <Navigate to={location.state?.source?.pathname || '/login'} replace={true} />;
  }

  function handleChange(ev: React.FormEvent<HTMLInputElement>) {
    dispatch({ type: 'edit', inputName: ev.currentTarget.name, inputValue: ev.currentTarget.value });
  }

  function handleSubmit(ev: React.FormEvent<HTMLFormElement>) {
    ev.preventDefault();
    if (state.tag !== 'editing') {
      return;
    }
    dispatch({ type: 'submit' });
    const username = state.inputs.username;
    const password = state.inputs.password;
    fetchRegister(username, password)
      .then(res => {
        if (res) {
          dispatch({ type: 'success' });
        } else {
          dispatch({ type: 'error', message: 'Invalid username or password' });
        }
      })
      .catch(error => {
        dispatch({ type: 'error', message: error.message });
      });
  }

  const username = state.tag === 'submitting' ? state.username : state.inputs.username;
  const password = state.tag === 'submitting' ? '' : state.inputs.password;
  const registerStyle = { borderRadius: '1rem' };

  return (
    <section className="vh-100 gradient-custom">
      <div className="container py-5 h-100">
        <div className="row d-flex justify-content-center align-items-center h-100">
          <div className="col-12 col-md-8 col-lg-6 col-xl-5">
            <div className="card bg-dark text-white" style={registerStyle}>
              <div className="card-body p-5 text-center">
                <div className="mb-md-5 mt-md-4 pb-5">
                  <form onSubmit={handleSubmit}>
                    <fieldset disabled={state.tag !== 'editing'}>
                      <h2 className="fw-bold mb-2 text-uppercase">Sign up</h2>
                      <p className="text-white-50 mb-5">Please enter a desired username and password!</p>
                      <div className="form-outline form-white mb-4">
                        <label className="form-label" htmlFor="username">Username</label>
                        <input className="form-control form-control-lg" id="username" type="text" name="username" value={username} onChange={handleChange}/>
                      </div>
                      <div className="form-outline form-white mb-4">
                        <label className="form-label" htmlFor="password">Password</label>
                        <input className="form-control form-control-lg" id="password" type="password" name="password" value={password} onChange={handleChange}/>
                      </div>
                      <div>
                        <button className="btn btn-outline-light btn-lg px-5" type="submit">Sign up</button>
                      </div>
                    </fieldset>
                    {state.tag === 'editing' && state.error}
                  </form>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}