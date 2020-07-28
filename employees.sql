USE moviedb;

create table employees (
email varchar(50) primary key,
password varchar(20) not null,
fullname varchar(100));

INSERT INTO employees VALUE('classta@email.edu', 'classta', 'TA CS122B');