package es.outlook.adriansrj.cv.api.registry.types;

import es.outlook.adriansrj.cv.api.item.ItemConfiguration;
import es.outlook.adriansrj.cv.api.registry.ConfigurationRegistryBase;
import es.outlook.adriansrj.cv.api.util.Constants;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author AdrianSR / 31/1/2024 / 3:27 p. m.
 */
public final class ItemConfigurationRegistry extends ConfigurationRegistryBase < ItemConfiguration > {
	
	private static final Set < ItemConfiguration > DEFAULTS = new HashSet <> ( );
	
	static {
		DEFAULTS.add ( new ItemConfiguration (
				"fuel" ,
				Material.PLAYER_HEAD ,
				"&4&lFuel" ,
				Arrays.asList (
						"" ,
						"&7Fuel content: &l%f" ,
						""
				) ,
				null ,
				"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly" +
						"90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGI4OWRl" +
						"NDhhOWI4N2NmZDA3YzcwNGYyYmU1ZTVhOGNjNDVlODA3OWQzOG" +
						"ZhYWVkZjEzYjE1ZDE1YTEwYTcwYyJ9fX0=" ,
				new ItemConfiguration.FuelAction ( 40.0F )
		) );
	}
	
	@Override
	protected @NotNull File getFolder ( ) {
		return Constants.Files.ITEMS_FOLDER;
	}
	
	@Override
	protected ItemConfiguration loadEntry ( File file ) throws Exception {
		return ItemConfiguration.load ( YamlConfiguration.loadConfiguration ( file ) );
	}
	
	@Override
	protected Set < ItemConfiguration > getDefaults ( ) {
		return DEFAULTS;
	}
	
	@Override
	protected void writeEntry ( @NotNull ItemConfiguration entry , YamlConfiguration yaml ) {
		entry.write ( yaml );
	}
}