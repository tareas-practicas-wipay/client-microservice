package com.wipay.client.mappers;

import com.wipay.client.model.ClientModel;
import com.wipay.client.model.entity.ClientEntity;
import com.wipay.client.model.request.ClientRequest;
import com.wipay.client.model.response.ClientResponse;
import com.wipay.client.model.response.SimpleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/** Este mapper deberá realizar las siguientes conversiones:
 * ----------------------------------------------------------------------------------------
 *                         (crear)                          (guardar)
 * [ CONTROLADOR ] === Request->Model ==> [ SERVICIO ] === Model->Entity ==> [ REPOSITORIO ]
 * ----------------------------------------------------------------------------------------
 * [ CONTROLADOR ] <= Response<-Model === [ SERVICIO ] <== Model<-Entity === [ REPOSITORIO ]
 *                <=SimpleResponse<-Model=                   (leer)
 *               (devolver completo/simple)
 * ----------------------------------------------------------------------------------------
 */

// componentModel = "spring -> @Component
// unmappedTargetPolicy = ReportingPolicy.ERROR -> Si un campo se queda sin recibir ningún valor, no solo da un warning, la compilación falla
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ClientMapper {

    /// ENTRADA -> MODELO GENÉRICO (controlador --creación-> servicio)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdDate", ignore = true) // id, status y createdDate los pone el servicio
    ClientModel requestToModel(ClientRequest request);

    /// MODELO GENÉRICO -> ENTIDAD (servicio --guardado-> repositorio)
    @Mapping(target = "pk", expression = "java(ClientEntity.pkOf(model.getId()))")
    @Mapping(target = "sk", constant = ClientEntity.SK_DATA)
    @Mapping(target = "GIndex2Pk", expression = "java(ClientEntity.gIndex2PkOf(model.getEmail()))")
    @Mapping(target = "GIndex3Pk", constant = ClientEntity.ENTITY_TYPE)
    @Mapping(target = "keywordsSearch", expression = "java(ClientEntity.keywordsOf(model.getName()))")
    ClientEntity modelToEntity(ClientModel model);

    /// ENTIDAD -> MODELO GENÉRICO (repositorio --lectura-> servicio)
    // Los campos claves se descartan
    ClientModel entityToModel(ClientEntity entity);
    List<ClientModel> entitiesToModels(List<ClientEntity> entities);

    /// MODELO GENÉRICO -> SALIDA (servicio --devolver-completo-> controlador)
    // Los campos claves se descartan
    ClientResponse modelToResponse(ClientModel model);
    List<ClientResponse> modelsToResponses(List<ClientModel> models);

    /// MODELO GENÉRICO -> SALIDA SIMPLE (servicio --devolver-simple-> controlador)
    // Los campos claves se descartan
    SimpleResponse modelToSimpleResponse(ClientModel model);
    List<SimpleResponse> modelsToSimpleResponses(List<ClientModel> models);
}
