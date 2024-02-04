package es.outlook.adriansrj.cv.api.util;

import es.outlook.adriansrj.cv.api.interfaces.ConfigurationSectionWritable;
import es.outlook.adriansrj.cv.api.interfaces.IDeyed;
import es.outlook.adriansrj.cv.api.interfaces.Named;
import es.outlook.adriansrj.cv.api.util.reflection.EnumReflection;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author AdrianSR / 23/11/2023 / 4:19 p. m.
 */
public class ConfigurationUtil {
	
	private static final Map < Class < ? >, LibraryObjectSingleEntryConfigurationWrapper < ? > >
			SINGLE_ENTRY_LIBRARY_OBJECT_WRAPPERS = new HashMap <> ( );
	
	private static final Map < Class < ? >, LibraryObjectConfigurationWrapper < ? > >
			LIBRARY_OBJECT_WRAPPERS = new HashMap <> ( );
	
	/**
	 * @author AdrianSR / 23/11/2023 / 4:54 p. m.
	 */
	private interface LibraryObjectSingleEntryConfigurationWrapper < T > {
		
		T load ( @NotNull ConfigurationSection section , @NotNull String key ) throws InvalidConfigurationException;
		
		void write ( @NotNull T object , @NotNull ConfigurationSection section , @NotNull String key );
	}
	
	/**
	 * @author AdrianSR / 23/11/2023 / 4:54 p. m.
	 */
	private interface LibraryObjectConfigurationWrapper < T > {
		
		T load ( @NotNull ConfigurationSection section ) throws InvalidConfigurationException;
		
		void write ( @NotNull T object , @NotNull ConfigurationSection section );
	}
	
	static {
		SINGLE_ENTRY_LIBRARY_OBJECT_WRAPPERS.put (
				UUID.class , new LibraryObjectSingleEntryConfigurationWrapper < UUID > ( ) {
					
					@Override
					public UUID load ( @NotNull ConfigurationSection section , @NotNull String key )
							throws InvalidConfigurationException {
						try {
							String string = section.getString ( key );
							
							return UUID.fromString ( string != null ? string : "" );
						} catch ( IllegalArgumentException ex ) {
							throw new InvalidConfigurationException ( ex );
						}
					}
					
					@Override
					public void write ( @NotNull UUID object , @NotNull ConfigurationSection section ,
							@NotNull String key ) {
						section.set ( key , object.toString ( ) );
					}
				} );
		
		LIBRARY_OBJECT_WRAPPERS.put ( Vector3D.class , new LibraryObjectConfigurationWrapper < Vector3D > ( ) {
			
			@Override
			public Vector3D load ( @NotNull ConfigurationSection section ) {
				double x = section.getDouble ( Constants.Key.X );
				double y = section.getDouble ( Constants.Key.Y );
				double z = section.getDouble ( Constants.Key.Z );
				
				return new Vector3D ( x , y , z );
			}
			
			@Override
			public void write ( @NotNull Vector3D object , @NotNull ConfigurationSection section ) {
				section.set ( Constants.Key.X , object.getX ( ) );
				section.set ( Constants.Key.Y , object.getY ( ) );
				section.set ( Constants.Key.Z , object.getZ ( ) );
			}
		} );
	}
	
	public static < T > T loadLibrarySingleEntryObject ( @NotNull Class < T > type ,
			@NotNull ConfigurationSection section , @NotNull String key ) throws InvalidConfigurationException {
		LibraryObjectSingleEntryConfigurationWrapper < ? > loader = SINGLE_ENTRY_LIBRARY_OBJECT_WRAPPERS.get ( type );
		Object                                             object = loader.load ( section , key );
		
		return object != null ? type.cast ( object ) : null;
	}
	
	public static < T > T loadLibraryObject ( @NotNull Class < T > type ,
			@NotNull ConfigurationSection section ) throws InvalidConfigurationException {
		LibraryObjectConfigurationWrapper < ? > loader = LIBRARY_OBJECT_WRAPPERS.get ( type );
		Object                                  object = loader.load ( section );
		
		return object != null ? type.cast ( object ) : null;
	}
	
