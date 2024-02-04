package es.outlook.adriansrj.cv.api.handler;

/**
 * @author AdrianSR / 23/12/2023 / 1:11 p. m.
 */
public interface PluginHandler {
	
	/**
	 * Called whenever the plugin disables.
	 */
	default void onPluginDisable ( ) {
		// nothing to do by default.
	}
}