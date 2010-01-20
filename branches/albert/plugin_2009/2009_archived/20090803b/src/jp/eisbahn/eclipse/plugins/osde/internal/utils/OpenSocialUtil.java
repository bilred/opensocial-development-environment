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
package jp.eisbahn.eclipse.plugins.osde.internal.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import jp.eisbahn.eclipse.plugins.osde.internal.Activator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.xml.sax.SAXException;

import com.google.gadgets.GadgetXmlParser;
import com.google.gadgets.Module;

public class OpenSocialUtil {

	public static ApplicationInformation createApplicationInformation(IFile file) throws CoreException, IOException, SAXException {
		try {
			GadgetXmlParser parser = Activator.getDefault().getGadgetXmlParser();
			Module module = parser.parse(file.getContents());
			String path = file.getFullPath().toPortableString();
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] hash = digest.digest(path.getBytes("UTF-8"));
			String appId = Gadgets.toHexString(hash);
			String consumerKey = "osde:" + path;
			digest.reset();
			hash = digest.digest(consumerKey.getBytes("UTF-8"));
			String consumerSecret = Gadgets.toHexString(hash);
			ApplicationInformation info = new ApplicationInformation();
			info.setAppId(appId);
			info.setModule(module);
			info.setPath(file.getFullPath().toPortableString());
			info.setConsumerKey(consumerKey);
			info.setConsumerSecret(consumerSecret);
			return info;
		} catch(NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		} catch(UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}

