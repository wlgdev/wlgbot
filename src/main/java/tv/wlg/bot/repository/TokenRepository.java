package tv.wlg.bot.repository;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.Query;
import tv.wlg.bot.model.Token;
import tv.wlg.bot.repository.mongo.Repository;

import java.util.List;

@org.springframework.stereotype.Repository
public interface TokenRepository extends Repository<Token> {
    @Query("{ 'user_id' : ?0 }")
    List<Token> findTokensByUserId(String userId);

    @Query("{ 'user_id' : ?0 }")
    Token findFirstByUserId(String userId);

    default Token findByUserId(String userId) {
        return find(
                new org.springframework.data.mongodb.core.query.Query().addCriteria(
                        Criteria.where(Token.user_id_field).is(userId)
                ),
                Token.class.getSimpleName().toLowerCase()
        ).getFirst();
    }
}
