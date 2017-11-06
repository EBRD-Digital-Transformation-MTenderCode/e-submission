CREATE KEYSPACE IF NOT EXISTS ocds WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};

CREATE TABLE IF NOT EXISTS  ocds.submission_period (
    oc_id text,
    start_date timestamp,
    end_date timestamp,
    primary key(oc_id)
);

CREATE TABLE IF NOT EXISTS  ocds.bid (
    oc_id text,
    bid_id UUID,
    json_data text,
    primary key(oc_id, bid_id)
)WITH CLUSTERING ORDER BY (bid_id DESC);