	public static < T > T loadLibraryObject ( @NotNull Class < T > type ,
			@NotNull ConfigurationSection parent ,
			@NotNull String sectionName ) throws InvalidConfigurationException {
		ConfigurationSection section = parent.getConfigurationSection ( sectionName );
		
		if ( section != null ) {
			return loadLibraryObject ( type , section );
		} else {
			return null;
		}
	}
	
	public static < T extends Enum < T > > @Nullable T loadEnum ( @NotNull Class < T > type ,
			@NotNull ConfigurationSection section , @NotNull String key , boolean upperCase ) {
		String name = section.getString ( key , StringUtils.EMPTY );
		
		return StringUtils.isNotBlank ( name )
				? EnumReflection.getEnumConstant ( type , name.trim ( ).toUpperCase ( ) ) : null;
	}
	
	public static < T extends Enum < T > > @Nullable T loadEnum ( @NotNull Class < T > type ,
			@NotNull ConfigurationSection section , @NotNull String key ) {
		return loadEnum ( type , section , key , true );
	}
	
	@SuppressWarnings ( "unchecked" )
	public static < T > void writeSingleEntryLibraryObject ( @NotNull Class < T > type ,
			@NotNull T object , @NotNull ConfigurationSection section , @NotNull String key ) {
		LibraryObjectSingleEntryConfigurationWrapper < T > writer =
				( LibraryObjectSingleEntryConfigurationWrapper < T > )
						SINGLE_ENTRY_LIBRARY_OBJECT_WRAPPERS.get ( type );
		
		writer.write ( object , section , key );
	}
	
	@SuppressWarnings ( "unchecked" )
	public static < T > void writeLibraryObject ( @NotNull Class < T > type ,
			@NotNull T object , @NotNull ConfigurationSection section ) {
		LibraryObjectConfigurationWrapper < T > writer =
				( LibraryObjectConfigurationWrapper < T > ) LIBRARY_OBJECT_WRAPPERS.get ( type );
		
		writer.write ( object , section );
	}
	
	public static void writeEnum ( @NotNull Enum < ? > entry ,
			@NotNull ConfigurationSection section , @NotNull String key ) {
		section.set ( key , entry.name ( ) );
	}
	
	public static void writeNullable ( @Nullable Object object ,
			@NotNull ConfigurationSection section , @NotNull String key ) {
		section.set ( key , object );
	}
	
	public static void writeConfigurationSectionWritables (
			@NotNull ConfigurationSection section ,
			@NotNull Collection < ? extends ConfigurationSectionWritable > writables ,
			@NotNull String subfix ) {
		int count = 0;
		
		for ( ConfigurationSectionWritable writable : writables ) {
			if ( writable instanceof Named ) {
				writable.write ( section.createSection ( ( ( Named ) writable ).getName ( ) ) );
			} else if ( writable instanceof IDeyed ) {
				writable.write ( section.createSection ( ( ( IDeyed ) writable ).getId ( ) ) );
			} else {
				writable.write ( section.createSection ( subfix + count ) );
			}
			
			count++;
		}
	}
	
	public static @NotNull Set < ConfigurationSection > getConfigurationSectionsAfter (
			@NotNull ConfigurationSection parent , @NotNull String sectionAfterParent , boolean deep ) {
		ConfigurationSection sectionAfter = parent.getConfigurationSection ( sectionAfterParent );
		
		if ( sectionAfter != null ) {
			return getConfigurationSections ( sectionAfter , deep );
		} else {
			return new HashSet <> ( );
		}
	}
	
	public static @NotNull Set < ConfigurationSection > getConfigurationSections (
			@NotNull ConfigurationSection parent , boolean deep ) {
		Set < String >               keys   = parent.getKeys ( deep );
		Set < ConfigurationSection > result = new HashSet <> ( );
		
		for ( String key : keys ) {
			ConfigurationSection subSection = parent.getConfigurationSection ( key );
			
			if ( subSection != null ) {
				result.add ( subSection );
			}
		}
		
		return result;
	}
}