	public static ApplicationInformation createApplicationInformation(String url) throws CoreException, IOException, SAXException {
		try {
			GadgetXmlParser parser = Activator.getDefault().getGadgetXmlParser();
			Module module = parser.parse(new URL(url).openStream());
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] hash = digest.digest(url.getBytes("UTF-8"));
			String appId = Gadgets.toHexString(hash);
			String consumerKey = "osde:" + url;
			digest.reset();
			hash = digest.digest(consumerKey.getBytes("UTF-8"));
			String consumerSecret = Gadgets.toHexString(hash);
			ApplicationInformation info = new ApplicationInformation();
			info.setAppId(appId);
			info.setModule(module);
			info.setPath(url);
			info.setConsumerKey(consumerKey);
			info.setConsumerSecret(consumerSecret);
			return info;
		} catch(NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		} catch(UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		} catch (MalformedURLException e) {
			throw new IllegalStateException(e);
		}
	}

	public static boolean isGadgetXml(IFile file) {
		IContentTypeManager manager = Platform.getContentTypeManager();
		IContentType[] contentTypes = manager.findContentTypesFor(file.getLocation().toOSString());
		for (IContentType contentType : contentTypes) {
			if (contentType.getId().equals("jp.eisbahn.eclipse.plugins.osde.gadgetXml")) {
				return true;
			}
		}
		return false;
	}

	public static final String ANY = "--- any ---";

	public static final String[] COUNTRIES = {
		ANY,
		"AFGHANISTAN (AF)",
		"ÅLAND ISLANDS (AX)",
		"ALBANIA (AL)",
		"ALGERIA (DZ)",
		"AMERICAN SAMOA (AS)",
		"ANDORRA (AD)",
		"ANGOLA (AO)",
		"ANGUILLA (AI)",
		"ANTARCTICA (AQ)",
		"ANTIGUA AND BARBUDA (AG)",
		"ARGENTINA (AR)",
		"ARMENIA (AM)",
		"ARUBA (AW)",
		"AUSTRALIA (AU)",
		"AUSTRIA (AT)",
		"AZERBAIJAN (AZ)",
		"BAHAMAS (BS)",
		"BAHRAIN (BH)",
		"BANGLADESH (BD)",
		"BARBADOS (BB)",
		"BELARUS (BY)",
		"BELGIUM (BE)",
		"BELIZE (BZ)",
		"BENIN (BJ)",
		"BERMUDA (BM)",
		"BHUTAN (BT)",
		"BOLIVIA (BO)",
		"BOSNIA AND HERZEGOVINA (BA)",
		"BOTSWANA (BW)",
		"BOUVET ISLAND (BV)",
		"BRAZIL (BR)",
		"BRITISH INDIAN OCEAN TERRITORY (IO)",
		"BRUNEI DARUSSALAM (BN)",
		"BULGARIA (BG)",
		"BURKINA FASO (BF)",
		"BURUNDI (BI)",
		"CAMBODIA (KH)",
		"CAMEROON (CM)",
		"CANADA (CA)",
		"CAPE VERDE (CV)",
		"CAYMAN ISLANDS (KY)",
		"CENTRAL AFRICAN REPUBLIC (CF)",
		"CHAD (TD)",
		"CHILE (CL)",
		"CHINA (CN)",
		"CHRISTMAS ISLAND (CX)",
		"COCOS (KEELING) ISLANDS (CC)",
		"COLOMBIA (CO)",
		"COMOROS (KM)",
		"CONGO (CG)",
		"CONGO, THE DEMOCRATIC REPUBLIC OF THE (CD)",
		"COOK ISLANDS (CK)",
		"COSTA RICA (CR)",
		"CÔTE D'IVOIRE (CI)",
		"CROATIA (HR)",
		"CUBA (CU)",
		"CYPRUS (CY)",
		"CZECH REPUBLIC (CZ)",
		"DENMARK (DK)",
		"DJIBOUTI (DJ)",
		"DOMINICA (DM)",
		"DOMINICAN REPUBLIC (DO)",
		"ECUADOR (EC)",
		"EGYPT (EG)",
		"EL SALVADOR (SV)",
		"EQUATORIAL GUINEA (GQ)",
		"ERITREA (ER)",
		"ESTONIA (EE)",
		"ETHIOPIA (ET)",
		"FALKLAND ISLANDS (MALVINAS) (FK)",
		"FAROE ISLANDS (FO)",
		"FIJI (FJ)",
		"FINLAND (FI)",
		"FRANCE (FR)",
		"FRENCH GUIANA (GF)",
		"FRENCH POLYNESIA (PF)",
		"FRENCH SOUTHERN TERRITORIES (TF)",
		"GABON (GA)",
		"GAMBIA (GM)",
		"GEORGIA (GE)",
		"GERMANY (DE)",
		"GHANA (GH)",
		"GIBRALTAR (GI)",
		"GREECE (GR)",
		"GREENLAND (GL)",
		"GRENADA (GD)",
		"GUADELOUPE (GP)",
		"GUAM (GU)",
		"GUATEMALA (GT)",
		"GUERNSEY (GG)",
		"GUINEA (GN)",
		"GUINEA-BISSAU (GW)",
		"GUYANA (GY)",
		"HAITI (HT)",
		"HEARD ISLAND AND MCDONALD ISLANDS (HM)",
		"HOLY SEE (VATICAN CITY STATE) (VA)",
		"HONDURAS (HN)",
		"HONG KONG (HK)",
		"HUNGARY (HU)",
		"ICELAND (IS)",
		"INDIA (IN)",
		"INDONESIA (ID)",
		"IRAN, ISLAMIC REPUBLIC OF (IR)",
		"IRAQ (IQ)",
		"IRELAND (IE)",
		"ISLE OF MAN (IM)",
		"ISRAEL (IL)",
		"ITALY (IT)",
		"JAMAICA (JM)",
		"JAPAN (JP)",
		"JERSEY (JE)",
		"JORDAN (JO)",
		"KAZAKHSTAN (KZ)",
		"KENYA (KE)",
		"KIRIBATI (KI)",
		"KOREA, DEMOCRATIC PEOPLE'S REPUBLIC OF (KP)",
		"KOREA, REPUBLIC OF (KR)",
		"KUWAIT (KW)",
		"KYRGYZSTAN (KG)",
		"LAO PEOPLE'S DEMOCRATIC REPUBLIC (LA)",
		"LATVIA (LV)",
		"LEBANON (LB)",
		"LESOTHO (LS)",
		"LIBERIA (LR)",
		"LIBYAN ARAB JAMAHIRIYA (LY)",
		"LIECHTENSTEIN (LI)",
		"LITHUANIA (LT)",
		"LUXEMBOURG (LU)",
		"MACAO (MO)",
		"MACEDONIA, THE FORMER YUGOSLAV REPUBLIC OF (MK)",
		"MADAGASCAR (MG)",
		"MALAWI (MW)",
		"MALAYSIA (MY)",
		"MALDIVES (MV)",
		"MALI (ML)",
		"MALTA (MT)",
		"MARSHALL ISLANDS (MH)",
		"MARTINIQUE (MQ)",
		"MAURITANIA (MR)",
		"MAURITIUS (MU)",
		"MAYOTTE (YT)",
		"MEXICO (MX)",
		"MICRONESIA, FEDERATED STATES OF (FM)",
		"MOLDOVA, REPUBLIC OF (MD)",
		"MONACO (MC)",
		"MONGOLIA (MN)",
		"MONTENEGRO (ME)",
		"MONTSERRAT (MS)",
		"MOROCCO (MA)",
		"MOZAMBIQUE (MZ)",
		"MYANMAR (MM)",
		"NAMIBIA (NA)",
		"NAURU (NR)",
		"NEPAL (NP)",
		"NETHERLANDS (NL)",
		"NETHERLANDS ANTILLES (AN)",
		"NEW CALEDONIA (NC)",
		"NEW ZEALAND (NZ)",
		"NICARAGUA (NI)",
		"NIGER (NE)",
		"NIGERIA (NG)",
		"NIUE (NU)",
		"NORFOLK ISLAND (NF)",
		"NORTHERN MARIANA ISLANDS (MP)",
		"NORWAY (NO)",
		"OMAN (OM)",
		"PAKISTAN (PK)",
		"PALAU (PW)",
		"PALESTINIAN TERRITORY, OCCUPIED (PS)",
		"PANAMA (PA)",
		"PAPUA NEW GUINEA (PG)",
		"PARAGUAY (PY)",
		"PERU (PE)",
		"PHILIPPINES (PH)",
		"PITCAIRN (PN)",
		"POLAND (PL)",
		"PORTUGAL (PT)",
		"PUERTO RICO (PR)",
		"QATAR (QA)",
		"RÉUNION (RE)",
		"ROMANIA (RO)",
		"RUSSIAN FEDERATION (RU)",
		"RWANDA (RW)",
		"SAINT BARTHÉLEMY (BL)",
		"SAINT HELENA (SH)",
		"SAINT KITTS AND NEVIS (KN)",
		"SAINT LUCIA (LC)",
		"SAINT MARTIN (MF)",
		"SAINT PIERRE AND MIQUELON (PM)",
		"SAINT VINCENT AND THE GRENADINES (VC)",
		"SAMOA (WS)",
		"SAN MARINO (SM)",
		"SAO TOME AND PRINCIPE (ST)",
		"SAUDI ARABIA (SA)",
		"SENEGAL (SN)",
		"SERBIA (RS)",
		"SEYCHELLES (SC)",
		"SIERRA LEONE (SL)",
		"SINGAPORE (SG)",
		"SLOVAKIA (SK)",
		"SLOVENIA (SI)",
		"SOLOMON ISLANDS (SB)",
		"SOMALIA (SO)",
		"SOUTH AFRICA (ZA)",
		"SOUTH GEORGIA AND THE SOUTH SANDWICH ISLANDS (GS)",
		"SPAIN (ES)",
		"SRI LANKA (LK)",
		"SUDAN (SD)",
		"SURINAME (SR)",
		"SVALBARD AND JAN MAYEN (SJ)",
		"SWAZILAND (SZ)",
		"SWEDEN (SE)",
		"SWITZERLAND (CH)",
		"SYRIAN ARAB REPUBLIC (SY)",
		"TAIWAN, PROVINCE OF CHINA (TW)",
		"TAJIKISTAN (TJ)",
		"TANZANIA, UNITED REPUBLIC OF (TZ)",
		"THAILAND (TH)",
		"TIMOR-LESTE (TL)",
		"TOGO (TG)",
		"TOKELAU (TK)",
		"TONGA (TO)",
		"TRINIDAD AND TOBAGO (TT)",
		"TUNISIA (TN)",
		"TURKEY (TR)",
		"TURKMENISTAN (TM)",
		"TURKS AND CAICOS ISLANDS (TC)",
		"TUVALU (TV)",
		"UGANDA (UG)",
		"UKRAINE (UA)",
		"UNITED ARAB EMIRATES (AE)",
		"UNITED KINGDOM (GB)",
		"UNITED STATES (US)",
		"UNITED STATES MINOR OUTLYING ISLANDS (UM)",
		"URUGUAY (UY)",
		"UZBEKISTAN (UZ)",
		"VANUATU (VU)",
		"VENEZUELA (VE)",
		"VIET NAM (VN)",
		"VIRGIN ISLANDS, BRITISH (VG)",
		"VIRGIN ISLANDS, U.S. (VI)",
		"WALLIS AND FUTUNA (WF)",
		"WESTERN SAHARA (EH)",
		"YEMEN (YE)",
		"ZAMBIA (ZM)",
		"ZIMBABWE (ZW)" };
	
	public static final String[] LANGUAGES = {
		ANY,
		"Afar (aa)",
		"Abkhazian (ab)",
		"Afrikaans (af)",
		"Amharic (am)",
		"Arabic (ar)",
		"Assamese (as)",
		"Aymara (ay)",
		"Azerbaijani (az)",
		"Bashkir (ba)",
		"Byelorussian (be)",
		"Bulgarian (bg)",
		"Bihari (bh)",
		"Bislama (bi)",
		"Bengali (bn)",
		"Tibetan (bo)",
		"Breton (br)",
		"Catalan (ca)",
		"Corsican (co)",
		"Czech (cs)",
		"Welsh (cy)",
		"Danish (da)",
		"German (de)",
		"Bhutani (dz)",
		"Greek (el)",
		"English (en)",
		"Esperanto (eo)",
		"Spanish (es)",
		"Estonian (et)",
		"Basque (eu)",
		"Persian (fa)",
		"Finnish (fi)",
		"Fiji (fj)",
		"Faeroese (fo)",
		"French (fr)",
		"Frisian (fy)",
		"Irish (ga)",
		"Gaelic (gd)",
		"Galician (gl)",
		"Guarani (gn)",
		"Gujarati (gu)",
		"Hausa (ha)",
		"Hindi (hi)",
		"Croatian (hr)",
		"Hungarian (hu)",
		"Armenian (hy)",
		"Interlingua (ia)",
		"Interlingue (ie)",
		"Inupiak (ik)",
		"Indonesian (in)",
		"Icelandic (is)",
		"Italian (it)",
		"Hebrew (iw)",
		"Japanese (ja)",
		"Yiddish (ji)",
		"Javanese (jw)",
		"Georgian (ka)",
		"Kazakh (kk)",
		"Greenlandic (kl)",
		"Cambodian (km)",
		"Kannada (kn)",
		"Korean (ko)",
		"Kashmiri (ks)",
		"Kurdish (ku)",
		"Kirghiz (ky)",
		"Latin (la)",
		"Lingala (ln)",
		"Laothian (lo)",
		"Lithuanian (lt)",
		"Latvian (lv)",
		"Malagasy (mg)",
		"Maori (mi)",
		"Macedonian (mk)",
		"Malayalam (ml)",
		"Mongolian (mn)",
		"Moldavian (mo)",
		"Marathi (mr)",
		"Malay (ms)",
		"Maltese (mt)",
		"Burmese (my)",
		"Nauru (na)",
		"Nepali (ne)",
		"Dutch (nl)",
		"Norwegian (no)",
		"Occitan (oc)",
		"Oromo (om)",
		"Oriya (or)",
		"Punjabi (pa)",
		"Polish (pl)",
		"Pashto (ps)",
		"Portuguese (pt)",
		"Quechua (qu)",
		"Rhaeto-Romance (rm)",
		"Kirundi (rn)",
		"Romanian (ro)",
		"Russian (ru)",
		"Kinyarwanda (rw)",
		"Sanskrit (sa)",
		"Sindhi (sd)",
		"Sangro (sg)",
		"Serbo-Croatian (sh)",
		"Singhalese (si)",
		"Slovak (sk)",
		"Slovenian (sl)",
		"Samoan (sm)",
		"Shona (sn)",
		"Somali (so)",
		"Albanian (sq)",
		"Serbian (sr)",
		"Siswati (ss)",
		"Sesotho (st)",
		"Sudanese (su)",
		"Swedish (sv)",
		"Swahili (sw)",
		"Tamil (ta)",
		"Tegulu (te)",
		"Tajik (tg)",
		"Thai (th)",
		"Tigrinya (ti)",
		"Turkmen (tk)",
		"Tagalog (tl)",
		"Setswana (tn)",
		"Tonga (to)",
		"Turkish (tr)",
		"Tsonga (ts)",
		"Tatar (tt)",
		"Twi (tw)",
		"Ukrainian (uk)",
		"Urdu (ur)",
		"Uzbek (uz)",
		"Vietnamese (vi)",
		"Volapuk (vo)",
		"Wolof (wo)",
		"Xhosa (xh)",
		"Yoruba (yo)",
		"Chinese (zh)",
		"Zulu (zu)" };
	
}
