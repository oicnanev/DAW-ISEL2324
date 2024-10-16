import * as React from 'react';
import { Link } from 'react-router-dom';

export function NavBarGamesUser() {
  return (
    <>
      <li className='nav-item'>
        <Link to='/games' className='nav-link' aria-current='page'>
          <i className='bi bi-grid-3x3'></i> Game
        </Link>
      </li>
      <li className='nav-item'>
        <Link to='/me' className='nav-link' aria-current='page'>
          <i className='bi bi-person'></i> Me
        </Link>
      </li>
      <li className='nav-item'>
        <Link to='/logout' className='nav-link' aria-current='page'>
          <i className='bi bi-person-x'></i> Logout
        </Link>
      </li>
    </>
  );
}
