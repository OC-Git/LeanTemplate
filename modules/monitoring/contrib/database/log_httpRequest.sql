# --- !Ups

create table log_httpRequest (
  id                        bigint not null,
  method                    varchar(16),
  url                       text,
  host                      varchar(255),
  response_code             integer,
  user_agent                text,
  referer                   text,
  start_time                bigint,
  end_time                  bigint,
  from_ip                   varchar(255),
  session_id                varchar(255),
  user_id                   varchar(255),
  constraint pk_log_httpRequest primary key (id))
;

create sequence log_httpRequest_seq;
alter sequence log_httpRequest_seq OWNED BY log_httpRequest.id;

# --- !Downs

drop table if exists log_httpRequest cascade;
drop sequence if exists log_httpRequest_seq;