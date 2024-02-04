package es.outlook.adriansrj.cv.api.vehicle.configuration.data;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author AdrianSR / 27/1/2024 / 6:43 p. m.
 */
public class ItemStackParser extends DataParser {
	
	@Override
	public @NotNull String getIdentifier ( ) {
		return "item-stack";
	}
	
	@Override
	public @NotNull Class < ? > getType ( ) {
		return ItemStack.class;
	}
	
	@Override
	public Object parse ( @NotNull ConfigurationSection section ) {
		return ItemStack.deserialize ( section.getValues ( false ) );
	}
	
	@Override
	public void write ( @NotNull Object value , @NotNull ConfigurationSection section ) {
		super.write ( value , section );
		
		if ( value instanceof ItemStack ) {
			for ( Map.Entry < String, Object > entry : ( ( ItemStack ) value ).serialize ( ).entrySet ( ) ) {
				section.set ( entry.getKey ( ) , entry.getValue ( ) );
			}
		}
	}
}