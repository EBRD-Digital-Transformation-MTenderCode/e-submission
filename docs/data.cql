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
    oc_id text,
    stage text,
    bid_id UUID,
    bid_token UUID,
    json_data text,
    bid_status text,
    created_date timestamp,
    pending_date timestamp,
    owner text,
    primary key(oc_id, stage, bid_id, bid_token)
)WITH CLUSTERING ORDER BY (stage DESC, bid_id DESC);