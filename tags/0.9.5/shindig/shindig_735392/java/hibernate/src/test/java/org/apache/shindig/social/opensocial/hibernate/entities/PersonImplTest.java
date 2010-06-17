/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.shindig.social.opensocial.hibernate.entities;

import java.util.Date;
import java.util.List;

import org.apache.shindig.social.opensocial.model.Account;
import org.apache.shindig.social.opensocial.model.Address;
import org.apache.shindig.social.opensocial.model.Enum;
import org.apache.shindig.social.opensocial.model.ListField;
import org.apache.shindig.social.opensocial.model.Organization;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.model.Url;
import org.apache.shindig.social.opensocial.model.Enum.Drinker;
import org.apache.shindig.social.opensocial.model.Enum.LookingFor;
import org.apache.shindig.social.opensocial.model.Enum.NetworkPresence;
import org.apache.shindig.social.opensocial.model.Enum.Smoker;
import org.apache.shindig.social.opensocial.model.Person.Gender;
import org.hibernate.Query;
import org.hibernate.Transaction;
import org.junit.Test;
import static org.junit.Assert.*;

public class PersonImplTest extends AbstractEntityTest {
	
	@Test
	public void testQueryFindByPersonId() throws Exception {
		Transaction tx = session.beginTransaction();
		PersonImpl person = new PersonImpl();
		person.setId("id1");
		person.setAboutMe("aboutMe1");
		session.save(person);
		tx.commit();
		session.clear();
		//
		tx = session.beginTransaction();
		Query query = session.getNamedQuery(PersonImpl.FINDBY_PERSONID);
		query.setParameter(PersonImpl.PARAM_PERSONID, "id1");
		query.setFirstResult(0);
		query.setMaxResults(1);
		List<?> people = query.list();
		assertEquals("people.size", 1, people.size());
		assertEquals("people[0].aboutMe", "aboutMe1", ((Person)people.get(0)).getAboutMe());
		tx.commit();
	}

	@Test
	public void testProfileVideo() throws Exception {
		Transaction tx = session.beginTransaction();
		PersonImpl person = new PersonImpl();
		UrlImpl profileVideo = new UrlImpl();
		profileVideo.setLinkText("linkText1");
		person.setProfileVideo(profileVideo);
		session.save(person);
		long oid = person.getObjectId();
		tx.commit();
		session.clear();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		profileVideo = (UrlImpl)person.getProfileVideo();
		assertNotNull(profileVideo);
		assertEquals("linkText", "linkText1", profileVideo.getLinkText());
		tx.commit();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		profileVideo = (UrlImpl)person.getProfileVideo();
		profileVideo.setLinkText("linkText2");
		session.update(person);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		profileVideo = (UrlImpl)person.getProfileVideo();
		assertEquals("linkText", "linkText2", profileVideo.getLinkText());
		tx.commit();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		person.setProfileVideo(null);
		tx.commit();
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		profileVideo = (UrlImpl)person.getProfileVideo();
		assertNull(profileVideo);
		tx.commit();
	}

	@Test
	public void testProfileSong() throws Exception {
		Transaction tx = session.beginTransaction();
		PersonImpl person = new PersonImpl();
		UrlImpl profileSong = new UrlImpl();
		profileSong.setLinkText("linkText1");
		person.setProfileSong(profileSong);
		session.save(person);
		long oid = person.getObjectId();
		tx.commit();
		session.clear();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		profileSong = (UrlImpl)person.getProfileSong();
		assertNotNull(profileSong);
		assertEquals("linkText", "linkText1", profileSong.getLinkText());
		tx.commit();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		profileSong = (UrlImpl)person.getProfileSong();
		profileSong.setLinkText("linkText2");
		session.update(person);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		profileSong = (UrlImpl)person.getProfileSong();
		assertEquals("linkText", "linkText2", profileSong.getLinkText());
		tx.commit();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		person.setProfileSong(null);
		tx.commit();
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		profileSong = (UrlImpl)person.getProfileSong();
		assertNull(profileSong);
		tx.commit();
	}

	@Test
	public void testProfileUrl() throws Exception {
		Transaction tx = session.beginTransaction();
		PersonImpl person = new PersonImpl();
		person.setProfileUrl("profileUrl1");
		session.save(person);
		long oid = person.getObjectId();
		tx.commit();
		session.clear();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		assertEquals("profileUrl", "profileUrl1", person.getProfileUrl());
		List<Url> urls = person.getUrls();
		assertEquals("urls.length", 1, urls.size());
		assertEquals("urls[0].type", Person.PROFILE_URL_TYPE, urls.get(0).getType());
		assertEquals("urls[0].value", "profileUrl1", urls.get(0).getValue());
		tx.commit();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		person.setProfileUrl("profileUrl2");
		session.update(person);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		assertEquals("profileUrl", "profileUrl2", person.getProfileUrl());
		urls = person.getUrls();
		assertEquals("urls.length", 1, urls.size());
		assertEquals("urls[0].type", Person.PROFILE_URL_TYPE, urls.get(0).getType());
		assertEquals("urls[0].value", "profileUrl2", urls.get(0).getValue());
		tx.commit();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		person.setProfileUrl(null);
		session.update(person);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		assertNull("profileUrl", person.getProfileUrl());
		urls = person.getUrls();
		assertEquals("urls.length", 0, urls.size());
		tx.commit();
	}

