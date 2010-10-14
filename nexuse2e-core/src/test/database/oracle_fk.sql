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
alter table nx_action add constraint fk__action__status_upd_ppl_id foreign key (status_update_nx_pipeline_id) references nx_pipeline;
alter table nx_action add constraint fk__action__inbound_ppl_id foreign key (inbound_nx_pipeline_id) references nx_pipeline;
alter table nx_action add constraint fk__action__outbound_ppl_id foreign key (outbound_nx_pipeline_id) references nx_pipeline;
alter table nx_conversation add constraint fk__conversation__c_action_id foreign key (current_nx_action_id) references nx_action;
alter table nx_conversation add constraint fk__conversation__chor_id foreign key (nx_choreography_id) references nx_choreography;
alter table nx_follow_up_action add constraint fk__follow_u_a__follow_u_a_id foreign key (follow_up_nx_action_id) references nx_action;
alter table nx_follow_up_action add constraint fk__follow_u_a__action_id foreign key (nx_action_id) references nx_action;
alter table nx_message add constraint fk__message__ref_message_id foreign key (referenced_nx_message_id) references nx_message;
alter table nx_message_payload add constraint fk__message_payl__message_id foreign key (nx_message_id) references nx_message;
alter table nx_participant add constraint fk__participant__cert_id foreign key (nx_local_certificate_id) references nx_certificate;
alter table nx_participant add constraint fk__participant__chor_id foreign key (nx_choreography_id) references nx_choreography;
alter table nx_participant add constraint fk__participant__l_partner_id foreign key (nx_local_partner_id) references nx_partner;
