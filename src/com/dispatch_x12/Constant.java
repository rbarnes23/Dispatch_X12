package com.dispatch_x12;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by Rick on 10/7/13.
 */
public class Constant
{
    public static final int MEMBERID = 1;
    public static final int CONNECTED = 2;
    public static final int MESSAGEMENU = 3;
    public static final int COMPANYMENU = 4;
    public static final int VEHICLEMENU = 5;
    public static final int EMPLOYEEMENU = 6;
    public static final int RATESMENU = 7;
    public static final int COMPANYDATA = 8;
    public static final int VEHICLEDATA = 9;
    public static final int EMPLOYEEDATA = 10;
    public static final int RATESDATA = 11;
    public static final int MESSAGE = 12;
    
    public static final int X204 = 204;
    public static final int X990 = 990;
    public static final int X997 = 997;
    public static final int X210 = 210;
    public static final int STOPINFO = 64;
    public static final int LOADDATA = 65;
    public static final int FINDMEMBER = 67;
    public static final int SESSIONS = 68;
    public static final int PAYDRIVER = 69;
    public static final int STOPS = 70;
    public static final double KMTOMILES =0.621371192;

	public static final String NOMINATIM_SERVICE_URL = "http://nominatim.openstreetmap.org/";
	public static final String MAPQUEST_SERVICE_URL = "http://open.mapquestapi.com/nominatim/v1/";
	public static final String CLOUDMADE_SERVICE_URL = "http://beta.geocoding.cloudmade.com/v3/4193f0b619af44bca5e2684f3e3843b3/api/geo.location.search.2?format=json&source=OSM&enc=UTF-8&limit=10&locale=en&q=Leinfelden-Echterdingen%20Germany%20Karlstr.%2012";
	public static final String CLOUDMADE_SERVICE_URL3 = "http://beta.geocoding.cloudmade.com/v3/8ee2a50541944fb9bcedded5165f09d9/api/geo.location.search.2?format=json&source=OSM&enc=UTF-8&limit=10&locale=de&q=Leinfelden-Echterdingen%20Germany%20Karlstr.%2012";
	public static final String CLOUDMADE_SERVICE_URL2 = "http://beta.geocoding.cloudmade.com/v3/4193f0b619af44bca5e2684f3e3843b3/api/geo.location.search.2?format=json&source=OSM&enc=UTF-8&limit=10&locale=de&q=Leinfelden-Echterdingen%20Germany%20Karlstr.%2012";

	
	public static final Pattern EMAIL_ADDRESS_PATTERN = Pattern
			.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@"
					+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
					+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");

	public static final Pattern ADDRESS_PATTERN = Pattern.compile(Constant.ADDRESS_REGEX);

	
	/*
	 * Usage email.matches(EMAIL_REGEX)
	 */
    public static final String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
    /*
     * www.rexegg.com/regex-uses.html
     */
    public static final String EMAIL_REGEX2 = "(?i)\\b[A-Z0-9._%+-]+@(?:[A-Z0-9-]+\\.)+[A-Z]{2,4}\\b";

    public static final String ADDRESS_REGEX = "(.+?),\\s*(.+?)(?:,\\s*|\\s\\s)(.+?)\\s(\\d{5})";
    
    public static final String ADDRESS_REGEX2 = "(.+?),\\s*(.+?)(?:,\\s*|\\s\\s)(.+?)\\s\\s*(\\d{5})";
	public static final String EDKEY = "4455414744176343";

	public static final SimpleDateFormat NEWDATE = new SimpleDateFormat("MMM dd, yyyy",
			Locale.getDefault());

	public static final SimpleDateFormat TODATEFORMAT = new SimpleDateFormat("yyyy-MM-dd");
		
	public static final SimpleDateFormat EDIDATE = new SimpleDateFormat("yyyyMMdd",
			Locale.getDefault());
	

	public static final SimpleDateFormat SHORTDATE = new SimpleDateFormat("yyMMdd",
			Locale.getDefault());

	public static final SimpleDateFormat EDITIME = new SimpleDateFormat("HHmm",
			Locale.getDefault());

}