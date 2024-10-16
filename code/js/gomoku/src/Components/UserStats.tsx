import * as React from 'react';
import {useEffect, useState} from 'react';

interface UserRank {
    username: string,
    rank: number,
    variant: string,
    opening: string,
    gamesPlayed: number,
    wins: number,
    draws: number,
    link: string
}

interface Rankings {
    userRanks: UserRank[]
}

export function UserStats() {
    const [rankings, setRankings] =
        useState<Rankings | undefined>(undefined);

    useEffect(() => {
        const fetchStats = async () => {
            try {
                const response = await fetch('/api/stats');
                const stats = await response.json();
                setRankings(stats);
            } catch (error) {
                console.log(error);
                return (
                    <>
                        <div>Failed to fetch rankings</div>
                        <div>{error.message}</div>
                    </>
                );
            }
        };
        fetchStats();
    }, []);

    const rankingsStyle = {padding: '70px'};
    const textAreaStyle = {padding: '10px'};

    return (
        <section className="vh-100 gradient-custom">
            <main style={rankingsStyle} role="main" className="container">
                <div className="card bg-dark text-white-50" style={textAreaStyle}>
                    <h2><i className="bi bi-graph-up-arrow"></i> Rankings:</h2>
                    <ul>
                        {Object.values(rankings?.userRanks?.map((userRank, index) => (
                                <li key={index}>
                                    <p><b><i className="bi bi-person"></i> User: {userRank.username}</b></p>
                                    <p><i className="bi bi-award"></i> Rank: {userRank.rank}</p>
                                    <p><i className="bi bi-signpost-split"></i> Variant: {userRank.variant}</p>
                                    <p><i className="bi bi-play"></i> Opening: {userRank.opening}</p>
                                    <p><i className="bi bi-clipboard-data"></i> Games Played: {userRank.gamesPlayed}</p>
                                    <p><i className="bi bi-trophy"></i> Wins: {userRank.wins}</p>
                                    <p><i className="bi bi-fire"></i> Draws: {userRank.draws}</p>
                                    <p><i className="bi bi-person-vcard"></i> Page: <a
                                        href={userRank.link}>{userRank.link}</a>
                                    </p>
                                </li>
                            )
                        ) ?? [])}
                    </ul>
                </div>
            </main>
        </section>
    );
}