package com.wipay.client.repository;

import java.util.List;
import java.util.Optional;

import com.wipay.client.model.entity.ClientEntity;
import com.wipay.dynamodb.entity.MainTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@Repository
@RequiredArgsConstructor
public class ClientRepository {

    private final DynamoDbTable<ClientEntity> clientTable;

    public ClientEntity save(ClientEntity client) {
        clientTable.putItem(client);
        return client;
    }

    public Optional<ClientEntity> findById(String id) {
        Key key = Key.builder()
                .partitionValue(ClientEntity.pkOf(id))
                .sortValue(ClientEntity.SK_DATA)
                .build();

        return Optional.ofNullable(clientTable.getItem(key));
    }

    public Optional<ClientEntity> findByEmail(String email) {
        QueryConditional condition = QueryConditional.keyEqualTo(
                key -> key.partitionValue(ClientEntity.gIndex2PkOf(email)));

        return clientTable.index(MainTable.GINDEX2)
                .query(condition)
                .stream()
                .flatMap(page -> page.items().stream())
                .findFirst();
    }

    public List<ClientEntity> findByName(String text) {
        QueryConditional condition = QueryConditional.keyEqualTo(
                key -> key.partitionValue(ClientEntity.ENTITY_TYPE));

        Expression filter = Expression.builder()
                .expression("contains(#keywords, :text)")
                .putExpressionName("#keywords", "keywordsSearch")
                .putExpressionValue(":text", AttributeValue.fromS(ClientEntity.keywordsOf(text)))
                .build();

        QueryEnhancedRequest request = QueryEnhancedRequest.builder()
                .queryConditional(condition)
                .filterExpression(filter)
                .build();

        return clientTable.index(MainTable.GINDEX3)
                .query(request)
                .stream()
                .flatMap(page -> page.items().stream())
                .toList();
    }
}
