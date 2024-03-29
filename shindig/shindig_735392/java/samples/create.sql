CREATE TABLE phone (oid BIGINT NOT NULL, person_id BIGINT, PRIMARY KEY (oid))
CREATE TABLE template_params (oid BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL, version BIGINT, template_name VARCHAR(255), template_value VARCHAR(255), activity_id BIGINT, PRIMARY KEY (oid))
CREATE TABLE message (oid BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL, version BIGINT, body VARCHAR(255), title VARCHAR(255), message_type VARCHAR(255), PRIMARY KEY (oid))
CREATE TABLE address (oid BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL, address_usage VARCHAR(31), version BIGINT, country VARCHAR(255), latitude FLOAT, longitude FLOAT, locality VARCHAR(255), postal_code VARCHAR(255), region VARCHAR(255), street_address VARCHAR(255), type VARCHAR(255), formatted VARCHAR(255), primary_address SMALLINT DEFAULT 0, PRIMARY KEY (oid))
CREATE TABLE person (oid BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL, version BIGINT, about_me VARCHAR(255), age INTEGER, children VARCHAR(255), birthday DATE, drinker VARCHAR(255), display_name VARCHAR(255), ethnicity VARCHAR(255), fashion VARCHAR(255), gender VARCHAR(255), happiest_when VARCHAR(255), humor VARCHAR(255), person_id VARCHAR(255), job_interests VARCHAR(255), updated TIMESTAMP, living_arrangement VARCHAR(255), network_presence VARCHAR(255), nickname VARCHAR(255), pets VARCHAR(255), political_views VARCHAR(255), relationship_status VARCHAR(255), religion VARCHAR(255), romance VARCHAR(255), scared_of VARCHAR(255), sexual_orientation VARCHAR(255), smoker VARCHAR(255), status VARCHAR(255), utc_offset BIGINT, address_id BIGINT, name_id BIGINT, body_type_id BIGINT, PRIMARY KEY (oid))
CREATE TABLE media_item (oid BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL, version BIGINT, mime_type VARCHAR(255), media_type VARCHAR(255), url VARCHAR(255), PRIMARY KEY (oid))
CREATE TABLE person_group (oid BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL, version BIGINT, id VARCHAR(255), person_id BIGINT, PRIMARY KEY (oid))
CREATE TABLE group_property (oid BIGINT NOT NULL, group_id BIGINT, PRIMARY KEY (oid))
CREATE TABLE FRIENDDB (oid BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL, version BIGINT, score INTEGER, person_id BIGINT, friend_id BIGINT, PRIMARY KEY (oid))
CREATE TABLE url (oid BIGINT NOT NULL, link_text VARCHAR(255), person_id BIGINT, PRIMARY KEY (oid))
CREATE TABLE body_type (oid BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL, version BIGINT, build VARCHAR(255), eye_color VARCHAR(255), hair_color VARCHAR(255), height FLOAT, weight FLOAT, PRIMARY KEY (oid))
CREATE TABLE photo (oid BIGINT NOT NULL, person_id BIGINT, PRIMARY KEY (oid))
CREATE TABLE name (oid BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL, version BIGINT, additional_name VARCHAR(255), family_name VARCHAR(255), given_name VARCHAR(255), honorific_prefix VARCHAR(255), honorific_suffix VARCHAR(255), unstructured VARCHAR(255), PRIMARY KEY (oid))
CREATE TABLE person_properties (oid BIGINT NOT NULL, person_id BIGINT, PRIMARY KEY (oid))
CREATE TABLE organization (oid BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL, org_usage VARCHAR(31), version BIGINT, description VARCHAR(255), endDate DATE, field VARCHAR(255), name VARCHAR(255), salary VARCHAR(255), start_date DATE, sub_field VARCHAR(255), title VARCHAR(255), webpage VARCHAR(255), type VARCHAR(255), primary_organization SMALLINT DEFAULT 0, PRIMARY KEY (oid))
CREATE TABLE application (oid BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL, version BIGINT, id VARCHAR(255), PRIMARY KEY (oid))
CREATE TABLE list_field (oid BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL, list_field_type VARCHAR(30), version BIGINT, field_type VARCHAR(255), field_value VARCHAR(255), primary_field SMALLINT DEFAULT 0, PRIMARY KEY (oid))
CREATE TABLE activity (oid BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL, version BIGINT, app_id VARCHAR(255), body VARCHAR(255), body_id VARCHAR(255), external_id VARCHAR(255), activity_id VARCHAR(255), updated TIMESTAMP, posted_time BIGINT, priority FLOAT, stream_favicon_url VARCHAR(255), stream_source_url VARCHAR(255), stream_title VARCHAR(255), stream_url VARCHAR(255), title VARCHAR(255), title_id VARCHAR(255), url VARCHAR(255), user_id VARCHAR(255), PRIMARY KEY (oid))
CREATE TABLE person_address (oid BIGINT NOT NULL, person_id BIGINT, PRIMARY KEY (oid))
CREATE TABLE person_application (person_id BIGINT NOT NULL, application_id BIGINT NOT NULL, PRIMARY KEY (person_id, application_id))
CREATE TABLE application_datamap (oid BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL, version BIGINT, person_id VARCHAR(255), application_id BIGINT, PRIMARY KEY (oid))
CREATE TABLE account (oid BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL, account_usage VARCHAR(31), version BIGINT, domain VARCHAR(255), user_id VARCHAR(255), username VARCHAR(255), PRIMARY KEY (oid))
CREATE TABLE activity_media (media_id BIGINT NOT NULL, activity_id BIGINT NOT NULL, PRIMARY KEY (media_id, activity_id))
CREATE TABLE person_organization (oid BIGINT NOT NULL, person_id BIGINT, PRIMARY KEY (oid))
CREATE TABLE im (oid BIGINT NOT NULL, person_id BIGINT, PRIMARY KEY (oid))
CREATE TABLE application_property (oid BIGINT NOT NULL, application_id BIGINT, PRIMARY KEY (oid))
CREATE TABLE friend_property (oid BIGINT NOT NULL, friend_id BIGINT, PRIMARY KEY (oid))
CREATE TABLE person_account (oid BIGINT NOT NULL, primary_account SMALLINT DEFAULT 0, type VARCHAR(255), person_id BIGINT, PRIMARY KEY (oid))
CREATE TABLE organizational_address (oid BIGINT NOT NULL, organization_id BIGINT, PRIMARY KEY (oid))
CREATE TABLE email (oid BIGINT NOT NULL, person_id BIGINT, PRIMARY KEY (oid))
CREATE TABLE membership (group_id BIGINT NOT NULL, person_id BIGINT NOT NULL, PRIMARY KEY (group_id, person_id))
CREATE TABLE application_datavalue (oid BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL, version BIGINT, name VARCHAR(255), value VARCHAR(4094), application_datamap_id BIGINT, PRIMARY KEY (oid))
ALTER TABLE phone ADD CONSTRAINT FK_phone_oid FOREIGN KEY (oid) REFERENCES list_field (oid)
ALTER TABLE phone ADD CONSTRAINT FK_phone_person_id FOREIGN KEY (person_id) REFERENCES person (oid)
ALTER TABLE template_params ADD CONSTRAINT tmpltparamsctvtyid FOREIGN KEY (activity_id) REFERENCES activity (oid)
ALTER TABLE person ADD CONSTRAINT personbody_type_id FOREIGN KEY (body_type_id) REFERENCES body_type (oid)
ALTER TABLE person ADD CONSTRAINT FK_person_name_id FOREIGN KEY (name_id) REFERENCES name (oid)
ALTER TABLE person ADD CONSTRAINT person_address_id FOREIGN KEY (address_id) REFERENCES address (oid)
ALTER TABLE person_group ADD CONSTRAINT prsongrouppersonid FOREIGN KEY (person_id) REFERENCES person (oid)
ALTER TABLE group_property ADD CONSTRAINT gruppropertygrupid FOREIGN KEY (group_id) REFERENCES person_group (oid)
ALTER TABLE group_property ADD CONSTRAINT group_property_oid FOREIGN KEY (oid) REFERENCES list_field (oid)
ALTER TABLE FRIENDDB ADD CONSTRAINT FRIENDDB_person_id FOREIGN KEY (person_id) REFERENCES person (oid)
ALTER TABLE FRIENDDB ADD CONSTRAINT FRIENDDB_friend_id FOREIGN KEY (friend_id) REFERENCES person (oid)
ALTER TABLE url ADD CONSTRAINT FK_url_oid FOREIGN KEY (oid) REFERENCES list_field (oid)
ALTER TABLE url ADD CONSTRAINT FK_url_person_id FOREIGN KEY (person_id) REFERENCES person (oid)
ALTER TABLE photo ADD CONSTRAINT FK_photo_person_id FOREIGN KEY (person_id) REFERENCES person (oid)
ALTER TABLE photo ADD CONSTRAINT FK_photo_oid FOREIGN KEY (oid) REFERENCES list_field (oid)
ALTER TABLE person_properties ADD CONSTRAINT personpropertiesid FOREIGN KEY (oid) REFERENCES list_field (oid)
ALTER TABLE person_properties ADD CONSTRAINT prsnprpertiesprsnd FOREIGN KEY (person_id) REFERENCES person (oid)
ALTER TABLE person_address ADD CONSTRAINT person_address_oid FOREIGN KEY (oid) REFERENCES address (oid)
ALTER TABLE person_address ADD CONSTRAINT prsnaddressprsonid FOREIGN KEY (person_id) REFERENCES person (oid)
ALTER TABLE person_application ADD CONSTRAINT prsnpplctonpplctnd FOREIGN KEY (application_id) REFERENCES application (oid)
ALTER TABLE person_application ADD CONSTRAINT prsnpplcationprsnd FOREIGN KEY (person_id) REFERENCES person (oid)
ALTER TABLE application_datamap ADD CONSTRAINT pplctndtmappplctnd FOREIGN KEY (application_id) REFERENCES application (oid)
ALTER TABLE activity_media ADD CONSTRAINT ctivitymediamdiaid FOREIGN KEY (media_id) REFERENCES media_item (oid)
ALTER TABLE activity_media ADD CONSTRAINT ctvtymediactvityid FOREIGN KEY (activity_id) REFERENCES activity (oid)
ALTER TABLE person_organization ADD CONSTRAINT prsnrgnzationprsnd FOREIGN KEY (person_id) REFERENCES person (oid)
ALTER TABLE person_organization ADD CONSTRAINT prsonorganizationd FOREIGN KEY (oid) REFERENCES organization (oid)
ALTER TABLE im ADD CONSTRAINT FK_im_person_id FOREIGN KEY (person_id) REFERENCES person (oid)
ALTER TABLE im ADD CONSTRAINT FK_im_oid FOREIGN KEY (oid) REFERENCES list_field (oid)
ALTER TABLE application_property ADD CONSTRAINT pplctnprprtpplctnd FOREIGN KEY (application_id) REFERENCES application (oid)
ALTER TABLE application_property ADD CONSTRAINT pplcationpropertyd FOREIGN KEY (oid) REFERENCES list_field (oid)
ALTER TABLE friend_property ADD CONSTRAINT friend_propertyoid FOREIGN KEY (oid) REFERENCES list_field (oid)
ALTER TABLE friend_property ADD CONSTRAINT frndpropertyfrndid FOREIGN KEY (friend_id) REFERENCES FRIENDDB (oid)
ALTER TABLE person_account ADD CONSTRAINT person_account_oid FOREIGN KEY (oid) REFERENCES account (oid)
ALTER TABLE person_account ADD CONSTRAINT prsnaccountprsonid FOREIGN KEY (person_id) REFERENCES person (oid)
ALTER TABLE organizational_address ADD CONSTRAINT rgnztionaladdressd FOREIGN KEY (oid) REFERENCES address (oid)
ALTER TABLE organizational_address ADD CONSTRAINT rgnztnlddrsrgnztnd FOREIGN KEY (organization_id) REFERENCES organization (oid)
ALTER TABLE email ADD CONSTRAINT FK_email_person_id FOREIGN KEY (person_id) REFERENCES person (oid)
ALTER TABLE email ADD CONSTRAINT FK_email_oid FOREIGN KEY (oid) REFERENCES list_field (oid)
ALTER TABLE membership ADD CONSTRAINT membershipgroup_id FOREIGN KEY (group_id) REFERENCES person_group (oid)
ALTER TABLE membership ADD CONSTRAINT membershippersonid FOREIGN KEY (person_id) REFERENCES person (oid)
ALTER TABLE application_datavalue ADD CONSTRAINT pplctndpplctndtmpd FOREIGN KEY (application_datamap_id) REFERENCES application_datamap (oid)
