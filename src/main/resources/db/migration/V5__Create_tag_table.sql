create table tag
(
	id bigint auto_increment,
	tag_name varchar(50) default '' not null,
	category varchar(50) default '' not null,
	constraint tag_pk
		primary key (id)
);