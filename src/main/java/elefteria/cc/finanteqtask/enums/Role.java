package elefteria.cc.finanteqtask.enums;

public enum Role {
    EMPLOYEE, PROJECT_MANAGER, DEPARTMENT_MANAGER;

    public static Role getRoleByName(String name) {
        if(name == null) {
            return null;
        } else if(name.equals("PROJECT_MANAGER")) {
            return Role.PROJECT_MANAGER;
        } else if(name.equals("DEPARTMENT_MANAGER")) {
            return Role.DEPARTMENT_MANAGER;
        } else  if(name.equals("EMPLOYEE")){
            return Role.EMPLOYEE;
        } else {
            return null;
        }
    }
}
