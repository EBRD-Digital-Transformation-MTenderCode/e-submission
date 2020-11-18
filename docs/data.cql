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

DROP TABLE IF EXISTS ocds.submission_rules;

CREATE TABLE IF NOT EXISTS ocds.submission_rules
(
    country        text,
    pmd            text,
    operation_type text,
    parameter      text,
    value          text,
    primary key (country, pmd, operation_type, parameter)
);

INSERT INTO ocds.submission_rules (country, pmd, operation_type, parameter, value)  VALUES ('MD', 'OT', 'all', 'minBids', '1');
INSERT INTO ocds.submission_rules (country, pmd, operation_type, parameter, value)  VALUES ('MD', 'OT', 'all','interval', '259200');
INSERT INTO ocds.submission_rules (country, pmd, operation_type, parameter, value)  VALUES ('MD', 'OT', 'all','interval_before', '172800');
INSERT INTO ocds.submission_rules (country, pmd, operation_type, parameter, value)  VALUES ('MD', 'OT', 'all','unsuspend_interval', '259200');

INSERT INTO ocds.submission_rules (country, pmd, operation_type, parameter, value)  VALUES ('MD', 'TEST_OT', 'all','minBids', '1');
INSERT INTO ocds.submission_rules (country, pmd, operation_type, parameter, value)  VALUES ('MD', 'TEST_OT', 'all','interval', '30');
INSERT INTO ocds.submission_rules (country, pmd, operation_type, parameter, value)  VALUES ('MD', 'TEST_OT', 'all','interval_before', '20');
INSERT INTO ocds.submission_rules (country, pmd, operation_type, parameter, value)  VALUES ('MD', 'TEST_OT', 'all','unsuspend_interval', '30');

INSERT INTO ocds.submission_rules (country, pmd, operation_type, parameter, value)  VALUES ('MD', 'SV', 'all','minBids', '1');
INSERT INTO ocds.submission_rules (country, pmd, operation_type, parameter, value)  VALUES ('MD', 'SV', 'all','interval', '259200');
INSERT INTO ocds.submission_rules (country, pmd, operation_type, parameter, value)  VALUES ('MD', 'SV', 'all','interval_before', '172800');
INSERT INTO ocds.submission_rules (country, pmd, operation_type, parameter, value)  VALUES ('MD', 'SV', 'all','unsuspend_interval', '259200');

INSERT INTO ocds.submission_rules (country, pmd, operation_type, parameter, value)
VALUES ('MD', 'TEST_SV', 'all', 'minBids', '1');
INSERT INTO ocds.submission_rules (country, pmd, operation_type, parameter, value)
VALUES ('MD', 'TEST_SV', 'all', 'interval', '30');
INSERT INTO ocds.submission_rules (country, pmd, operation_type, parameter, value)
VALUES ('MD', 'TEST_SV', 'all', 'interval_before', '20');
INSERT INTO ocds.submission_rules (country, pmd, operation_type, parameter, value)
VALUES ('MD', 'TEST_SV', 'all', 'unsuspend_interval', '30');

INSERT INTO ocds.submission_rules (country, pmd, operation_type, parameter, value)
VALUES ('MD', 'TEST_SV', 'all', 'unsuspend_interval', '30');

INSERT INTO ocds.submission_rules (country, pmd, operation_type, parameter, value)
VALUES ('MD', 'GPA', 'startSecondStage', 'minTenderPeriodDuration', '864000');
INSERT INTO ocds.submission_rules (country, pmd, operation_type, parameter, value)
VALUES ('MD', 'TEST_GPA', 'startSecondStage', 'minTenderPeriodDuration', '0');

CREATE KEYSPACE IF NOT EXISTS submission
    WITH REPLICATION = {
        'class' : 'SimpleStrategy',
        'replication_factor' : 1
        };

CREATE TABLE IF NOT EXISTS submission.history
(
    command_id   TEXT,
    command_name TEXT,
    command_date TIMESTAMP,
    json_data    TEXT,
    PRIMARY KEY (command_id)
);

CREATE TABLE IF NOT EXISTS submission.rules
(
    country        TEXT,
    pmd            TEXT,
    operation_type TEXT,
    parameter      TEXT,
    value          TEXT,
    PRIMARY KEY (country, pmd, operation_type, parameter)
);

CREATE TABLE IF NOT EXISTS submission.bids
(
    cpid         TEXT,
    ocid         TEXT,
    id           TEXT,
    owner        TEXT,
    token_entity TEXT,
    status       TEXT,
    created_date TIMESTAMP,
    pending_date TIMESTAMP,
    json_data    TEXT,
    PRIMARY KEY (cpid, ocid, id)
);

