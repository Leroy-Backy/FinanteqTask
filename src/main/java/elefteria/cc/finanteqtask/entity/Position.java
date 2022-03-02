package elefteria.cc.finanteqtask.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Data
@NoArgsConstructor
public class Position implements SmallEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @NotNull(message = "Code filed is required in Position")
    @Size(min = 2, message = "Minimal position code length is 2 characters")
    private String code;

    private String name;

    public Position(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
