package capstone.capbackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity(name = "user_tbl")
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column
    @NotNull
    private String email;

    @Column
    @NotNull
    private String oauthType;

    @Column
    private String oauthSub;
}
