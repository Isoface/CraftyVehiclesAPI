package es.outlook.adriansrj.cv.api.enums;

import es.outlook.adriansrj.cv.api.util.Constants;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 * @author AdrianSR / 21/11/2023 / 12:23 p. m.
 */
public enum EnumRotableLimb {
	
	HEAD ( ) {
		@Override
		public Vector3D getDefaultRotation ( ) {
			return Vector3D.ZERO;
		}
		
		@Override
		public Vector3D getRotationPivot ( boolean small ) {
			return small ? Constants.Pivot.SMALL_ARMOR_STAND_HEAD_ROTATION_PIVOT
					: Constants.Pivot.ARMOR_STAND_HEAD_ROTATION_PIVOT;
		}
	},
	
	LEFT_ARM ( ) {
		@Override
		public Vector3D getDefaultRotation ( ) {
			return new Vector3D ( -10.0D , 0.0D , -10.0D );
		}
		
		@Override
		public Vector3D getRotationPivot ( boolean small ) {
			return small ? Constants.Pivot.SMALL_ARMOR_STAND_LEFT_ARM_ROTATION_PIVOT
					: Constants.Pivot.ARMOR_STAND_LEFT_ARM_ROTATION_PIVOT;
		}
	},
	
	RIGHT_ARM ( ) {
		@Override
		public Vector3D getDefaultRotation ( ) {
			return new Vector3D ( -15.0D , 0.0D , 10.0D );
		}
		
		@Override
		public Vector3D getRotationPivot ( boolean small ) {
			return small ? Constants.Pivot.SMALL_ARMOR_STAND_RIGHT_ARM_ROTATION_PIVOT
					: Constants.Pivot.ARMOR_STAND_RIGHT_ARM_ROTATION_PIVOT;
		}
	};
	
	public abstract Vector3D getDefaultRotation ( );
	
	public abstract Vector3D getRotationPivot ( boolean small );
	
	public EnumStandSlot toStandSlot ( ) {
		switch ( this ) {
			case HEAD:
				return EnumStandSlot.HEAD;
			case LEFT_ARM:
				return EnumStandSlot.LEFT_HAND;
			case RIGHT_ARM:
				return EnumStandSlot.RIGHT_HAND;
		}
		
		throw new IllegalStateException ( );
	}
}