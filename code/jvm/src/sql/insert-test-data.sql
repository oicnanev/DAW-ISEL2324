-- STATIC DATA -----------------------------------------------------------------
BEGIN;
insert into dbo.author (name, email) values ('Nuno Venâncio', '45824@alunos.isel.pt');
insert into dbo.author (name, email) values ('João Mafra', '45828@alunos.isel.pt');
insert into dbo.author (name, email) values ('Gonçalo Machuqueiro', '45831@alunos.isel.pt');
COMMIT;

BEGIN;
insert into dbo.version (version, created_at) values ('1.0.0', 1698846190); -- 01/11/2023 @ 01:43pm (UTC)
COMMIT;

BEGIN;
-- freestyle simple
insert into dbo.types (id, variant, opening, board_size) values (1, 'freestyle simple', 'open', 15);
insert into dbo.types (id, variant, opening, board_size) values (2, 'freestyle simple', 'open', 19);
insert into dbo.types (id, variant, opening, board_size) values (3, 'freestyle simple', 'swap', 15);
insert into dbo.types (id, variant, opening, board_size) values (4, 'freestyle simple', 'swap', 19);
insert into dbo.types (id, variant, opening, board_size) values (5, 'freestyle simple', 'swap2', 15);
insert into dbo.types (id, variant, opening, board_size) values (6, 'freestyle simple', 'swap2', 19);
insert into dbo.types (id, variant, opening, board_size) values (7, 'freestyle simple', 'pro', 15);
insert into dbo.types (id, variant, opening, board_size) values (8, 'freestyle simple', 'pro', 19);
insert into dbo.types (id, variant, opening, board_size) values (9, 'freestyle simple', 'long_pro', 15);
insert into dbo.types (id, variant, opening, board_size) values (10, 'freestyle simple', 'long_pro', 19);
-- freestyle swap after first move
insert into dbo.types (id, variant, opening, board_size) values (11, 'freestyle swap after 1st move', 'open', 15);
insert into dbo.types (id, variant, opening, board_size) values (12, 'freestyle swap after 1st move', 'open', 19);
insert into dbo.types (id, variant, opening, board_size) values (13, 'freestyle swap after 1st move', 'swap', 15);
insert into dbo.types (id, variant, opening, board_size) values (14, 'freestyle swap after 1st move', 'swap', 19);
insert into dbo.types (id, variant, opening, board_size) values (15, 'freestyle swap after 1st move', 'swap2', 15);
insert into dbo.types (id, variant, opening, board_size) values (16, 'freestyle swap after 1st move', 'swap2', 19);
insert into dbo.types (id, variant, opening, board_size) values (17, 'freestyle swap after 1st move', 'pro', 15);
insert into dbo.types (id, variant, opening, board_size) values (18, 'freestyle swap after 1st move', 'pro', 19);
insert into dbo.types (id, variant, opening, board_size) values (19, 'freestyle swap after 1st move', 'long_pro', 15);
insert into dbo.types (id, variant, opening, board_size) values (20, 'freestyle swap after 1st move', 'long_pro', 19);
-- renju
insert into dbo.types (id, variant, opening, board_size) values (21, 'renju', 'open', 15);
insert into dbo.types (id, variant, opening, board_size) values (22, 'renju', 'swap', 15);
insert into dbo.types (id, variant, opening, board_size) values (23, 'renju', 'swap2', 15);
insert into dbo.types (id, variant, opening, board_size) values (24, 'renju', 'pro', 15);
insert into dbo.types (id, variant, opening, board_size) values (25, 'renju', 'long_pro', 15);
insert into dbo.types (id, variant, opening, board_size) values (26, 'renju', 'soosorv', 15);  -- a different opening in renju :/
-- caro
insert into dbo.types (id, variant, opening, board_size) values (27, 'caro', 'open', 15);
insert into dbo.types (id, variant, opening, board_size) values (28, 'caro', 'open', 19);
insert into dbo.types (id, variant, opening, board_size) values (29, 'caro', 'swap', 15);
insert into dbo.types (id, variant, opening, board_size) values (30, 'caro', 'swap', 19);
insert into dbo.types (id, variant, opening, board_size) values (31, 'caro', 'swap2', 15);
insert into dbo.types (id, variant, opening, board_size) values (32, 'caro', 'swap2', 19);
insert into dbo.types (id, variant, opening, board_size) values (33, 'caro', 'pro', 15);
insert into dbo.types (id, variant, opening, board_size) values (34, 'caro', 'pro', 19);
insert into dbo.types (id, variant, opening, board_size) values (35, 'caro', 'long_pro', 15);
insert into dbo.types (id, variant, opening, board_size) values (36, 'caro', 'long_pro', 19);
-- omok
insert into dbo.types (id, variant, opening, board_size) values (37, 'omok', 'open', 19);
insert into dbo.types (id, variant, opening, board_size) values (38, 'omok', 'swap', 19);
insert into dbo.types (id, variant, opening, board_size) values (39, 'omok', 'swap2', 19);
insert into dbo.types (id, variant, opening, board_size) values (40, 'omok', 'pro', 19);
insert into dbo.types (id, variant, opening, board_size) values (41, 'omok', 'long_pro', 19);
-- ninuki-renju
insert into dbo.types (id, variant, opening, board_size) values (42, 'ninuki-renju', 'open', 15);
insert into dbo.types (id, variant, opening, board_size) values (43, 'ninuki-renju', 'swap', 15);
insert into dbo.types (id, variant, opening, board_size) values (44, 'ninuki-renju', 'swap2', 15);
insert into dbo.types (id, variant, opening, board_size) values (45, 'ninuki-renju', 'pro', 15);
insert into dbo.types (id, variant, opening, board_size) values (46, 'ninuki-renju', 'long_pro', 15);
-- pente
insert into dbo.types (id, variant, opening, board_size) values (47, 'pente', 'open', 15);
insert into dbo.types (id, variant, opening, board_size) values (48, 'pente', 'open', 19);
insert into dbo.types (id, variant, opening, board_size) values (49, 'pente', 'swap', 15);
insert into dbo.types (id, variant, opening, board_size) values (50, 'pente', 'swap', 19);
insert into dbo.types (id, variant, opening, board_size) values (51, 'pente', 'swap2', 15);
insert into dbo.types (id, variant, opening, board_size) values (52, 'pente', 'swap2', 19);
insert into dbo.types (id, variant, opening, board_size) values (53, 'pente', 'pro', 15);
insert into dbo.types (id, variant, opening, board_size) values (54, 'pente', 'pro', 19);
insert into dbo.types (id, variant, opening, board_size) values (55, 'pente', 'long_pro', 15);
insert into dbo.types (id, variant, opening, board_size) values (56, 'pente', 'long_pro', 19);
COMMIT;

BEGIN;
insert into dbo.users(username, password_validation, rank, games_played, wins, draws)
values ('TestGomoku', '$2a$10$AqS9wYR94IKfwNTjUOvyn.YG5LOJlL6tljkSuB/uadoCVB7.eLJMe', 0, 0, 0, 0);
insert into dbo.users(username, password_validation, rank, games_played, wins, draws)
values ('oicnanev', '$2a$10$AqS9wYR94IKfwNTjUOvyn.YG5LOJlL6tljkSuB/uadoCVB7.eLJMe', 0, 0, 0, 0);
COMMIT;
