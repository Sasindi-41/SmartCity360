package lk.sasindi.smartcity360.model;

import java.sql.Timestamp;

public class UserProfile {

    private final int id;
    private final String fullName;
    private final String email;
    private final String phoneNumber;
    private final String passwordHash;
    private final int roleId;
    private final String roleName;
    private final Integer cityId;
    private final String cityName;
    private final Timestamp createdAt;

    public UserProfile(int id, String fullName, String email,
                       String phoneNumber, String passwordHash,
                       int roleId, String roleName, Integer cityId,
                       String cityName, Timestamp createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.passwordHash = passwordHash;
        this.roleId = roleId;
        this.roleName = roleName;
        this.cityId = cityId;
        this.cityName = cityName;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getPasswordHash() { return passwordHash; }
    public int getRoleId() { return roleId; }
    public String getRoleName() { return roleName; }
    public Integer getCityId() { return cityId; }
    public String getCityName() { return cityName; }
    public Timestamp getCreatedAt() { return createdAt; }
}
