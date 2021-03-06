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
alter table nx_action drop foreign key fk__action__st_upd_pipeline_id;
alter table nx_action drop foreign key fk__action__in_pipeline_id;
alter table nx_action drop foreign key fk__action__out_pipeline_id;
alter table nx_action drop foreign key fk__action__choreography_id;
alter table nx_certificate drop foreign key fk__certificate__partner_id;
alter table nx_connection drop foreign key fk__connection__certificate_id;
alter table nx_connection drop foreign key fk__connection__partner_id;
alter table nx_connection drop foreign key fk__connection__trp_id;
alter table nx_conversation drop foreign key fk__conversation__current_action_id;
alter table nx_conversation drop foreign key fk__conv__partner_id;
alter table nx_conversation drop foreign key fk__conv__choreography_id;
alter table nx_follow_up_action drop foreign key fk__fua__follow_up_action_id;
alter table nx_follow_up_action drop foreign key fk__fua__action_id;
alter table nx_grant drop foreign key fk__grant__grant_id;
alter table nx_grant drop foreign key fk__grant__role_id;
alter table nx_logger drop foreign key fk__logger__component_id;
alter table nx_logger_param drop foreign key fk__logger_param__logger_id;
alter table nx_message drop foreign key fk__msg__conversation_id;
alter table nx_message drop foreign key fk__msg__trp_id;
alter table nx_message drop foreign key fk__message__action_id;
alter table nx_message drop foreign key fk__msg__referenced_message_id;
alter table nx_message_label drop foreign key fk__message_label__message_id;
alter table nx_message_payload drop foreign key fk__msg_payload__message_id;
alter table nx_participant drop foreign key fk__part__connection_id;
alter table nx_participant drop foreign key fk__part__partner_id;
alter table nx_participant drop foreign key fk__part__certificate_id;
alter table nx_participant drop foreign key fk__part__choreography_id;
alter table nx_participant drop foreign key fk__part__local_partner_id;
alter table nx_pipelet drop foreign key fk__pipelet__component_id;
alter table nx_pipelet drop foreign key fk__pipelet__pipeline_id;
alter table nx_pipelet_param drop foreign key fk__pipelet_param__pipelet_id;
alter table nx_pipeline drop foreign key fk__pipeline__trp_id;
alter table nx_service drop foreign key fk__service__component_id;
alter table nx_service_param drop foreign key fk__service_param__service_id;
alter table nx_user drop foreign key fk__user__role_id;
drop table if exists nx_action;
drop table if exists nx_certificate;
drop table if exists nx_choreography;
drop table if exists nx_component;
drop table if exists nx_connection;
drop table if exists nx_conversation;
drop table if exists nx_follow_up_action;
drop table if exists nx_generic_param;
drop table if exists nx_grant;
drop table if exists nx_log;
drop table if exists nx_logger;
drop table if exists nx_logger_param;
drop table if exists nx_mapping;
drop table if exists nx_message;
drop table if exists nx_message_label;
drop table if exists nx_message_payload;
drop table if exists nx_participant;
drop table if exists nx_partner;
drop table if exists nx_pipelet;
drop table if exists nx_pipelet_param;
drop table if exists nx_pipeline;
drop table if exists nx_role;
drop table if exists nx_service;
drop table if exists nx_service_param;
drop table if exists nx_trp;
drop table if exists nx_user;
create table nx_action (nx_action_id integer not null auto_increment, nx_choreography_id integer not null, created_date datetime not null comment '', modified_date datetime not null comment '', modified_nx_user_id integer not null comment '', start_flag bit not null comment '', end_flag bit not null comment '', inbound_nx_pipeline_id integer not null, outbound_nx_pipeline_id integer not null, status_update_nx_pipeline_id integer, name varchar(64) not null comment '', primary key (nx_action_id)) comment='';
create table nx_certificate (nx_certificate_id integer not null auto_increment, type integer not null comment 'e.g. 1=Local Server Certificate, 2=PartnerCertificate, 3=CA Certificate, 4=Request', password varchar(64) comment '', created_date datetime not null comment '', modified_date datetime not null comment '', modified_nx_user_id integer not null comment '', nx_partner_id integer, name text not null comment '', description text comment '', binary_data mediumblob comment '', primary key (nx_certificate_id)) comment='';
create table nx_choreography (nx_choreography_id integer not null auto_increment, description varchar(255) comment '', name varchar(255) not null unique comment '', created_date datetime not null comment '', modified_date datetime not null comment '', modified_nx_user_id integer not null comment '', primary key (nx_choreography_id)) comment='';
create table nx_component (nx_component_id integer not null auto_increment, created_date datetime not null comment '', modified_date datetime not null comment '', modified_nx_user_id integer not null comment '', type integer not null comment '1=pipelet, 2=notifier', name varchar(64) not null comment '', class_name varchar(64) not null comment '', description varchar(64) comment '', primary key (nx_component_id)) comment='';
create table nx_connection (nx_connection_id integer not null auto_increment, nx_certificate_id integer, nx_trp_id integer not null, nx_partner_id integer not null, timeout integer not null comment '', message_interval integer not null comment '', security_flag bit not null comment '', reliable_flag bit not null comment '', synchronous_flag bit not null, synchronous_timeout integer not null comment '', retries integer not null comment '', created_date datetime not null comment '', modified_date datetime not null comment '', modified_nx_user_id integer not null comment '', uri text not null comment '', name varchar(64) not null comment '', login_name varchar(64) comment '', password varchar(64) comment '', description varchar(64) comment '', primary key (nx_connection_id)) comment='';
create table nx_conversation (nx_conversation_id integer not null auto_increment, nx_choreography_id integer not null, nx_partner_id integer not null, conversation_id varchar(96) not null comment 'Protocol specific conversation Id', created_date datetime not null comment '', end_date datetime comment '', modified_date datetime not null comment '', modified_nx_user_id integer not null comment '', status integer not null comment 'Message Status: e.g. processing, idle, awaiting_ack, error, complete', message_count integer not null, current_nx_action_id integer not null, primary key (nx_conversation_id)) comment='';
create table nx_follow_up_action (nx_follow_up_action_id integer not null auto_increment, nx_action_id integer not null, follow_up_nx_action_id integer not null, created_date datetime not null comment '', modified_date datetime not null comment '', modified_nx_user_id integer not null comment '', primary key (nx_follow_up_action_id)) comment='';
create table nx_generic_param (nx_generic_param_id integer not null auto_increment, category varchar(128) not null comment '', param_tag varchar(128) comment '', created_date datetime not null comment '', modified_date datetime not null comment '', modified_nx_user_id integer not null comment '', param_name varchar(64) not null comment '', param_label varchar(64) comment '', param_value text comment '', sequence_number integer comment '', primary key (nx_generic_param_id)) comment='';
create table nx_grant (nx_grant_id integer not null auto_increment, target varchar(64) not null comment '', created_date datetime not null comment '', modified_date datetime not null comment '', modified_nx_user_id integer not null comment '', nx_role_id integer, nxGrantId integer not null, primary key (nx_grant_id)) comment='';
create table nx_log (nx_log_id integer not null auto_increment, log_id varchar(255) not null comment 'might be used to separate different nexus processes', class_name varchar(255) not null comment '', method_name varchar(255) not null comment '', event_id integer not null comment '', severity integer not null comment '', conversation_id varchar(96) not null comment '', message_id varchar(96) not null comment '', description varchar(255) not null comment '', created_date datetime not null comment '', primary key (nx_log_id)) comment='';
create table nx_logger (nx_logger_id integer not null auto_increment, nx_component_id integer not null, created_date datetime not null comment '', modified_date datetime not null comment '', modified_nx_user_id integer not null comment '', name varchar(64) not null comment '', autostart_flag bit not null comment '', threshold integer not null comment '', filter text not null comment '', description varchar(64) comment '', primary key (nx_logger_id)) comment='';
create table nx_logger_param (nx_logger_param_id integer not null auto_increment, nx_logger_id integer not null, created_date datetime not null comment '', modified_date datetime not null comment '', modified_nx_user_id integer not null comment '', param_name varchar(64) not null comment '', param_label varchar(64) comment '', param_value text comment '', sequence_number integer comment '', primary key (nx_logger_param_id)) comment='';
create table nx_mapping (nx_mapping_id integer not null auto_increment, category varchar(128) not null comment '', created_date datetime not null comment '', modified_date datetime not null comment '', modified_nx_user_id integer not null comment '', left_type integer not null comment '', left_value varchar(128) comment '', right_type integer not null comment '', right_value varchar(128) comment '', primary key (nx_mapping_id)) comment='';
create table nx_message (nx_message_id integer not null auto_increment, nx_conversation_id integer not null, message_id varchar(96) not null comment 'Protocol specific Message Id', header_data mediumblob comment '', type integer not null comment 'Message Type: e.g. 1=Normal, 2=Ack, 3=Error (Defined in messaging.Constants)', nx_action_id integer not null, status integer not null comment 'Message Status: e.g. queued, stopped, failed, retrying, send', nx_trp_id integer, referenced_nx_message_id integer, retries integer not null comment '', direction_flag bit not null comment 'outbound is true, inbound is false', expiration_date datetime comment '', end_date datetime comment '', created_date datetime not null comment '', modified_date datetime not null comment '', modified_nx_user_id integer not null comment '', primary key (nx_message_id)) comment='';
create table nx_message_label (nx_message_label_id integer not null auto_increment, nx_message_id integer not null, created_date datetime not null comment '', modified_date datetime not null comment '', modified_nx_user_id integer not null comment '', message_label varchar(64) comment '', message_label_value text not null comment '', primary key (nx_message_label_id)) comment='';
create table nx_message_payload (nx_message_payload_id integer not null auto_increment, nx_message_id integer not null, sequence_number integer not null comment '', mime_type varchar(64) not null comment '', content_id varchar(96) not null comment '', payload_data mediumblob not null comment '', created_date datetime not null comment '', modified_date datetime not null comment '', modified_nx_user_id integer not null comment '', primary key (nx_message_payload_id)) comment='';
create table nx_participant (nx_participant_id integer not null auto_increment, nx_local_certificate_id integer, nx_partner_id integer not null, nx_choreography_id integer not null, nx_local_partner_id integer not null, nx_connection_id integer not null, created_date datetime not null comment '', modified_date datetime not null comment '', modified_nx_user_id integer not null comment '', description varchar(64) comment '', primary key (nx_participant_id)) comment='';
create table nx_partner (nx_partner_id integer not null auto_increment, type integer not null comment 'e.g Localpartner information or Businesspartner', company_name varchar(64) comment '', address_line_1 varchar(64) comment '', address_line_2 varchar(64) comment '', city varchar(64) comment '', state varchar(64) comment '', zip varchar(64) comment '', country varchar(64) comment '', created_date datetime not null comment '', modified_date datetime not null comment '', modified_nx_user_id integer not null comment '', name varchar(64) comment '', description varchar(64) comment '', partner_id varchar(64) not null comment '', partner_id_type varchar(64) not null comment '', primary key (nx_partner_id), unique (type, partner_id)) comment='';
create table nx_pipelet (nx_pipelet_id integer not null auto_increment, nx_pipeline_id integer not null, nx_component_id integer not null, frontend_flag bit not null comment 'frontend is true, backend is false', forward_flag bit not null comment 'forward is true, return is false', endpoint_flag bit not null comment 'endpoint is true, pipelet in pipeline is false', created_date datetime not null comment '', modified_date datetime not null comment '', modified_nx_user_id integer not null comment '', position integer not null comment 'position in pipeline. Starting with 0', name varchar(64) not null comment '', description varchar(64) comment '', primary key (nx_pipelet_id)) comment='';
create table nx_pipelet_param (nx_pipelet_param_id integer not null auto_increment, nx_pipelet_id integer not null, created_date datetime not null comment '', modified_date datetime not null comment '', modified_nx_user_id integer not null comment '', param_name varchar(64) not null comment '', param_label varchar(64) comment '', param_value text comment '', sequence_number integer comment '', primary key (nx_pipelet_param_id)) comment='';
create table nx_pipeline (nx_pipeline_id integer not null auto_increment, direction_flag bit not null comment '', frontend_flag bit not null comment 'frontend is true, backend is false', nx_trp_id integer, created_date datetime not null comment '', modified_date datetime not null comment '', modified_nx_user_id integer not null comment '', description varchar(64) comment '', name varchar(64) not null comment '', primary key (nx_pipeline_id)) comment='';
create table nx_role (nx_role_id integer not null auto_increment, name varchar(64) not null comment '', description varchar(64) comment '', created_date datetime not null comment '', modified_date datetime not null comment '', modified_nx_user_id integer not null comment '', primary key (nx_role_id)) comment='';
create table nx_service (nx_service_id integer not null auto_increment, nx_component_id integer not null, created_date datetime not null comment '', modified_date datetime not null comment '', modified_nx_user_id integer not null comment '', position integer not null comment 'Service position, starting with 0', autostart_flag bit not null comment '', name varchar(64) not null comment '', description varchar(64) comment '', primary key (nx_service_id)) comment='';
create table nx_service_param (nx_service_param_id integer not null auto_increment, nx_service_id integer not null, created_date datetime not null comment '', modified_date datetime not null comment '', modified_nx_user_id integer not null comment '', param_name varchar(64) not null comment '', param_label varchar(64) comment '', param_value text comment '', sequence_number integer comment '', primary key (nx_service_param_id)) comment='';
create table nx_trp (nx_trp_id integer not null auto_increment, protocol varchar(64) not null comment '', version varchar(64) not null comment '', transport varchar(64) not null comment '', created_date datetime not null comment '', modified_date datetime not null comment '', modified_nx_user_id integer not null comment '', primary key (nx_trp_id)) comment='';
create table nx_user (nx_user_id integer not null auto_increment, login_name varchar(64) not null comment '', first_name varchar(64) not null comment '', middle_name varchar(64) comment '', last_name varchar(64) not null comment '', password varchar(64) not null comment '', created_date datetime not null comment '', modified_date datetime not null comment '', modified_nx_user_id integer not null comment '', active_flag bit not null comment '', visible_flag bit not null comment '', nx_role_id integer, primary key (nx_user_id)) comment='';
create index ix_action_3 on nx_action (outbound_nx_pipeline_id);
create index ix_action_1 on nx_action (nx_choreography_id);
create index ix_action_2 on nx_action (inbound_nx_pipeline_id);
create index ix_action_4 on nx_action (status_update_nx_pipeline_id);
alter table nx_action add index fk__action__st_upd_pipeline_id (status_update_nx_pipeline_id), add constraint fk__action__st_upd_pipeline_id foreign key (status_update_nx_pipeline_id) references nx_pipeline (nx_pipeline_id);
alter table nx_action add index fk__action__in_pipeline_id (inbound_nx_pipeline_id), add constraint fk__action__in_pipeline_id foreign key (inbound_nx_pipeline_id) references nx_pipeline (nx_pipeline_id);
alter table nx_action add index fk__action__out_pipeline_id (outbound_nx_pipeline_id), add constraint fk__action__out_pipeline_id foreign key (outbound_nx_pipeline_id) references nx_pipeline (nx_pipeline_id);
alter table nx_action add index fk__action__choreography_id (nx_choreography_id), add constraint fk__action__choreography_id foreign key (nx_choreography_id) references nx_choreography (nx_choreography_id);
create index ix_certificate_1 on nx_certificate (nx_partner_id);
alter table nx_certificate add index fk__certificate__partner_id (nx_partner_id), add constraint fk__certificate__partner_id foreign key (nx_partner_id) references nx_partner (nx_partner_id);
create index ix_connection_3 on nx_connection (nx_partner_id);
create index ix_connection_2 on nx_connection (nx_trp_id);
create index ix_connection_1 on nx_connection (nx_certificate_id);
alter table nx_connection add index fk__connection__certificate_id (nx_certificate_id), add constraint fk__connection__certificate_id foreign key (nx_certificate_id) references nx_certificate (nx_certificate_id);
alter table nx_connection add index fk__connection__partner_id (nx_partner_id), add constraint fk__connection__partner_id foreign key (nx_partner_id) references nx_partner (nx_partner_id);
alter table nx_connection add index fk__connection__trp_id (nx_trp_id), add constraint fk__connection__trp_id foreign key (nx_trp_id) references nx_trp (nx_trp_id);
create index ix_conversation_3 on nx_conversation (current_nx_action_id);
create index ix_conversation_2 on nx_conversation (nx_partner_id);
create index ix_conversation_1 on nx_conversation (nx_choreography_id);
alter table nx_conversation add index fk__conversation__current_action_id (current_nx_action_id), add constraint fk__conversation__current_action_id foreign key (current_nx_action_id) references nx_action (nx_action_id);
alter table nx_conversation add index fk__conv__partner_id (nx_partner_id), add constraint fk__conv__partner_id foreign key (nx_partner_id) references nx_partner (nx_partner_id);
alter table nx_conversation add index fk__conv__choreography_id (nx_choreography_id), add constraint fk__conv__choreography_id foreign key (nx_choreography_id) references nx_choreography (nx_choreography_id);
create index ix_follow_up_action_2 on nx_follow_up_action (follow_up_nx_action_id);
create index ix_follow_up_action_1 on nx_follow_up_action (nx_action_id);
alter table nx_follow_up_action add index fk__fua__follow_up_action_id (follow_up_nx_action_id), add constraint fk__fua__follow_up_action_id foreign key (follow_up_nx_action_id) references nx_action (nx_action_id);
alter table nx_follow_up_action add index fk__fua__action_id (nx_action_id), add constraint fk__fua__action_id foreign key (nx_action_id) references nx_action (nx_action_id);
create index ix_grant_1 on nx_grant (nx_role_id);
alter table nx_grant add index fk__grant__grant_id (nxGrantId), add constraint fk__grant__grant_id foreign key (nxGrantId) references nx_role (nx_role_id);
alter table nx_grant add index fk__grant__role_id (nx_role_id), add constraint fk__grant__role_id foreign key (nx_role_id) references nx_role (nx_role_id);
create index ix_logger_1 on nx_logger (nx_component_id);
alter table nx_logger add index fk__logger__component_id (nx_component_id), add constraint fk__logger__component_id foreign key (nx_component_id) references nx_component (nx_component_id);
alter table nx_logger_param add index fk__logger_param__logger_id (nx_logger_id), add constraint fk__logger_param__logger_id foreign key (nx_logger_id) references nx_logger (nx_logger_id);
create index ix_message_1 on nx_message (nx_conversation_id);
create index ix_message_3 on nx_message (nx_trp_id);
create index ix_message_4 on nx_message (referenced_nx_message_id);
create index ix_message_5 on nx_message (status);
create index ix_message_2 on nx_message (nx_action_id);
alter table nx_message add index fk__msg__conversation_id (nx_conversation_id), add constraint fk__msg__conversation_id foreign key (nx_conversation_id) references nx_conversation (nx_conversation_id);
alter table nx_message add index fk__msg__trp_id (nx_trp_id), add constraint fk__msg__trp_id foreign key (nx_trp_id) references nx_trp (nx_trp_id);
alter table nx_message add index fk__message__action_id (nx_action_id), add constraint fk__message__action_id foreign key (nx_action_id) references nx_action (nx_action_id);
alter table nx_message add index fk__msg__referenced_message_id (referenced_nx_message_id), add constraint fk__msg__referenced_message_id foreign key (referenced_nx_message_id) references nx_message (nx_message_id);
create index ix_message_label_1 on nx_message_label (nx_message_id);
alter table nx_message_label add index fk__message_label__message_id (nx_message_id), add constraint fk__message_label__message_id foreign key (nx_message_id) references nx_message (nx_message_id);
create index ix_message_payload_1 on nx_message_payload (nx_message_id);
alter table nx_message_payload add index fk__msg_payload__message_id (nx_message_id), add constraint fk__msg_payload__message_id foreign key (nx_message_id) references nx_message (nx_message_id);
create index ix_participant_5 on nx_participant (nx_connection_id);
create index ix_participant_2 on nx_participant (nx_partner_id);
create index ix_participant_4 on nx_participant (nx_local_partner_id);
create index ix_participant_1 on nx_participant (nx_local_certificate_id);
create index ix_participant_3 on nx_participant (nx_choreography_id);
alter table nx_participant add index fk__part__connection_id (nx_connection_id), add constraint fk__part__connection_id foreign key (nx_connection_id) references nx_connection (nx_connection_id);
alter table nx_participant add index fk__part__partner_id (nx_partner_id), add constraint fk__part__partner_id foreign key (nx_partner_id) references nx_partner (nx_partner_id);
alter table nx_participant add index fk__part__certificate_id (nx_local_certificate_id), add constraint fk__part__certificate_id foreign key (nx_local_certificate_id) references nx_certificate (nx_certificate_id);
alter table nx_participant add index fk__part__choreography_id (nx_choreography_id), add constraint fk__part__choreography_id foreign key (nx_choreography_id) references nx_choreography (nx_choreography_id);
alter table nx_participant add index fk__part__local_partner_id (nx_local_partner_id), add constraint fk__part__local_partner_id foreign key (nx_local_partner_id) references nx_partner (nx_partner_id);
create index ix_pipelet_1 on nx_pipelet (nx_pipeline_id);
create index ix_pipelet_2 on nx_pipelet (nx_component_id);
alter table nx_pipelet add index fk__pipelet__component_id (nx_component_id), add constraint fk__pipelet__component_id foreign key (nx_component_id) references nx_component (nx_component_id);
alter table nx_pipelet add index fk__pipelet__pipeline_id (nx_pipeline_id), add constraint fk__pipelet__pipeline_id foreign key (nx_pipeline_id) references nx_pipeline (nx_pipeline_id);
create index ix_pipelet_param_1 on nx_pipelet_param (nx_pipelet_id);
alter table nx_pipelet_param add index fk__pipelet_param__pipelet_id (nx_pipelet_id), add constraint fk__pipelet_param__pipelet_id foreign key (nx_pipelet_id) references nx_pipelet (nx_pipelet_id);
create index ix_pipeline_1 on nx_pipeline (nx_trp_id);
alter table nx_pipeline add index fk__pipeline__trp_id (nx_trp_id), add constraint fk__pipeline__trp_id foreign key (nx_trp_id) references nx_trp (nx_trp_id);
create index ix_service_1 on nx_service (nx_component_id);
alter table nx_service add index fk__service__component_id (nx_component_id), add constraint fk__service__component_id foreign key (nx_component_id) references nx_component (nx_component_id);
create index ix_service_param_1 on nx_service_param (nx_service_id);
alter table nx_service_param add index fk__service_param__service_id (nx_service_id), add constraint fk__service_param__service_id foreign key (nx_service_id) references nx_service (nx_service_id);
create index ix_user_1 on nx_user (nx_role_id);
alter table nx_user add index fk__user__role_id (nx_role_id), add constraint fk__user__role_id foreign key (nx_role_id) references nx_role (nx_role_id);
