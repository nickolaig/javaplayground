package playground.dal;

import org.springframework.data.repository.CrudRepository;

import playground.jpaLogic.IdGenerator;

public interface IdGeneratorDao extends CrudRepository<IdGenerator, Long>{

}