	@Test
	public void testUrls() throws Exception {
		Transaction tx = session.beginTransaction();
		PersonImpl person = new PersonImpl();
		List<Url> urls = person.getUrls();
		UrlImpl url1 = new UrlImpl();
		url1.setType("type1");
//		url1.setPerson(person);
		urls.add(url1);
		UrlImpl url2 = new UrlImpl();
		url2.setType("type2");
//		url2.setPerson(person);
		urls.add(url2);
		session.save(person);
		long oid = person.getObjectId();
		tx.commit();
		session.clear();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		urls = person.getUrls();
		assertEquals("urls.size", 2, urls.size());
		assertEquals("urls[0].type", "type1", urls.get(0).getType());
		assertEquals("urls[1].type", "type2", urls.get(1).getType());
		tx.commit();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		urls = person.getUrls();
		urls.remove(0);
		UrlImpl url3 = new UrlImpl();
		url3.setType("type3");
//		url3.setPerson(person);
		urls.add(url3);
		session.update(person);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		urls = person.getUrls();
		assertEquals("urls.size", 2, urls.size());
		assertEquals("urls[0].type", "type2", urls.get(0).getType());
		assertEquals("urls[1].type", "type3", urls.get(1).getType());
		tx.commit();
	}

	@Test
	public void testThumbnailUrl() throws Exception {
		Transaction tx = session.beginTransaction();
		PersonImpl person = new PersonImpl();
		person.setThumbnailUrl("thumbnailUrl1");
		session.save(person);
		long oid = person.getObjectId();
		tx.commit();
		session.clear();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		assertEquals("thumbnailUrl", "thumbnailUrl1", person.getThumbnailUrl());
		List<ListField> photos = person.getPhotos();
		assertEquals("photos.length", 1, photos.size());
		assertEquals("photos[0].type", Person.THUMBNAIL_PHOTO_TYPE, photos.get(0).getType());
		assertEquals("photos[0].value", "thumbnailUrl1", photos.get(0).getValue());
		tx.commit();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		person.setThumbnailUrl("thumbnailUrl2");
		session.update(person);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		assertEquals("thumbnailUrl", "thumbnailUrl2", person.getThumbnailUrl());
		photos = person.getPhotos();
		assertEquals("photos.length", 1, photos.size());
		assertEquals("photos[0].type", Person.THUMBNAIL_PHOTO_TYPE, photos.get(0).getType());
		assertEquals("photos[0].value", "thumbnailUrl2", photos.get(0).getValue());
		tx.commit();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		person.setThumbnailUrl(null);
		session.update(person);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		assertNull("thumbnailUrl", person.getThumbnailUrl());
		photos = person.getPhotos();
		assertEquals("photos.length", 0, photos.size());
		tx.commit();
	}
	
	@Test
	public void testCurrentLocation() throws Exception {
		Transaction tx = session.beginTransaction();
		PersonImpl person = new PersonImpl();
		AddressImpl currentLocation = new AddressImpl();
		currentLocation.setCountry("country1");
		person.setCurrentLocation(currentLocation);
		session.save(person);
		long oid = person.getObjectId();
		tx.commit();
		session.clear();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		currentLocation = (AddressImpl)person.getCurrentLocation();
		assertNotNull(currentLocation);
		assertEquals("country", "country1", currentLocation.getCountry());
		tx.commit();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		currentLocation = (AddressImpl)person.getCurrentLocation();
		currentLocation.setCountry("country2");
		session.update(person);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		currentLocation = (AddressImpl)person.getCurrentLocation();
		assertEquals("country", "country2", currentLocation.getCountry());
		tx.commit();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		person.setCurrentLocation(null);
		tx.commit();
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		currentLocation = (AddressImpl)person.getCurrentLocation();
		assertNull(currentLocation);
		tx.commit();
	}
	
	@Test
	public void testAddresses() throws Exception {
		Transaction tx = session.beginTransaction();
		PersonImpl person = new PersonImpl();
		List<Address> addresses = person.getAddresses();
		AddressImpl address1 = new AddressImpl();
		address1.setType("type1");
//		address1.setPerson(person);
		addresses.add(address1);
		AddressImpl address2 = new AddressImpl();
		address2.setType("type2");
//		address2.setPerson(person);
		addresses.add(address2);
		session.save(person);
		long oid = person.getObjectId();
		tx.commit();
		session.clear();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		addresses = person.getAddresses();
		assertEquals("addresses.size", 2, addresses.size());
		assertEquals("addresses[0].type", "type1", addresses.get(0).getType());
		assertEquals("addresses[1].type", "type2", addresses.get(1).getType());
		tx.commit();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		addresses = person.getAddresses();
		addresses.remove(0);
		AddressImpl address3 = new AddressImpl();
		address3.setType("type3");
//		address3.setPerson(person);
		addresses.add(address3);
		session.update(person);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		addresses = person.getAddresses();
		assertEquals("addresses.size", 2, addresses.size());
		assertEquals("addresses[0].type", "type2", addresses.get(0).getType());
		assertEquals("addresses[1].type", "type3", addresses.get(1).getType());
		tx.commit();
	}

