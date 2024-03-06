package tv.wlg.bot.datastore.repository.mongo;

import com.mongodb.client.result.DeleteResult;
import org.springframework.data.mongodb.core.FindAndReplaceOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.lang.NonNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tv.wlg.bot.config.ApplicationContextProvider;
import tv.wlg.bot.datastore.model.template.Model;

import java.lang.reflect.Field;
import java.util.List;

@SuppressWarnings("unused")
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
        return updateCollection(entity, classToCamelCase(entity.getClass().getSimpleName()), trustedMongoTemplate);
    }

    /**
     * Saves data class in second MongoDB repository with default collection.
     * default collection name is the class name of data.
     * @param entity S
     * @return S
     */
    default <S extends T> S saveRemote(S entity) {
        return updateCollection(entity, classToCamelCase(entity.getClass().getSimpleName()), remoteMongoTemplate);
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
        deleteRecord(entity, classToCamelCase(entity.getClass().getSimpleName()), trustedMongoTemplate);
    }

    /**
     * Deletes entity element from standard MongoDB collection
     * @param entity element to delete
     */
    default void deleteRemote(@NonNull T entity) {
        deleteRecord(entity, classToCamelCase(entity.getClass().getSimpleName()), remoteMongoTemplate);
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
        List<T> elements = find(query, collection, mongoTemplate);

        //if elements with key exists - we modify collection
        if (!elements.isEmpty()) {
            if (elements.size() > 1) {
                log.error("update(error) {} in   {} - key has more then 1 element: {}", entity.getId(), collection, entity.asKey());
                return entity;
            }

            if (entity.getId() != null && !entity.getId().equals(elements.getFirst().getId())) {
                log.error("update(error) {} in   {} - key exists: {}", entity.getId(), collection, entity.asKey());
                return entity;
            }

            log.info("update {} in   {}", entity, collection);
            S updatedEntity = mongoTemplate.findAndReplace(query, entity, FindAndReplaceOptions.options().returnNew(), collection);
            if (updatedEntity != null) {
                updateEntityID(entity, updatedEntity);
            }

            return updatedEntity;
        }

        //if elements with key not exists but id presented - we do modify existing record, so it's update
        if (entity.getId() != null) {
            log.info("update {} in   {}", entity, collection);
            return mongoTemplate.save(entity, collection);
        }

        log.info("save   {} to   {}", entity, collection);
        return mongoTemplate.save(entity, collection);
    }

    @SuppressWarnings("unchecked")
    private List<T> find(Query query, String collection, MongoTemplate mongoTemplate) {
        return (List<T>) mongoTemplate.find(query, Model.class, collection);
    }

    private DeleteResult deleteRecord(T entity, String collection, MongoTemplate mongoTemplate) {
        Query query = createQuery(entity);

        log.info("delete {} from {}", entity, collection);
        return mongoTemplate.remove(query, collection);
    }

    private <S extends T> void updateEntityID(S entity, S updatedEntity) {
        try {
            Field objectId = entity.getClass().getSuperclass().getDeclaredField("id");
            objectId.setAccessible(true);
            objectId.set(entity, updatedEntity.getId());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    default String classToCamelCase(String className) {
        return className.substring(0, 1).toLowerCase() + className.substring(1);
    }

    private Query createQuery(T entity) {
        Criteria criteria = new Criteria();
        entity.asKey().forEach((key, value) -> criteria.and(key).is(value));

        return new Query().addCriteria(criteria);
    }
}