package es.outlook.adriansrj.cv.api.enums;

/**
 * @author AdrianSR / 25/11/2023 / 1:34 p. m.
 */
public enum EnumSurface {
	
	SOLID,
	
	DUSTY,
	SNOWY,
	SLIPPERY,
	
	WATER,
	LAVA,
	
	EMPTY,
	UNKNOWN;
	
	public boolean isLiquid ( ) {
		return this == WATER || this == LAVA;
	}
}