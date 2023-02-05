
CREATE TABLE IF NOT EXISTS locations (
    id UUID PRIMARY KEY NOT NULL,
    opt_lock BIGINT NOT NULL,
    name TEXT NOT NULL UNIQUE CHECK(LENGTH(name) > 0),
    address TEXT NOT NULL,
    latitude DECIMAL(17,15) NOT NULL CHECK(latitude BETWEEN -90.0 and 90.0),
    longitude DECIMAL(18,15) NOT NULL CHECK(longitude BETWEEN -180.0 and 180.)
)
