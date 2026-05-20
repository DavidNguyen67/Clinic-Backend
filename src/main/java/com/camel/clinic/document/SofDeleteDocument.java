package com.camel.clinic.document;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Getter
@Setter
public class SofDeleteDocument extends BaseDocument {
    @Field("deleted_at")
    public Date deletedAt;

    public boolean isDeleted() {
        return deletedAt != null;
    }
}
