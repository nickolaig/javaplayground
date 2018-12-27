package playground.dal;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import playground.logic.ActivityEntity;

@RepositoryRestResource
public interface ActivityDao extends PagingAndSortingRepository <ActivityEntity,String> {

	@Query("SELECT a FROM ActivityEntity a WHERE a.type = :type and a.elementId = :elementId")
	public List<ActivityEntity> getAllPostMessagesByElementId(@Param("elementId") String elementId, @Param("type") String type, Pageable pageable);
}
