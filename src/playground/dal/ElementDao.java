package playground.dal;

import java.util.Date;


import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import playground.logic.ElementEntity;
import playground.logic.ElementKey;

public interface ElementDao extends PagingAndSortingRepository <ElementEntity,ElementKey>{

	public Page<ElementEntity> findAllByXBetweenAndYBetweenAndExpirationDateAfterOrExpirationDateIsNull(
			@Param("fromX") double x0,
			@Param("toX") double x1,
			@Param("fromY") double y0,
			@Param("toY") double y1,
			@Param("currentDate") Date currentDate,
			Pageable pageable);
	public Page<ElementEntity> findAllByExpirationDateAfterOrExpirationDateIsNull(@Param("currentDate") Date currentDate
			,Pageable pageable);
	
	public Page<ElementEntity> findAllByXBetweenAndYBetween(@Param("fromX") double x0,@Param("toX") double x1,
			@Param("fromY") double y0,@Param("toY") double y1,
			Pageable pageable);
	
	public Page<ElementEntity> findAllByNameLikeAndExpirationDateAfterOrExpirationDateIsNull( 
			@Param("value") String value,
			@Param("currentDate")Date currentDate,
			Pageable pageable);
	public Page<ElementEntity> findAllByTypeLikeAndExpirationDateAfterOrExpirationDateIsNull( 
			@Param("value") String value,
			@Param("currentDate")Date currentDate,
			Pageable pageable);
	public Page<ElementEntity> findAllByNameLike( 
			@Param("value") String value,
			Pageable pageable);
	public Page<ElementEntity> findAllByTypeLike( 
			@Param("value") String value,
			Pageable pageable);

}
