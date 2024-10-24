package capstone.capbackend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateChatResponseDTO {
    private Long chatId;
    private String chatName;

}
