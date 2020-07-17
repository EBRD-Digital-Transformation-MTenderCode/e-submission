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

INSERT INTO ocds.submission_rules (country, pmd, parameter, value)  VALUES ('MD', 'OT', 'minBids', '1');
INSERT INTO ocds.submission_rules (country, pmd, parameter, value)  VALUES ('MD', 'OT', 'interval', '259200');
INSERT INTO ocds.submission_rules (country, pmd, parameter, value)  VALUES ('MD', 'OT', 'interval_before', '172800');
INSERT INTO ocds.submission_rules (country, pmd, parameter, value)  VALUES ('MD', 'OT', 'unsuspend_interval', '259200');

INSERT INTO ocds.submission_rules (country, pmd, parameter, value)  VALUES ('MD', 'TEST_OT', 'minBids', '1');
INSERT INTO ocds.submission_rules (country, pmd, parameter, value)  VALUES ('MD', 'TEST_OT', 'interval', '30');
INSERT INTO ocds.submission_rules (country, pmd, parameter, value)  VALUES ('MD', 'TEST_OT', 'interval_before', '20');
INSERT INTO ocds.submission_rules (country, pmd, parameter, value)  VALUES ('MD', 'TEST_OT', 'unsuspend_interval', '30');

INSERT INTO ocds.submission_rules (country, pmd, parameter, value)  VALUES ('MD', 'SV', 'minBids', '1');
INSERT INTO ocds.submission_rules (country, pmd, parameter, value)  VALUES ('MD', 'SV', 'interval', '259200');
INSERT INTO ocds.submission_rules (country, pmd, parameter, value)  VALUES ('MD', 'SV', 'interval_before', '172800');
INSERT INTO ocds.submission_rules (country, pmd, parameter, value)  VALUES ('MD', 'SV', 'unsuspend_interval', '259200');

INSERT INTO ocds.submission_rules (country, pmd, parameter, value)  VALUES ('MD', 'TEST_SV', 'minBids', '1');
INSERT INTO ocds.submission_rules (country, pmd, parameter, value)  VALUES ('MD', 'TEST_SV', 'interval', '30');
INSERT INTO ocds.submission_rules (country, pmd, parameter, value)  VALUES ('MD', 'TEST_SV', 'interval_before', '20');
INSERT INTO ocds.submission_rules (country, pmd, parameter, value)  VALUES ('MD', 'TEST_SV', 'unsuspend_interval', '30');

//86400 sec(day) * 30 = 2592000 sec
//86400 sec(day) * 25 = 2160000 sec
//86400 sec(day) * 7 = 604800 sec
//86400 sec(day) * 6 = 518400 sec

//86400 sec(day) * 3 = 259200 sec

//60 sec (min)
//60 sec (min) * 10 = 600 sec


CREATE TABLE IF NOT EXISTS  ocds.submission_invitation(
    cpid      text,
    id        text,
    json_data text,
    primary key (cpid, id)
);


