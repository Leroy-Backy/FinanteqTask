package elefteria.cc.finanteqtask.entity;

import elefteria.cc.finanteqtask.enums.Gender;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
@Data
@ToString
@EqualsAndHashCode
public class PersonalData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "phone filed is required in PersonalData")
    @Size(min = 7, max = 12, message = "Phone number length must be between 7 and 11 characters")
    private String phone;

    @NotNull(message = "email filed is required in PersonalData")
    @Email(message = "invalid email address")
    private String email;

    private String city;

    private LocalDate birthDate;

    private int salary;

    @Enumerated(value = EnumType.STRING)
    @NotNull(message = "sex filed is required in PersonalData")
    private Gender gender;

    public PersonalData(String phone, String email, String city, LocalDate birthDate, Gender gender, int salary) {
        this.phone = phone;
        this.email = email;
        this.city = city;
        this.birthDate = birthDate;
        this.gender = gender;
        this.salary = salary;
    }
}
