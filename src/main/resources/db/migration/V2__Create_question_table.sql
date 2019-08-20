create table question
(
	id bigint auto_increment,
	title varchar(50) not null,
	tag varchar(256) not null,
	description text not null,
	gmt_create bigint not null,
	gmt_modified bigint not null,
	comment_count bigint default 0 null,
	view_count bigint default 0 null,
	like_count bigint default 0 null,
	creator bigint not null,
	constraint question_pk
		primary key (id)
);