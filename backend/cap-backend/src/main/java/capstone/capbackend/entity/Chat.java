package capstone.capbackend.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table(name = "chat_tbl")
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Chat {
    @Id
    private Long id;

    @Column
    private Long ownerId;

    @Column
    private String ownerName;

    @Column
    private String name;

    @CreatedDate
    private LocalDateTime insertTime;

    @LastModifiedDate
    @Setter
    private LocalDateTime lastChatTime;
}
