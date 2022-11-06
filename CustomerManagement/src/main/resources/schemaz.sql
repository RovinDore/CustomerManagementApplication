DROP TABLE IF EXISTS USERS;
DROP TABLE IF EXISTS CUSTOMER;
DROP TABLE IF EXISTS DEPENDANT;
DROP TABLE IF EXISTS FORGOT_PASSWORD;

CREATE TABLE USERS (
	id int NOT NULL,
	username varchar(255),
	password varchar(255),
	email varchar(255),
	phone_number varchar(255),
	roles varchar(100),
	enabled bit NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE CUSTOMER (
    id int,
    name varchar(255),
    phone_number varchar(255),
    email varchar(255),
    bio varchar(255),
    PRIMARY KEY (id)
);

CREATE TABLE DEPENDANT (
    id int NOT NULL,
    customer_id int,
    name varchar(255),
    gender varchar(255),
    created_date datetime,
    last_updated_date datetime,
    description varchar(255),
    dob bigint,
    PRIMARY KEY (id)
);

CREATE TABLE FORGOT_PASSWORD (
    id int NOT NULL,
    exp_date bigint not null,
    key_value varchar(255),
    user_id int Not null,
    PRIMARY KEY (id)
);
