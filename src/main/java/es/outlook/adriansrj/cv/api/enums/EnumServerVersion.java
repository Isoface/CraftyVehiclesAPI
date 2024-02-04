package es.outlook.adriansrj.cv.api.enums;

import es.outlook.adriansrj.cv.api.interfaces.VersionSensible;
import es.outlook.adriansrj.cv.api.util.reflection.EnumReflection;
import org.bukkit.Bukkit;

/**
 * An enumeration for most server versions, that implements some useful methods
 * for comparing versions.
 * <br>
 * Server versions that are not implemented here are not supported.
 * <p>
 * @author AdrianSR / Sunday 12 April, 2020 / 05:19 PM
 */
@VersionSensible
public enum EnumServerVersion {
	
	v1_9_R1,
	v1_9_R2,
	v1_10_R1,
	v1_11_R1,
	v1_12_R1,
	v1_13_R1,
	v1_13_R2,
	v1_14_R1,
	v1_15_R1,
	v1_16_R1,
	v1_16_R2,
	v1_16_R3,
	v1_17_R1,
	v1_18_R1,
	v1_18_R2,
	v1_19_R1,
	v1_19_R2,
	v1_19_R3,
	v1_20_R1,
	v1_20_R2,
	
	;
	
	private final int id;
	
	EnumServerVersion ( ) {
		StringBuilder idBuilder = new StringBuilder ( );
		String        enumName  = name ( );
		
		for ( int i = 0 ; i < enumName.length ( ) ; i++ ) {
			char character = enumName.charAt ( i );
			
			if ( Character.isDigit ( character ) ) {
				idBuilder.append ( character );
			}
		}
		
		id = Integer.parseInt ( idBuilder.toString ( ) );
	}
	
	public static EnumServerVersion getServerVersion ( ) {
		final String serverClassPackage = Bukkit.getServer ( ).getClass ( ).getPackage ( ).getName ( );
		final String version = serverClassPackage.substring (
				serverClassPackage.lastIndexOf ( "." ) + 1 );
		
		return EnumReflection.getEnumConstant ( EnumServerVersion.class , version );
	}
	
	public int getId ( ) {
		return id;
	}
	
	public boolean isOlder ( EnumServerVersion other ) {
		return getId ( ) < other.getId ( );
	}
	
	public boolean isOlderEquals ( EnumServerVersion other ) {
		return getId ( ) <= other.getId ( );
	}
	
	public boolean isNewer ( EnumServerVersion other ) {
		return getId ( ) > other.getId ( );
	}
	
	public boolean isNewerEquals ( EnumServerVersion other ) {
		return getId ( ) >= other.getId ( );
	}
	
	public boolean isSameVersion ( EnumServerVersion other ) {
		final String s0 = name ( ).substring ( 0 , name ( ).indexOf ( "_R" ) );
		final String s1 = other.name ( ).substring ( 0 , other.name ( ).indexOf ( "_R" ) );
		
		return s0.equals ( s1 );
	}
	
	public boolean isSameRevision ( EnumServerVersion other ) {
		final String s0 = name ( ).substring ( name ( ).indexOf ( "R" ) + 1 );
		final String s1 = other.name ( ).substring ( other.name ( ).indexOf ( "R" ) + 1 );
		
		return s0.equals ( s1 );
	}
	
	/**
	 * Gets whether this version supports display entities.
	 * <br>
	 * @return whether this version supports display entities.
	 */
	public boolean isSupportsDisplayEntities ( ) {
		return this.id >= EnumServerVersion.v1_19_R3.id;
	}
}