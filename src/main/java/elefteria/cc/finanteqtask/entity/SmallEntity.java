package elefteria.cc.finanteqtask.entity;

import elefteria.cc.finanteqtask.enums.EntityType;

public interface SmallEntity {
    Long getId();
    String getCode();
    String getName();
    void setCode(String code);
    void setName(String name);
}
