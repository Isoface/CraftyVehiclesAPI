package es.outlook.adriansrj.cv.api.vehicle.input;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import es.outlook.adriansrj.cv.api.interfaces.VersionSensible;
import org.jetbrains.annotations.NotNull;

/**
 * @author AdrianSR / 23/12/2023 / 12:57 p. m.
 */
@VersionSensible
public class PlayerSteerInput {
	
	// https://wiki.vg/Protocol#Player_Input
	
	public static PlayerSteerInput fromPacket ( @NotNull PacketContainer packet ) {
		StructureModifier < Float >   floats   = packet.getFloat ( );
		StructureModifier < Boolean > booleans = packet.getBooleans ( );
		
		float   sideways = floats.read ( 0 );
		float   forward  = floats.read ( 1 );
		boolean jump     = booleans.read ( 0 );
		boolean unmount  = booleans.read ( 1 );
		
		return new PlayerSteerInput (
				sideways != 0.0F ? ( sideways < 0 ? -1 : 1 ) : 0 ,
				forward != 0.0F ? ( forward < 0 ? -1 : 1 ) : 0 ,
				jump , unmount
		);
	}
	
	/** positive = left | negative = right */
	public final int sideways;
	
	/** positive = forward | negative = backward */
	public final int forward;
	
	public final boolean jump;
	public final boolean unmount;
	public final boolean keepAlive;
	
	public PlayerSteerInput ( int sideways , int forward , boolean jump , boolean unmount ) {
		this.sideways = sideways;
		this.forward  = forward;
		this.jump     = jump;
		this.unmount  = unmount;
		
		keepAlive = sideways == 0 && forward == 0 && !jump && !unmount;
	}
	
	@Override
	public boolean equals ( Object o ) {
		if ( this == o ) { return true; }
		if ( o == null || getClass ( ) != o.getClass ( ) ) { return false; }
		
		PlayerSteerInput that = ( PlayerSteerInput ) o;
		
		if ( sideways != that.sideways ) { return false; }
		if ( forward != that.forward ) { return false; }
		if ( jump != that.jump ) { return false; }
		if ( unmount != that.unmount ) { return false; }
		return keepAlive == that.keepAlive;
	}
	
	@Override
	public int hashCode ( ) {
		int result = sideways;
		result = 31 * result + forward;
		result = 31 * result + ( jump ? 1 : 0 );
		result = 31 * result + ( unmount ? 1 : 0 );
		result = 31 * result + ( keepAlive ? 1 : 0 );
		return result;
	}
}