	@Test
	public void testOrganizations() throws Exception {
		Transaction tx = session.beginTransaction();
		PersonImpl person = new PersonImpl();
		List<Organization> organizations = person.getOrganizations();
		OrganizationImpl organization1 = new OrganizationImpl();
		organization1.setType("type1");
//		organization1.setPerson(person);
		organizations.add(organization1);
		OrganizationImpl organization2 = new OrganizationImpl();
		organization2.setType("type2");
//		organization2.setPerson(person);
		organizations.add(organization2);
		session.save(person);
		long oid = person.getObjectId();
		tx.commit();
		session.clear();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		organizations = person.getOrganizations();
		assertEquals("organizations.size", 2, organizations.size());
		assertEquals("organizations[0].type", "type1", organizations.get(0).getType());
		assertEquals("organizations[1].type", "type2", organizations.get(1).getType());
		tx.commit();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		organizations = person.getOrganizations();
		organizations.remove(0);
		OrganizationImpl organization3 = new OrganizationImpl();
		organization3.setType("type3");
//		organization3.setPerson(person);
		organizations.add(organization3);
		session.update(person);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		organizations = person.getOrganizations();
		assertEquals("organizations.size", 2, organizations.size());
		assertEquals("organizations[0].type", "type2", organizations.get(0).getType());
		assertEquals("organizations[1].type", "type3", organizations.get(1).getType());
		tx.commit();
	}

	@Test
	public void testPhotos() throws Exception {
		Transaction tx = session.beginTransaction();
		PersonImpl person = new PersonImpl();
		List<ListField> photos = person.getPhotos();
		PhotoImpl photo1 = new PhotoImpl();
		photo1.setType("type1");
//		photo1.setPerson(person);
		photos.add(photo1);
		PhotoImpl photo2 = new PhotoImpl();
		photo2.setType("type2");
//		photo2.setPerson(person);
		photos.add(photo2);
		session.save(person);
		long oid = person.getObjectId();
		tx.commit();
		session.clear();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		photos = person.getPhotos();
		assertEquals("photos.size", 2, photos.size());
		assertEquals("photos[0].type", "type1", photos.get(0).getType());
		assertEquals("photos[1].type", "type2", photos.get(1).getType());
		tx.commit();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		photos = person.getPhotos();
		photos.remove(0);
		PhotoImpl photo3 = new PhotoImpl();
		photo3.setType("type3");
//		photo3.setPerson(person);
		photos.add(photo3);
		session.update(person);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		photos = person.getPhotos();
		assertEquals("photos.size", 2, photos.size());
		assertEquals("photos[0].type", "type2", photos.get(0).getType());
		assertEquals("photos[1].type", "type3", photos.get(1).getType());
		tx.commit();
	}

	@Test
	public void testPhoneNumbers() throws Exception {
		Transaction tx = session.beginTransaction();
		PersonImpl person = new PersonImpl();
		List<ListField> phoneNumbers = person.getPhoneNumbers();
		PhoneNumberImpl phoneNumber1 = new PhoneNumberImpl();
		phoneNumber1.setType("type1");
//		phoneNumber1.setPerson(person);
		phoneNumbers.add(phoneNumber1);
		PhoneNumberImpl phoneNumber2 = new PhoneNumberImpl();
		phoneNumber2.setType("type2");
//		phoneNumber2.setPerson(person);
		phoneNumbers.add(phoneNumber2);
		session.save(person);
		long oid = person.getObjectId();
		tx.commit();
		session.clear();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		phoneNumbers = person.getPhoneNumbers();
		assertEquals("phoneNumbers.size", 2, phoneNumbers.size());
		assertEquals("phoneNumbers[0].type", "type1", phoneNumbers.get(0).getType());
		assertEquals("phoneNumbers[1].type", "type2", phoneNumbers.get(1).getType());
		tx.commit();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		phoneNumbers = person.getPhoneNumbers();
		phoneNumbers.remove(0);
		PhoneNumberImpl phoneNumber3 = new PhoneNumberImpl();
		phoneNumber3.setType("type3");
//		phoneNumber3.setPerson(person);
		phoneNumbers.add(phoneNumber3);
		session.update(person);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		phoneNumbers = person.getPhoneNumbers();
		assertEquals("phoneNumbers.size", 2, phoneNumbers.size());
		assertEquals("phoneNumbers[0].type", "type2", phoneNumbers.get(0).getType());
		assertEquals("phoneNumbers[1].type", "type3", phoneNumbers.get(1).getType());
		tx.commit();
	}

	@Test
	public void testIms() throws Exception {
		Transaction tx = session.beginTransaction();
		PersonImpl person = new PersonImpl();
		List<ListField> ims = person.getIms();
		ImImpl im1 = new ImImpl();
		im1.setType("type1");
//		im1.setPerson(person);
		ims.add(im1);
		ImImpl im2 = new ImImpl();
		im2.setType("type2");
//		im2.setPerson(person);
		ims.add(im2);
		session.save(person);
		long oid = person.getObjectId();
		tx.commit();
		session.clear();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		ims = person.getIms();
		assertEquals("ims.size", 2, ims.size());
		assertEquals("ims[0].type", "type1", ims.get(0).getType());
		assertEquals("ims[1].type", "type2", ims.get(1).getType());
		tx.commit();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		ims = person.getIms();
		ims.remove(0);
		ImImpl im3 = new ImImpl();
		im3.setType("type3");
//		im3.setPerson(person);
		ims.add(im3);
		session.update(person);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		ims = person.getIms();
		assertEquals("ims.size", 2, ims.size());
		assertEquals("ims[0].type", "type2", ims.get(0).getType());
		assertEquals("ims[1].type", "type3", ims.get(1).getType());
		tx.commit();
	}

