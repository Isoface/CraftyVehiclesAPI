package es.outlook.adriansrj.cv.api.vehicle.configuration.model;

import com.google.common.base.Preconditions;
import es.outlook.adriansrj.cv.api.interfaces.ConfigurationSectionWritable;
import es.outlook.adriansrj.cv.api.interfaces.Validable;
import es.outlook.adriansrj.cv.api.util.ConfigurationUtil;
import es.outlook.adriansrj.cv.api.util.Constants;
import es.outlook.adriansrj.cv.api.util.reflection.EnumReflection;
import es.outlook.adriansrj.cv.api.vehicle.VehicleState;
import gnu.trove.set.hash.THashSet;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

/**
 * @author AdrianSR / 26/12/2023 / 12:19 p. m.
 */
@Getter
public class VehicleSoundConfiguration implements ConfigurationSectionWritable, Validable {
	
	public static VehicleSoundConfiguration load ( @NotNull ConfigurationSection section )
			throws InvalidConfigurationException {
		String typeString = section.getString ( Constants.Key.TYPE );
		Sound  type;
		String typeCustom = null;
		
		if ( StringUtils.isBlank ( typeString ) ) {
			throw new InvalidConfigurationException ( "sound type must be set" );
		}
		
		if ( ( type = EnumReflection.getEnumConstant (
				Sound.class , typeString.trim ( ).toUpperCase ( ) ) ) == null ) {
			typeCustom = typeString;
		}
		
		SoundCategory category = ConfigurationUtil.loadEnum (
				SoundCategory.class , section , Constants.Key.CATEGORY );
		int     delay  = section.getInt ( Constants.Key.DELAY );
		float   volume = section.getInt ( Constants.Key.VOLUME );
		float   pitch  = section.getInt ( Constants.Key.PITCH );
		boolean global = section.getBoolean ( Constants.Key.GLOBAL );
		
		if ( delay < 0 ) {
			throw new InvalidConfigurationException ( "delay cannot be negative" );
		} else if ( volume <= 0.0F ) {
			throw new InvalidConfigurationException ( "volume must be greater than 0" );
		}
		
		// states
		Set < String > statesToApply       = new THashSet <> ( );
		String         statesToApplyString = section.getString ( Constants.Key.STATES_TO_APPLY );
		
		if ( StringUtils.isNotBlank ( statesToApplyString ) ) {
			String[] states = statesToApplyString.split ( "," );
			
			Arrays.stream ( states )
					.filter ( StringUtils :: isNotBlank )
					.map ( string -> string.toLowerCase ( ).trim ( ) )
					.forEach ( statesToApply :: add );
		}
		
		return new VehicleSoundConfiguration (
				type , typeCustom , category , delay , volume , pitch ,
				global , statesToApply
		);
	}
	
	protected final @Nullable Sound         type;
	protected final @Nullable String        typeCustom;
	protected final @Nullable SoundCategory category;
	protected final           int           delay;
	protected final           float         volume;
	protected final           float         pitch;
	protected final           boolean       global;
	
	// states this sound applies to
	private final Set < String > statesToApply = new THashSet <> ( );
	
	public boolean appliesTo ( @NotNull VehicleState state ) {
		for ( String name : statesToApply ) {
			if ( state.getName ( ).equalsIgnoreCase ( name ) ) {
				return true;
			}
		}
		
		return false;
	}
	
	public VehicleSoundConfiguration ( @Nullable Sound type , @Nullable String typeCustom ,
			@Nullable SoundCategory category , int delay , float volume , float pitch ,
			boolean global , @Nullable Collection < String > statesToApply ) {
		Preconditions.checkArgument (
				type != null || StringUtils.isNotBlank ( typeCustom ) ,
				"either a valid type or a valid typeCustom must be provided"
		);
		
		if ( type == null ) {
			Preconditions.checkArgument (
					StringUtils.isNotBlank ( typeCustom ) ,
					"typeCustom cannot be blank"
			);
		}
		
		Preconditions.checkArgument ( delay >= 0 , "delay cannot be negative" );
		Preconditions.checkArgument ( volume > 0.0F , "volume must be > 0" );
		
		this.type       = type;
		this.typeCustom = typeCustom;
		this.category   = category;
		this.delay      = delay;
		this.volume     = volume;
		this.pitch      = pitch;
		this.global     = global;
		
		if ( statesToApply != null ) {
			this.statesToApply.addAll ( statesToApply );
		}
	}
	
	@Builder
	public VehicleSoundConfiguration ( @NotNull Sound type ,
			@Nullable SoundCategory category , int delay ,
			float volume , float pitch , boolean global ,
			@Nullable Collection < String > statesToApply ) {
		this ( type , null , category , delay , volume , pitch , global , statesToApply );
	}
	
	@Builder ( builderMethodName = "builderCustomType" )
	public VehicleSoundConfiguration ( @NotNull String typeCustom ,
			@Nullable SoundCategory category , int delay ,
			float volume , float pitch , boolean global ,
			@Nullable Collection < String > statesToApply ) {
		this ( null , typeCustom , category , delay , volume , pitch , global , statesToApply );
	}
	
	@Override
	public boolean isValid ( ) {
		return type != null || StringUtils.isNotBlank ( typeCustom );
	}
	
	public boolean applies ( @NotNull VehicleState state ) {
		return statesToApply.contains ( state.getName ( ) );
	}
	
	@Override
	public void write ( @NotNull ConfigurationSection section ) {
		if ( type != null ) {
			section.set ( Constants.Key.TYPE , type.name ( ) );
		} else if ( StringUtils.isNotBlank ( typeCustom ) ) {
			section.set ( Constants.Key.TYPE , typeCustom );
		}
		
		if ( category != null ) {
			ConfigurationUtil.writeEnum ( category , section , Constants.Key.CATEGORY );
		}
		
		section.set ( Constants.Key.DELAY , delay );
		section.set ( Constants.Key.VOLUME , volume );
		section.set ( Constants.Key.PITCH , pitch );
		section.set ( Constants.Key.GLOBAL , global );
		
		if ( statesToApply.size ( ) > 0 ) {
			section.set ( Constants.Key.STATES_TO_APPLY , String.join ( "," , statesToApply ) );
		} else {
			section.set ( Constants.Key.STATES_TO_APPLY , null );
		}
	}
}