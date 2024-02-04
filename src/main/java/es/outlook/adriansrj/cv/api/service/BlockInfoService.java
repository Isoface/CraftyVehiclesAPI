package es.outlook.adriansrj.cv.api.service;

import es.outlook.adriansrj.cv.api.enums.EnumSurface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

/**
 * @author AdrianSR / 25/11/2023 / 2:31 p. m.
 */
public interface BlockInfoService extends Service {
	
	@Getter
	@AllArgsConstructor
	class SurfaceResult {
		
		private final          int           minX;
		private final          int           minZ;
		private final          int           maxX;
		private final          int           maxZ;
		private final @NotNull EnumSurface[] value;
		
		public @NotNull EnumSurface getSurfaceType ( int x , int z ) {
			int depth = maxZ - minZ;
			
			x = ( x - minX ); // starting at 0
			z = ( z - minZ ); // starting at 0
			
			return value[ x * depth + z ];
		}
	}
	
	boolean isCanStandOnSurfaceAt ( World world , int x , int y , int z );
	
	EnumSurface getSurfaceTypeAt ( World world , int x , int y , int z , boolean ignoreWeather );
	
	SurfaceResult getSurfaceTypesAt ( World world , int y , int minX , int minZ ,
			int maxX , int maxZ , boolean ignoreCover , boolean ignoreWeather );

	default boolean isEmpty ( World world , int x , int y , int z ) {
		return getSurfaceTypeAt ( world , x , y , z , false ) == EnumSurface.EMPTY;
	}
	
	default boolean isSolid ( World world , int x , int y , int z , boolean ignoreWeather ) {
		return getSurfaceTypeAt ( world , x , y , z , ignoreWeather ) == EnumSurface.SOLID;
	}
	
	default boolean isWater ( World world , int x , int y , int z ) {
		return getSurfaceTypeAt ( world , x , y , z , false ) == EnumSurface.WATER;
	}
	
	default boolean isLava ( World world , int x , int y , int z ) {
		return getSurfaceTypeAt ( world , x , y , z , false ) == EnumSurface.LAVA;
	}
}