	@Test
	public void testEmails() throws Exception {
		Transaction tx = session.beginTransaction();
		PersonImpl person = new PersonImpl();
		List<ListField> emails = person.getEmails();
		EmailImpl email1 = new EmailImpl();
		email1.setType("type1");
//		email1.setPerson(person);
		emails.add(email1);
		EmailImpl email2 = new EmailImpl();
		email2.setType("type2");
//		email2.setPerson(person);
		emails.add(email2);
		session.save(person);
		long oid = person.getObjectId();
		tx.commit();
		session.clear();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		emails = person.getEmails();
		assertEquals("emails.size", 2, emails.size());
		assertEquals("emails[0].type", "type1", emails.get(0).getType());
		assertEquals("emails[1].type", "type2", emails.get(1).getType());
		tx.commit();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		emails = person.getEmails();
		emails.remove(0);
		EmailImpl email3 = new EmailImpl();
		email3.setType("type3");
//		email3.setPerson(person);
		emails.add(email3);
		session.update(person);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		emails = person.getEmails();
		assertEquals("emails.size", 2, emails.size());
		assertEquals("emails[0].type", "type2", emails.get(0).getType());
		assertEquals("emails[1].type", "type3", emails.get(1).getType());
		tx.commit();
	}

	@Test
	public void testTvShows() throws Exception {
		Transaction tx = session.beginTransaction();
		PersonImpl person = new PersonImpl();
		List<String> tvShows = person.getTvShows();
		tvShows.add("tvShow1");
		session.save(person);
		long oid = person.getObjectId();
		tx.commit();
		session.clear();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		tvShows = person.getTvShows();
		assertEquals("tvShows.size", 1, tvShows.size());
		assertEquals("tvShow[0]", "tvShow1", tvShows.get(0));
		tx.commit();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		tvShows = person.getTvShows();
		tvShows.remove(0);
		tvShows.add("tvShow2");
		session.update(person);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		tvShows = person.getTvShows();
		assertEquals("tvShows.size", 1, tvShows.size());
		assertEquals("tvShow[0]", "tvShow2", tvShows.get(0));
		tx.commit();
	}

	@Test
	public void testTurnOns() throws Exception {
		Transaction tx = session.beginTransaction();
		PersonImpl person = new PersonImpl();
		List<String> turnOns = person.getTurnOns();
		turnOns.add("turnOn1");
		session.save(person);
		long oid = person.getObjectId();
		tx.commit();
		session.clear();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		turnOns = person.getTurnOns();
		assertEquals("turnOns.size", 1, turnOns.size());
		assertEquals("turnOn[0]", "turnOn1", turnOns.get(0));
		tx.commit();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		turnOns = person.getTurnOns();
		turnOns.remove(0);
		turnOns.add("turnOn2");
		session.update(person);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		turnOns = person.getTurnOns();
		assertEquals("turnOns.size", 1, turnOns.size());
		assertEquals("turnOn[0]", "turnOn2", turnOns.get(0));
		tx.commit();
	}

	@Test
	public void testTurnOffs() throws Exception {
		Transaction tx = session.beginTransaction();
		PersonImpl person = new PersonImpl();
		List<String> turnOffs = person.getTurnOffs();
		turnOffs.add("turnOff1");
		session.save(person);
		long oid = person.getObjectId();
		tx.commit();
		session.clear();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		turnOffs = person.getTurnOffs();
		assertEquals("turnOffs.size", 1, turnOffs.size());
		assertEquals("turnOff[0]", "turnOff1", turnOffs.get(0));
		tx.commit();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		turnOffs = person.getTurnOffs();
		turnOffs.remove(0);
		turnOffs.add("turnOff2");
		session.update(person);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		turnOffs = person.getTurnOffs();
		assertEquals("turnOffs.size", 1, turnOffs.size());
		assertEquals("turnOff[0]", "turnOff2", turnOffs.get(0));
		tx.commit();
	}

	@Test
	public void testTags() throws Exception {
		Transaction tx = session.beginTransaction();
		PersonImpl person = new PersonImpl();
		List<String> tags = person.getTags();
		tags.add("tag1");
		session.save(person);
		long oid = person.getObjectId();
		tx.commit();
		session.clear();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		tags = person.getTags();
		assertEquals("tags.size", 1, tags.size());
		assertEquals("tag[0]", "tag1", tags.get(0));
		tx.commit();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		tags = person.getTags();
		tags.remove(0);
		tags.add("tag2");
		session.update(person);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		tags = person.getTags();
		assertEquals("tags.size", 1, tags.size());
		assertEquals("tag[0]", "tag2", tags.get(0));
		tx.commit();
	}

