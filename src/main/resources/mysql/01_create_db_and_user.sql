show databases;
-- +--------------------+
-- | Database           |
-- +--------------------+
-- | information_schema |
-- | mysql              |
-- | performance_schema |
-- | sys                |
-- +--------------------+

-- Creates the new database
create database tcspike;

show databases;
-- +--------------------+
-- | Database           |
-- +--------------------+
-- | tcspike            |
-- | information_schema |
-- | mysql              |
-- | performance_schema |
-- | sys                |
-- +--------------------+

select user, host from mysql.user;
-- +------------------+-----------+
-- | user             | host      |
-- +------------------+-----------+
-- | mysql.infoschema | localhost |
-- | mysql.session    | localhost |
-- | mysql.sys        | localhost |
-- | root             | localhost |
-- +------------------+-----------+

-- Creates the user
create user 'tcspikeuser'@'%' identified by 'TcspikePassword';

select user, host from mysql.user;
-- +------------------+-----------+
-- | user             | host      |
-- +------------------+-----------+
-- | tcspikeuser      | %         |
-- | mysql.infoschema | localhost |
-- | mysql.session    | localhost |
-- | mysql.sys        | localhost |
-- | root             | localhost |
-- +------------------+-----------+

-- Gives all privileges to the new user on the newly created database
grant all on tcspike.* to 'tcspikeuser'@'%';

FLUSH PRIVILEGES;