package lk.sasindi.smartcity360.model;

public class User {

    private int id;
    private String fullName;
    private String email;
    private String passwordHash;
    private int roleId;
    private int statusId;

    public User(
            int id,
            String fullName,
            String email,
            String passwordHash,
            int roleId,
            int statusId
    ) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.roleId = roleId;
        this.statusId = statusId;
    }

    public int getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public int getRoleId() {
        return roleId;
    }

    public int getStatusId() {
        return statusId;
    }
}