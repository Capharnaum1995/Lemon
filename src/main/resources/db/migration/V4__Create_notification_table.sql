create table notification
(
	id bigint auto_increment,
	receiver bigint not null,
	status int default 0 not null,
	comment_id bigint not null,
	gmt_create bigint not null,
	constraint notification_pk
		primary key (id)
);