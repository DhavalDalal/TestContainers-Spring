-- 1. Create table transactions
CREATE TABLE IF NOT EXISTS transactions (
  id uuid NOT NULL,
  date timestamp DEFAULT NULL,
  order_id varchar(255) DEFAULT NULL,
  status varchar(255) DEFAULT NULL,
  amount float8 DEFAULT NULL,
  currency varchar(255) DEFAULT NULL,
  PRIMARY KEY (id)
);

-- 2. Describe table transactions
--\d transactions

--                                 Table "public.transactions"
--    Column  |            Type             | Collation | Nullable |         Default
--  ----------+-----------------------------+-----------+----------+-------------------------
--   id       | uuid                        |           | not null |
--   date     | timestamp without time zone |           |          |
--   order_id | character varying(255)      |           |          | NULL::character varying
--   status   | character varying(255)      |           |          | NULL::character varying
--   amount   | double precision            |           |          |
--   currency | character varying(255)      |           |          | NULL::character varying
--  Indexes:
--      "transactions_pkey" PRIMARY KEY, btree (id)

-- 3. Show tables in the database
--\dt
--                List of relations
--   Schema |     Name     | Type  |    Owner
--  --------+--------------+-------+-------------
--   public | transactions | table | tcspikeuser
--  (1 row)


