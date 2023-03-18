package com.cleanup.config;

import com.cleanup.model.User;
import com.cleanup.model.dto.UserRequest;
import com.cleanup.model.dto.UserResponse;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Mappers {

    @Bean
    public ModelMapper userMapper(){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        TypeMap<UserRequest, User> requestToModel = modelMapper.createTypeMap(UserRequest.class, User.class);
        requestToModel.addMappings(new PropertyMap<>() {
            @Override
            protected void configure() {
                map().setSubscribed(source.isSubscribe());
            }
        });

        TypeMap<User, UserResponse> modelToResponse = modelMapper.createTypeMap(User.class, UserResponse.class);

        return modelMapper;
    }
    //
}
