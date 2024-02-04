package es.outlook.adriansrj.cv.api.registry;

import es.outlook.adriansrj.cv.api.interfaces.IDeyed;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;

/**
 * @author AdrianSR / 23/11/2023 / 3:48 p. m.
 */
public abstract class ConfigurationRegistryBase < T extends IDeyed >
		extends RegistryBase < T > implements ConfigurationRegistry < T > {
	
	protected abstract @NotNull File getFolder ( );
	
	@Override
	public void load ( ) {
		File folder = getFolder ( );
		
		if ( folder.exists ( ) ) {
			for ( File file : Objects.requireNonNull ( folder.listFiles (
					( dir , name ) -> name.toLowerCase ( ).endsWith ( ".yml" ) ) ) ) {
				try {
					T value = loadEntry ( file );
					
					if ( value != null ) {
						register ( value );
					}
				} catch ( Exception e ) {
					e.printStackTrace ( );
				}
			}
		}
	}
	
	protected abstract T loadEntry ( File file ) throws Exception;
	
	@Override
	public void reload ( ) {
		try {
			saveDefaults ( );
		} catch ( IOException e ) {
			e.printStackTrace ( );
		}
		
		entries.clear ( );
		load ( );
	}
	
	// -- defaults
	
	protected abstract @Nullable Set < T > getDefaults ( );
	
	@Override
	public void saveDefaults ( ) throws IOException {
		Set < T > defaults = getDefaults ( );
		File      folder   = getFolder ( );
		
		if ( ( defaults == null || defaults.isEmpty ( ) ) || folder.exists ( ) ) {
			return;
		}
		
		if ( !folder.mkdirs ( ) ) {
			throw new IllegalStateException (
					"couldn't create " + folder.getName ( ) + " folder" );
		}
		
		for ( T entry : defaults ) {
			try {
				saveDefault ( entry , new File (
						folder , entry.getId ( ) + ".yml" ) );
			} catch ( Exception e ) {
				e.printStackTrace ( );
			}
		}
	}
	
	protected void saveDefault ( T defaultEntry , File file ) throws Exception {
		if ( !file.exists ( ) && !file.createNewFile ( ) ) {
			throw new IllegalStateException (
					"couldn't create configuration" +
							" file for default entry (" + file.getName ( ) + ")" );
		}
		
		YamlConfiguration yaml = YamlConfiguration.loadConfiguration ( file );
		
		writeEntry ( defaultEntry , yaml );
		yaml.save ( file );
	}
	
	protected abstract void writeEntry ( @NotNull T entry , YamlConfiguration yaml );
}