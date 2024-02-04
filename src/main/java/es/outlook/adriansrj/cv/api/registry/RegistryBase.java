package es.outlook.adriansrj.cv.api.registry;

import es.outlook.adriansrj.cv.api.interfaces.IDeyed;
import gnu.trove.map.hash.THashMap;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * @author AdrianSR / 23/11/2023 / 3:48 p. m.
 */
public abstract class RegistryBase < T extends IDeyed > implements Registry < T > {
	
	protected final Map < String, T > entries = new THashMap <> ( );
	
	@Override
	public @Nullable T get ( @NotNull String id ) {
		return entries.get ( id.toLowerCase ( ) );
	}
	
	@Override
	public @NotNull Collection < T > getEntries ( ) {
		return entries.values ( );
	}
	
	@Override
	public @NotNull Collection < String > getIds ( ) {
		return entries.keySet ( );
	}
	
	@Override
	public void register ( @NotNull T entry ) {
		entries.put ( entry.getId ( ) , entry );
	}
	
	@Override
	public void unregister ( @NotNull String id ) {
		entries.remove ( id.toLowerCase ( ) );
	}
	
	@NotNull
	@Override
	public Iterator < T > iterator ( ) {
		return entries.values ( ).iterator ( );
	}
}