package com.example.demo.utility;

import com.example.demo.HttpAuthRegisterRequest;
import com.example.demo.domain.User;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserMapper {
    UserMapper INSTANCE= Mappers.getMapper(UserMapper.class);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "publicId", ignore = true),
            @Mapping(target = "joinDate", ignore = true),
            @Mapping(target = "lastLoginDate", ignore = true),
            @Mapping(target = "active", ignore = true),
            @Mapping(target = "notLocked", ignore = true),
            @Mapping(target = "roles", ignore = true),
            @Mapping(target = "authorities", ignore = true),
    })
    User HttpAuthRegisterRequestToUser(HttpAuthRegisterRequest request);
}
