package es.outlook.adriansrj.cv.api.vehicle.configuration.data;

import es.outlook.adriansrj.cv.api.util.reflection.EnumReflection;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * @author AdrianSR / 27/1/2024 / 6:54 p. m.
 */
public class BlockDataParser extends DataParser {
	
	@Override
	public @NotNull String getIdentifier ( ) {
		return "block-data";
	}
	
	@Override
	public @NotNull Class < ? > getType ( ) {
		return BlockData.class;
	}
	
	@Override
	public BlockData parse ( @NotNull ConfigurationSection section ) {
		String value = section.getString ( "value" , "" );
		
		try {
			return Bukkit.createBlockData ( value );
		} catch ( IllegalArgumentException ex ) {
			Material material = EnumReflection.getEnumConstant (
					Material.class , value.trim ( ).toUpperCase ( ) );
			
			return material != null ? material.createBlockData ( ) : null;
		}
	}
	
	@Override
	public void write ( @NotNull Object value , @NotNull ConfigurationSection section ) {
		super.write ( value , section );
		
		if ( value instanceof BlockData ) {
			section.set ( "value" , ( ( BlockData ) value ).getAsString ( true ) );
		}
	}
}