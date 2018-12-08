package playground.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import playground.logic.UserEntity;
import playground.logic.UserKey;

@Repository
public interface UserDao extends JpaRepository<UserEntity, UserKey> {
}
