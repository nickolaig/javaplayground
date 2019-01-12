package playground.dal;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import playground.logic.ActivityEntity;
import playground.logic.ActivityKey;

@RepositoryRestResource
public interface ActivityDao extends PagingAndSortingRepository <ActivityEntity,ActivityKey> {


	public Page<ActivityEntity> findAllByType(@Param("type") String type, Pageable pageable);
	
	public Page<ActivityEntity> findAllByTypeAndElementId(@Param("type")String type,
			@Param("elementId") String elementId, Pageable pageable);

}
