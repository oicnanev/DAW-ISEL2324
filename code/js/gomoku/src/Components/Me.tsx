import * as React from 'react';
import {useEffect, useState} from 'react';
import {useCurrentUser} from './Authn';

interface Me {
    id: number,
    username: string,
    rank: number,
    gamesPlayed: number,
    wins: number,
    draws: number
}

export function Me() {
    const [userHome, setUserHome] = useState<Me | undefined>(undefined);
    const currentUser = useCurrentUser();

    useEffect(() => {
        const fetchMe = async () => {
            try {
                const response = await fetch('/api/me', {
                    method: 'GET'
                });
                const userHome = await response.json();
                setUserHome(userHome);
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
        fetchMe();

        return () => {
            console.log('cleanup');
        }
    }, [currentUser]);

    const meStyle = {padding: '70px'};
    const textAreaStyle = {padding: '10px'};

    if (userHome !== undefined) {
        return (
            <section className="vh-100 gradient-custom">
                <main style={meStyle} role='main' className='container'>
                    <div className='card bg-dark text-white-50' style={textAreaStyle}>
                        <h2><i className="bi bi-person"></i> {userHome.username}</h2>
                        <br/>
                        <div>
                            <h4><i className="bi bi-dot"></i> Rank: {userHome.rank}</h4>
                            <hr/>
                            <h4><i className="bi bi-dot"></i> Games Played: {userHome.gamesPlayed}</h4>
                            <h4><i className="bi bi-dot"></i> Wins: {userHome.wins}</h4>
                            <h4><i className="bi bi-dot"></i> Draws: {userHome.draws}</h4>
                        </div>
                    </div>
                </main>
            </section>
        )
    }

    return (
        <section className="vh-100 gradient-custom">
            <main style={meStyle} role='main' className='container'>
                <div className='card bg-dark text-white-50' style={textAreaStyle}>
                    <h2><i className="bi bi-person-vcard"></i> {currentUser[0]}</h2>
                    <br/>
                    <div>
                        <h4><i className="bi bi-dot"></i> Rank: 0</h4>
                        <hr/>
                        <h4><i className="bi bi-dot"></i> Games Played: 0</h4>
                        <h4><i className="bi bi-dot"></i> Wins: 0</h4>
                        <h4><i className="bi bi-dot"></i> Draws: 0</h4>
                    </div>
                </div>
            </main>
        </section>
    )
}
