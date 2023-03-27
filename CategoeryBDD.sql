create table Category(
    id bigInt not null AUTO_INCREMENT,
    name_ varchar(45) not null,
    discount int not null,
    
    constraint onlyPositiveCat check(discount between 0 and 100),

    primary key (id)
);

create table Product(
    id bigInt not null AUTO_INCREMENT,
    name_ varchar(45) not null,
    category bigInt,
    description_ varchar(255) not null,
    price int not null,
    stock bigInt not null,

    constraint onlyPositivePr check(price >= 0),

    primary key (id),
    foreign key (category) references dbo.Category(Id) on delete cascade
);

