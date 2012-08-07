# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table db_image (
  id                        bigint not null,
  filename                  varchar(255),
  image_id                  bigint,
  thumbnail_id              bigint,
  last_update               timestamp not null,
  constraint pk_db_image primary key (id))
;

create table ding (
  id                        bigint not null,
  name                      varchar(255),
  description               varchar(255),
  special                   boolean,
  price                     decimal(15,2),
  user_id                   bigint,
  some_date                 date,
  some_time                 timestamp,
  ding_enum                 varchar(2),
  image_id                  bigint,
  last_update               timestamp not null,
  constraint ck_ding_ding_enum check (ding_enum in ('JS','C+','OC','VB','RU','C','PE','PY','PH','J','CS')),
  constraint pk_ding primary key (id))
;

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

create table log_variant_event (
  id                        bigint not null,
  user_id                   bigint,
  mv_test_variant_id        bigint,
  date                      timestamp,
  info                      varchar(255),
  last_update               timestamp not null,
  constraint pk_log_variant_event primary key (id))
;

create table monitor_exceptions_fine (
  id                        bigint not null,
  timestamp                 timestamp,
  node_id                   varchar(255),
  exception_type            varchar(255),
  exceptions_sum            integer,
  constraint pk_monitor_exceptions_fine primary key (id))
;

create table monitor_fine (
  id                        bigint not null,
  timestamp                 timestamp,
  node_id                   varchar(255),
  request_count             bigint,
  response_time_avg         integer,
  exceptions_sum            integer,
  db_connections_open       integer,
  db_connections_leased     integer,
  heap_used                 bigint,
  heap_max                  bigint,
  heap_free                 bigint,
  swap_used                 bigint,
  gc_count                  bigint,
  gc_time_avg               bigint,
  load_avg                  float,
  thread_count              bigint,
  constraint pk_monitor_fine primary key (id))
;

create table monitor_response_time_fine (
  id                        bigint not null,
  timestamp                 timestamp,
  node_id                   varchar(255),
  request_method            varchar(255),
  response_time             integer,
  constraint pk_monitor_response_time_fine primary key (id))
;

create table mv_test_feature (
  id                        bigint not null,
  name                      varchar(255),
  start_time                timestamp,
  end_time                  timestamp,
  description               varchar(1024),
  fix_random_mask           bigint,
  last_update               timestamp not null,
  constraint pk_mv_test_feature primary key (id))
;

create table mv_test_variant (
  id                        bigint not null,
  index                     integer,
  name                      varchar(255),
  percent                   float,
  feature_id                bigint,
  last_update               timestamp not null,
  constraint pk_mv_test_variant primary key (id))
;

create table raw_image (
  id                        bigint not null,
  image                     bytea,
  width                     integer,
  height                    integer,
  mimetype                  varchar(255),
  last_update               timestamp not null,
  constraint pk_raw_image primary key (id))
;

create table report_query (
  id                        bigint not null,
  name                      varchar(255),
  description               varchar(255),
  query                     varchar(255),
  last_update               timestamp not null,
  constraint pk_report_query primary key (id))
;

create table report_query_parameter (
  id                        bigint not null,
  report_query_id           bigint,
  name                      varchar(255),
  description               varchar(255),
  last_update               timestamp not null,
  constraint pk_report_query_parameter primary key (id))
;

create table shopping_cart (
  id                        bigint not null,
  user_id                   bigint,
  last_update               timestamp not null,
  constraint pk_shopping_cart primary key (id))
;

create table shopping_cart_entry (
  id                        bigint not null,
  shopping_cart_id          bigint not null,
  ding_id                   bigint,
  amount                    integer,
  last_update               timestamp not null,
  constraint pk_shopping_cart_entry primary key (id))
;

create table unter_ding (
  id                        bigint not null,
  name                      varchar(255),
  ding_id                   bigint,
  image_id                  bigint,
  last_update               timestamp not null,
  constraint pk_unter_ding primary key (id))
;

create table user_account (
  id                        bigint not null,
  fix_random_number         bigint,
  email                     varchar(255),
  password_hash             varchar(255),
  role                      varchar(255),
  timezone                  varchar(255),
  validated                 boolean,
  firstname                 varchar(255),
  surname                   varchar(255),
  address                   varchar(255),
  country                   varchar(255),
  zip_code                  varchar(255),
  city                      varchar(255),
  random_pwrecover          varchar(255),
  pwrecover_triggered       timestamp,
  last_update               timestamp not null,
  constraint pk_user_account primary key (id))
;


create table ding_user_account (
  ding_id                        bigint not null,
  user_account_id                bigint not null,
  constraint pk_ding_user_account primary key (ding_id, user_account_id))