	@Test
	public void testSports() throws Exception {
		Transaction tx = session.beginTransaction();
		PersonImpl person = new PersonImpl();
		List<String> sports = person.getSports();
		sports.add("sport1");
		session.save(person);
		long oid = person.getObjectId();
		tx.commit();
		session.clear();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		sports = person.getSports();
		assertEquals("sports.size", 1, sports.size());
		assertEquals("sport[0]", "sport1", sports.get(0));
		tx.commit();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		sports = person.getSports();
		sports.remove(0);
		sports.add("sport2");
		session.update(person);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		sports = person.getSports();
		assertEquals("sports.size", 1, sports.size());
		assertEquals("sport[0]", "sport2", sports.get(0));
		tx.commit();
	}

	@Test
	public void testQuotes() throws Exception {
		Transaction tx = session.beginTransaction();
		PersonImpl person = new PersonImpl();
		List<String> quotes = person.getQuotes();
		quotes.add("quote1");
		session.save(person);
		long oid = person.getObjectId();
		tx.commit();
		session.clear();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		quotes = person.getQuotes();
		assertEquals("quotes.size", 1, quotes.size());
		assertEquals("quote[0]", "quote1", quotes.get(0));
		tx.commit();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		quotes = person.getQuotes();
		quotes.remove(0);
		quotes.add("quote2");
		session.update(person);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		quotes = person.getQuotes();
		assertEquals("quotes.size", 1, quotes.size());
		assertEquals("quote[0]", "quote2", quotes.get(0));
		tx.commit();
	}

	@Test
	public void testMusic() throws Exception {
		Transaction tx = session.beginTransaction();
		PersonImpl person = new PersonImpl();
		List<String> musics = person.getMusic();
		musics.add("music1");
		session.save(person);
		long oid = person.getObjectId();
		tx.commit();
		session.clear();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		musics = person.getMusic();
		assertEquals("musics.size", 1, musics.size());
		assertEquals("music[0]", "music1", musics.get(0));
		tx.commit();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		musics = person.getMusic();
		musics.remove(0);
		musics.add("music2");
		session.update(person);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		musics = person.getMusic();
		assertEquals("musics.size", 1, musics.size());
		assertEquals("music[0]", "music2", musics.get(0));
		tx.commit();
	}

	@Test
	public void testMovies() throws Exception {
		Transaction tx = session.beginTransaction();
		PersonImpl person = new PersonImpl();
		List<String> movies = person.getMovies();
		movies.add("movie1");
		session.save(person);
		long oid = person.getObjectId();
		tx.commit();
		session.clear();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		movies = person.getMovies();
		assertEquals("movies.size", 1, movies.size());
		assertEquals("movie[0]", "movie1", movies.get(0));
		tx.commit();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		movies = person.getMovies();
		movies.remove(0);
		movies.add("movie2");
		session.update(person);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		movies = person.getMovies();
		assertEquals("movies.size", 1, movies.size());
		assertEquals("movie[0]", "movie2", movies.get(0));
		tx.commit();
	}

	@Test
	public void testLanguagesSpoken() throws Exception {
		Transaction tx = session.beginTransaction();
		PersonImpl person = new PersonImpl();
		List<String> languagesSpoken = person.getLanguagesSpoken();
		languagesSpoken.add("languageSpoken1");
		session.save(person);
		long oid = person.getObjectId();
		tx.commit();
		session.clear();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		languagesSpoken = person.getLanguagesSpoken();
		assertEquals("languagesSpoken.size", 1, languagesSpoken.size());
		assertEquals("languageSpoken[0]", "languageSpoken1", languagesSpoken.get(0));
		tx.commit();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		languagesSpoken = person.getLanguagesSpoken();
		languagesSpoken.remove(0);
		languagesSpoken.add("languageSpoken2");
		session.update(person);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		languagesSpoken = person.getLanguagesSpoken();
		assertEquals("languagesSpoken.size", 1, languagesSpoken.size());
		assertEquals("languageSpoken[0]", "languageSpoken2", languagesSpoken.get(0));
		tx.commit();
	}

	@Test
	public void testInterests() throws Exception {
		Transaction tx = session.beginTransaction();
		PersonImpl person = new PersonImpl();
		List<String> interests = person.getInterests();
		interests.add("interest1");
		session.save(person);
		long oid = person.getObjectId();
		tx.commit();
		session.clear();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		interests = person.getInterests();
		assertEquals("interests.size", 1, interests.size());
		assertEquals("interest[0]", "interest1", interests.get(0));
		tx.commit();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		interests = person.getInterests();
		interests.remove(0);
		interests.add("interest2");
		session.update(person);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		interests = person.getInterests();
		assertEquals("interests.size", 1, interests.size());
		assertEquals("interest[0]", "interest2", interests.get(0));
		tx.commit();
	}

	@Test
	public void testHeroes() throws Exception {
		Transaction tx = session.beginTransaction();
		PersonImpl person = new PersonImpl();
		List<String> heroes = person.getHeroes();
		heroes.add("hero1");
		session.save(person);
		long oid = person.getObjectId();
		tx.commit();
		session.clear();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		heroes = person.getHeroes();
		assertEquals("heroes.size", 1, heroes.size());
		assertEquals("hero[0]", "hero1", heroes.get(0));
		tx.commit();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		heroes = person.getHeroes();
		heroes.remove(0);
		heroes.add("hero2");
		session.update(person);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		heroes = person.getHeroes();
		assertEquals("heroes.size", 1, heroes.size());
		assertEquals("hero[0]", "hero2", heroes.get(0));
		tx.commit();
	}

