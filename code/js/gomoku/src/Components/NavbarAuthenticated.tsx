import * as React from 'react';
import { NavBarCommon } from './NavBarCommon';
import { NavBarGamesUser } from './NavBarGamesUser';
import { NavBarAbout } from './NavBarAbout';

export function NavbarAuthenticated() {
  return (
    <>
      <nav className='navbar navbar-expand-lg bg-body-tertiary bg-dark border-bottom border-body fixed-top'
           data-bs-theme='dark'>
        <div className='container-fluid'>
          <div className='navbar-brand'>
            <img
              src='https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRVJh5FkDZEztPY4jztuR-32Q-bBlbNZeFexb_civxhbF8W8mJhZaHw89kav54Dyd9SFyk&usqp=CAU'
              alt='gomoku' width='30' height='30' />
            <b> Gomoku</b>
          </div>
          <button className='navbar-toggler' type='button' data-bs-toggle='collapse' data-bs-target='#navbarNav'
                  aria-controls='navbarNav' aria-expanded='false' aria-label='Toggle navigation'>
            <span className='navbar-toggler-icon'></span>
          </button>
          <div className='collapse navbar-collapse' id='navbarNav'>
            <ul className='navbar-nav'>
              <NavBarCommon />
              <NavBarGamesUser />
              <NavBarAbout />
              </ul>
          </div>
        </div>
      </nav>
    </>
  );
}
