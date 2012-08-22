create table monitor_fine (
	id						bigint not null,
	timestamp  				timestamp not null, 
	node_id					varchar(255) not null, 
	request_count			bigint not null, 
	response_time_avg 		int not null,
	exceptions_sum			int not null,
	db_connections_open		int not null,
	db_connections_leased	int not null,
	heap_used				bigint not null,
	heap_max				bigint not null,
	heap_free				bigint not null,
	swap_used				bigint not null,
	gc_count				bigint not null,
	gc_time_avg				int not null,
	load_avg				decimal not null,
	thread_count			bigint not null,
	constraint pk_monitor_fine primary key (id)
);

create sequence monitor_fine_seq MINVALUE 1 START 1;
alter sequence monitor_fine_seq OWNED BY monitor_fine.id;

create table monitor_exceptions_fine (
	id						bigint not null,
	timestamp  				timestamp not null, 
	node_id					varchar(255) not null,
	exception_type			text not null,
	exceptions_sum			int not null,
	constraint pk_monitor_exceptions_fine primary key(id)
);

create sequence monitor_exceptions_fine_seq MINVALUE 1 START 1;
alter sequence monitor_exceptions_fine_seq OWNED BY monitor_exceptions_fine.id;

create table monitor_response_time_fine (
	id						bigint not null,
	timestamp  				timestamp not null, 
	node_id					varchar(255) not null,
	request_method			text not null, 
	response_time			int not null,
	request_count			int not null, 
	constraint pk_monitor_response_time_fine primary key (id)
);

create sequence monitor_response_time_fine_seq MINVALUE 1 START 1;
alter sequence monitor_response_time_fine_seq OWNED BY monitor_response_time_fine.id;