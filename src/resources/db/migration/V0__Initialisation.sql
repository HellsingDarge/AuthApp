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
    user varchar(10) NOT NULL,
    FOREIGN KEY (user) REFERENCES UsersCredentials(login),
    PRIMARY KEY (id)
);

CREATE INDEX FilteredResource ON Resources(role, user);

CREATE TABLE Sessions (
    id int auto_increment,
    user varchar(10) NOT NULL,
    resource_id int NOT NULL,
    date_start date NOT NULL,
    date_end date NOT NULL,
    volume int NOT NULL,
    FOREIGN KEY (user) REFERENCES UsersCredentials(login),
    FOREIGN KEY (resource_id) REFERENCES Resources(id)
);