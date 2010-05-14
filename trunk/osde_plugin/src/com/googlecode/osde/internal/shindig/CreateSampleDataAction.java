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
package com.googlecode.osde.internal.shindig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.googlecode.osde.internal.Activator;
import com.googlecode.osde.internal.ConnectionException;
import com.googlecode.osde.internal.ui.views.people.PersonView;
import com.googlecode.osde.internal.utils.Gadgets;

import org.apache.shindig.protocol.model.Enum;
import org.apache.shindig.social.opensocial.hibernate.entities.AddressImpl;
import org.apache.shindig.social.opensocial.hibernate.entities.BodyTypeImpl;
import org.apache.shindig.social.opensocial.hibernate.entities.DrinkerImpl;
import org.apache.shindig.social.opensocial.hibernate.entities.EmailImpl;
import org.apache.shindig.social.opensocial.hibernate.entities.LookingForImpl;
import org.apache.shindig.social.opensocial.hibernate.entities.NameImpl;
import org.apache.shindig.social.opensocial.hibernate.entities.NetworkPresenceImpl;
import org.apache.shindig.social.opensocial.hibernate.entities.OrganizationImpl;
import org.apache.shindig.social.opensocial.hibernate.entities.PersonImpl;
import org.apache.shindig.social.opensocial.hibernate.entities.PhoneNumberImpl;
import org.apache.shindig.social.opensocial.hibernate.entities.SmokerImpl;
import org.apache.shindig.social.opensocial.hibernate.entities.UrlImpl;
import org.apache.shindig.social.opensocial.model.Address;
import org.apache.shindig.social.opensocial.model.BodyType;
import org.apache.shindig.social.opensocial.model.Drinker;
import org.apache.shindig.social.opensocial.model.ListField;
import org.apache.shindig.social.opensocial.model.LookingFor;
import org.apache.shindig.social.opensocial.model.Name;
import org.apache.shindig.social.opensocial.model.NetworkPresence;
import org.apache.shindig.social.opensocial.model.Organization;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.model.Person.Gender;
import org.apache.shindig.social.opensocial.model.Smoker;
import org.apache.shindig.social.opensocial.model.Url;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;

public class CreateSampleDataAction extends Action implements IWorkbenchWindowActionDelegate {

    private Shell shell;
    private IWorkbenchPart targetPart;

    public CreateSampleDataAction() {
        super();
    }

    public void run(IAction action) {
        try {
            final PersonService personService = Activator.getDefault().getPersonService();
            boolean confirm = MessageDialog.openConfirm(shell,
                    "Confirm", "Would you like to create sample data in Shindig database?\n"
                            + "(Created people are 'conrad.doe', 'john.doe', 'jane.doe' and 'george.doe')");
            if (confirm) {
                Job job = new Job("Create sample data") {
                    @Override
                    protected IStatus run(IProgressMonitor monitor) {
                        Person[] samplePeople = createSamplePeople();
                        monitor.beginTask("Create sample data", samplePeople.length + 2);
                        if (isAlreadyExistsPeople(samplePeople, personService)) {
                            shell.getDisplay().syncExec(new Runnable() {
                                public void run() {
                                    IWorkbenchWindow window =
                                        targetPart.getSite().getWorkbenchWindow();
                                    MessageDialog.openWarning(
                                            window.getShell(), "Warning", "Sample people already exists.");
                                }
                            });
                            monitor.done();
                            return Status.OK_STATUS;
                        }
                        for (Person p : samplePeople) {
                            personService.storePerson(p);
                            monitor.worked(1);
                        }
                        setRelations(samplePeople, personService);
                        monitor.worked(1);
                        shell.getDisplay().syncExec(new Runnable() {
                            public void run() {
                                try {
                                    IWorkbenchWindow window =
                                            targetPart.getSite().getWorkbenchWindow();
                                    PersonView personView = (PersonView) window.getActivePage()
                                            .showView(PersonView.ID);
                                    personView.loadPeople();
                                } catch (PartInitException e) {
                                    // TODO: Handle PartInitException.
                                    throw new IllegalStateException(e);
                                }
                            }
                        });
                        monitor.worked(1);
                        monitor.done();
                        return Status.OK_STATUS;
                    }
                };
                job.setUser(true);
                job.schedule();
            }
        } catch (ConnectionException ce) {
            MessageDialog.openError(shell, "Error", "Shindig database not started yet.");
        }
    }

