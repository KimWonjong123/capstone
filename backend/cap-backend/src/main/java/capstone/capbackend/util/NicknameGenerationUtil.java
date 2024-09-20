package capstone.capbackend.util;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class NicknameGenerationUtil {
    private final List<String> prefix = List.of("파이썬하는", "마라탕먹는", "해리포터광", "지갑잃어버린", "개강한");
    private final List<String> postfix = List.of("해리포터", "헤르미온느", "로니", "스테판커리", "코코");
    private final Random random = new Random();

    public String generateRandomNickname() {
         String prefixStr = prefix.get(random.nextInt(prefix.size()));
         String postfixStr = postfix.get(random.nextInt(postfix.size()));
         return prefixStr + postfixStr;
    }

}