CREATE TABLE IF NOT EXISTS submission.periods
(
    cpid       TEXT,
    ocid       TEXT,
    start_date TIMESTAMP,
    end_date   TIMESTAMP,
    PRIMARY KEY (cpid, ocid)
);

CREATE TABLE IF NOT EXISTS submission.invitations
(
    cpid      TEXT,
    id        TEXT,
    json_data TEXT,
    PRIMARY KEY (cpid, id)
);

INSERT INTO submission.rules (country, pmd, operation_type, parameter, value)
VALUES ('MD', 'OT', 'all', 'minBids', '1');
INSERT INTO submission.rules (country, pmd, operation_type, parameter, value)
VALUES ('MD', 'OT', 'all', 'interval', '259200');
INSERT INTO submission.rules (country, pmd, operation_type, parameter, value)
VALUES ('MD', 'OT', 'all', 'interval_before', '172800');
INSERT INTO submission.rules (country, pmd, operation_type, parameter, value)
VALUES ('MD', 'OT', 'all', 'unsuspend_interval', '259200');

INSERT INTO submission.rules (country, pmd, operation_type, parameter, value)
VALUES ('MD', 'TEST_OT', 'all', 'minBids', '1');
INSERT INTO submission.rules (country, pmd, operation_type, parameter, value)
VALUES ('MD', 'TEST_OT', 'all', 'interval', '30');
INSERT INTO submission.rules (country, pmd, operation_type, parameter, value)
VALUES ('MD', 'TEST_OT', 'all', 'interval_before', '20');
INSERT INTO submission.rules (country, pmd, operation_type, parameter, value)
VALUES ('MD', 'TEST_OT', 'all', 'unsuspend_interval', '30');

INSERT INTO submission.rules (country, pmd, operation_type, parameter, value)
VALUES ('MD', 'SV', 'all', 'minBids', '1');
INSERT INTO submission.rules (country, pmd, operation_type, parameter, value)
VALUES ('MD', 'SV', 'all', 'interval', '259200');
INSERT INTO submission.rules (country, pmd, operation_type, parameter, value)
VALUES ('MD', 'SV', 'all', 'interval_before', '172800');
INSERT INTO submission.rules (country, pmd, operation_type, parameter, value)
VALUES ('MD', 'SV', 'all', 'unsuspend_interval', '259200');

INSERT INTO submission.rules (country, pmd, operation_type, parameter, value)
VALUES ('MD', 'TEST_SV', 'all', 'minBids', '1');
INSERT INTO submission.rules (country, pmd, operation_type, parameter, value)
VALUES ('MD', 'TEST_SV', 'all', 'interval', '30');
INSERT INTO submission.rules (country, pmd, operation_type, parameter, value)
VALUES ('MD', 'TEST_SV', 'all', 'interval_before', '20');
INSERT INTO submission.rules (country, pmd, operation_type, parameter, value)
VALUES ('MD', 'TEST_SV', 'all', 'unsuspend_interval', '30');
INSERT INTO submission.rules (country, pmd, operation_type, parameter, value)
VALUES ('MD', 'TEST_SV', 'all', 'unsuspend_interval', '30');

INSERT INTO submission.rules (country, pmd, operation_type, parameter, value)
VALUES ('MD', 'GPA', 'startSecondStage', 'minTenderPeriodDuration', '864000');
INSERT INTO submission.rules (country, pmd, operation_type, parameter, value)
VALUES ('MD', 'TEST_GPA', 'startSecondStage', 'minTenderPeriodDuration', '0');

DROP TABLE IF EXISTS submission.history;

CREATE TABLE IF NOT EXISTS submission.history
(
    command_id   TEXT,
    command_name TEXT,
    command_date TIMESTAMP,
    json_data    TEXT,
    PRIMARY KEY (command_id, command_name)
);

INSERT INTO ocds.submission_rules (country, pmd, operation_type, parameter, value)
VALUES ('MD', 'OF', 'all', 'extensionAfterUnsuspended', '1000');
INSERT INTO ocds.submission_rules (country, pmd, operation_type, parameter, value)
VALUES ('MD', 'TEST_OF', 'all', 'extensionAfterUnsuspended', '1000');
INSERT INTO ocds.submission_rules (country, pmd, operation_type, parameter, value)
VALUES ('MD', 'CF', 'all', 'extensionAfterUnsuspended', '1000');
INSERT INTO ocds.submission_rules (country, pmd, operation_type, parameter, value)
VALUES ('MD', 'TEST_CF', 'all', 'extensionAfterUnsuspended', '1000');