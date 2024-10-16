import * as React from 'react';

import { Link, Outlet, useParams } from 'react-router-dom';


export function UserDetail() {
    const { uid } = useParams()
    return (
        <div>
            <h2>User Detail</h2>
            {uid}
            <p><Link to="latest">Latest Games</Link></p>
            <p><Link to="stats">Statistics</Link></p>
            <Outlet />
        </div>
    )
}