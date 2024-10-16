import * as React from 'react';
import { router } from './Components/Router';
import { RouterProvider } from 'react-router-dom';


export function App() {
  return (
    <RouterProvider router={router} />
  );
}


