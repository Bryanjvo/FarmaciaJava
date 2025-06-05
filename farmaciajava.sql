create schema farmacia default character set utf8mb4;
use farmacia;
drop schema farmacia;

create table clientes (
	id int not null primary key auto_increment,
    nome varchar(255) not null,
    email varchar(255),
    senha varchar(255),
    endereco varchar(255)
    ) Engine = InnoDB;
    
create table produtos (
	id int primary key auto_increment,
    nome varchar(255) not null,
    preco decimal(10,2) not null,
    imagem varchar(700),
    estoque int,
    receita boolean
    ) Engine = InnoDB;
    
create table pedidos (
	id int primary key auto_increment,
    data_pedido datetime default current_timestamp,
    valor_total double,
    id_cliente int not null,
    foreign key (id_cliente) references clientes(id)
    ) Engine = InnoDB;
    
create table pedido_produto (
	id_pedido int,
    id_produto int,
    quantidade int not null,
    subtotal double,
    primary key(id_pedido, id_produto),
    foreign key(id_pedido) references pedidos(id),
    foreign key(id_produto) references produtos(id)
    ) Engine = InnoDB;
    
create table carrinho(
	id_cliente int not null, 
    id_produto int not null,
    quantidade int not null,
    primary key(id_cliente, id_produto),
    foreign key(id_cliente) references clientes(id),
    foreign key(id_produto) references produtos(id)
) Engine = InnoDB;
    
create table farmaceuticos (
	id int primary key auto_increment,
    usuario varchar(255),
    senha varchar(255)
    ) Engine = InnoDB;
    
create table administradores (
	id int primary key auto_increment,
    usuario varchar(255),
    senha varchar(255)
    ) Engine = InnoDB;
    
select * from clientes;
select * from produtos;
select * from pedidos;
select * from pedido_produto;
select * from carrinho;
alter table pedido_produto add column subtotal double;
alter table produtos add column imagem varchar(700);
truncate table clientes;
delete from clientes where id=3213123;

INSERT INTO produtos (id, nome, preco, estoque, receita) 
VALUES (null, 'dipirona', 30.50, 10, TRUE);
INSERT INTO produtos (id, nome, preco, estoque, receita, imagem) 
VALUES (null, 'nimesulida', 10.50, 70, false, 'https://d1jgmae0hcnr1i.cloudfront.net/Custom/Content/Products/91/33/91331_nimesulida-100mg-c-12-cp-p132263_m1_638285770622513878.webp');
INSERT INTO produtos (id, nome, preco, estoque, receita, imagem) 
VALUES (null, 'shampoo johnsons', 9.50, 70, false,'https://m.media-amazon.com/images/I/61tBTPR81dL._AC_UF1000,1000_QL80_.jpg' );
INSERT INTO produtos (id, nome, preco, estoque, receita, imagem) 
VALUES (null, 'creme nivea', 15.55, 70, false, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRY6hUYtoTDAtZ6BrlpGkSplW7lud3r1hi7og&s');
INSERT INTO produtos (id, nome, preco, estoque, receita, imagem) 
VALUES (null, 'desodorante giovanna baby', 15.55, 70, false, 'https://giovannababy.vteximg.com.br/arquivos/ids/158476-1200-1200/7896044928539_Rollon_Blue-frente_1000.jpg?v=638229501588430000');

update produtos set imagem='https://th.bing.com/th/id/OIP.9IoA1fpFKXRIZZFU6MaFaQHaHa?rs=1&pid=ImgDetMain' where id=1;
delete from produtos where id=2;
SELECT c.id_produto, c.quantidade, p.nome, p.preco, p.imagem, p.receita, p.estoque 
FROM carrinho c INNER JOIN produtos p ON c.id_produto = p.id WHERE c.id_cliente = 1;

alter table pedidos add column valor_total double;

SELECT p.id AS pedido_id, p.data_pedido, p.valor_total,
                   pr.nome AS nome_produto, pp.quantidade, pp.subtotal
            FROM pedidos p
            JOIN pedido_produto pp ON p.id = pp.id_pedido
            JOIN produtos pr ON pp.id_produto = pr.id
            WHERE p.id_cliente = 3
            ORDER BY p.id DESC;



