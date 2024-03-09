package tv.wlg.bot.datastore.repository;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import tv.wlg.bot.datastore.model.RefreshToken;
import tv.wlg.bot.datastore.repository.mongo.MongoRepository;

import java.util.List;

@Repository
public interface RefreshTokenRepository extends MongoRepository<RefreshToken> {
    @Query("{ 'user_id' : ?0 }")
    List<RefreshToken> findTokensByUserId(String userId);

    @Query("{ 'user_id' : ?0 }")
    RefreshToken findFirstByUserId(String userId);

    default RefreshToken findByUserId(String userId) {
        return find(
                new org.springframework.data.mongodb.core.query.Query().addCriteria(
                        Criteria.where(RefreshToken.user_id_field).is(userId)
                ),
                classToCamelCase(RefreshToken.class.getSimpleName())
        ).getFirst();
    }
}
