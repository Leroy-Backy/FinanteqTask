package elefteria.cc.finanteqtask.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Data
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Project implements SmallEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @NotNull(message = "Code filed is required in Project")
    @Size(min = 2, message = "Minimal Project code length is 2 characters")
    private String code;

    private String name;

    public Project(String code, String name) {
        this.code = code;
        this.name = name;
    }
}

