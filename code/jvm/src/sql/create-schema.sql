-- DATABASE --------------------------------------------------------------------
CREATE SCHEMA dbo;

-- TABLES ----------------------------------------------------------------------
CREATE TABLE dbo.users (
	id serial primary key,
	username varchar(64) unique not null,
	password_validation varchar(265) not null,
	rank integer not null DEFAULT 0,
	games_played integer not null DEFAULT 0,
	wins integer not null DEFAULT 0,
	draws integer not null DEFAULT 0
	-- rank, games_played, wins and draws updated automatically by function and trigger
);

CREATE TABLE dbo.token (
	token_validation varchar(256) primary key,
	user_id integer not null references dbo.users(id),
	created_at bigint not null,
	last_used_at bigint not null
);

CREATE TABLE dbo.types (
	id smallint primary key,
	variant varchar(32) not null CHECK (variant IN (
		'freestyle simple', 'freestyle swap after 1st move',
        'renju', 'caro', 'omok', 'ninuki-renju', 'pente')),
	opening varchar(8) not null CHECK (opening IN (
		'open', 'swap', 'swap2', 'pro', 'long_pro', 'soosorv')),
	board_size smallint not null CHECK (board_size IN (15, 19)),
	UNIQUE (variant, opening, board_size)
	-- we gone repeat the data but are not a big table
);

CREATE TABLE dbo.lobby (
	id bigserial primary key,
	user_id integer not null references dbo.users(id),
	type_id integer not null references dbo.types(id),
	created_at bigint not null,
	active boolean not null DEFAULT true -- need to be false after 30 min (function and trigger)
	--CONSTRAINT unique_user_when_active CHECK ((active = false) or (user_id IS DISTINCT FROM lobby.user_id)) -- prevent same user to create more games
);

CREATE TABLE dbo.game (
	id bigserial primary key,
	type_id integer not null references dbo.types(id),
	board jsonb not null,
	-- {"size": 15, "player1": {"userId": 1231, "stone": "b", "coords": {"1": {timestamp": 1231245132535, "x": 0, "y": 3}}}, "player2": {....}}
	-- stone is b (black) or w (white), coords is an object of num_turn_objects, num_turn_object have a timestamp, a X and an Y
	player1_id integer not null references dbo.users(id),
	player2_id integer not null references dbo.users(id) CHECK (player2_id <> player1_id),
	current_player int not null CHECK (current_player IN (1, 2)),
	state varchar(16) not null CHECK (state in ('in-progress', 'player1_won', 'player2_won', 'draw', 'timed-out')),
	created_at bigint not null,
	updated_at bigint not null -- state need to be "timed-out" after 5 min (function and trigger)
);

CREATE TABLE dbo.author (
	email varchar(256) primary key, -- https://www.rfc-editor.org/rfc/rfc5321#section-4.5.3, section 4.5.3.1.3 Path says maximum 256
	name varchar(256) not null,
	CONSTRAINT "email_format_chk" CHECK (email LIKE '%@%.%')
);

CREATE TABLE dbo.version (
	version varchar(5) primary key,
	created_at bigint not null
);

-- VIEWS -----------------------------------------------------------------------
CREATE OR REPLACE VIEW dbo.user_stats_by_game_type_view AS
SELECT
    u.id AS user_id,  -- in the app show username and rank
    t.id AS type_id,  -- in the app show variant, opening, and board_size
    COALESCE(gpg.games_played, 0) AS games_played,
    COALESCE(gpg.wins, 0) AS wins,
    COALESCE(gpg.draws, 0) AS draws
FROM
    dbo.users u
CROSS JOIN
    dbo.types t
LEFT JOIN (
    SELECT
        u.id AS user_id,
        g.type_id AS type_id,
        COUNT(g.id) AS games_played,
        SUM(CASE
            WHEN (g.state = 'player1_won' AND u.id = g.player1_id) THEN 1
            WHEN (g.state = 'player2_won' AND u.id = g.player2_id) THEN 1
            ELSE 0
        END) AS wins,
        SUM(CASE WHEN g.state = 'draw' AND (u.id = g.player1_id OR u.id = g.player2_id) THEN 1 ELSE 0 END) AS draws
    FROM
        dbo.users u
    CROSS JOIN
        dbo.types t
    LEFT JOIN
        dbo.game g ON g.type_id = t.id
    GROUP BY
        u.id, t.id, g.type_id
) gpg ON u.id = gpg.user_id AND t.id = gpg.type_id
ORDER BY games_played DESC;
