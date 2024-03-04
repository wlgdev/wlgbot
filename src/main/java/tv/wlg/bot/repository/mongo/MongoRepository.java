package tv.wlg.bot.repository.mongo;

import com.mongodb.client.result.DeleteResult;
import org.springframework.data.mongodb.core.FindAndReplaceOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.lang.NonNull;
import tv.wlg.bot.config.ApplicationContextProvider;
import tv.wlg.bot.model.template.Model;

import java.util.List;

@SuppressWarnings({"unchecked", "UnusedReturnValue", "unused"})
public interface MongoRepository<T extends Model> extends org.springframework.data.mongodb.repository.MongoRepository<T, String> {
    Logger log = LoggerFactory.getLogger(MongoRepository.class);

    MongoTemplate trustedMongoTemplate = (MongoTemplate) ApplicationContextProvider.getContext().getBean("trustedMongoTemplate");
    MongoTemplate remoteMongoTemplate = (MongoTemplate) ApplicationContextProvider.getContext().getBean("remoteMongoTemplate");

    /**
     * Saves data class in default MongoDB collection.
     * default collection name is the class name of data.
     * @param entity S
     * @return S
     */
    @Override @NonNull
    default <S extends T> S save(@NonNull S entity) {
        return updateCollection(entity, entity.getClass().getSimpleName().toLowerCase(), trustedMongoTemplate);
    }

    /**
     * Saves data class in second MongoDB repository with default collection.
     * default collection name is the class name of data.
     * @param entity S
     * @return S
     */
    default <S extends T> S saveRemote(S entity) {
        return updateCollection(entity, entity.getClass().getSimpleName().toLowerCase(), remoteMongoTemplate);
    }

    /**
     * Saves data class in MongoDB repository with specific collection name.
     * @param entity S
     * @param collection String
     * @return S
     */
    default <S extends T> S save(S entity, String collection) {
        return updateCollection(entity, collection, trustedMongoTemplate);
    }

    /**
     * Saves data class in second MongoDB repository with specific collection name.
     * @param entity S
     * @param collection String
     * @return S
     */
    default <S extends T> S saveRemote(S entity, String collection) {
        return updateCollection(entity, collection, remoteMongoTemplate);
    }

    /**
     * find all records which applies Query.
     * @param query which data to get
     * @param collection from which collection
     * @return List of elements
     */
    default List<T> find(Query query, String collection) {
        return find(query, collection, trustedMongoTemplate);
    }

    /**
     * find all records which applies Query.
     * @param query which data to get
     * @param collection from which collection
     * @return List of elements
     */
    default List<T> findRemote(Query query, String collection) {
        return find(query, collection, remoteMongoTemplate);
    }

    /**
     * Deletes entity element from standard MongoDB collection
     * @param entity element to delete
     */
    @Override
    default void delete(@NonNull T entity) {
        deleteRecord(entity, entity.getClass().getSimpleName().toLowerCase(), trustedMongoTemplate);
    }

    /**
     * Deletes entity element from standard MongoDB collection
     * @param entity element to delete
     */
    default void deleteRemote(@NonNull T entity) {
        deleteRecord(entity, entity.getClass().getSimpleName().toLowerCase(), remoteMongoTemplate);
    }

    /**
     * Deletes entity element from custom MongoDB collection
     * @param entity element to delete
     */
    default DeleteResult delete(T entity, String collection) {
        return deleteRecord(entity, collection, trustedMongoTemplate);
    }

    /**
     * Deletes entity element from custom MongoDB collection
     * @param entity element to delete
     */
    default DeleteResult deleteRemote(T entity, String collection) {
        return deleteRecord(entity, collection, remoteMongoTemplate);
    }

    //------------------------------------------------------------------------------------------------------------------
    // private methods to do exact action required to perform
    //------------------------------------------------------------------------------------------------------------------
    private <S extends T> S updateCollection(S entity, String collection, MongoTemplate mongoTemplate) {
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

    private Query createQuery(T entity) {
        Criteria criteria = new Criteria();
        entity.asKey().forEach((key, value) -> criteria.and(key).is(value));

        return new Query().addCriteria(criteria);
    }
}