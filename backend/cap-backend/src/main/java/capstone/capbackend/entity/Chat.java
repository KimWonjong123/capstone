package capstone.capbackend.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Table(name = "chat_tbl")
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Chat {
    @Id
    private Long chatId;

    @Column
    private Long ownerId;

    @Column
    private String name;

    @CreatedDate
    private LocalDate insertTime;

    @LastModifiedDate
    private LocalDate updateTime;
}
