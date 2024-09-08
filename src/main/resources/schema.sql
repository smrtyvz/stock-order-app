CREATE TABLE IF NOT EXISTS users (
    id INT NOT NULL AUTO_INCREMENT,
    username VARCHAR(45) NOT NULL,
    password VARCHAR(45) NOT NULL,
    enabled INT NOT NULL,
    PRIMARY KEY (id));

CREATE TABLE IF NOT EXISTS authorities (
    id INT NOT NULL AUTO_INCREMENT,
    username VARCHAR(45) NOT NULL,
    authority VARCHAR(45) NOT NULL,
    PRIMARY KEY (id));

INSERT INTO authorities
(username, authority)
VALUES
    ('agent', 'basic');
INSERT INTO authorities
(username, authority)
VALUES
    ('admin', 'god-mode');

INSERT INTO users
(username, password, enabled)
VALUES
    ('agent', '123456', '1');

INSERT INTO users
(username, password, enabled)
VALUES
    ('admin', '112233', '1');