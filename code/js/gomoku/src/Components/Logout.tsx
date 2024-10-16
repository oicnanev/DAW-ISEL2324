import * as React from 'react';
import { useEffect } from 'react';
import { useCurrentUser, useSetUser } from './Authn';
import { Navigate } from 'react-router-dom';

export function Logout() {
  const currentUser = useCurrentUser();
  const setCurrentUser = useSetUser();

  useEffect(() => {
    const fetchMe = async () => {
      try {
        const response = await fetch('/api/logout', {
          method: 'POST'
        } );
        if(response.ok) {
          setCurrentUser(undefined);
        }
      } catch (error) {
        console.log(error);
        return (
          <>
            <div>Failed to fetch user home</div>
            <div>{error.message}</div>
          </>
        );
      }
    };
    fetchMe().then(
      (response) => {
        return response;
      },
      (error) => {
        console.log(error);
      }
    )
  }, [currentUser, setCurrentUser]);

  return (
    <>
      <Navigate to="/" state={{source: location.pathname}} replace={true}/>
    </>
  );
}
