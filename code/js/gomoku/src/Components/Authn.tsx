import * as React from 'react';
import { useState, createContext, useContext, } from 'react';
import Cookies from 'js-cookie';

type ContextType = {
  user: string | undefined,
  setUser: (v: string | undefined) => void
}

const LoggedInContext = createContext<ContextType>({
  user: undefined,
  setUser: () => { }
})

export function AuthnContainer({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<string | undefined>(Cookies.get('gomoku'));
  return (
    <LoggedInContext.Provider value={{ user: user, setUser: setUser }}>
      {children}
    </LoggedInContext.Provider>
  )
}

export function useCurrentUser(): string | undefined {
  return useContext(LoggedInContext).user
}

export function useSetUser() {
  return useContext(LoggedInContext).setUser
}