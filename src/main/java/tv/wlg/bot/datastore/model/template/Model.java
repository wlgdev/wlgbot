package tv.wlg.bot.datastore.model.template;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Map;

@Getter
public abstract class Model {
    @SuppressWarnings("unused")
    @MongoId private ObjectId id;

    public abstract Map<String, String> asKey();
}
