CREATE KEYSPACE IF NOT EXISTS ocds WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};

CREATE TABLE IF NOT EXISTS  ocds.contract_period (
    tender_id text,
    start_date timestamp,
    end_date timestamp,
    primary key(tender_id)
);