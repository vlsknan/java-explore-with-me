DROP TABLE IF EXISTS EVENT_COMPILATIONS CASCADE;
DROP TABLE IF EXISTS COMPILATIONS CASCADE;
DROP TABLE IF EXISTS REQUEST CASCADE;
DROP TABLE IF EXISTS EVENTS CASCADE;
DROP TABLE IF EXISTS CATEGORIES CASCADE;
DROP TABLE IF EXISTS USERS CASCADE;
DROP TABLE IF EXISTS COMMENTS CASCADE;

CREATE TABLE IF NOT EXISTS CATEGORIES(
    ID INT GENERATED BY DEFAULT AS IDENTITY,
    NAME VARCHAR(32),
    CONSTRAINT PK_CATEGORIES PRIMARY KEY (ID),
    CONSTRAINT UQ_CATEGORY_NAME UNIQUE (NAME)
);

CREATE TABLE IF NOT EXISTS USERS(
    ID INT GENERATED BY DEFAULT AS IDENTITY,
    NAME VARCHAR(32),
    EMAIL VARCHAR(32),
    CONSTRAINT PK_USERS PRIMARY KEY (ID),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (EMAIL)
);

CREATE TABLE IF NOT EXISTS EVENTS(
    ID INT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    ANNOTATION VARCHAR(2048),
    CATEGORY_ID INT,
    INITIATOR_ID INT,
    TITLE VARCHAR(128),
    CREATED_ON TIMESTAMP WITH TIME ZONE,
    DESCRIPTION VARCHAR(2048),
    EVENT_DATE TIMESTAMP WITH TIME ZONE,
    PAID BOOLEAN,
    PARTICIPANT_LIMIT INT,
    PUBLISHED_ON TIMESTAMP WITH TIME ZONE,
    STATE VARCHAR DEFAULT 'PENDING',
    LOCATION_LATITUDE FLOAT,
    LOCATION_LONGITUDE FLOAT,
    REQUEST_MODERATION BOOLEAN,
    CONSTRAINT PK_EVENT PRIMARY KEY (ID),
    CONSTRAINT STATE CHECK (STATE IN ('PENDING', 'PUBLISHED', 'CANCELED')),
    FOREIGN KEY (CATEGORY_ID) REFERENCES CATEGORIES(ID) ON DELETE CASCADE,
    FOREIGN KEY (INITIATOR_ID) REFERENCES USERS(ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS COMPILATIONS(
    ID INT GENERATED BY DEFAULT AS IDENTITY,
    TITLE VARCHAR(128),
    PINNED BOOLEAN,
    CONSTRAINT PK_COMPILATION PRIMARY KEY (ID)
);

CREATE TABLE IF NOT EXISTS EVENT_COMPILATIONS(
    EVENT_ID INT,
    COMPILATION_ID INT,
    FOREIGN KEY (EVENT_ID) REFERENCES EVENTS (ID) ON DELETE CASCADE,
    FOREIGN KEY (COMPILATION_ID) REFERENCES COMPILATIONS (ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS REQUEST(
    ID INT GENERATED BY DEFAULT AS IDENTITY,
    CREATED TIMESTAMP WITH TIME ZONE,
    EVENT_ID INT,
    REQUESTER_ID INT,
    STATUS VARCHAR(32),
    CONSTRAINT PK_REQUEST PRIMARY KEY (ID),
    FOREIGN KEY (EVENT_ID) REFERENCES EVENTS(ID) ON DELETE CASCADE,
    FOREIGN KEY (REQUESTER_ID) REFERENCES USERS(ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS COMMENTS(
    ID BIGINT GENERATED BY DEFAULT AS IDENTITY,
    TEXT VARCHAR(512),
    EVENT_ID BIGINT,
    USER_ID BIGINT,
    STATUS VARCHAR(32),
    PUBLISHED TIMESTAMP WITH TIME ZONE,
    CREATED TIMESTAMP WITH TIME ZONE,
    CONSTRAINT PK_COMMENT PRIMARY KEY (ID),
    FOREIGN KEY (EVENT_ID) REFERENCES EVENTS(ID) ON DELETE CASCADE,
    FOREIGN KEY (USER_ID) REFERENCES USERS(ID) ON DELETE CASCADE
);