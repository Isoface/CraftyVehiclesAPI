package es.outlook.adriansrj.cv.api.interfaces;

import es.outlook.adriansrj.cv.api.vehicle.controller.VehicleController;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @author AdrianSR / 28/12/2023 / 5:31 p. m.
 */
public interface ControllerFactoryProvider {
	
	/**
	 * Return the {@link VehicleController.Factory} instances responsible
	 * for creating the instances of your custom controllers.
	 * <br>
	 * @return factory objects responsible for creating your custom controller instances.
	 */
	@NotNull Collection < VehicleController.Factory > create ( );
}