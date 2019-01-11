package playground.dal;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;

import playground.logic.ElementEntity;
import playground.logic.ElementKey;

public interface ElementDao extends PagingAndSortingRepository <ElementEntity,ElementKey>{

}
