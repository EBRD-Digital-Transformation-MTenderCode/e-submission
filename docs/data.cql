CREATE KEYSPACE IF NOT EXISTS ocds WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};

CREATE TABLE IF NOT EXISTS  ocds.submission_rules (
    country text,
    method text,
    parameter text,
    value text,
    primary key(country, method, parameter)
);
INSERT INTO ocds.submission_rules (country, method, parameter, value)  VALUES ('UA', 'micro', 'interval', '15');
SELECT * FROM ocds.submission_rules;

CREATE TABLE IF NOT EXISTS  ocds.submission_period (
    cp_id text,
    stage text,
    start_date timestamp,
    end_date timestamp,
    primary key(cp_id, stage)
);
SELECT * FROM ocds.submission_period;

CREATE TABLE IF NOT EXISTS  ocds.submission_bid (
    cp_id text,
    stage text,
    owner text,
    bid_id text,
    token_entity text,
    status text,
    created_date timestamp,
    pending_date timestamp,
    json_data text,
    primary key(cp_id, stage, bid_id, token_entity)
);
