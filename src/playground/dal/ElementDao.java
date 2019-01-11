package playground.dal;

import java.util.Date;
import java.util.Optional;


import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import playground.logic.ElementEntity;
import playground.logic.ElementKey;

public interface ElementDao extends PagingAndSortingRepository <ElementEntity,ElementKey>{

	public Page<ElementEntity> findAllByExpirationDateAfterOrExpirationDateIsNull(@Param("currentDate") Date currentDate
			,Pageable pageable);

}
