 create table book(
    id int auto_increment primary key,
    name varchar (255) not null,
    price decimal (10,2) not null,
    status varchar (255) not null,
    costumer_id int not null,
    FOREIGN KEY (costumer_id) REFERENCES costumer(id)
 );