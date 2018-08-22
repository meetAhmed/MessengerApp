package app.messenger.gosling.james.messengerapp.Model;

import java.util.Comparator;

public class user_model {

    String name;
    String profileAddress;
    String key;
    String email;
    String password;
    Long joiningDate;

    public user_model(String name, String email, String password, String key) {
        this.name = name;
        this.profileAddress = "none";
        this.key = key;
        this.email = email;
        this.password = password;
        this.joiningDate = System.currentTimeMillis();
    }

    public user_model() {
    }

    public Long getJoiningDate() {
        return joiningDate;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getProfileAddress() {
        return profileAddress;
    }

    public String getKey() {
        return key;
    }

    public static Comparator<user_model> COMPARE_BY_NAME = new Comparator<user_model>() {
        @Override
        public int compare(user_model one, user_model other) {
            return one.getName().compareTo(other.getName());
        }
    };
}
