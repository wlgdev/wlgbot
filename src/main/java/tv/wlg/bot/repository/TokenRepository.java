package tv.wlg.bot.repository;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import tv.wlg.bot.model.Token;
import tv.wlg.bot.repository.mongo.MongoRepository;

import java.util.List;

@Repository
public interface TokenRepository extends MongoRepository<Token> {
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
