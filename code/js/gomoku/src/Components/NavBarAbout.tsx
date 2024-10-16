import { Link } from 'react-router-dom';
import * as React from 'react';


export function NavBarAbout() {
  return (
    <>
      <li className='nav-item'>
        <Link to='/about' className='nav-link' aria-current='page'>
          <i className='bi bi-info-circle'></i> About
        </Link>
      </li>
    </>
  );
}