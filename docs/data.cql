CREATE KEYSPACE IF NOT EXISTS ocds WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};

CREATE TABLE IF NOT EXISTS  ocds.submission_rules (
    country text,
    pmd text,
    parameter text,
    value text,
    primary key(country, pmd, parameter)
);
INSERT INTO ocds.submission_rules (country, pmd, parameter, value)  VALUES ('MD', 'RT', 'interval', '30');
INSERT INTO ocds.submission_rules (country, pmd, parameter, value)  VALUES ('MD', 'RT', 'unsuspend_interval', '7');
INSERT INTO ocds.submission_rules (country, pmd, parameter, value)  VALUES ('MD', 'RT', 'minBids', '2');
INSERT INTO ocds.submission_rules (country, pmd, parameter, value)  VALUES ('MD', 'OT', 'interval', '30');
INSERT INTO ocds.submission_rules (country, pmd, parameter, value)  VALUES ('MD', 'OT', 'unsuspend_interval', '7');
INSERT INTO ocds.submission_rules (country, pmd, parameter, value)  VALUES ('MD', 'OT', 'minBids', '2');

INSERT INTO ocds.submission_rules (country, pmd, parameter, value)  VALUES ('TEST', 'TEST_RT', 'interval', '1');
INSERT INTO ocds.submission_rules (country, pmd, parameter, value)  VALUES ('TEST', 'TEST_RT', 'unsuspend_interval', '10');
INSERT INTO ocds.submission_rules (country, pmd, parameter, value)  VALUES ('TEST', 'TEST_RT', 'minBids', '3');
INSERT INTO ocds.submission_rules (country, pmd, parameter, value)  VALUES ('TEST', 'TEST_OT', 'interval', '1');
INSERT INTO ocds.submission_rules (country, pmd, parameter, value)  VALUES ('TEST', 'TEST_OT', 'unsuspend_interval', '10');
INSERT INTO ocds.submission_rules (country, pmd, parameter, value)  VALUES ('TEST', 'TEST_OT', 'minBids', '3');


SELECT * FROM ocds.submission_rules;

CREATE TABLE IF NOT EXISTS  ocds.submission_period (
    cp_id text,
    stage text,
    start_date timestamp,
    end_date timestamp,
    primary key(cp_id, stage)
);
SELECT * FROM ocds.submission_period;

INSERT INTO ocds.submission_period (cp_id, stage, start_date, end_date)  VALUES ('ocds-t1s2t3-TEST-1524575697411',
'TEST', '2018-04-23 13:18:01+0000', '2018-04-26 13:18:01+0000');

CREATE TABLE IF NOT EXISTS  ocds.submission_bid (
    cp_id text,
    stage text,
    owner text,
    bid_id uuid,
    token_entity uuid,
    status text,
    created_date timestamp,
    json_data text,
    primary key(cp_id, stage, bid_id, token_entity)
);
