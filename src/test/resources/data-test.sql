drop table if exists Product;
drop table if exists Category;

create table Category(
    id bigInt not null AUTO_INCREMENT,
    name varchar(45) not null,
    discount int not null,

    primary key (name)
);

create table Product(
    id bigInt not null AUTO_INCREMENT,
    name varchar(45) not null,
    category varchar(45),
    description varchar(255) not null,
    price decimal(10,2) not null,
    stock int not null,

    primary key (id),
    foreign key (category) references Category(name) on delete cascade
);


insert into Category (name, discount) values ('Juguetes', 20);
insert into Category (name, discount) values ('Libros', 15);
insert into Category (name, discount) values ('Deportes', 5);
insert into Category (name, discount) values ('Comida', 25);
insert into Category (name, discount) values ('Ropa', 35);
insert into Category (name, discount) values ('Otros', 0);

insert into Product (name, category, description, price, stock) values ('Wonder', 'Libros','Author: R.J. Palacio', 12.45, 90);
insert into Product (name, category, description, price, stock) values ('Los Surcos del Azar', 'Libros','Author: Paco Roca', 24.89, 100);
insert into Product (name, category, description, price, stock) values ('Pelota', 'Juguetes', 'Pelota del Barça', 10, 4);
insert into Product (name, category, description, stock, price) values ('Coleman', 'Juguetes', 'Proin leo odio, porttitor id, consequat in, consequat ut, nulla. Sed accumsan felis.', 98.18, 87);
insert into Product (name, category, description, stock, price) values ('Delphine', 'Juguetes', 'Aenean fermentum.', 91.98, 28);
insert into Product (name, category, description, stock, price) values ('Dahlia', 'Comida', 'Nunc nisl. Duis bibendum, felis sed interdum venenatis, turpis enim blandit mi, in porttitor pede justo eu massa.', 68.72, 84);
insert into Product (name, category, description, stock, price) values ('Leena', 'Comida', 'Curabitur at ipsum ac tellus semper interdum. Mauris ullamcorper purus sit amet nulla.', 16.81, 81);
insert into Product (name, category, description, stock, price) values ('Hagan', 'Deportes', 'Nulla tempus. Vivamus in felis eu sapien cursus vestibulum.', 82.93, 33);
insert into Product (name, category, description, stock, price) values ('Corey', 'Ropa', 'Nulla neque libero, convallis eget, eleifend luctus, ultricies eu, nibh.', 20.69, 75);
insert into Product (name, category, description, stock, price) values ('Kyle', 'Otros', 'Quisque porta volutpat erat. Quisque erat eros, viverra eget, congue eget, semper rutrum, nulla.', 28.13, 33);
insert into Product (name, category, description, price, stock) values ('Los Surcos del Azar', 'Libros','Author: Paco Roca', 24.89, 100);
insert into Product (name, category, description, stock, price) values ('Nanice', 'Libros', 'Suspendisse ornare consequat lectus.', 93.64, 100);
insert into Product (name, category, description, stock, price) values ('Mirabelle', 'Ropa', 'Quisque erat eros, viverra eget, congue eget, semper rutrum, nulla.', 91.13, 27);
