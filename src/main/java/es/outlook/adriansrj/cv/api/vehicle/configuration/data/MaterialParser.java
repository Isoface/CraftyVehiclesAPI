package es.outlook.adriansrj.cv.api.vehicle.configuration.data;

import es.outlook.adriansrj.cv.api.util.Constants;
import es.outlook.adriansrj.cv.api.util.reflection.EnumReflection;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author AdrianSR / 28/1/2024 / 1:51 a. m.
 */
public class MaterialParser extends DataParser {
	
	@Override
	public @NotNull String getIdentifier ( ) {
		return "material";
	}
	
	@Override
	public @NotNull Class < ? > getType ( ) {
		return Material.class;
	}
	
	@Override
	public @Nullable Material parse ( @NotNull ConfigurationSection section ) {
		return EnumReflection.getEnumConstant (
				Material.class , section.getString (
						Constants.Key.VALUE , "" ).trim ( ).toUpperCase ( ) );
	}
	
	@Override
	public void write ( @NotNull Object value , @NotNull ConfigurationSection section ) {
		super.write ( value , section );
		
		if ( value instanceof Material ) {
			section.set ( Constants.Key.VALUE , ( ( Material ) value ).name ( ) );
		}
	}
}