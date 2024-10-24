package capstone.capbackend.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

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
    private String userName;

    @Column
    private Long chatId;

    @CreatedDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime insertTime;

    @Column
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastChatTime;

}
