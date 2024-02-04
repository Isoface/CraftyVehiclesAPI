package es.outlook.adriansrj.cv.api.enums;

import org.bukkit.inventory.EquipmentSlot;

/**
 * @author AdrianSR / 23/11/2023 / 12:57 p. m.
 */
public enum EnumStandSlot {
	
	HEAD,
	LEFT_HAND,
	RIGHT_HAND;
	
	public EquipmentSlot toEquipmentSlot ( ) {
		switch ( this ) {
			case HEAD:
				return EquipmentSlot.HEAD;
			case LEFT_HAND:
				return EquipmentSlot.OFF_HAND;
			case RIGHT_HAND:
				return EquipmentSlot.HAND;
		}
		
		throw new IllegalStateException ( );
	}
	
	public EnumRotableLimb toRotableLimb ( ) {
		switch ( this ) {
			case HEAD:
				return EnumRotableLimb.HEAD;
			case LEFT_HAND:
				return EnumRotableLimb.LEFT_ARM;
			case RIGHT_HAND:
				return EnumRotableLimb.RIGHT_ARM;
		}
		
		throw new IllegalStateException ( );
	}
}