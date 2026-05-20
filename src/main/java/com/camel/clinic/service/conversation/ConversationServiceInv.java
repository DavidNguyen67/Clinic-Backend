package com.camel.clinic.service.conversation;

import com.camel.clinic.document.ConversationDocument;
import com.camel.clinic.repository.ConversationRepository;
import com.camel.clinic.service.BaseMongoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ConversationServiceInv extends BaseMongoService<ConversationDocument, ConversationRepository> {
    protected final MongoTemplate mongoTemplate;
    protected final Class<ConversationDocument> documentClass;

    public ConversationServiceInv(ConversationRepository repository, MongoTemplate mongoTemplate, Class<ConversationDocument> documentClass) {
        super(ConversationDocument::new, repository, mongoTemplate, documentClass);
        this.mongoTemplate = mongoTemplate;
        this.documentClass = documentClass;
    }
}