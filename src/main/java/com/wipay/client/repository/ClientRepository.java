package com.wipay.client.repository;

import java.util.List;
import java.util.Optional;

import com.wipay.client.model.EntityStatus;
import com.wipay.client.model.entity.ClientEntity;
import com.wipay.dynamodb.entity.MainTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@Repository
@RequiredArgsConstructor
public class ClientRepository {

    /** Solo clientes activos: los borrados (INACTIVE) no aparecen en ninguna consulta. */
    private static final Expression ACTIVE_FILTER = Expression.builder()
            .expression("#status = :active")
            .putExpressionName("#status", "status")
            .putExpressionValue(":active", AttributeValue.fromS(EntityStatus.ACTIVE))
            .build();

    private final DynamoDbTable<ClientEntity> clientTable;

    public ClientEntity save(ClientEntity client) {
        clientTable.putItem(client);
        return client;
    }

    /** GetItem no admite filtros: el status se comprueba después de leer. */
    public Optional<ClientEntity> findById(String id) {
        Key key = Key.builder()
                .partitionValue(ClientEntity.pkOf(id))
                .sortValue(ClientEntity.SK_DATA)
                .build();

        return Optional.ofNullable(clientTable.getItem(key))
                .filter(client -> EntityStatus.ACTIVE.equals(client.getStatus()));
    }

    /** Un cliente borrado no reserva su email: se puede volver a usar. */
    public Optional<ClientEntity> findByEmail(String email) {
        QueryConditional condition = QueryConditional.keyEqualTo(
                key -> key.partitionValue(ClientEntity.gIndex2PkOf(email)));

        QueryEnhancedRequest request = QueryEnhancedRequest.builder()
                .queryConditional(condition)
                .filterExpression(ACTIVE_FILTER)
                .build();

        return clientTable.index(MainTable.GINDEX2)
                .query(request)
                .stream()
                .flatMap(page -> page.items().stream())
                .findFirst();
    }

    public List<ClientEntity> findAll() {
        return queryClients(ACTIVE_FILTER);
    }

    public List<ClientEntity> findByName(String text) {
        Expression nameFilter = Expression.builder()
                .expression("contains(#keywords, :text)")
                .putExpressionName("#keywords", "keywordsSearch")
                .putExpressionValue(":text", AttributeValue.fromS(ClientEntity.keywordsOf(text)))
                .build();

        return queryClients(Expression.join(nameFilter, ACTIVE_FILTER, " AND "));
    }

    /** Query sobre gIndex3 (ENTITY#CLIENT): lee solo clientes, no los merchants ni el resto de la tabla. */
    private List<ClientEntity> queryClients(Expression filter) {
        QueryConditional condition = QueryConditional.keyEqualTo(
                key -> key.partitionValue(ClientEntity.ENTITY_TYPE));

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
