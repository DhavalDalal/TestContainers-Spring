-- 1.
CREATE TABLE `transactions` (
  `id` binary(255) NOT NULL,
  `date` datetime DEFAULT NULL,
  `order_id` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `amount` double DEFAULT NULL,
  `currency` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

desc `transactions`;

-- +----------+--------------+------+-----+---------+-------+
-- | Field    | Type         | Null | Key | Default | Extra |
-- +----------+--------------+------+-----+---------+-------+
-- | id       | binary(255)  | NO   | PRI | NULL    |       |
-- | date     | datetime     | YES  |     | NULL    |       |
-- | order_id | varchar(255) | YES  |     | NULL    |       |
-- | status   | varchar(255) | YES  |     | NULL    |       |
-- | amount   | double       | YES  |     | NULL    |       |
-- | currency | varchar(255) | YES  |     | NULL    |       |
-- +----------+--------------+------+-----+---------+-------+

show tables;
--  +----------------------+
--  | Tables_in_tcspike    |
--  +----------------------+
--  | transactions         |
--  +----------------------+


