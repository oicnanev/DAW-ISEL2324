import * as React from 'react';
import { Link } from 'react-router-dom';

export function NavBarLoginRegister() {
  return (
    <>
      <li className='nav-item'>
        <Link to='/login' className='nav-link' aria-current='page'>
          <i className='bi bi-person'></i> Login
        </Link>
      </li>
      <li className='nav-item'>
        <Link to='/register' className='nav-link' aria-current='page'>
          <i className='bi bi-person-plus'></i> Register
        </Link>
      </li>
    </>
  );
}
