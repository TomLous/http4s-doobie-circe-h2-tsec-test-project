CREATE TABLE JoggingTime(
 id BIGINT AUTO_INCREMENT PRIMARY KEY,
 datetime DATETIME NOT NULL,
 distance DOUBLE NOT NULL,
 duration TIME NOT NULL,
 location VARCHAR(255) NOT NULL,
 weather VARCHAR(255),
 created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE User(
 id BIGINT AUTO_INCREMENT PRIMARY KEY,
 role ENUM('regular', 'userManager', 'admin'),
 username VARCHAR(255) UNIQUE,
 password BINARY(1000) NOT NULL,
 created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);


INSERT INTO User(role, username, password) VALUES('admin', 'admin', HASH('SHA256', STRINGTOUTF8('test12'), 1000));

