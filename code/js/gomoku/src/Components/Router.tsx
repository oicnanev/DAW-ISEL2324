import * as React from 'react';
import { createBrowserRouter, Outlet } from 'react-router-dom';
import { Login } from './Login';
import { AuthnContainer } from './Authn'
import { RequireAuthn} from './RequireAuthn';
import { Home } from './Home';
import { Me } from './Me';
import { About } from './About';
import { UserDetail } from './UserDetails';
import { UserStats } from './UserStats';
import { Game } from './Game';
import { Rules } from './Rules';
import { Register } from './Register';
import { Logout } from './Logout';
import { Board } from './Board';


export const router = createBrowserRouter([
  {
    path: '/',
    element: <AuthnContainer><Outlet /></AuthnContainer>,
    children: [
      {
        path: '/',
        element: <Home />,
        children: [
          {
            path: '/authors',
            element: <About />,
          },
          {
            path: '/login',
            element: <Login />,
          },
          {
            path: '/register',
            element: <Register />,
          },
          {
            path: '/about',
            element: <About />,
          },
          {
            path: '/rules',
            element: <Rules />,
          },
          {
            path: '/logout',
            element: <Logout />,
          },
          {
            path: '/stats',
            element: <UserStats />,
          },
          {
            path: '/users/:uid',
            element: <RequireAuthn><UserDetail /></RequireAuthn>,
          },
          {
            path: '/games',
            element: <RequireAuthn><Game /></RequireAuthn>,
          },
          {
            path: '/games/:gid',
            element: <RequireAuthn><Board /></RequireAuthn>,
          },
          {
            path: '/me',
            element: <RequireAuthn><Me /></RequireAuthn>,
          },
        ],
      },
    ],
  }
]);