    protected boolean isAlreadyExistsPeople(Person[] samplePeople, PersonService personService) {
        List<Person> entities = personService.getPeople();
        for (Person sample : samplePeople) {
            for (Person entity: entities) {
                if (entity.getId().equals(sample.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    protected void setRelations(Person[] samplePeople, PersonService personService) {
        Person conrad = samplePeople[0];
        Person john = samplePeople[1];
        Person jane = samplePeople[2];
        Person george = samplePeople[3];
        personService.createRelationship("friends", conrad, john);
        personService.createRelationship("friends", conrad, jane);
        personService.createRelationship("friends", conrad, george);
        personService.createRelationship("friends", john, jane);
        personService.createRelationship("friends", john, george);
        personService.createRelationship("friends", jane, john);
        personService.createRelationship("friends", george, john);
    }

    protected Person[] createSamplePeople() {
        return  new Person[] {
                createConradPerson(),
                createJohnPerson(),
                createJanePerson(),
                createGeorgePerson()
        };
    }

    protected Person createGeorgePerson() {
        Person george = new PersonImpl();
        george.setId("george.doe");
        george.setDisplayName("Georgey");
        george.setGender(Gender.male);
        george.setHasApp(true);
        Name name = new NameImpl();
        name.setFamilyName("Doe");
        name.setGivenName("George");
        name.setFormatted("George Doe");
        return george;
    }

    protected Person createJanePerson() {
        Person jane = new PersonImpl();
        jane.setId("jane.doe");
        jane.setDisplayName("Janey");
        jane.setGender(Gender.female);
        jane.setHasApp(true);
        Name name = new NameImpl();
        name.setFamilyName("Doe");
        name.setGivenName("Jane");
        name.setFormatted("Jane Doe");
        return jane;
    }

    protected Person createJohnPerson() {
        Person john = new PersonImpl();
        john.setId("john.doe");
        john.setDisplayName("Johnny");
        john.setGender(Gender.male);
        john.setHasApp(true);
        Name name = new NameImpl();
        name.setFamilyName("Doe");
        name.setGivenName("John");
        name.setFormatted("John Doe");
        return john;
    }

    private Person createConradPerson() {
        Person person = new PersonImpl();
        person.setId("conrad.doe");
        person.setAboutMe("I have an example of every piece of data");
        person.setActivities(Arrays.asList("Coding Shindig"));
        Address address = new AddressImpl();
        address.setCountry("US");
        address.setLatitude(28.3043f);
        address.setLongitude(143.0859f);
        address.setLocality("who knows");
        address.setPostalCode("12345");
        address.setRegion("Apache, CA");
        address.setStreetAddress("1 OpenStandards Way");
        address.setType("home");
        address.setFormatted("PoBox 3565, 1 OpenStandards Way, Apache CA");
        person.setAddresses(Arrays.asList(address));
        person.setAge(33);
        BodyType bodyType = new BodyTypeImpl();
        bodyType.setBuild("svelte");
        bodyType.setEyeColor("blue");
        bodyType.setHairColor("black");
        bodyType.setHeight(1.84f);
        bodyType.setWeight(74f);
        person.setBodyType(bodyType);
        person.setBooks(Arrays.asList("The Cathedral & the Bazaar", "Catch 22"));
        person.setCars(Arrays.asList("beetle", "prius"));
        person.setChildren("3");
        Address currentLocation = new AddressImpl();
        currentLocation.setLatitude(48.858193f);
        currentLocation.setLongitude(2.29419f);
        person.setCurrentLocation(currentLocation);
        person.setBirthday(Gadgets.getDate(1975, 0, 1));
        person.setDisplayName("Conrad Doe");
        person.setDrinker(new DrinkerImpl(Drinker.SOCIALLY, "Socially"));
        ListField email = new EmailImpl();
        email.setValue("shindig-dev@incubator.apache.org");
        email.setType("work");
        person.setEmails(Arrays.asList(email));
        person.setEthnicity("developer");
        person.setFashion("t-shirts");
        person.setFood(Arrays.asList("sushi", "burgers"));
        person.setGender(Gender.male);
        person.setHappiestWhen("coding");
        person.setHasApp(true);
        person.setHeroes(Arrays.asList("Doug Crockford", "Charles Babbage"));
        person.setHumor("none to speak of");
        person.setInterests(Arrays.asList("PHP", "Java"));
        person.setJobInterests("will work for beer");
        Organization organization1 = new OrganizationImpl();
        address = new AddressImpl();
        address.setFormatted("1 Shindig Drive");
        organization1.setAddress(address);
        organization1.setDescription("lots of coding");
        organization1.setEndDate(Gadgets.getDate(2010, 9, 10));
        organization1.setField("Software Engineering");
        organization1.setName("Apache.com");
        organization1.setSalary("$1000000000");
        organization1.setStartDate(Gadgets.getDate(1995, 0, 1));
        organization1.setSubField("Development");
        organization1.setTitle("Grand PooBah");
        organization1.setWebpage("http://incubator.apache.org/projects/shindig.html");
        organization1.setType("job");
        Organization organization2 = new OrganizationImpl();
        address = new AddressImpl();
        address.setFormatted("1 Skid Row");
        organization2.setAddress(address);
        organization2.setDescription("");
        organization2.setEndDate(Gadgets.getDate(1995, 0, 1));
        organization2.setField("College");
        organization2.setName("School of hard knocks");
        organization2.setSalary("$100");
        organization2.setStartDate(Gadgets.getDate(1991, 0, 1));
        organization2.setSubField("Lab Tech");
        organization2.setTitle("Gopher");
        organization2.setWebpage("");
        organization2.setType("job");
        person.setOrganizations(Arrays.asList(organization1, organization2));
        person.setLanguagesSpoken(Arrays.asList("English", "Dutch", "Esperanto"));
        person.setUpdated(Gadgets.getDate(2006, 5, 6, 12, 12, 12));
        person.setLivingArrangement("in a house");
        Enum<LookingFor> lookingFor1 = new LookingForImpl();
        lookingFor1.setValue(LookingFor.RANDOM);
        lookingFor1.setDisplayValue("Random");
        Enum<LookingFor> lookingFor2 = new LookingForImpl();
        lookingFor2.setValue(LookingFor.NETWORKING);
        lookingFor2.setDisplayValue("Networking");

        List<Enum<LookingFor>> lookingFors = new ArrayList<Enum<LookingFor>>();
        lookingFors.add(lookingFor1);
        lookingFors.add(lookingFor2);
        person.setLookingFor(lookingFors);

        person.setMovies(Arrays.asList("Iron Man", "Nosferatu"));
        person.setMusic(Arrays.asList("Chieftains", "Beck"));
        Name name = new NameImpl();
        name.setAdditionalName("H");
        name.setFamilyName("Doe");
        name.setGivenName("Conrad");
        name.setHonorificPrefix("Sir");
        name.setHonorificSuffix("Social Butterfly");
        name.setFormatted("Sir Conrad H. Doe Social Butterfly");
        person.setName(name);
        person.setNetworkPresence(new NetworkPresenceImpl(NetworkPresence.ONLINE, "Online"));
        person.setNickname("diggy");
        person.setPets("dog,cat");
        PhoneNumberImpl phone1 = new PhoneNumberImpl();
        phone1.setValue("111-111-111");
        phone1.setType("work");
        PhoneNumberImpl phone2 = new PhoneNumberImpl();
        phone2.setValue("999-999-999");
        phone2.setType("mobile");
        person.setPhoneNumbers(Arrays.<ListField>asList(phone1, phone2));
        person.setPoliticalViews("open leaning");
        UrlImpl profileSong = new UrlImpl();
        profileSong.setValue("http://www.example.org/songs/OnlyTheLonely.mp3");
        profileSong.setLinkText("Feelin' blue");
        profileSong.setType("road");
        person.setProfileSong(profileSong);
        person.setProfileUrl("http://www.example.org/?id=1");
        UrlImpl profileVideo = new UrlImpl();
        profileVideo.setValue("http://www.example.org/videos/Thriller.flv");
        profileVideo.setLinkText("Thriller");
        profileVideo.setType("video");
        person.setProfileVideo(profileVideo);
        person.setQuotes(Arrays.asList("I am therfore I code", "Doh!"));
        person.setRelationshipStatus("married to my job");
        person.setReligion("druidic");
        person.setRomance("twice a year");
        person.setScaredOf("COBOL");
        person.setSexualOrientation("north");
        person.setSmoker(new SmokerImpl(Smoker.NO, "No"));
        person.setSports(Arrays.asList("frisbee", "rugby"));
        person.setStatus("happy");
        person.setTags(Arrays.asList("C#", "JSON", "template"));
        person.setThumbnailUrl("http://www.example.org/pic/?id=1");
        person.setUtcOffset(-8l);
        person.setTurnOffs(Arrays.asList("lack of unit tests", "cabbage"));
        person.setTurnOns(Arrays.asList("well document code"));
        person.setTvShows(Arrays.asList("House", "Battlestart Galactica"));
        UrlImpl url1 = new UrlImpl();
        url1.setValue("http://www.example.org/?id=1");
        url1.setLinkText("my profile");
        url1.setType("profile");
        UrlImpl url2 = new UrlImpl();
        url2.setValue("http://www.example.org/pic/?id=1");
        url2.setLinkText("my awesome picture");
        url2.setType("thumbnail");
        person.setUrls(Arrays.<Url>asList(url1, url2));
        return person;
    }

    public void selectionChanged(IAction action, ISelection selection) {
    }

    public void dispose() {
        shell = null;
        targetPart = null;
    }

    public void init(IWorkbenchWindow window) {
        targetPart = window.getActivePage().getActivePart();
        shell = targetPart.getSite().getShell();
    }

}
