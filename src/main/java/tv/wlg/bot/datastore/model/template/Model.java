package tv.wlg.bot.datastore.model.template;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Map;

public abstract class Model {
    @SuppressWarnings("unused")
    @MongoId @Getter private ObjectId id;
    protected Map<String, String> key;

    public abstract Map<String, String> asKey();
}
