-- show databases
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

-- Switch to tc Database change
\connect tcspike tcspikeuser
--Or:

\c tcspike tcspikeuser
--You are now connected to database "tcspike" as user "tcspikeuser".