;
create sequence db_image_seq;

create sequence ding_seq;

create sequence log_httpRequest_seq;

create sequence log_variant_event_seq;

create sequence monitor_exceptions_fine_seq;

create sequence monitor_fine_seq;

create sequence monitor_response_time_fine_seq;

create sequence mv_test_feature_seq;

create sequence mv_test_variant_seq;

create sequence raw_image_seq;

create sequence report_query_seq;

create sequence report_query_parameter_seq;

create sequence shopping_cart_seq;

create sequence shopping_cart_entry_seq;

create sequence unter_ding_seq;

create sequence user_account_seq;

alter table db_image add constraint fk_db_image_image_1 foreign key (image_id) references raw_image (id);
create index ix_db_image_image_1 on db_image (image_id);
alter table db_image add constraint fk_db_image_thumbnail_2 foreign key (thumbnail_id) references raw_image (id);
create index ix_db_image_thumbnail_2 on db_image (thumbnail_id);
alter table ding add constraint fk_ding_user_3 foreign key (user_id) references user_account (id);
create index ix_ding_user_3 on ding (user_id);
alter table ding add constraint fk_ding_image_4 foreign key (image_id) references db_image (id);
create index ix_ding_image_4 on ding (image_id);
alter table log_variant_event add constraint fk_log_variant_event_mvTestVar_5 foreign key (mv_test_variant_id) references mv_test_variant (id);
create index ix_log_variant_event_mvTestVar_5 on log_variant_event (mv_test_variant_id);
alter table mv_test_variant add constraint fk_mv_test_variant_feature_6 foreign key (feature_id) references mv_test_feature (id);
create index ix_mv_test_variant_feature_6 on mv_test_variant (feature_id);
alter table report_query_parameter add constraint fk_report_query_parameter_repo_7 foreign key (report_query_id) references report_query (id);
create index ix_report_query_parameter_repo_7 on report_query_parameter (report_query_id);
alter table shopping_cart add constraint fk_shopping_cart_user_8 foreign key (user_id) references user_account (id);
create index ix_shopping_cart_user_8 on shopping_cart (user_id);
alter table shopping_cart_entry add constraint fk_shopping_cart_entry_shoppin_9 foreign key (shopping_cart_id) references shopping_cart (id);
create index ix_shopping_cart_entry_shoppin_9 on shopping_cart_entry (shopping_cart_id);
alter table shopping_cart_entry add constraint fk_shopping_cart_entry_ding_10 foreign key (ding_id) references ding (id);
create index ix_shopping_cart_entry_ding_10 on shopping_cart_entry (ding_id);
alter table unter_ding add constraint fk_unter_ding_ding_11 foreign key (ding_id) references ding (id);
create index ix_unter_ding_ding_11 on unter_ding (ding_id);
alter table unter_ding add constraint fk_unter_ding_image_12 foreign key (image_id) references db_image (id);
create index ix_unter_ding_image_12 on unter_ding (image_id);



alter table ding_user_account add constraint fk_ding_user_account_ding_01 foreign key (ding_id) references ding (id);

alter table ding_user_account add constraint fk_ding_user_account_user_acc_02 foreign key (user_account_id) references user_account (id);

# --- !Downs

drop table if exists db_image cascade;

drop table if exists ding cascade;

drop table if exists ding_user_account cascade;

drop table if exists log_httpRequest cascade;

drop table if exists log_variant_event cascade;

drop table if exists monitor_exceptions_fine cascade;

drop table if exists monitor_fine cascade;

drop table if exists monitor_response_time_fine cascade;

drop table if exists mv_test_feature cascade;

drop table if exists mv_test_variant cascade;

drop table if exists raw_image cascade;

drop table if exists report_query cascade;

drop table if exists report_query_parameter cascade;

drop table if exists shopping_cart cascade;

drop table if exists shopping_cart_entry cascade;

drop table if exists unter_ding cascade;

drop table if exists user_account cascade;

drop sequence if exists db_image_seq;

drop sequence if exists ding_seq;

drop sequence if exists log_httpRequest_seq;

drop sequence if exists log_variant_event_seq;

drop sequence if exists monitor_exceptions_fine_seq;

drop sequence if exists monitor_fine_seq;

drop sequence if exists monitor_response_time_fine_seq;

drop sequence if exists mv_test_feature_seq;

drop sequence if exists mv_test_variant_seq;

drop sequence if exists raw_image_seq;

drop sequence if exists report_query_seq;

drop sequence if exists report_query_parameter_seq;

drop sequence if exists shopping_cart_seq;

drop sequence if exists shopping_cart_entry_seq;

drop sequence if exists unter_ding_seq;

drop sequence if exists user_account_seq;

