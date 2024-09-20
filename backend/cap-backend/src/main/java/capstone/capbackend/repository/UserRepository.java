package capstone.capbackend.repository;

import capstone.capbackend.entity.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends R2dbcRepository<User, Long> {
    Mono<User> findByUserId(String userId);

    /**
     * Generate Random Nickname + Random Number
     * @param prefix Generated Random Nickname
     * @return Nickname + Number e.g., 귀여운고릴라13
     */
    @Query("SELECT CONCAT(:prefix, COALESCE(MAX(CAST(REGEXP_REPLACE(u.nickname, '[^0-9]', '', 'g') AS INTEGER)), 0) + 1) " +
            "FROM user_tbl u " +
            "WHERE u.nickname LIKE CONCAT(:prefix, '%')")
    Mono<String> getUniqueNickname(@Param("prefix") String prefix);
}
