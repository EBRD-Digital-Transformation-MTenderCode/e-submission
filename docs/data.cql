CREATE KEYSPACE IF NOT EXISTS ocds WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};

CREATE TABLE IF NOT EXISTS  ocds.submission_rules (
    country text,
    pmd text,
    parameter text,
    value text,
    primary key(country, pmd, parameter)
);

CREATE TABLE IF NOT EXISTS  ocds.submission_bid (
    cp_id text,
    stage text,
    owner text,
    bid_id uuid,
    token_entity uuid,
    status text,
    created_date timestamp,
    pending_date timestamp,
    json_data text,
    primary key(cp_id, stage, bid_id)
);

CREATE TABLE IF NOT EXISTS  ocds.submission_period (
    cp_id text,
    stage text,
    start_date timestamp,
    end_date timestamp,
    primary key(cp_id, stage)
);

CREATE TABLE IF NOT EXISTS  ocds.submission_history (
    operation_id text,
    command text,
    operation_date timestamp,
    json_data text,
    primary key(operation_id, command)
);


INSERT INTO ocds.submission_rules (country, pmd, parameter, value)  VALUES ('MD', 'OT', 'interval', '2160000');
INSERT INTO ocds.submission_rules (country, pmd, parameter, value)  VALUES ('MD', 'TEST_OT', 'interval', '250');
INSERT INTO ocds.submission_rules (country, pmd, parameter, value)  VALUES ('MD', 'OT', 'unsuspend_interval', '604800');
INSERT INTO ocds.submission_rules (country, pmd, parameter, value)  VALUES ('MD', 'TEST_OT', 'unsuspend_interval', '70');
INSERT INTO ocds.submission_rules (country, pmd, parameter, value)  VALUES ('MD', 'OT', 'minBids', '2');
INSERT INTO ocds.submission_rules (country, pmd, parameter, value)  VALUES ('MD', 'TEST_OT', 'minBids', '2');
INSERT INTO ocds.submission_rules (country, pmd, parameter, value)  VALUES ('MD', 'OT', 'interval_before', '518400');
INSERT INTO ocds.submission_rules (country, pmd, parameter, value)  VALUES ('MD', 'TEST_OT', 'interval_before', '60');

SELECT * FROM ocds.submission_rules;


//86400 sec(day) * 30 = 2592000 sec
//86400 sec(day) * 25 = 2160000 sec
//86400 sec(day) * 7 = 604800 sec
//86400 sec(day) * 6 = 518400 sec

//60 sec (min)
//60 sec (min) * 10 = 600 sec



