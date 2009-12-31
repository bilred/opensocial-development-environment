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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.shindig.social.opensocial.model.Account;
import org.apache.shindig.social.opensocial.model.Address;
import org.apache.shindig.social.opensocial.model.BodyType;
import org.apache.shindig.social.opensocial.model.Enum;
import org.apache.shindig.social.opensocial.model.ListField;
import org.apache.shindig.social.opensocial.model.Name;
import org.apache.shindig.social.opensocial.model.Organization;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.model.Url;
import org.apache.shindig.social.opensocial.model.Enum.Drinker;
import org.apache.shindig.social.opensocial.model.Enum.LookingFor;
import org.apache.shindig.social.opensocial.model.Enum.NetworkPresence;
import org.apache.shindig.social.opensocial.model.Enum.Smoker;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.IndexColumn;

@Entity
@Table(name="person")
@NamedQueries(value={
		@NamedQuery(name=PersonImpl.FINDBY_PERSONID,
				query="select p from PersonImpl p where p.id = :id")
})
public class PersonImpl implements Person {
	
//--- queries
	public static final String FINDBY_PERSONID = "q.person.findbypersonid";
	
	public static final String PARAM_PERSONID = "id";

//--- fields
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="oid")
	protected long objectId;
	
	@Version
	@Column(name="version")
	protected long version;
	
	@Basic
	@Column(name="about_me", length=255)
	protected String aboutMe;
	
	@Basic
	@Column(name="age")
	protected Integer age;
	
	@Basic
	@Column(name="birthday")
	@Temporal(TemporalType.DATE)
	protected Date birthday;

	@OneToOne(targetEntity=DrinkerImpl.class, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	protected Enum<Drinker> drinker;
	
	@OneToOne(targetEntity=BodyTypeImpl.class, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	protected BodyType bodyType;
	
	@Basic
	@Column(name="children", length=255)
	protected String children;
	
	@Basic
	@Column(name="display_name", length=255)
	protected String displayName;

	@Basic
	@Column(name="ethnicity", length=255)
	protected String ethnicity;

	@Basic
	@Column(name="fashion", length=255)
	protected String fashion;

	@Basic
	@Column(name="happest_when", length=255)
	protected String happiestWhen;

	@Basic
	@Column(name="humor", length=255)
	protected String humor;

	@Basic
	@Column(name="person_id", length=255)
	protected String id;

	@Basic
	@Column(name="job_interests", length=255)
	protected String jobInterests;

	@Basic
	@Column(name="living_arrangement", length=255)
	protected String livingArrangement;

	@Basic
	@Column(name="pets", length=255)
	protected String pets;

	@Basic
	@Column(name="nickname", length=255)
	protected String nickname;

	@Basic
	@Column(name="political_views", length=255)
	protected String politicalViews;

	@Basic
	@Column(name="relationship_status")
	protected String relationshipStatus;

	@Basic
	@Column(name="religion")
	protected String religion;

	@Basic
	@Column(name="rommance")
	protected String romance;

	@Basic
	@Column(name="scared_of")
	protected String scaredOf;

	@Basic
	@Column(name="sexual_orientation")
	protected String sexualOrientation;

	@Basic
	@Column(name="status")
	protected String status;

	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="upated")
	protected Date updated;

	@Basic
	@Column(name="utc_offset")
	protected Long utcOffset;
	
	@OneToMany(targetEntity=LookingForImpl.class, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	@OrderBy("objectId")
	protected List<Enum<LookingFor>> lookingFor;

	@OneToOne(targetEntity=NetworkPresenceImpl.class, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	protected Enum<NetworkPresence> networkPresence;

	@OneToOne(targetEntity=SmokerImpl.class, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	protected Enum<Smoker> smoker;
	
	@OneToMany(targetEntity=AccountImpl.class, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	@OrderBy("objectId")
	protected List<Account> accounts;

	@CollectionOfElements(targetElement=String.class)
	@JoinTable(name="person_activities", joinColumns=@JoinColumn(name="oid"))
	@IndexColumn(name="person_activity_id", base=1)
	@Column(name="activity", nullable=false)
	@OrderBy("person_activity_id")
	protected List<String> activities;

	@CollectionOfElements(targetElement=String.class)
	@JoinTable(name="person_books", joinColumns=@JoinColumn(name="oid"))
	@IndexColumn(name="person_books_id", base=1)
	@Column(name="book", nullable=false)
	@OrderBy("person_books_id")
	protected List<String> books;

	@CollectionOfElements(targetElement=String.class)
	@JoinTable(name="person_cars", joinColumns=@JoinColumn(name="oid"))
	@IndexColumn(name="person_cars_id", base=1)
	@Column(name="car", nullable=false)
	@OrderBy("person_cars_id")
	protected List<String> cars;

	@CollectionOfElements(targetElement=String.class)
	@JoinTable(name="person_food", joinColumns=@JoinColumn(name="oid"))
	@IndexColumn(name="person_food_id", base=1)
	@Column(name="food", nullable=false)
	@OrderBy("person_food_id")
	protected List<String> food;

	@CollectionOfElements(targetElement=String.class)
	@JoinTable(name="person_heroes", joinColumns=@JoinColumn(name="oid"))
	@IndexColumn(name="person_heroes_id", base=1)
	@Column(name="hero", nullable=false)
	@OrderBy("person_heroes_id")
	protected List<String> heroes;

	@CollectionOfElements(targetElement=String.class)
	@JoinTable(name="person_interests", joinColumns=@JoinColumn(name="oid"))
	@IndexColumn(name="person_interests_id", base=1)
	@Column(name="interest", nullable=false)
	@OrderBy("person_interests_id")
	protected List<String> interests;

	@CollectionOfElements(targetElement=String.class)
	@JoinTable(name="person_language_spoken", joinColumns=@JoinColumn(name="oid"))
	@IndexColumn(name="person_language_spoken_id", base=1)
	@Column(name="language_spoken", nullable=false)
	@OrderBy("person_language_spoken_id")
	protected List<String> languagesSpoken;

	@CollectionOfElements(targetElement=String.class)
	@JoinTable(name="person_movies", joinColumns=@JoinColumn(name="oid"))
	@IndexColumn(name="person_movies_id", base=1)
	@Column(name="movie", nullable=false)
	@OrderBy("person_movies_id")
	protected List<String> movies;

	@CollectionOfElements(targetElement=String.class)
	@JoinTable(name="person_music", joinColumns=@JoinColumn(name="oid"))
	@IndexColumn(name="person_music_id", base=1)
	@Column(name="music", nullable=false)
	@OrderBy("person_music_id")
	protected List<String> music;

	@CollectionOfElements(targetElement=String.class)
	@JoinTable(name="person_quotes", joinColumns=@JoinColumn(name="oid"))
	@IndexColumn(name="person_quotes_id", base=1)
	@Column(name="quote", nullable=false)
	@OrderBy("person_quotes_id")
	protected List<String> quotes;

	@CollectionOfElements(targetElement=String.class)
	@JoinTable(name="person_sports", joinColumns=@JoinColumn(name="oid"))
	@IndexColumn(name="person_sports_id", base=1)
	@Column(name="sport", nullable=false)
	@OrderBy("person_sports_id")
	protected List<String> sports;

	@CollectionOfElements(targetElement=String.class)
	@JoinTable(name="person_tags", joinColumns=@JoinColumn(name="oid"))
	@IndexColumn(name="person_tags_id", base=1)
	@Column(name="tag", nullable=false)
	@OrderBy("person_tags_id")
	protected List<String> tags;

	@CollectionOfElements(targetElement=String.class)
	@JoinTable(name="person_turn_offs", joinColumns=@JoinColumn(name="oid"))
	@IndexColumn(name="person_turn_offs_id", base=1)
	@Column(name="turn_off", nullable=false)
	@OrderBy("person_turn_offs_id")
	protected List<String> turnOffs;

	@CollectionOfElements(targetElement=String.class)
	@JoinTable(name="person_turn_ons", joinColumns=@JoinColumn(name="oid"))
	@IndexColumn(name="person_turn_ons_id", base=1)
	@Column(name="turn_on", nullable=false)
	@OrderBy("person_turn_ons_id")
	protected List<String> turnOns;

	@CollectionOfElements(targetElement=String.class)
	@JoinTable(name="person_tv_shows", joinColumns=@JoinColumn(name="oid"))
	@IndexColumn(name="person_tv_shows_id", base=1)
	@Column(name="tv_show", nullable=false)
	@OrderBy("person_tv_shows_id")
	protected List<String> tvShows;

	@OneToMany(targetEntity=EmailImpl.class, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	@OrderBy("objectId")
	protected List<ListField> emails;

	@OneToMany(targetEntity=ImImpl.class, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	@OrderBy("objectId")
	protected List<ListField> ims;

	@OneToMany(targetEntity=PhoneNumberImpl.class, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	@OrderBy("objectId")
	protected List<ListField> phoneNumbers;

	@OneToMany(targetEntity=PhotoImpl.class, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	@OrderBy("objectId")
	protected List<ListField> photos;
	
	@Enumerated
	@Column(name="gender")
	protected Gender gender;

	@OneToOne(targetEntity=NameImpl.class, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	protected Name name;

	@Transient
	private boolean isOwner = false;

	@Transient
	private boolean isViewer = false;

	@Transient
	private Boolean hasApp;

	@OneToMany(targetEntity=OrganizationImpl.class, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	@OrderBy("objectId")
	protected List<Organization> organizations;

	@OneToMany(targetEntity=AddressImpl.class, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	@OrderBy("objectId")
	protected List<Address> addresses;
	
	@OneToOne(targetEntity=AddressImpl.class, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	protected Address currentLocation;

	@OneToMany(targetEntity=UrlImpl.class, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	@OrderBy("objectId")
	protected List<Url> urls;

	@OneToOne(targetEntity=UrlImpl.class, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	protected Url profileSong;

	@OneToOne(targetEntity=UrlImpl.class, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	protected Url profileVideo;

	public PersonImpl() {
		super();
		lookingFor = new ArrayList<Enum<LookingFor>>();
		accounts = new ArrayList<Account>();
		activities = new ArrayList<String>();
		books = new ArrayList<String>();
		cars = new ArrayList<String>();
		food = new ArrayList<String>();
		heroes = new ArrayList<String>();
		interests = new ArrayList<String>();
		languagesSpoken = new ArrayList<String>();
		movies = new ArrayList<String>();
		music = new ArrayList<String>();
		quotes = new ArrayList<String>();
		sports = new ArrayList<String>();
		tags = new ArrayList<String>();
		turnOffs = new ArrayList<String>();
		turnOns = new ArrayList<String>();
		tvShows = new ArrayList<String>();
		emails = new ArrayList<ListField>();
		ims = new ArrayList<ListField>();
		phoneNumbers = new ArrayList<ListField>();
		photos = new ArrayList<ListField>();
		organizations = new ArrayList<Organization>();
		addresses = new ArrayList<Address>();
		urls = new ArrayList<Url>();
	}

//--- getter
	
	public String getAboutMe() {
		return aboutMe;
	}

	public List<Account> getAccounts() {
		return accounts;
	}

	public List<String> getActivities() {
		return activities;
	}

	public List<Address> getAddresses() {
		return addresses;
	}

	public Integer getAge() {
		return age;
	}

	public Date getBirthday() {
		return birthday;
	}

	public BodyType getBodyType() {
		return bodyType;
	}

	public List<String> getBooks() {
		return books;
	}

	public List<String> getCars() {
		return cars;
	}

	public String getChildren() {
		return children;
	}

	public Address getCurrentLocation() {
		return currentLocation;
	}

	public String getDisplayName() {
		return displayName;
	}

	public Enum<Drinker> getDrinker() {
		return drinker;
	}

	public List<ListField> getEmails() {
		return emails;
	}

	public String getEthnicity() {
		return ethnicity;
	}

	public String getFashion() {
		return fashion;
	}

	public List<String> getFood() {
		return food;
	}

	public Gender getGender() {
		return gender;
	}

	public String getHappiestWhen() {
		return happiestWhen;
	}

	public Boolean getHasApp() {
		return hasApp;
	}

	public List<String> getHeroes() {
		return heroes;
	}

	public String getHumor() {
		return humor;
	}

	public String getId() {
		return id;
	}

	public List<ListField> getIms() {
		return ims;
	}

	public List<String> getInterests() {
		return interests;
	}

	public boolean getIsOwner() {
		return isOwner;
	}

	public boolean getIsViewer() {
		return isViewer;
	}

	public String getJobInterests() {
		return jobInterests;
	}

	public List<String> getLanguagesSpoken() {
		return languagesSpoken;
	}

	public String getLivingArrangement() {
		return livingArrangement;
	}

	public List<Enum<LookingFor>> getLookingFor() {
		return lookingFor;
	}

	public List<String> getMovies() {
		return movies;
	}

	public List<String> getMusic() {
		return music;
	}

	public Name getName() {
		return name;
	}

	public Enum<NetworkPresence> getNetworkPresence() {
		return networkPresence;
	}

	public String getNickname() {
		return nickname;
	}

	public List<Organization> getOrganizations() {
		return organizations;
	}

	public String getPets() {
		return pets;
	}

	public List<ListField> getPhoneNumbers() {
		return phoneNumbers;
	}

	public List<ListField> getPhotos() {
		return photos;
	}

	public String getPoliticalViews() {
		return politicalViews;
	}

	public Url getProfileSong() {
		return profileSong;
	}

	public String getProfileUrl() {
		for (Url url : urls) {
			if (Person.PROFILE_URL_TYPE.equals(url.getType())) {
				return url.getValue();
			}
		}
		return null;
	}

	public Url getProfileVideo() {
		return profileVideo;
	}

	public List<String> getQuotes() {
		return quotes;
	}

	public String getRelationshipStatus() {
		return relationshipStatus;
	}

	public String getReligion() {
		return religion;
	}

	public String getRomance() {
		return romance;
	}

	public String getScaredOf() {
		return scaredOf;
	}

	public String getSexualOrientation() {
		return sexualOrientation;
	}

	public Enum<Smoker> getSmoker() {
		return smoker;
	}

	public List<String> getSports() {
		return sports;
	}

	public String getStatus() {
		return status;
	}

	public List<String> getTags() {
		return tags;
	}

	public String getThumbnailUrl() {
		for (ListField photo : photos) {
			if (Person.THUMBNAIL_PHOTO_TYPE.equals(photo.getType())) {
				return photo.getValue();
			}
		}
		return null;
	}

	public List<String> getTurnOffs() {
		return turnOffs;
	}

	public List<String> getTurnOns() {
		return turnOns;
	}

	public List<String> getTvShows() {
		return tvShows;
	}

	public Date getUpdated() {
		return updated;
	}

	public List<Url> getUrls() {
		return urls;
	}

	public Long getUtcOffset() {
		return utcOffset;
	}
	
	public long getObjectId() {
		return objectId;
	}
	
//--- setter

	public void setAboutMe(String aboutMe) {
		this.aboutMe = aboutMe;
	}

	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}

	public void setActivities(List<String> activities) {
		this.activities = activities;
	}

	public void setAddresses(List<Address> addresses) {
		this.addresses = addresses;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public void setBodyType(BodyType bodyType) {
		this.bodyType = bodyType;
	}

	public void setBooks(List<String> books) {
		this.books = books;
	}

	public void setCars(List<String> cars) {
		this.cars = cars;
	}

	public void setChildren(String children) {
		this.children = children;
	}

	public void setCurrentLocation(Address currentLocation) {
		this.currentLocation = currentLocation;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setDrinker(Enum<Drinker> newDrinker) {
		drinker = newDrinker;
	}

	public void setEmails(List<ListField> emails) {
		this.emails = emails;
	}

	public void setEthnicity(String ethnicity) {
		this.ethnicity = ethnicity;
	}

	public void setFashion(String fashion) {
		this.fashion = fashion;
	}

	public void setFood(List<String> food) {
		this.food = food;
	}

	public void setGender(Gender newGender) {
		gender = newGender;
	}

	public void setHappiestWhen(String happiestWhen) {
		this.happiestWhen = happiestWhen;
	}

	public void setHasApp(Boolean hasApp) {
		this.hasApp = hasApp;
	}

	public void setHeroes(List<String> heroes) {
		this.heroes = heroes;
	}

	public void setHumor(String humor) {
		this.humor = humor;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setIms(List<ListField> ims) {
		this.ims = ims;
	}

	public void setInterests(List<String> interests) {
		this.interests = interests;
	}

	public void setIsOwner(boolean isOwner) {
		this.isOwner = isOwner;
	}

	public void setIsViewer(boolean isViewer) {
		this.isViewer = isViewer;
	}

	public void setJobInterests(String jobInterests) {
		this.jobInterests = jobInterests;
	}

	public void setLanguagesSpoken(List<String> languagesSpoken) {
		this.languagesSpoken = languagesSpoken;
	}

	public void setLivingArrangement(String livingArrangement) {
		this.livingArrangement = livingArrangement;
	}

	public void setLookingFor(List<Enum<LookingFor>> lookingFor) {
		this.lookingFor = lookingFor;
	}

	public void setMovies(List<String> movies) {
		this.movies = movies;
	}

	public void setMusic(List<String> music) {
		this.music = music;
	}

	public void setName(Name name) {
		this.name = name;
	}

	public void setNetworkPresence(Enum<NetworkPresence> networkPresence) {
		this.networkPresence = networkPresence;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public void setOrganizations(List<Organization> organizations) {
		this.organizations = organizations;
	}

	public void setPets(String pets) {
		this.pets = pets;
	}

	public void setPhoneNumbers(List<ListField> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}

	public void setPhotos(List<ListField> photos) {
		this.photos = photos;
	}

	public void setPoliticalViews(String politicalViews) {
		this.politicalViews = politicalViews;
	}

	public void setProfileSong(Url profileSong) {
		this.profileSong = profileSong;
	}

	public void setProfileUrl(String profileUrl) {
		if (profileUrl != null) {
			for (Url url : urls) {
				if (Person.PROFILE_URL_TYPE.equals(url.getType())) {
					url.setValue(profileUrl);
					return;
				}
			}
			UrlImpl url = new UrlImpl();
			url.setType(Person.PROFILE_URL_TYPE);
			url.setValue(profileUrl);
//			url.setPerson(this);
			urls.add(url);
		} else {
			int idx = -1;
			for (int i = 0; i < urls.size(); i++) {
				Url url = urls.get(i);
				if (Person.PROFILE_URL_TYPE.equals(url.getType())) {
					idx = i;
					break;
				}
			}
			if (idx != -1) {
				urls.remove(idx);
			}
		}
	}

	public void setProfileVideo(Url profileVideo) {
		this.profileVideo = profileVideo;
	}

	public void setQuotes(List<String> quotes) {
		this.quotes = quotes;
	}

	public void setRelationshipStatus(String relationshipStatus) {
		this.relationshipStatus = relationshipStatus;
	}

	public void setReligion(String religion) {
		this.religion = religion;
	}

	public void setRomance(String romance) {
		this.romance = romance;
	}

	public void setScaredOf(String scaredOf) {
		this.scaredOf = scaredOf;
	}

	public void setSexualOrientation(String sexualOrientation) {
		this.sexualOrientation = sexualOrientation;
	}

	public void setSmoker(Enum<Smoker> newSmoker) {
		this.smoker = newSmoker;
	}

	public void setSports(List<String> sports) {
		this.sports = sports;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		if (thumbnailUrl != null) {
			for (ListField photo : photos) {
				if (Person.THUMBNAIL_PHOTO_TYPE.equals(photo.getType())) {
					photo.setValue(thumbnailUrl);
					return;
				}
			}
			PhotoImpl photo = new PhotoImpl();
			photo.setType(Person.THUMBNAIL_PHOTO_TYPE);
			photo.setValue(thumbnailUrl);
//			photo.setPerson(this);
			photos.add(photo);
		} else {
			int idx = -1;
			for (int i = 0; i < photos.size(); i++) {
				ListField photo = photos.get(i);
				if (Person.THUMBNAIL_PHOTO_TYPE.equals(photo.getType())) {
					idx = i;
					break;
				}
			}
			if (idx != -1) {
				photos.remove(idx);
			}
		}
	}

	public void setTurnOffs(List<String> turnOffs) {
		this.turnOffs = turnOffs;
	}

	public void setTurnOns(List<String> turnOns) {
		this.turnOns = turnOns;
	}

	public void setTvShows(List<String> tvShows) {
		this.tvShows = tvShows;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public void setUrls(List<Url> urls) {
		this.urls = urls;
	}

	public void setUtcOffset(Long utcOffset) {
		this.utcOffset = utcOffset;
	}

}
