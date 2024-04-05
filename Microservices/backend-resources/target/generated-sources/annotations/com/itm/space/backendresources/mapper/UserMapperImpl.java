package com.itm.space.backendresources.mapper;

import com.itm.space.backendresources.api.response.UserResponse;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-04-05T09:18:23+0300",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.10 (BellSoft)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserResponse userRepresentationToUserResponse(UserRepresentation userRepresentation, List<RoleRepresentation> roleList, List<GroupRepresentation> groupList) {
        if ( userRepresentation == null && roleList == null && groupList == null ) {
            return null;
        }

        String firstName = null;
        String lastName = null;
        String email = null;
        if ( userRepresentation != null ) {
            firstName = userRepresentation.getFirstName();
            lastName = userRepresentation.getLastName();
            email = userRepresentation.getEmail();
        }
        List<String> roles = null;
        roles = mapRoleRepresentationToString( roleList );
        List<String> groups = null;
        groups = mapGroupRepresentationToString( groupList );

        UserResponse userResponse = new UserResponse( firstName, lastName, email, roles, groups );

        return userResponse;
    }
}
