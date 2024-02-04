package es.outlook.adriansrj.cv.api.util;

import es.outlook.adriansrj.cv.api.CraftyVehicles;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.bukkit.NamespacedKey;

import java.io.File;

/**
 * @author AdrianSR / 23/11/2023 / 3:31 p. m.
 */
public class Constants {
	
	public static final String VALID_ID_PATTERN         = "[a-z0-9\\-_.]+";
	public static final String VALID_NAME_PATTERN       = "[a-zA-Z0-9\\-_.]+";
	public static final String VALID_STATE_NAME_PATTERN = "[a-z0-9/._-]+";
	
	/**
	 * @author AdrianSR / 9/12/2023 / 10:37 p. m.
	 */
	public static class Files {
		
		public static final File ITEMS_FOLDER;
		public static final File VEHICLES_FOLDER;
		public static final File VEHICLE_MODELS_FOLDER;
		public static final File VEHICLE_CONTROLLERS_FOLDER;
		
		public static final File RECIPES_CONFIGURATION_FILE;
		
		static {
			ITEMS_FOLDER = new File (
					CraftyVehicles.getPlugin ( ).getDataFolder ( ) ,
					"item"
			);
			
			VEHICLES_FOLDER = new File (
					CraftyVehicles.getPlugin ( ).getDataFolder ( ) ,
					"vehicle"
			);
			
			VEHICLE_MODELS_FOLDER = new File (
					CraftyVehicles.getPlugin ( ).getDataFolder ( ) ,
					"model"
			);
			
			VEHICLE_CONTROLLERS_FOLDER = new File (
					CraftyVehicles.getPlugin ( ).getDataFolder ( ) ,
					"controllers"
			);
			
			RECIPES_CONFIGURATION_FILE = new File (
					CraftyVehicles.getPlugin ( ).getDataFolder ( ) ,
					"RecipesConfiguration.yml"
			);
		}
		
		public static void mkdirs ( ) {
			VEHICLES_FOLDER.mkdirs ( );
			VEHICLE_MODELS_FOLDER.mkdirs ( );
			VEHICLE_CONTROLLERS_FOLDER.mkdirs ( );
		}
	}
	
	/**
	 * @author AdrianSR / 23/11/2023 / 4:02 p. m.
	 */
	public static class Key {
		
		public static String nodes ( String... keys ) {
			StringBuilder key = new StringBuilder ( );
			
			for ( int i = 0 ; i < keys.length ; i++ ) {
				key.append ( keys[ i ] );
				
				if ( i < keys.length - 1 ) {
					key.append ( "." );
				}
			}
			
			return key.toString ( );
		}
		
		public static final String VALUE        = "value";
		public static final String ID           = "id";
		public static final String IDENTIFIER   = "identifier";
		public static final String NAME         = "name";
		public static final String DISPLAY_NAME = "display-name";
		public static final String TYPE         = "type";
		public static final String MODEL        = "model";
		public static final String DATA         = "data";
		public static final String DESCRIPTION  = "description";
		public static final String ITEM         = "item";
		public static final String SPAWN        = "spawn";
		public static final String PICKUP_ITEM  = "pickup-item";
		
		public static final String RESULT    = "result";
		public static final String SHAPED    = "shaped";
		public static final String SHAPELESS = "shapeless";
		
		public static final String EXIT_SHORTCUT           = "exit-shortcut";
		public static final String OPERATOR_EXIT_SHORTCUT  = nodes ( EXIT_SHORTCUT , "operator" );
		public static final String PASSENGER_EXIT_SHORTCUT = nodes ( EXIT_SHORTCUT , "passenger" );
		
		public static final String X = "x";
		public static final String Y = "y";
		public static final String Z = "z";
		
		public static final String WIDTH  = "width";
		public static final String HEIGHT = "height";
		public static final String DEPTH  = "depth";
		
		public static final String HIT_BOX   = "hitbox";
		public static final String PARTS     = "parts";
		public static final String BONES     = "bones";
		public static final String RIG       = "rig";
		public static final String SEATS     = "seats";
		public static final String PARTICLES = "particles";
		public static final String SOUNDS    = "sounds";
		public static final String MAIN      = "main";
		
		public static final String TEXTURES            = "textures";
		public static final String MATERIAL            = "material";
		public static final String BLOCK_DATA          = "block-data";
		public static final String CUSTOM_MODEL_DATA   = "custom-model-data";
		public static final String CUSTOM_HEAD_TEXTURE = "custom-head-texture";
		public static final String HEAD_TEXTURE        = "head-texture";
		public static final String SMALL               = "small";
		public static final String ROTATION            = "rotation";
		public static final String OFFSET              = "offset";
		public static final String SCALE               = "scale";
		
