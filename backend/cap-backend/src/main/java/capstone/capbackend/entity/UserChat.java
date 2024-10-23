package capstone.capbackend.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "user_chat_tbl")
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserChat {
    @Id
    private Long id;

    @Column
    private Long userId;

    @Column
    private Long chatId;

}