	@Test
	public void testFood() throws Exception {
		Transaction tx = session.beginTransaction();
		PersonImpl person = new PersonImpl();
		List<String> foods = person.getFood();
		foods.add("food1");
		session.save(person);
		long oid = person.getObjectId();
		tx.commit();
		session.clear();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		foods = person.getFood();
		assertEquals("foods.size", 1, foods.size());
		assertEquals("food[0]", "food1", foods.get(0));
		tx.commit();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		foods = person.getFood();
		foods.remove(0);
		foods.add("food2");
		session.update(person);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		foods = person.getFood();
		assertEquals("foods.size", 1, foods.size());
		assertEquals("food[0]", "food2", foods.get(0));
		tx.commit();
	}

	@Test
	public void testCars() throws Exception {
		Transaction tx = session.beginTransaction();
		PersonImpl person = new PersonImpl();
		List<String> cars = person.getCars();
		cars.add("car1");
		session.save(person);
		long oid = person.getObjectId();
		tx.commit();
		session.clear();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		cars = person.getCars();
		assertEquals("cars.size", 1, cars.size());
		assertEquals("car[0]", "car1", cars.get(0));
		tx.commit();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		cars = person.getCars();
		cars.remove(0);
		cars.add("car2");
		session.update(person);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		cars = person.getCars();
		assertEquals("cars.size", 1, cars.size());
		assertEquals("car[0]", "car2", cars.get(0));
		tx.commit();
	}

	@Test
	public void testBooks() throws Exception {
		Transaction tx = session.beginTransaction();
		PersonImpl person = new PersonImpl();
		List<String> books = person.getBooks();
		books.add("book1");
		session.save(person);
		long oid = person.getObjectId();
		tx.commit();
		session.clear();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		books = person.getBooks();
		assertEquals("books.size", 1, books.size());
		assertEquals("book[0]", "book1", books.get(0));
		tx.commit();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		books = person.getBooks();
		books.remove(0);
		books.add("book2");
		session.update(person);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		books = person.getBooks();
		assertEquals("books.size", 1, books.size());
		assertEquals("book[0]", "book2", books.get(0));
		tx.commit();
	}
	
	@Test
	public void testActivities() throws Exception {
		Transaction tx = session.beginTransaction();
		PersonImpl person = new PersonImpl();
		List<String> activities = person.getActivities();
		activities.add("activity1");
		session.save(person);
		long oid = person.getObjectId();
		tx.commit();
		session.clear();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		activities = person.getActivities();
		assertEquals("activities.size", 1, activities.size());
		assertEquals("activity[0]", "activity1", activities.get(0));
		tx.commit();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		activities = person.getActivities();
		activities.remove(0);
		activities.add("activity2");
		session.update(person);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		activities = person.getActivities();
		assertEquals("activities.size", 1, activities.size());
		assertEquals("activity[0]", "activity2", activities.get(0));
		tx.commit();
	}

	@Test
	public void testAccounts() throws Exception {
		Transaction tx = session.beginTransaction();
		PersonImpl person = new PersonImpl();
		person.setAboutMe("aboutMe1");
		List<Account> accounts = person.getAccounts();
		AccountImpl account1 = new AccountImpl();
		account1.setDomain("domain1");
//		account1.setPerson(person);
		accounts.add(account1);
		session.save(person);
		long oid = person.getObjectId();
		tx.commit();
		session.clear();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		accounts = person.getAccounts();
		assertEquals("accounts.size", 1, accounts.size());
		account1 = (AccountImpl)accounts.get(0);
		assertEquals("accounts[0].domain", "domain1", account1.getDomain());
		tx.commit();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		accounts = person.getAccounts();
		account1 = (AccountImpl)accounts.get(0);
		account1.setDomain("domain2");
		AccountImpl account2 = new AccountImpl();
		account2.setDomain("domain3");
//		account2.setPerson(person);
		accounts.add(account2);
		session.update(person);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		accounts = person.getAccounts();
		assertEquals("accounts.size", 2, accounts.size());
		account1 = (AccountImpl)accounts.get(0);
		assertEquals("accounts[0].domain", "domain2", account1.getDomain());
		account2 = (AccountImpl)accounts.get(1);
		assertEquals("account[1].domain", "domain3", account2.getDomain());
		tx.commit();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		accounts = person.getAccounts();
		account1 = (AccountImpl)accounts.get(0);
		accounts.remove(account1);
		session.update(person);
		tx.commit();
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		accounts = person.getAccounts();
		assertEquals("accounts.size", 1, accounts.size());
		account1 = (AccountImpl)accounts.get(0);
		assertEquals("accounts[0].domain", "domain3", account1.getDomain());
		tx.commit();
	}

