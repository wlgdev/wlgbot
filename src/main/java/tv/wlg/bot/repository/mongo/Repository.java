package tv.wlg.bot.repository.mongo;

import com.mongodb.client.result.DeleteResult;
import org.springframework.data.mongodb.core.FindAndReplaceOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tv.wlg.bot.config.ApplicationContextProvider;
import tv.wlg.bot.model.template.Model;

import java.util.List;

@SuppressWarnings({"unchecked", "UnusedReturnValue", "unused"})
public interface Repository<T extends Model> extends MongoRepository<T, String> {
    Logger log = LoggerFactory.getLogger(Repository.class);

    MongoTemplate trustedMongoTemplate = (MongoTemplate) ApplicationContextProvider.getContext().getBean("trustedMongoTemplate");
    MongoTemplate remoteMongoTemplate = (MongoTemplate) ApplicationContextProvider.getContext().getBean("remoteMongoTemplate");

    @Override
    default <S extends T> S save(S entity) {
        return (S) updateCollection(entity, entity.getClass().getSimpleName().toLowerCase(), trustedMongoTemplate);
    }

    default <S extends T> S saveRemote(S entity) {
        return (S) updateCollection(entity, entity.getClass().getSimpleName().toLowerCase(), remoteMongoTemplate);
    }

    default T save(T entity, String collection) {
        return updateCollection(entity, collection, trustedMongoTemplate);
    }

    default T saveRemote(T entity, String collection) {
        return updateCollection(entity, collection, remoteMongoTemplate);
    }

    default List<T> find(Query query, String collection) {
        return find(query, collection, trustedMongoTemplate);
    }

    default List<T> findRemote(Query query, String collection) {
        return find(query, collection, remoteMongoTemplate);
    }

    default DeleteResult delete(T entity, String collection) {
        return deleteRecord(entity, collection, trustedMongoTemplate);
    }

    default DeleteResult deleteRemote(T entity, String collection) {
        return deleteRecord(entity, collection, remoteMongoTemplate);
    }

    private T updateCollection(T entity, String collection, MongoTemplate mongoTemplate) {
        Query query = createQuery(entity);
        if (!find(query, collection, mongoTemplate).isEmpty()) {
            log.info("update {} in   {}", entity, collection);
            return mongoTemplate.findAndReplace(query, entity, FindAndReplaceOptions.options().returnNew(), collection);
        }

        log.info("save   {} to   {}", entity, collection);
        return mongoTemplate.save(entity, collection);
    }

    private List<T> find(Query query, String collection, MongoTemplate mongoTemplate) {
        return (List<T>) mongoTemplate.find(query, Model.class, collection);
    }

    private DeleteResult deleteRecord(T entity, String collection, MongoTemplate mongoTemplate) {
        Query query = createQuery(entity);

        log.info("delete {} from {}", entity, collection);
        return mongoTemplate.remove(query, collection);
    }

    default Query createQuery(T entity) {
        Criteria criteria = new Criteria();
        entity.getKey().forEach((key, value) -> criteria.and(key).is(value));

        return new Query().addCriteria(criteria);
    }
}