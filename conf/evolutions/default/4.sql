# --- !Ups

create table monitor_fine (
	id						bigint not null,
	timestamp  				timestamp not null, 
	node_id					varchar(255) not null, 
	request_count			bigint, 
	response_time_avg 		int,
	exceptions_sum			int,
	db_connections_open		int,
	db_connections_leased	int,
	heap_used				bigint,
	heap_max				bigint,
	heap_free				bigint,
	swap_used				bigint,
	gc_count				bigint,
	gc_time_avg				int,
	load_avg				decimal,
	thread_count			bigint,
	constraint pk_monitor_fine primary key (id)
);

create sequence monitor_fine_seq MINVALUE 1 START 1;
alter sequence monitor_fine_seq OWNED BY monitor_fine.id;

# --- !Downs
DROP SEQUENCE IF EXISTS monitor_fine_seq;
DROP TABLE IF EXISTS monitor_fine;
