package me.ifmo.backend.mappers;

import me.ifmo.backend.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    default Long toId(User user) {
        return user == null ? null : user.getId();
    }

    default User fromId(Long id) {
        if (id == null) return null;
        User u = new User();
        u.setId(id);
        return u;
    }
}