	@Test
	public void testLookingFor() throws Exception {
		Transaction tx = session.beginTransaction();
		PersonImpl person = new PersonImpl();
		person.setAboutMe("aboutMe1");
		List<Enum<LookingFor>> lookingFor = person.getLookingFor();
		LookingForImpl lookingFor1 = new LookingForImpl();
		lookingFor1.setDisplayValue("displayValue1");
		lookingFor1.setValue(LookingFor.ACTIVITY_PARTNERS);
//		lookingFor1.setPerson(person);
		lookingFor.add(lookingFor1);
		session.save(person);
		long oid = person.getObjectId();
		tx.commit();
		session.clear();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		lookingFor = person.getLookingFor();
		assertEquals("lookingFor.size", 1, lookingFor.size());
		lookingFor1 = (LookingForImpl)lookingFor.get(0);
		assertEquals("lookingFor[0].value", LookingFor.ACTIVITY_PARTNERS, lookingFor1.getValue());
		assertEquals("lookingFor[0].displayValue", "displayValue1", lookingFor1.getDisplayValue());
		tx.commit();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		lookingFor = person.getLookingFor();
		lookingFor1 = (LookingForImpl)lookingFor.get(0);
		lookingFor1.setValue(LookingFor.DATING);
		lookingFor1.setDisplayValue("displayValue2");
		LookingForImpl lookingFor2 = new LookingForImpl();
		lookingFor2.setDisplayValue("displayValue3");
		lookingFor2.setValue(LookingFor.FRIENDS);
//		lookingFor2.setPerson(person);
		lookingFor.add(lookingFor2);
		session.update(person);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		lookingFor = person.getLookingFor();
		assertEquals("lookingFor.size", 2, lookingFor.size());
		lookingFor1 = (LookingForImpl)lookingFor.get(0);
		assertEquals("lookingFor[0].value", LookingFor.DATING, lookingFor1.getValue());
		assertEquals("lookingFor[0].displayValue", "displayValue2", lookingFor1.getDisplayValue());
		lookingFor2 = (LookingForImpl)lookingFor.get(1);
		assertEquals("lookingFor[1].value", LookingFor.FRIENDS, lookingFor2.getValue());
		assertEquals("lookingFor[1].displayValue", "displayValue3", lookingFor2.getDisplayValue());
		tx.commit();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		lookingFor = person.getLookingFor();
		lookingFor1 = (LookingForImpl)lookingFor.get(0);
		lookingFor.remove(lookingFor1);
		session.update(person);
		tx.commit();
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		lookingFor = person.getLookingFor();
		assertEquals("lookingFor.size", 1, lookingFor.size());
		lookingFor1 = (LookingForImpl)lookingFor.get(0);
		assertEquals("lookingFor[0].value", LookingFor.FRIENDS, lookingFor1.getValue());
		assertEquals("lookingFor[0].displayValue", "displayValue3", lookingFor1.getDisplayValue());
		tx.commit();
	}

