import * as React from 'react';
import { Outlet } from 'react-router-dom';
import { useCurrentUser } from './Authn';
import { Footer} from './Footer';
import { NavbarAuthenticated } from './NavbarAuthenticated';
import { NavbarUnauthenticated } from './NavbarUnauthenticated';


export function Home() {
  const currentUser = useCurrentUser();

  if (currentUser) {
    return (
      <div>
        <NavbarAuthenticated />
        <Footer />
        <Outlet />
    </div>
    )
  }

  return (
    <div>
      <NavbarUnauthenticated />
      <Footer />
      <Outlet />
    </div>
  );
}
