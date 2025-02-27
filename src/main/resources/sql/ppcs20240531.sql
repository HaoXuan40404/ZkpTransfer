alter table d_authorization_request add column dataset_title varchar(255) default '' after dataset_id;
alter table d_authorization_request add column owner_agency_id varchar(255) default '' after authorized_agency_id;

