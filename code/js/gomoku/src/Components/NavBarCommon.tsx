import { Link } from 'react-router-dom';
import * as React from 'react';

export function NavBarCommon() {
  return (
    <>
      <li className='nav-item'>
        <Link to='/' className='nav-link active' aria-current='page'>
          <i className='bi bi-house'></i> Home
        </Link>
      </li>
      <li className='nav-item'>
        <Link to='/rules' className='nav-link' aria-current='page'>
          <i className='bi bi-journal'></i> Rules
        </Link>
      </li>
      <li className='nav-item'>
        <Link to='/stats' className='nav-link' aria-current='page'>
          <i className='bi bi-graph-up-arrow'></i> Rankings
        </Link>
      </li>
    </>
  );
}