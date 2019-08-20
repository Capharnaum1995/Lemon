create table user
(
	id BIGINT auto_increment,
	account_id varchar(20) not null,
	name varchar(50) not null,
	token char(36) not null,
	gmt_create bigint not null,
	gmt_modified bigint not null,
	avatar_url varchar(200) not null,
	constraint user_pk
		primary key (id)
);
