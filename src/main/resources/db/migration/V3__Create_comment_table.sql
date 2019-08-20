create table comment
(
	id bigint auto_increment,
	parent_id bigint not null,
	type int not null,
	commentator bigint not null,
	gmt_create bigint not null,
	like_count bigint default 0 not null,
	comment_count bigint default 0 not null,
	content varchar(1024) default '' not null,
	at_id bigint null,
	origin_id bigint null,
	constraint comment_pk
		primary key (id)
);