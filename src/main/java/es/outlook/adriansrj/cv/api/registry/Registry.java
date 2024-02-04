package es.outlook.adriansrj.cv.api.registry;

import es.outlook.adriansrj.cv.api.interfaces.IDeyed;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @author AdrianSR / 23/11/2023 / 3:46 p. m.
 */
public interface Registry < T extends IDeyed > extends Iterable < T > {
	
	@Nullable
	T get ( @NotNull String id );
	
	@NotNull
	Collection < T > getEntries ( );
	
	@NotNull
	Collection < String > getIds ( );
	
	void register ( @NotNull T entry );
	
	void unregister ( @NotNull String id );
	
	default void load ( ) {
		// nothing to do by default
	}
}