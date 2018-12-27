package playground.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import playground.logic.ActivityEntity;
@Repository
public interface ActivityDao extends JpaRepository <ActivityEntity,String> {

}
