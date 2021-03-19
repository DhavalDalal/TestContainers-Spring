-- Show Databases
\l
--                             List of databases
--     Name    |  Owner  | Encoding | Collate | Ctype |  Access privileges
--  -----------+---------+----------+---------+-------+---------------------
--   postgres  | dhavald | UTF8     | C       | C     |
--   template0 | dhavald | UTF8     | C       | C     | =c/dhavald         +
--             |         |          |         |       | dhavald=CTc/dhavald
--   template1 | dhavald | UTF8     | C       | C     | =c/dhavald         +
--             |         |          |         |       | dhavald=CTc/dhavald
--  (3 rows)

CREATE DATABASE tcspike\g

\l

--                             List of databases
--     Name    |  Owner  | Encoding | Collate | Ctype |  Access privileges
--  -----------+---------+----------+---------+-------+---------------------
--   postgres  | dhavald | UTF8     | C       | C     |
--   tcspike   | dhavald | UTF8     | C       | C     |
--   template0 | dhavald | UTF8     | C       | C     | =c/dhavald         +
--             |         |          |         |       | dhavald=CTc/dhavald
--   template1 | dhavald | UTF8     | C       | C     | =c/dhavald         +
--             |         |          |         |       | dhavald=CTc/dhavald
--  (4 rows)


-- Show users
\du

--   Role name |                         Attributes                         | Member of
--  -----------+------------------------------------------------------------+-----------
--   dhavald   | Superuser, Create role, Create DB, Replication, Bypass RLS | {}

CREATE USER tcspikeuser WITH ENCRYPTED PASSWORD 'TcspikePassword'\g

\du
--                                      List of roles
--    Role name  |                         Attributes                         | Member of
--  -------------+------------------------------------------------------------+-----------
--   dhavald     | Superuser, Create role, Create DB, Replication, Bypass RLS | {}
--   tcspikeuser |                                                            | {}

GRANT ALL PRIVILEGES ON DATABASE tcspike TO tcspikeuser\g


\l
--                               List of databases
--     Name    |  Owner  | Encoding | Collate | Ctype |    Access privileges
--  -----------+---------+----------+---------+-------+-------------------------
--   postgres  | dhavald | UTF8     | C       | C     |
--   tcspike   | dhavald | UTF8     | C       | C     | =Tc/dhavald            +
--             |         |          |         |       | dhavald=CTc/dhavald    +
--             |         |          |         |       | tcspikeuser=CTc/dhavald
--   template0 | dhavald | UTF8     | C       | C     | =c/dhavald             +
--             |         |          |         |       | dhavald=CTc/dhavald
--   template1 | dhavald | UTF8     | C       | C     | =c/dhavald             +
--             |         |          |         |       | dhavald=CTc/dhavald
--  (4 rows)
