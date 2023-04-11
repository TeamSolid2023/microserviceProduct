drop table if exists Product;
drop table if exists Category;
create table Category (
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
insert into Product (name, category, description, price, stock) values ('Wonder', 'Libros','Author: R.J. Palacio', 12.45, 90);
insert into Product (name, category, description, price, stock) values ('Los Surcos del Azar', 'Libros','Author: Paco Roca', 24.89, 100);
insert into Product (name, category, description, price, stock) values ('Pelota', 'Juguetes', 'Pelota del Barça', 10, 4);
insert into Product (name, category, description, price, stock) values ('Wonder', 'Libros','Author: R.J. Palacio', 12.45, 90);
insert into Product (name, category, description, price, stock) values ('Los Surcos del Azar', 'Libros','Author: Paco Roca', 24.89, 100);
insert into Product (name, category, description, price, stock) values ('Pelota', 'Juguetes', 'Pelota del Barça', 10, 4);
insert into Product (name, category, description, price, stock) values ('Wonder', 'Libros','Author: R.J. Palacio', 12.45, 90);
insert into Product (name, category, description, price, stock) values ('Los Surcos del Azar', 'Libros','Author: Paco Roca', 24.89, 100);
insert into Product (name, category, description, price, stock) values ('Pelota', 'Juguetes', 'Pelota del Barça', 10, 4);
insert into Product (name, category, description, price, stock) values ('Wonder', 'Libros','Author: R.J. Palacio', 12.45, 90);
insert into Product (name, category, description, price, stock) values ('Los Surcos del Azar', 'Libros','Author: Paco Roca', 24.89, 100);
insert into Product (name, category, description, price, stock) values ('Pelota', 'Juguetes', 'Pelota del Barça', 10, 4);
insert into Product (name, category, description, price, stock) values ('Wonder', 'Libros','Author: R.J. Palacio', 12.45, 90);
insert into Product (name, category, description, price, stock) values ('Los Surcos del Azar', 'Libros','Author: Paco Roca', 24.89, 100);
insert into Product (name, category, description, price, stock) values ('Pelota', 'Juguetes', 'Pelota del Barça', 10, 4);
insert into Product (name, category, description, price, stock) values ('Wonder', 'Libros','Author: R.J. Palacio', 12.45, 90);
insert into Product (name, category, description, price, stock) values ('Los Surcos del Azar', 'Libros','Author: Paco Roca', 24.89, 100);
insert into Product (name, category, description, price, stock) values ('Pelota', 'Juguetes', 'Pelota del Barça', 10, 4);
insert into Product (name, category, description, price, stock) values ('Wonder', 'Libros','Author: R.J. Palacio', 12.45, 90);
insert into Product (name, category, description, price, stock) values ('Los Surcos del Azar', 'Libros','Author: Paco Roca', 24.89, 100);
insert into Product (name, category, description, price, stock) values ('Pelota', 'Juguetes', 'Pelota del Barça', 10, 4);
insert into Product (name, category, description, price, stock) values ('Wonder', 'Libros','Author: R.J. Palacio', 12.45, 90);
insert into Product (name, category, description, price, stock) values ('Los Surcos del Azar', 'Libros','Author: Paco Roca', 24.89, 100);
insert into Product (name, category, description, price, stock) values ('Pelota', 'Juguetes', 'Pelota del Barça', 10, 4);
