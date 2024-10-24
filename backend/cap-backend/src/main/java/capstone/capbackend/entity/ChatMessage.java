package capstone.capbackend.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "chat_message_tbl")
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    @Id
    private Long id;

    @Column
    private Long userChatId;

    @Column
    private String message;

    @CreatedDate
    private String insertTime;
}