		public static final String PIVOT    = "pivot";
		public static final String PARENT   = "parent";
		public static final String CHILDREN = "children";
		
		public static final String ANIMATIONS         = "animations";
		public static final String KEYFRAMES          = "keyframes";
		public static final String DURATION           = "duration";
		public static final String INTERPOLATION_MODE = "interpolation-mode";
		public static final String LOOP_MODE          = "loop-mode";
		public static final String TRANSLATIONS       = "translations";
		public static final String ROTATIONS          = "rotations";
		public static final String SCALES             = "scales";
		
		public static final String CATEGORY        = "category";
		public static final String DELAY           = "delay";
		public static final String VOLUME          = "volume";
		public static final String PITCH           = "pitch";
		public static final String GLOBAL          = "global";
		public static final String COUNT           = "count";
		public static final String DISPERSION      = "dispersion";
		public static final String PHYSICS         = "physics";
		public static final String CONTROLLERS     = "controllers";
		public static final String PROPERTIES      = "properties";
		public static final String FLOATS          = "floats";
		public static final String GRAVITY         = "gravity";
		public static final String MAXIMUM         = "maximum";
		public static final String ACCELERATION    = "acceleration";
		public static final String FRICTION        = "friction";
		public static final String CLIMBING        = "climbing";
		public static final String AIR             = "air";
		public static final String ON_UNKNOWN      = "on-unknown";
		public static final String ON_SOLID        = "on-solid";
		public static final String ON_DUSTY        = "on-dusty";
		public static final String ON_SNOWY        = "on-snowy";
		public static final String ON_SLIPPERY     = "on-slippery";
		public static final String ON_WATER        = "on-water";
		public static final String ON_LAVA         = "on-lava";
		public static final String THROUGH_WATER   = "through-water";
		public static final String THROUGH_LAVA    = "through-lava";
		public static final String STATES_TO_APPLY = "states-to-apply";
		
		public static final String GRAVITY_MAXIMUM      = nodes ( GRAVITY , MAXIMUM );
		public static final String GRAVITY_ACCELERATION = nodes ( GRAVITY , ACCELERATION );
		
		public static final String FRICTION_AIR           = nodes ( FRICTION , AIR );
		public static final String FRICTION_ON_UNKNOWN    = nodes ( FRICTION , ON_UNKNOWN );
		public static final String FRICTION_ON_SOLID      = nodes ( FRICTION , ON_SOLID );
		public static final String FRICTION_ON_DUSTY      = nodes ( FRICTION , ON_DUSTY );
		public static final String FRICTION_ON_SNOWY      = nodes ( FRICTION , ON_SNOWY );
		public static final String FRICTION_ON_SLIPPERY   = nodes ( FRICTION , ON_SLIPPERY );
		public static final String FRICTION_ON_WATER      = nodes ( FRICTION , ON_WATER );
		public static final String FRICTION_ON_LAVA       = nodes ( FRICTION , ON_LAVA );
		public static final String FRICTION_THROUGH_WATER = nodes ( FRICTION , THROUGH_WATER );
		public static final String FRICTION_THROUGH_LAVA  = nodes ( FRICTION , THROUGH_LAVA );
		
		public static final String BLOCK_CLIMB_CAPACITY = nodes ( CLIMBING , "block-climb-capacity" );
		
		public static final String VEHICLE_ID      = "vehicle-id";
		public static final String FUEL            = "fuel";
		public static final String FUEL_AMOUNT     = "fuel-amount";
		public static final String ACTION          = "action";
		public static final String CAPACITY        = "capacity";
		public static final String CONSUMPTION     = "consumption";
		public static final String MIN_CONSUMPTION = "min-consumption";
		public static final String MAX_CONSUMPTION = "max-consumption";
	}
	
	/**
	 * @author AdrianSR / 29/1/2024 / 5:26 p. m.
	 */
	public static class NamespacedKeys {
		
		public static final NamespacedKey ITEM_ID;
		public static final NamespacedKey VEHICLE_ID;
		
		static {
			ITEM_ID    = new NamespacedKey ( CraftyVehicles.getPlugin ( ) , "crafty-item-id" );
			VEHICLE_ID = new NamespacedKey ( CraftyVehicles.getPlugin ( ) , "crafty-vehicle-id" );
		}
	}
	
	/**
	 * @author AdrianSR / 23/12/2023 / 1:52 p. m.
	 */
	public static class HitBox {
		
		public static final double MIN_HIT_BOX_SIZE = 0.5D;
	}
	
