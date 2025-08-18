CREATE TABLE car (
id serial primary key,
brand VARCHAR(10) NOT NULL,
model VARCHAR(15) NOT NULL,
price NUMERIC(12,2) NOT NULL
);

CREATE TABLE person (
id serial primary key,
name VARCHAR(40) NOT NULL,
has_licence BOOLEAN NOT NULL,
car_id INT REFERENCES car(id)
);