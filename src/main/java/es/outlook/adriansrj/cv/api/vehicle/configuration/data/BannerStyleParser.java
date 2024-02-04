package es.outlook.adriansrj.cv.api.vehicle.configuration.data;

import org.bukkit.block.banner.Pattern;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author AdrianSR / 28/1/2024 / 12:59 a. m.
 */
public class BannerStyleParser extends DataParser {
	
	@Override
	public @NotNull String getIdentifier ( ) {
		return "banner-style";
	}
	
	@Override
	public @NotNull Class < ? > getType ( ) {
		return BannerStyle.class;
	}
	
	@Override
	public @Nullable BannerStyle parse ( @NotNull ConfigurationSection section ) {
		List < Pattern > patterns = new ArrayList <> ( );
		
		for ( String key : section.getKeys ( false ) ) {
			Pattern pattern = section.getSerializable ( key , Pattern.class );
			
			if ( pattern != null ) {
				patterns.add ( pattern );
			}
		}
		
		return new BannerStyle ( patterns );
	}
	
	@Override
	public void write ( @NotNull Object value , @NotNull ConfigurationSection section ) {
		super.write ( value , section );
		
		if ( value instanceof BannerStyle ) {
			BannerStyle      bannerStyle = ( BannerStyle ) value;
			List < Pattern > patterns    = bannerStyle.getPatterns ( );
			
			for ( int i = 0 ; i < patterns.size ( ) ; i++ ) {
				section.set ( "pattern-" + i , patterns.get ( i ) );
			}
		}
	}
}