	/**
	 * @author AdrianSR / 26/11/2023 / 1:41 p. m.
	 */
	public static class Dimensions {
		
		/**
		 * Converts an item model size to a real-world minecraft size.
		 */
		public static final double FANCY_PROJECTION_SCALE = 0.0625D; // 0.0609678D;
		
		/**
		 * Converts the width of text to real-world minecraft size.
		 */
		public static final double FANCY_NAME_PROJECTION_SCALE = 0.0270586D; // 0.02618; // 0.0270586D;// 0.023D;
		
		public static final double ARMOR_STAND_HEIGHT       = 30.0D * FANCY_PROJECTION_SCALE;
		public static final double SMALL_ARMOR_STAND_HEIGHT = ARMOR_STAND_HEIGHT * 0.5D;
		
		// armor stand head
		
		public static final double ARMOR_STAND_HEAD_BOX_SIZE       = 10.0D * FANCY_PROJECTION_SCALE;
		public static final double SMALL_ARMOR_STAND_HEAD_BOX_SIZE = 6.5D * FANCY_PROJECTION_SCALE;
		
		// armor stand hands
		
		public static final double ARMOR_STAND_HAND_BOX_SIZE       = 6.25D * FANCY_PROJECTION_SCALE;
		public static final double SMALL_ARMOR_STAND_HAND_BOX_SIZE = 4.0625D * FANCY_PROJECTION_SCALE; //TODO: CHECK
		
		// armor stand right arm
		
		// armor stand name box
		
		public static final double ARMOR_STAND_NAME_BOX_HEIGHT = 0.285D; // 0.23D;
	}
	
	/**
	 * @author AdrianSR / 28/11/2023 / 12:53 p. m.
	 */
	public static class Pivot {
		
		// armor stand head
		
		public static final Vector3D ARMOR_STAND_HEAD_ROTATION_PIVOT = new Vector3D (
				0 , 23.0D * Dimensions.FANCY_PROJECTION_SCALE , 0
		);
		
		public static final Vector3D SMALL_ARMOR_STAND_HEAD_ROTATION_PIVOT
				= ARMOR_STAND_HEAD_ROTATION_PIVOT.scalarMultiply ( 0.5D );
		
		// armor stand left arm
		
		public static final Vector3D ARMOR_STAND_LEFT_ARM_ROTATION_PIVOT = new Vector3D (
				5.0D * Dimensions.FANCY_PROJECTION_SCALE ,
				22.0D * Dimensions.FANCY_PROJECTION_SCALE ,
				0
		);
		
		public static final Vector3D ARMOR_STAND_LEFT_HAND_BOX_ROTATION_PIVOT = new Vector3D (
				6.0D * Dimensions.FANCY_PROJECTION_SCALE ,
				0.0D ,
				2.0D * Dimensions.FANCY_PROJECTION_SCALE
		);
		
		public static final Vector3D SMALL_ARMOR_STAND_LEFT_ARM_ROTATION_PIVOT
				= ARMOR_STAND_LEFT_ARM_ROTATION_PIVOT.scalarMultiply ( 0.5D ); // TODO: CHECK
		
		public static final Vector3D SMALL_ARMOR_STAND_LEFT_HAND_BOX_ROTATION_PIVOT
				= ARMOR_STAND_LEFT_HAND_BOX_ROTATION_PIVOT.scalarMultiply ( 0.5D ); // TODO: CHECK
		
		// armor stand right arm
		
		public static final Vector3D ARMOR_STAND_RIGHT_ARM_ROTATION_PIVOT = new Vector3D (
				-5.0D * Dimensions.FANCY_PROJECTION_SCALE ,
				22.0D * Dimensions.FANCY_PROJECTION_SCALE ,
				0
		);
		
		public static final Vector3D ARMOR_STAND_RIGHT_HAND_BOX_ROTATION_PIVOT = new Vector3D (
				-6.0D * Dimensions.FANCY_PROJECTION_SCALE ,
				0.0D ,
				2.0D * Dimensions.FANCY_PROJECTION_SCALE
		);
		
		public static final Vector3D SMALL_ARMOR_STAND_RIGHT_ARM_ROTATION_PIVOT
				= ARMOR_STAND_RIGHT_ARM_ROTATION_PIVOT.scalarMultiply ( 0.5D ); // TODO: CHECK
		
		public static final Vector3D SMALL_ARMOR_STAND_RIGHT_HAND_BOX_ROTATION_PIVOT
				= ARMOR_STAND_RIGHT_HAND_BOX_ROTATION_PIVOT.scalarMultiply ( 0.5D ); // TODO: CHECK
	}
}