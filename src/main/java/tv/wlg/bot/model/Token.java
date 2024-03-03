package tv.wlg.bot.model;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;
import tv.wlg.bot.model.template.Model;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Document
public class Token extends Model {

    public static final String user_id_field = "user_id";
    public static final String scope_field = "scope";
    public static final String refresh_token_field = "refresh_token";

    @MongoId
    private ObjectId id;

    @Field(user_id_field) private String userId;
    @Field(scope_field) private String scope;
    @Field(refresh_token_field) private String refreshToken;

    @Override
    public Map<String, String> getKey() {
        return new HashMap<>() {{
            put(user_id_field, getUserId());
        }};
    }
}
