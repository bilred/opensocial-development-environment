ALTER TABLE phone DROP CONSTRAINT FK_phone_oid
ALTER TABLE phone DROP CONSTRAINT FK_phone_person_id
ALTER TABLE template_params DROP CONSTRAINT tmpltparamsctvtyid
ALTER TABLE person DROP CONSTRAINT personbody_type_id
ALTER TABLE person DROP CONSTRAINT FK_person_name_id
ALTER TABLE person DROP CONSTRAINT person_address_id
ALTER TABLE person_group DROP CONSTRAINT prsongrouppersonid
ALTER TABLE group_property DROP CONSTRAINT gruppropertygrupid
ALTER TABLE group_property DROP CONSTRAINT group_property_oid
ALTER TABLE FRIENDDB DROP CONSTRAINT FRIENDDB_person_id
ALTER TABLE FRIENDDB DROP CONSTRAINT FRIENDDB_friend_id
ALTER TABLE url DROP CONSTRAINT FK_url_oid
ALTER TABLE url DROP CONSTRAINT FK_url_person_id
ALTER TABLE photo DROP CONSTRAINT FK_photo_person_id
ALTER TABLE photo DROP CONSTRAINT FK_photo_oid
ALTER TABLE person_properties DROP CONSTRAINT personpropertiesid
ALTER TABLE person_properties DROP CONSTRAINT prsnprpertiesprsnd
ALTER TABLE person_address DROP CONSTRAINT person_address_oid
ALTER TABLE person_address DROP CONSTRAINT prsnaddressprsonid
ALTER TABLE person_application DROP CONSTRAINT prsnpplctonpplctnd
ALTER TABLE person_application DROP CONSTRAINT prsnpplcationprsnd
ALTER TABLE application_datamap DROP CONSTRAINT pplctndtmappplctnd
ALTER TABLE activity_media DROP CONSTRAINT ctivitymediamdiaid
ALTER TABLE activity_media DROP CONSTRAINT ctvtymediactvityid
ALTER TABLE person_organization DROP CONSTRAINT prsnrgnzationprsnd
ALTER TABLE person_organization DROP CONSTRAINT prsonorganizationd
ALTER TABLE im DROP CONSTRAINT FK_im_person_id
ALTER TABLE im DROP CONSTRAINT FK_im_oid
ALTER TABLE friend_property DROP CONSTRAINT friend_propertyoid
ALTER TABLE friend_property DROP CONSTRAINT frndpropertyfrndid
ALTER TABLE application_property DROP CONSTRAINT pplctnprprtpplctnd
ALTER TABLE application_property DROP CONSTRAINT pplcationpropertyd
ALTER TABLE person_account DROP CONSTRAINT person_account_oid
ALTER TABLE person_account DROP CONSTRAINT prsnaccountprsonid
ALTER TABLE organizational_address DROP CONSTRAINT rgnztionaladdressd
ALTER TABLE organizational_address DROP CONSTRAINT rgnztnlddrsrgnztnd
ALTER TABLE email DROP CONSTRAINT FK_email_person_id
ALTER TABLE email DROP CONSTRAINT FK_email_oid
ALTER TABLE membership DROP CONSTRAINT membershipgroup_id
ALTER TABLE membership DROP CONSTRAINT membershippersonid
ALTER TABLE application_datavalue DROP CONSTRAINT pplctndpplctndtmpd
DROP TABLE phone
DROP TABLE template_params
DROP TABLE message
DROP TABLE address
DROP TABLE person
DROP TABLE media_item
DROP TABLE person_group
DROP TABLE group_property
DROP TABLE FRIENDDB
DROP TABLE url
DROP TABLE body_type
DROP TABLE photo
DROP TABLE name
DROP TABLE person_properties
DROP TABLE organization
DROP TABLE application
DROP TABLE list_field
DROP TABLE activity
DROP TABLE person_address
DROP TABLE person_application
DROP TABLE application_datamap
DROP TABLE account
DROP TABLE activity_media
DROP TABLE person_organization
DROP TABLE im
DROP TABLE friend_property
DROP TABLE application_property
DROP TABLE person_account
DROP TABLE organizational_address
DROP TABLE email
DROP TABLE membership
DROP TABLE application_datavalue
