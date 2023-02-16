package com.example.utaeventtracker;

import java.util.ArrayList;
import java.util.List;

public class SharedClass {
    public Boolean isAdmin;
    public String eventUrl = "http://utaeventtracker-env.eba-nevd4ufs.us-east-1.elasticbeanstalk.com/tracker/events/";
    public String deptUrl = "http://utaeventtracker-env.eba-nevd4ufs.us-east-1.elasticbeanstalk.com/departments/";
    public String registerEventUrl = "http://utaeventtracker-env.eba-nevd4ufs.us-east-1.elasticbeanstalk.com/tracker/registerEvent";
    public String userDetailsUrl = "http://utaeventtracker-env.eba-nevd4ufs.us-east-1.elasticbeanstalk.com/EventTracker/auth/user/";
    public String checkRegDetails = "http://utaeventtracker-env.eba-nevd4ufs.us-east-1.elasticbeanstalk.com/tracker/checkRegistrationDetails";
    public String deRegisterUrl = "http://utaeventtracker-env.eba-nevd4ufs.us-east-1.elasticbeanstalk.com/tracker/cancelRegistration";
    public String userLogin = "http://utaeventtracker-env.eba-nevd4ufs.us-east-1.elasticbeanstalk.com/EventTracker/auth/Login";
    public String signupUser = "http://utaeventtracker-env.eba-nevd4ufs.us-east-1.elasticbeanstalk.com/EventTracker/auth/SignUp";
    public String adminUser1 = "admin";
    public String adminPassword1 = "johndoe";
    public UserDetails currentUser;
    public String signupJsonBody = "";
    public String currentUserName = "";
    public String currentUserPassword = "";
    public Integer eventId;
    public Integer[] selectedDepartmentPreference = new Integer[] {0,0,0,0,0,0,0,0};
    public List<Events> globalEventList = new ArrayList<Events>();
    public Events newlyAddedEvent = new Events();

    private static final SharedClass instance = new SharedClass();
    public static SharedClass getInstance() {
        return instance;
    }
    private SharedClass() {
    }

    public List<Events> getGlobalEventList() {
        return globalEventList;
    }

    public void setGlobalEventList(List<Events> globalEventList) {
        this.globalEventList = globalEventList;
    }
    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public String getEventUrl() {
        return eventUrl;
    }

    public void setEventUrl(String eventUrl) {
        this.eventUrl = eventUrl;
    }

    public String getDeptUrl() {
        return deptUrl;
    }

    public void setDeptUrl(String deptUrl) {
        this.deptUrl = deptUrl;
    }

    public String getAdminUser1() {
        return adminUser1;
    }

    public void setAdminUser1(String adminUser1) {
        this.adminUser1 = adminUser1;
    }

    public String getAdminPassword1() {
        return adminPassword1;
    }

    public void setAdminPassword1(String adminPassword1) {
        this.adminPassword1 = adminPassword1;
    }
}
