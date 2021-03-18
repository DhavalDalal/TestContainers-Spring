-- Delete the database
drop database tcspike;

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

-- Drop the user,
-- NOTE: Don't use delete from mysql.user where user = 'tcspikeuser';
DROP USER 'tcspikeuser'@'%';

FLUSH PRIVILEGES;

select user, host from mysql.user;
-- +------------------+-----------+
-- | user             | host      |
-- +------------------+-----------+
-- | mysql.infoschema | localhost |
-- | mysql.session    | localhost |
-- | mysql.sys        | localhost |
-- | root             | localhost |
-- +------------------+-----------+