	@Test
	public void testSimple() throws Exception {
		Transaction tx = session.beginTransaction();
		PersonImpl person = new PersonImpl();
		person.setAboutMe("aboutMe1");
		person.setAge(12);
		Date birthday1 = new Date();
		person.setBirthday(birthday1);
		person.setDrinker(new DrinkerImpl(Drinker.HEAVILY, "drinker1"));
		person.setChildren("children1");
		person.setDisplayName("displayName1");
		person.setEthnicity("ethnicity1");
		person.setFashion("fashion1");
		person.setHappiestWhen("happiestWhen1");
		person.setHumor("humor1");
		person.setId("id1");
		person.setJobInterests("jobInterests1");
		person.setLivingArrangement("livingArrangement1");
		person.setNickname("nickname1");
		person.setPets("pets1");
		person.setPoliticalViews("politicalViews1");
		person.setRelationshipStatus("relationshipStatus1");
		person.setReligion("religion1");
		person.setRomance("romance1");
		person.setScaredOf("scaredOf1");
		person.setSexualOrientation("sexualOrientation1");
		person.setStatus("status1");
		Date updated1 = new Date();
		person.setUpdated(updated1);
		person.setUtcOffset(123L);
		person.setNetworkPresence(new NetworkPresenceImpl(NetworkPresence.AWAY, "displayValue1"));
		person.setSmoker(new SmokerImpl(Smoker.HEAVILY, "displayValue1"));
		person.setGender(Gender.male);
		session.save(person);
		long oid = person.getObjectId();
		tx.commit();
		session.clear();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		assertEquals("aboutMe", "aboutMe1", person.getAboutMe());
		assertEquals("age", new Integer(12), person.getAge());
		assertDate("birthday", birthday1, person.getBirthday());
		Enum<Drinker> drinker = person.getDrinker();
		assertEquals("drinker.value", Drinker.HEAVILY, drinker.getValue());
		assertEquals("drinker.displayValue", "drinker1", drinker.getDisplayValue());
		assertEquals("children", "children1", person.getChildren());
		assertEquals("displayName", "displayName1", person.getDisplayName());
		assertEquals("ethnicity", "ethnicity1", person.getEthnicity());
		assertEquals("fashion", "fashion1", person.getFashion());
		assertEquals("happiestWhen", "happiestWhen1", person.getHappiestWhen());
		assertEquals("humor", "humor1", person.getHumor());
		assertEquals("id", "id1", person.getId());
		assertEquals("jobInterests", "jobInterests1", person.getJobInterests());
		assertEquals("livingArrangement", "livingArrangement1", person.getLivingArrangement());
		assertEquals("nickname", "nickname1", person.getNickname());
		assertEquals("pets", "pets1", person.getPets());
		assertEquals("politicalViews", "politicalViews1", person.getPoliticalViews());
		assertEquals("relationshipStatus", "relationshipStatus1", person.getRelationshipStatus());
		assertEquals("religion", "religion1", person.getReligion());
		assertEquals("romance", "romance1", person.getRomance());
		assertEquals("scaredOf", "scaredOf1", person.getScaredOf());
		assertEquals("sexualOrientation", "sexualOrientation1", person.getSexualOrientation());
		assertEquals("status", "status1", person.getStatus());
		assertTimestamp("updated", updated1, person.getUpdated());
		assertEquals("utcOffset", new Long(123L), person.getUtcOffset());
		Enum<NetworkPresence> networkPresence = person.getNetworkPresence();
		assertEquals("networkPresence.value", NetworkPresence.AWAY, networkPresence.getValue());
		assertEquals("networkPresence.displayValue", "displayValue1", networkPresence.getDisplayValue());
		Enum<Smoker> smoker = person.getSmoker();
		assertEquals("smoker.value", Smoker.HEAVILY, smoker.getValue());
		assertEquals("smoker.displayValue", "displayValue1", smoker.getDisplayValue());
		assertEquals("gender", Gender.male, person.getGender());
		tx.commit();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		person.setAboutMe("aboutMe2");
		person.setAge(34);
		Date birthday2 = new Date();
		person.setBirthday(birthday2);
		person.setDrinker(new DrinkerImpl(Drinker.NO, "drinker2"));
		person.setChildren("children2");
		person.setDisplayName("displayName2");
		person.setEthnicity("ethnicity2");
		person.setFashion("fashion2");
		person.setHappiestWhen("happiestWhen2");
		person.setHumor("humor2");
		person.setId("id2");
		person.setJobInterests("jobInterests2");
		person.setLivingArrangement("livingArrangement2");
		person.setNickname("nickname2");
		person.setPets("pets2");
		person.setPoliticalViews("politicalViews2");
		person.setRelationshipStatus("relationshipStatus2");
		person.setReligion("religion2");
		person.setRomance("romance2");
		person.setScaredOf("scaredOf2");
		person.setSexualOrientation("sexualOrientation2");
		person.setStatus("status2");
		Date updated2 = new Date();
		person.setUpdated(updated2);
		person.setUtcOffset(456L);
		person.setNetworkPresence(new NetworkPresenceImpl(NetworkPresence.CHAT, "displayValue2"));
		person.setSmoker(new SmokerImpl(Smoker.NO, "displayValue2"));
		person.setGender(Gender.female);
		session.update(person);
		tx.commit();
		session.clear();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		assertEquals("aboutMe", "aboutMe2", person.getAboutMe());
		assertEquals("age", new Integer(34), person.getAge());
		assertDate("birthday", birthday2, person.getBirthday());
		drinker = person.getDrinker();
		assertEquals("drinker.value", Drinker.NO, drinker.getValue());
		assertEquals("drinker.displayValue", "drinker2", drinker.getDisplayValue());
		assertEquals("children", "children2", person.getChildren());
		assertEquals("displayName", "displayName2", person.getDisplayName());
		assertEquals("ethnicity", "ethnicity2", person.getEthnicity());
		assertEquals("fashion", "fashion2", person.getFashion());
		assertEquals("happiestWhen", "happiestWhen2", person.getHappiestWhen());
		assertEquals("humor", "humor2", person.getHumor());
		assertEquals("id", "id2", person.getId());
		assertEquals("jobInterests", "jobInterests2", person.getJobInterests());
		assertEquals("livingArrangement", "livingArrangement2", person.getLivingArrangement());
		assertEquals("nickname", "nickname2", person.getNickname());
		assertEquals("pets", "pets2", person.getPets());
		assertEquals("politicalViews", "politicalViews2", person.getPoliticalViews());
		assertEquals("relationshipStatus", "relationshipStatus2", person.getRelationshipStatus());
		assertEquals("religion", "religion2", person.getReligion());
		assertEquals("romance", "romance2", person.getRomance());
		assertEquals("scaredOf", "scaredOf2", person.getScaredOf());
		assertEquals("sexualOrientation", "sexualOrientation2", person.getSexualOrientation());
		assertEquals("status", "status2", person.getStatus());
		assertTimestamp("updated", updated2, person.getUpdated());
		assertEquals("utcOffset", new Long(456L), person.getUtcOffset());
		networkPresence = person.getNetworkPresence();
		assertEquals("networkPresence.value", NetworkPresence.CHAT, networkPresence.getValue());
		assertEquals("networkPresence.displayValue", "displayValue2", networkPresence.getDisplayValue());
		smoker = person.getSmoker();
		assertEquals("smoker.value", Smoker.NO, smoker.getValue());
		assertEquals("smoker.displayValue", "displayValue2", smoker.getDisplayValue());
		assertEquals("gender", Gender.female, person.getGender());
		tx.commit();
		//
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		session.delete(person);
		tx.commit();
		tx = session.beginTransaction();
		person = (PersonImpl)session.get(PersonImpl.class, oid);
		assertNull(person);
		tx.commit();
	}

}
