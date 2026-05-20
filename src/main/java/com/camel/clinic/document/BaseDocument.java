package com.camel.clinic.document;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Getter
@Setter
public abstract class BaseDocument {
    @Id
    public String id;

    @CreatedDate
    @Field("created_at")
    public Date createdAt;

    @LastModifiedDate
    @Field("updated_at")
    public Date updatedAt;
}