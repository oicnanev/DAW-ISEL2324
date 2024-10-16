import * as React from 'react';
import {useEffect, useState} from 'react';

interface About {
    version: Version,
    authors: Author[]
}

interface Version {
    version: number
}

interface Author {
    name: string,
    email: string
}

// let fetched = false;

export function About() {
    const [about, setAbout] = useState<About | undefined>(undefined);

    useEffect(() => {
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

        return () => {
            console.log('cleanup');
        }
    }, []);

    const aboutStyle = { padding: '70px' };
    const textAreaStyle = { padding: '10px' };

    return (
        <section className="vh-100 gradient-custom">
            <main style={aboutStyle} role="main" className="container">
                <div className="card bg-dark text-white-50" style={textAreaStyle}>
                    <h2><i className="bi bi-emoji-sunglasses"></i> Authors:</h2>
                    <ul>
                        {about?.authors.map((value, index) => (
                            <li key={index}> {value.name} <br/>
                                <i className="bi bi-envelope"></i>
                                <a href={`mailto:${value.email}`}> {value.email}</a>
                            </li>
                        ))}
                    </ul>
                    <hr/>
                    <h5><i className="bi bi-gear"></i> version: {about?.version.version}</h5>
                </div>
            </main>
        </section>
    );
}
