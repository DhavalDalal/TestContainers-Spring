-- Drop Privileges for a user
REVOKE ALL ON tcspike FROM tcspikeuser\g

-- Delete the database
DROP DATABASE tcspike\g

-- Drop the user,
-- NOTE: Don't use delete from mysql.user where user = 'tcspikeuser';
DROP USER tcspikeuser\g
