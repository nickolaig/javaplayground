package playground.plugins;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import playground.logic.ActivityEntity;
import playground.logic.ElementEntity;
import playground.logic.UserEntity;
import playground.logic.exceptions.ElementAlreadyExistsException;
import playground.logic.exceptions.NoSuchElementID;

public interface PlaygroundPlugin {
		public Object invokeOperation( ElementEntity element,UserEntity user, ActivityEntity activity)
				throws Exception;

		

}
