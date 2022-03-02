package elefteria.cc.finanteqtask.enums;

public enum Gender {
    MALE, FEMALE;

    public static Gender getGenderByName(String name) {
        if(name == null) {
            return null;
        } else if(name.equals("MALE")) {
            return Gender.MALE;
        } else if(name.equals("FEMALE")) {
            return Gender.FEMALE;
        } else {
            return null;
        }
    }
}
