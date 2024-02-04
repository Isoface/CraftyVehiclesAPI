package es.outlook.adriansrj.cv.api.vehicle;

import com.google.common.base.Preconditions;
import es.outlook.adriansrj.cv.api.util.Constants;
import es.outlook.adriansrj.cv.api.vehicle.configuration.model.VehicleHitBoxConfiguration;
import lombok.Getter;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.util.FastMath;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

/**
 * @author AdrianSR / 23/11/2023 / 12:08 p. m.
 */
public class VehicleHitBox {
	
	public static VehicleHitBox of ( @NotNull VehicleHitBoxConfiguration configuration ) {
		return new VehicleHitBox (
				Math.max ( configuration.getWidth ( ) , Constants.HitBox.MIN_HIT_BOX_SIZE ) ,
				Math.max ( configuration.getHeight ( ) , Constants.HitBox.MIN_HIT_BOX_SIZE ) ,
				Math.max ( configuration.getDepth ( ) , Constants.HitBox.MIN_HIT_BOX_SIZE )
		);
	}
	
	private final @Getter double width;
	private final @Getter double height;
	private final @Getter double depth;
	
	// currents
	private @Getter double originX, originY, originZ;
	private @Getter double minX, minY, minZ;
	private @Getter double maxX, maxY, maxZ;
	
	public VehicleHitBox ( double width , double height , double depth ) {
		Preconditions.checkArgument (
				width >= Constants.HitBox.MIN_HIT_BOX_SIZE ,
				"width must be >= " + Constants.HitBox.MIN_HIT_BOX_SIZE
		);
		Preconditions.checkArgument (
				height >= Constants.HitBox.MIN_HIT_BOX_SIZE ,
				"height must be >= " + Constants.HitBox.MIN_HIT_BOX_SIZE
		);
		Preconditions.checkArgument (
				depth >= Constants.HitBox.MIN_HIT_BOX_SIZE ,
				"depth must be >= " + Constants.HitBox.MIN_HIT_BOX_SIZE
		);
		
		this.width  = width;
		this.height = height;
		this.depth  = depth;
		
		recalculate ( );
	}
	
	public int getBlockMinX ( ) {
		return ( int ) FastMath.floor ( minX );
	}
	
	public int getBlockMinY ( ) {
		return ( int ) FastMath.floor ( minY );
	}
	
	public int getBlockMinZ ( ) {
		return ( int ) FastMath.floor ( minZ );
	}
	
	public int getBlockMaxX ( ) {
		return ( int ) FastMath.floor ( maxX );
	}
	
	public int getBlockMaxY ( ) {
		return ( int ) FastMath.floor ( maxY );
	}
	
	public int getBlockMaxZ ( ) {
		return ( int ) FastMath.floor ( maxZ );
	}
	
	public void setOrigin ( double x , double y , double z ) {
		if ( Double.compare ( x , originX ) == 0
				&& Double.compare ( y , originY ) == 0
				&& Double.compare ( z , originZ ) == 0 ) {
			return;
		}
		
		originX = x;
		originY = y;
		originZ = z;
		
		recalculate ( );
	}
	
	public VehicleHitBox future ( double originX , double originY , double originZ ) {
		VehicleHitBox future = copy ( );
		future.setOrigin ( originX , originY , originZ );
		
		return future;
	}
	
	public void recalculate ( ) {
		double widthHalf = width / 2.0D;
		double depthHalf = depth / 2.0D;
		
		double minX = originX - widthHalf;
		double minZ = originZ - depthHalf;
		
		double maxX = originX + widthHalf;
		double maxZ = originZ + depthHalf;
		
		this.minX = Math.min ( minX , maxX );
		this.minZ = Math.min ( minZ , maxZ );
		
		this.maxX = Math.max ( minX , maxX );
		this.maxZ = Math.max ( minZ , maxZ );
		
		minY = originY;
		maxY = originY + height;
	}
	
	public Vector3D getCorner000 ( ) {
		return new Vector3D ( minX , minY , minZ );
	}
	
	public Vector3D getCorner001 ( ) {
		return new Vector3D ( minX , minY , maxZ );
	}
	
	public Vector3D getCorner010 ( ) {
		return new Vector3D ( minX , maxY , minZ );
	}
	
	public Vector3D getCorner011 ( ) {
		return new Vector3D ( minX , maxY , maxZ );
	}
	
	public Vector3D getCorner100 ( ) {
		return new Vector3D ( maxX , minY , minZ );
	}
	
	public Vector3D getCorner101 ( ) {
		return new Vector3D ( maxX , minY , maxZ );
	}
	
	public Vector3D getCorner110 ( ) {
		return new Vector3D ( maxX , maxY , minZ );
	}
	
	public Vector3D getCorner111 ( ) {
		return new Vector3D ( maxX , maxY , maxZ );
	}
	
	public Vector3D[] getCorners ( ) {
		return new Vector3D[] {
				getCorner000 ( ) ,
				getCorner001 ( ) ,
				getCorner010 ( ) ,
				getCorner011 ( ) ,
				getCorner100 ( ) ,
				getCorner101 ( ) ,
				getCorner110 ( ) ,
				getCorner111 ( )
		};
	}
	
	public BoundingBox toBoundingBox ( ) {
		return new BoundingBox ( minX , minY , minZ , maxX , maxY , maxZ );
	}
	
	public VehicleHitBox copy ( ) {
		VehicleHitBox copy = new VehicleHitBox ( width , height , depth );
		
		copy.originX = originX;
		copy.originY = originY;
		copy.originZ = originZ;
		
		copy.minX = minX;
		copy.minY = minY;
		copy.minZ = minZ;
		copy.maxX = maxX;
		copy.maxY = maxY;
		copy.maxZ = maxZ;
		
		return copy;
	}
}