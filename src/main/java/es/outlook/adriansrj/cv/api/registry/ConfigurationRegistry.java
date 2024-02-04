package es.outlook.adriansrj.cv.api.registry;

import es.outlook.adriansrj.cv.api.interfaces.IDeyed;

import java.io.IOException;

/**
 * @author AdrianSR / 23/11/2023 / 3:46 p. m.
 */
public interface ConfigurationRegistry < T extends IDeyed > extends Registry < T > {
	
	void reload ( );
	
	void saveDefaults ( ) throws IOException;
}