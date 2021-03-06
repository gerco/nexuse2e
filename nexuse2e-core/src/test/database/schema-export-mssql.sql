--
--  NEXUSe2e Business Messaging Open Source
--  Copyright 2000-2009, Tamgroup and X-ioma GmbH
--
--  This is free software; you can redistribute it and/or modify it
--  under the terms of the GNU Lesser General Public License as
--  published by the Free Software Foundation version 2.1 of
--  the License.
--
--  This software is distributed in the hope that it will be useful,
--  but WITHOUT ANY WARRANTY; without even the implied warranty of
--  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
--  Lesser General Public License for more details.
--
--  You should have received a copy of the GNU Lesser General Public
--  License along with this software; if not, write to the Free
--  Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
--  02110-1301 USA, or see the FSF site: http://www.fsf.org.
--
alter table nx_action drop constraint fk__action__st_upd_pipeline_id;
alter table nx_action drop constraint fk__action__in_pipeline_id;
alter table nx_action drop constraint fk__action__out_pipeline_id;
alter table nx_action drop constraint fk__action__choreography_id;
alter table nx_certificate drop constraint fk__certificate__partner_id;
alter table nx_connection drop constraint fk__connection__certificate_id;
alter table nx_connection drop constraint fk__connection__partner_id;
alter table nx_connection drop constraint fk__connection__trp_id;
alter table nx_conversation drop constraint fk__conversation__current_action_id;
alter table nx_conversation drop constraint fk__conv__partner_id;
alter table nx_conversation drop constraint fk__conv__choreography_id;
alter table nx_follow_up_action drop constraint fk__fua__follow_up_action_id;
alter table nx_follow_up_action drop constraint fk__fua__action_id;
alter table nx_grant drop constraint fk__grant__grant_id;
alter table nx_grant drop constraint fk__grant__role_id;
alter table nx_logger drop constraint fk__logger__component_id;
alter table nx_logger_param drop constraint fk__logger_param__logger_id;
alter table nx_message drop constraint fk__msg__conversation_id;
alter table nx_message drop constraint fk__msg__trp_id;
alter table nx_message drop constraint fk__message__action_id;
alter table nx_message drop constraint fk__msg__referenced_message_id;
alter table nx_message_label drop constraint fk__message_label__message_id;
alter table nx_message_payload drop constraint fk__msg_payload__message_id;
alter table nx_participant drop constraint fk__part__connection_id;
alter table nx_participant drop constraint fk__part__partner_id;
alter table nx_participant drop constraint fk__part__certificate_id;
alter table nx_participant drop constraint fk__part__choreography_id;
alter table nx_participant drop constraint fk__part__local_partner_id;
alter table nx_pipelet drop constraint fk__pipelet__component_id;
alter table nx_pipelet drop constraint fk__pipelet__pipeline_id;
alter table nx_pipelet_param drop constraint fk__pipelet_param__pipelet_id;
alter table nx_pipeline drop constraint fk__pipeline__trp_id;
alter table nx_service drop constraint fk__service__component_id;
alter table nx_service_param drop constraint fk__service_param__service_id;
alter table nx_user drop constraint fk__user__role_id;
drop table nx_action;
drop table nx_certificate;
drop table nx_choreography;
drop table nx_component;
drop table nx_connection;
drop table nx_conversation;
drop table nx_follow_up_action;
drop table nx_generic_param;
drop table nx_grant;
drop table nx_log;
drop table nx_logger;
drop table nx_logger_param;
drop table nx_mapping;
drop table nx_message;
drop table nx_message_label;
drop table nx_message_payload;
drop table nx_participant;
drop table nx_partner;
drop table nx_pipelet;
drop table nx_pipelet_param;
drop table nx_pipeline;
drop table nx_role;
drop table nx_service;
drop table nx_service_param;
drop table nx_trp;
drop table nx_user;
create table nx_action (nx_action_id integer identity not null, nx_choreography_id int not null, created_date datetime not null, modified_date datetime not null, modified_nx_user_id integer not null, start_flag tinyint not null, end_flag tinyint not null, inbound_nx_pipeline_id int not null, outbound_nx_pipeline_id int not null, status_update_nx_pipeline_id int null, name varchar(64) not null, primary key (nx_action_id));
create table nx_certificate (nx_certificate_id integer identity not null, type integer not null, password varchar(64) null, created_date datetime not null, modified_date datetime not null, modified_nx_user_id integer not null, nx_partner_id int null, name varchar(512) not null, description varchar(256) null, binary_data image null, primary key (nx_certificate_id));
create table nx_choreography (nx_choreography_id integer identity not null, description varchar(255) null, name varchar(255) not null unique, created_date datetime not null, modified_date datetime not null, modified_nx_user_id integer not null, primary key (nx_choreography_id));
create table nx_component (nx_component_id integer identity not null, created_date datetime not null, modified_date datetime not null, modified_nx_user_id integer not null, type integer not null, name varchar(64) not null, class_name varchar(64) not null, description varchar(64) null, primary key (nx_component_id));
create table nx_connection (nx_connection_id integer identity not null, nx_certificate_id int null, nx_trp_id int not null, nx_partner_id int not null, timeout integer not null, message_interval integer not null, security_flag tinyint not null, reliable_flag tinyint not null, synchronous_flag tinyint not null, synchronous_timeout integer not null, retries integer not null, created_date datetime not null, modified_date datetime not null, modified_nx_user_id integer not null, uri varchar(512) not null, name varchar(64) not null, login_name varchar(64) null, password varchar(64) null, description varchar(64) null, primary key (nx_connection_id));
create table nx_conversation (nx_conversation_id integer identity not null, nx_choreography_id int not null, nx_partner_id int not null, conversation_id varchar(96) not null, created_date datetime not null, end_date datetime null, modified_date datetime not null, modified_nx_user_id integer not null, status integer not null, message_count integer not null, current_nx_action_id int not null, primary key (nx_conversation_id));
create table nx_follow_up_action (nx_follow_up_action_id integer identity not null, nx_action_id int not null, follow_up_nx_action_id int not null, created_date datetime not null, modified_date datetime not null, modified_nx_user_id integer not null, primary key (nx_follow_up_action_id));
create table nx_generic_param (nx_generic_param_id integer identity not null, category varchar(128) not null, param_tag varchar(128) null, created_date datetime not null, modified_date datetime not null, modified_nx_user_id integer not null, param_name varchar(64) not null, param_label varchar(64) null, param_value varchar(1024) null, sequence_number integer null, primary key (nx_generic_param_id));
create table nx_grant (nx_grant_id integer identity not null, target varchar(64) not null, created_date datetime not null, modified_date datetime not null, modified_nx_user_id integer not null, nx_role_id int null, nxGrantId int not null, primary key (nx_grant_id));
create table nx_log (nx_log_id integer identity not null, log_id varchar(255) not null, class_name varchar(255) not null, method_name varchar(255) not null, event_id integer not null, severity integer not null, conversation_id varchar(96) not null, message_id varchar(96) not null, description varchar(255) not null, created_date datetime not null, primary key (nx_log_id));
create table nx_logger (nx_logger_id int identity not null, nx_component_id int not null, created_date datetime not null, modified_date datetime not null, modified_nx_user_id integer not null, name varchar(64) not null, autostart_flag tinyint not null, threshold integer not null, filter varchar(4098) not null, description varchar(64) null, primary key (nx_logger_id));
create table nx_logger_param (nx_logger_param_id integer identity not null, nx_logger_id int not null, created_date datetime not null, modified_date datetime not null, modified_nx_user_id integer not null, param_name varchar(64) not null, param_label varchar(64) null, param_value varchar(1024) null, sequence_number integer null, primary key (nx_logger_param_id));
create table nx_mapping (nx_mapping_id integer identity not null, category varchar(128) not null, created_date datetime not null, modified_date datetime not null, modified_nx_user_id integer not null, left_type integer not null, left_value varchar(128) null, right_type integer not null, right_value varchar(128) null, primary key (nx_mapping_id));
create table nx_message (nx_message_id integer identity not null, nx_conversation_id int not null, message_id varchar(96) not null, header_data image null, type integer not null, nx_action_id int not null, status integer not null, nx_trp_id int null, referenced_nx_message_id int null, retries integer not null, direction_flag tinyint not null, expiration_date datetime null, end_date datetime null, created_date datetime not null, modified_date datetime not null, modified_nx_user_id integer not null, primary key (nx_message_id));
create table nx_message_label (nx_message_label_id integer identity not null, nx_message_id int not null, created_date datetime not null, modified_date datetime not null, modified_nx_user_id integer not null, message_label varchar(64) null, message_label_value varchar(512) not null, primary key (nx_message_label_id));
create table nx_message_payload (nx_message_payload_id integer identity not null, nx_message_id int not null, sequence_number integer not null, mime_type varchar(64) not null, content_id varchar(96) not null, payload_data image not null, created_date datetime not null, modified_date datetime not null, modified_nx_user_id integer not null, primary key (nx_message_payload_id));
create table nx_participant (nx_participant_id integer identity not null, nx_local_certificate_id int null, nx_partner_id int not null, nx_choreography_id int not null, nx_local_partner_id int not null, nx_connection_id int not null, created_date datetime not null, modified_date datetime not null, modified_nx_user_id integer not null, description varchar(64) null, primary key (nx_participant_id));
create table nx_partner (nx_partner_id integer identity not null, type integer not null, company_name varchar(64) null, address_line_1 varchar(64) null, address_line_2 varchar(64) null, city varchar(64) null, state varchar(64) null, zip varchar(64) null, country varchar(64) null, created_date datetime not null, modified_date datetime not null, modified_nx_user_id integer not null, name varchar(64) null, description varchar(64) null, partner_id varchar(64) not null, partner_id_type varchar(64) not null, primary key (nx_partner_id), unique (type, partner_id));
create table nx_pipelet (nx_pipelet_id int identity not null, nx_pipeline_id int not null, nx_component_id int not null, frontend_flag tinyint not null, forward_flag tinyint not null, endpoint_flag tinyint not null, created_date datetime not null, modified_date datetime not null, modified_nx_user_id integer not null, position integer not null, name varchar(64) not null, description varchar(64) null, primary key (nx_pipelet_id));
create table nx_pipelet_param (nx_pipelet_param_id integer identity not null, nx_pipelet_id int not null, created_date datetime not null, modified_date datetime not null, modified_nx_user_id integer not null, param_name varchar(64) not null, param_label varchar(64) null, param_value varchar(1024) null, sequence_number integer null, primary key (nx_pipelet_param_id));
create table nx_pipeline (nx_pipeline_id integer identity not null, direction_flag tinyint not null, frontend_flag tinyint not null, nx_trp_id int null, created_date datetime not null, modified_date datetime not null, modified_nx_user_id integer not null, description varchar(64) null, name varchar(64) not null, primary key (nx_pipeline_id));
create table nx_role (nx_role_id integer identity not null, name varchar(64) not null, description varchar(64) null, created_date datetime not null, modified_date datetime not null, modified_nx_user_id integer not null, primary key (nx_role_id));
create table nx_service (nx_service_id int identity not null, nx_component_id int not null, created_date datetime not null, modified_date datetime not null, modified_nx_user_id integer not null, position integer not null, autostart_flag tinyint not null, name varchar(64) not null, description varchar(64) null, primary key (nx_service_id));
create table nx_service_param (nx_service_param_id integer identity not null, nx_service_id int not null, created_date datetime not null, modified_date datetime not null, modified_nx_user_id integer not null, param_name varchar(64) not null, param_label varchar(64) null, param_value varchar(1024) null, sequence_number integer null, primary key (nx_service_param_id));
create table nx_trp (nx_trp_id integer identity not null, protocol varchar(64) not null, version varchar(64) not null, transport varchar(64) not null, created_date datetime not null, modified_date datetime not null, modified_nx_user_id integer not null, primary key (nx_trp_id));
create table nx_user (nx_user_id integer identity not null, login_name varchar(64) not null, first_name varchar(64) not null, middle_name varchar(64) null, last_name varchar(64) not null, password varchar(64) not null, created_date datetime not null, modified_date datetime not null, modified_nx_user_id integer not null, active_flag tinyint not null, visible_flag tinyint not null, nx_role_id int null, primary key (nx_user_id));
create index ix_action_3 on nx_action (outbound_nx_pipeline_id);
create index ix_action_1 on nx_action (nx_choreography_id);
create index ix_action_2 on nx_action (inbound_nx_pipeline_id);
create index ix_action_4 on nx_action (status_update_nx_pipeline_id);
alter table nx_action add constraint fk__action__st_upd_pipeline_id foreign key (status_update_nx_pipeline_id) references nx_pipeline;
alter table nx_action add constraint fk__action__in_pipeline_id foreign key (inbound_nx_pipeline_id) references nx_pipeline;
alter table nx_action add constraint fk__action__out_pipeline_id foreign key (outbound_nx_pipeline_id) references nx_pipeline;
alter table nx_action add constraint fk__action__choreography_id foreign key (nx_choreography_id) references nx_choreography;
create index ix_certificate_1 on nx_certificate (nx_partner_id);
alter table nx_certificate add constraint fk__certificate__partner_id foreign key (nx_partner_id) references nx_partner;
create index ix_connection_3 on nx_connection (nx_partner_id);
create index ix_connection_2 on nx_connection (nx_trp_id);
create index ix_connection_1 on nx_connection (nx_certificate_id);
alter table nx_connection add constraint fk__connection__certificate_id foreign key (nx_certificate_id) references nx_certificate;
alter table nx_connection add constraint fk__connection__partner_id foreign key (nx_partner_id) references nx_partner;
alter table nx_connection add constraint fk__connection__trp_id foreign key (nx_trp_id) references nx_trp;
create index ix_conversation_3 on nx_conversation (current_nx_action_id);
create index ix_conversation_2 on nx_conversation (nx_partner_id);
create index ix_conversation_1 on nx_conversation (nx_choreography_id);
alter table nx_conversation add constraint fk__conversation__current_action_id foreign key (current_nx_action_id) references nx_action;
alter table nx_conversation add constraint fk__conv__partner_id foreign key (nx_partner_id) references nx_partner;
alter table nx_conversation add constraint fk__conv__choreography_id foreign key (nx_choreography_id) references nx_choreography;
create index ix_follow_up_action_2 on nx_follow_up_action (follow_up_nx_action_id);
create index ix_follow_up_action_1 on nx_follow_up_action (nx_action_id);
alter table nx_follow_up_action add constraint fk__fua__follow_up_action_id foreign key (follow_up_nx_action_id) references nx_action;
alter table nx_follow_up_action add constraint fk__fua__action_id foreign key (nx_action_id) references nx_action;
create index ix_grant_1 on nx_grant (nx_role_id);
alter table nx_grant add constraint fk__grant__grant_id foreign key (nxGrantId) references nx_role;
alter table nx_grant add constraint fk__grant__role_id foreign key (nx_role_id) references nx_role;
create index ix_logger_1 on nx_logger (nx_component_id);
alter table nx_logger add constraint fk__logger__component_id foreign key (nx_component_id) references nx_component;
alter table nx_logger_param add constraint fk__logger_param__logger_id foreign key (nx_logger_id) references nx_logger;
create index ix_message_1 on nx_message (nx_conversation_id);
create index ix_message_3 on nx_message (nx_trp_id);
create index ix_message_4 on nx_message (referenced_nx_message_id);
create index ix_message_5 on nx_message (status);
create index ix_message_2 on nx_message (nx_action_id);
alter table nx_message add constraint fk__msg__conversation_id foreign key (nx_conversation_id) references nx_conversation;
alter table nx_message add constraint fk__msg__trp_id foreign key (nx_trp_id) references nx_trp;
alter table nx_message add constraint fk__message__action_id foreign key (nx_action_id) references nx_action;
alter table nx_message add constraint fk__msg__referenced_message_id foreign key (referenced_nx_message_id) references nx_message;
create index ix_message_label_1 on nx_message_label (nx_message_id);
alter table nx_message_label add constraint fk__message_label__message_id foreign key (nx_message_id) references nx_message;
create index ix_message_payload_1 on nx_message_payload (nx_message_id);
alter table nx_message_payload add constraint fk__msg_payload__message_id foreign key (nx_message_id) references nx_message;
create index ix_participant_5 on nx_participant (nx_connection_id);
create index ix_participant_2 on nx_participant (nx_partner_id);
create index ix_participant_4 on nx_participant (nx_local_partner_id);
create index ix_participant_1 on nx_participant (nx_local_certificate_id);
create index ix_participant_3 on nx_participant (nx_choreography_id);
alter table nx_participant add constraint fk__part__connection_id foreign key (nx_connection_id) references nx_connection;
alter table nx_participant add constraint fk__part__partner_id foreign key (nx_partner_id) references nx_partner;
alter table nx_participant add constraint fk__part__certificate_id foreign key (nx_local_certificate_id) references nx_certificate;
alter table nx_participant add constraint fk__part__choreography_id foreign key (nx_choreography_id) references nx_choreography;
alter table nx_participant add constraint fk__part__local_partner_id foreign key (nx_local_partner_id) references nx_partner;
create index ix_pipelet_1 on nx_pipelet (nx_pipeline_id);
create index ix_pipelet_2 on nx_pipelet (nx_component_id);
alter table nx_pipelet add constraint fk__pipelet__component_id foreign key (nx_component_id) references nx_component;
alter table nx_pipelet add constraint fk__pipelet__pipeline_id foreign key (nx_pipeline_id) references nx_pipeline;
create index ix_pipelet_param_1 on nx_pipelet_param (nx_pipelet_id);
alter table nx_pipelet_param add constraint fk__pipelet_param__pipelet_id foreign key (nx_pipelet_id) references nx_pipelet;
create index ix_pipeline_1 on nx_pipeline (nx_trp_id);
alter table nx_pipeline add constraint fk__pipeline__trp_id foreign key (nx_trp_id) references nx_trp;
create index ix_service_1 on nx_service (nx_component_id);
alter table nx_service add constraint fk__service__component_id foreign key (nx_component_id) references nx_component;
create index ix_service_param_1 on nx_service_param (nx_service_id);
alter table nx_service_param add constraint fk__service_param__service_id foreign key (nx_service_id) references nx_service;
create index ix_user_1 on nx_user (nx_role_id);
alter table nx_user add constraint fk__user__role_id foreign key (nx_role_id) references nx_role;
