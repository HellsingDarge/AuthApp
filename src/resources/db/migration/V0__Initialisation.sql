CREATE TABLE UsersCredentials (
    login varchar(10),
    salt char(16) NOT NULL,
    hash char(64) NOT NULL,
    PRIMARY KEY (login)
);

CREATE TABLE Resources (
    id int auto_increment,
    resource varchar(256) NOT NULL,
    role ENUM('READ', 'WRITE', 'EXECUTE') NOT NULL,
    login varchar(10) NOT NULL,
    FOREIGN KEY (login) REFERENCES UsersCredentials(login),
    PRIMARY KEY (id)
);

CREATE INDEX FilteredResource ON Resources(role, login);

CREATE TABLE Sessions (
    id int auto_increment,
    login varchar(10) NOT NULL,
    resource_id int NOT NULL,
    date_start date NOT NULL,
    date_end date NOT NULL,
    volume int NOT NULL,
    FOREIGN KEY (login) REFERENCES UsersCredentials(login),
    FOREIGN KEY (resource_id) REFERENCES Resources(id)
);