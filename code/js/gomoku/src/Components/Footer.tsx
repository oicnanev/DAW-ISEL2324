import { useEffect, useState } from 'react';
import * as React from 'react';


interface About {
  version: Version,
  authors: Author[]
}

interface Version {
  version: number;
}

interface Author {
  name: string,
  email: string
}

export function Footer() {
  const [about, setAbout] = useState<About | undefined>(undefined);

  useEffect(() => {  // to get version number in the footer
    const fetchAbout = async () => {
      try {
        const response = await fetch('/api/about');
        const about = await response.json();
        setAbout(about);
      } catch (error) {
        console.log(error);
        return (
          <>
            <div>Failed to fetch about</div>
            <div>{error.message}</div>
          </>
        );
      }
    };
    fetchAbout();
  }, []);

  return (
    <footer className='footer fixed-bottom bg-dark'>
        <div className='container bg-dark'>
                <span className='bg-dark d-flex justify-content-end'>
                    <a className='nav-link text-bg-dark bg-dark'>
                      <i className='bi bi-gear'></i> Gomoku version {about?.version.version}
                    </a>
                </span>
        </div>
      </footer>
  );
}
