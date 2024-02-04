package es.outlook.adriansrj.cv.api.vehicle.model;

import es.outlook.adriansrj.cv.api.interfaces.Tickable;
import es.outlook.adriansrj.cv.api.vehicle.Vehicle;
import es.outlook.adriansrj.cv.api.vehicle.VehicleSeat;
import es.outlook.adriansrj.cv.api.vehicle.VehicleState;
import es.outlook.adriansrj.cv.api.vehicle.configuration.model.VehicleModelConfiguration;
import es.outlook.adriansrj.cv.api.vehicle.configuration.model.VehicleParticleConfiguration;
import es.outlook.adriansrj.cv.api.vehicle.configuration.model.VehicleSoundConfiguration;
import gnu.trove.set.hash.THashSet;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.util.FastMath;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;

/**
 * @author AdrianSR / 23/11/2023 / 12:39 p. m.
 */
public abstract class VehicleModelBase < C extends VehicleModelConfiguration >
		implements VehicleModel < C > {
	
	/**
	 * @author AdrianSR / 25/12/2023 / 8:48 p. m.
	 */
	protected static class ParticleTicker implements Tickable {
		
		protected final @NotNull VehicleModelBase < ? >       model;
		protected final @NotNull VehicleParticleConfiguration configuration;
		
		// offset
		protected final int   m;
		protected final float h;
		protected final float v;
		
		// currents
		protected double  x;
		protected double  y;
		protected double  z;
		protected int     wait;
		protected boolean firstTick;
		
		public ParticleTicker ( @NotNull VehicleModelBase < ? > model ,
				@NotNull VehicleParticleConfiguration configuration ) {
			this.model         = model;
			this.configuration = configuration;
			
			// offset
			Vector3D offset = configuration.getOffset ( );
			
			v = ( float ) offset.getY ( );
			h = ( float ) FastMath.sqrt (
					( offset.getX ( ) * offset.getX ( ) )
							+ ( offset.getZ ( ) * offset.getZ ( ) )
			);
			m = m (
					new Vector2D ( offset.getX ( ) , offset.getZ ( ) ) ,
					new Vector2D ( 0 , 1 )
			);
		}
		
		@Override
		public void tick ( ) {
			if ( firstTick ) {
				firstTick = false;
				
				updateLocation ( );
			}
			
			if ( wait > 0 ) {
				wait--; return;
			}
			
			if ( model.state == null
					|| !configuration.appliesTo ( model.state ) ) {
				return;
			}
			
			int   count      = configuration.getCount ( );
			int   delay      = configuration.getDelay ( );
			float dispersion = configuration.getDispersion ( );
			
			model.getWorld ( ).spawnParticle (
					configuration.getType ( ) ,
					new Location ( model.getWorld ( ) , x , y , z ) ,
					count ,
					dispersion , dispersion , dispersion ,
					0.0D ,
					configuration.getData ( )
			);
			
			if ( delay > 0 ) {
				wait += delay;
			}
		}
		
		protected void updateLocation ( ) {
			int m = ( ( int ) FastMath.floor ( model.getRotation ( ) ) + this.m ) + 90;
			
			y = model.y + v;
			x = model.x + ( FastMath.cos ( FastMath.toRadians ( m ) ) * h );
			z = model.z + ( FastMath.sin ( FastMath.toRadians ( m ) ) * h );
		}
		
		protected int m ( @NotNull Vector2D f , @NotNull Vector2D s ) {
			double a = ( f.getX ( ) * s.getY ( ) ) - ( f.getY ( ) * s.getX ( ) );
			double b = ( f.getX ( ) * s.getX ( ) ) + ( f.getY ( ) * s.getY ( ) );
			
			return ( int ) FastMath.toDegrees ( FastMath.atan2 ( a , b ) );
		}
	}
	
	/**
	 * @author AdrianSR / 26/12/2023 / 5:07 p. m.
	 */
	protected static class SoundTicker implements Tickable {
		
		protected final @NotNull VehicleModelBase < ? >    model;
		protected final @NotNull VehicleSoundConfiguration configuration;
		
		// currents
		protected int wait;
		
		public SoundTicker ( @NotNull VehicleModelBase < ? > model ,
				@NotNull VehicleSoundConfiguration configuration ) {
			this.model         = model;
			this.configuration = configuration;
		}
		
		@Override
		public void tick ( ) {
			if ( wait > 0 ) {
				wait--; return;
			} else if ( !configuration.isValid ( ) ) {
				return;
			}
			
			if ( model.state == null
					|| !configuration.appliesTo ( model.state ) ) {
				return;
			}
			
			Location location = model.getLocation ( );
			
			if ( configuration.isGlobal ( ) ) {
				playGlobally ( location );
			} else {
				playToPassengers ( location );
			}
		}
		
		protected void playGlobally ( @NotNull Location location ) {
			World world = location.getWorld ( );
			
			if ( world == null ) {
				return; // odd
			}
			
			SoundCategory category   = configuration.getCategory ( );
			Sound         type       = configuration.getType ( );
			String        typeCustom = configuration.getTypeCustom ( );
			float         volume     = configuration.getVolume ( );
			float         pitch      = configuration.getPitch ( );
			
			if ( type != null ) {
				if ( category != null ) {
					world.playSound ( location , type , category , volume , pitch );
				} else {
					world.playSound ( location , type , volume , pitch );
				}
			} else if ( StringUtils.isNotBlank ( typeCustom ) ) {
				if ( category != null ) {
					world.playSound ( location , typeCustom , category , volume , pitch );
				} else {
					world.playSound ( location , typeCustom , volume , pitch );
				}
			}
		}
		
		protected void playToPassengers ( @NotNull Location location ) {
			SoundCategory category   = configuration.getCategory ( );
			Sound         type       = configuration.getType ( );
			String        typeCustom = configuration.getTypeCustom ( );
			float         volume     = configuration.getVolume ( );
			float         pitch      = configuration.getPitch ( );
			
			for ( VehicleSeat seat : model.vehicle.getSeats ( ) ) {
				if ( !( seat.getPassenger ( ) instanceof Player ) ) {
					continue;
				}
				
				Player player = ( Player ) seat.getPassenger ( );
				
				if ( type != null ) {
					if ( category != null ) {
						player.playSound ( location , type , category , volume , pitch );
					} else {
						player.playSound ( location , type , volume , pitch );
					}
				} else if ( StringUtils.isNotBlank ( typeCustom ) ) {
					if ( category != null ) {
						player.playSound ( location , typeCustom , category , volume , pitch );
					} else {
						player.playSound ( location , typeCustom , volume , pitch );
					}
				}
			}
		}
	}
	
	protected final @NotNull Vehicle      vehicle;
	protected final @NotNull C            configuration;
	protected @NotNull       World        world;
	protected                double       x;
	protected                double       y;
	protected                double       z;
	protected                float        rotation;
	protected                boolean      spawned;
	protected @Nullable      VehicleState state;
	
	// currents
	protected final Set < ParticleTicker > particleTickers = new THashSet <> ( );
	protected final Set < SoundTicker >    soundTickers    = new THashSet <> ( );
	
	protected VehicleModelBase ( @NotNull Vehicle vehicle , @NotNull C configuration ,
			@NotNull World world , double x , double y , double z ) {
		this.vehicle       = vehicle;
		this.configuration = configuration;
		this.world         = world;
		this.x             = x;
		this.y             = y;
		this.z             = z;
		
		// creating particle tickers
		for ( VehicleParticleConfiguration particleConfiguration : configuration.getParticles ( ) ) {
			if ( particleConfiguration.isValid ( ) ) {
				particleTickers.add ( new ParticleTicker ( this , particleConfiguration ) );
			}
		}
		
		// creating sound tickers
		for ( VehicleSoundConfiguration soundConfiguration : configuration.getSounds ( ) ) {
			if ( soundConfiguration.isValid ( ) ) {
				soundTickers.add ( new SoundTicker ( this , soundConfiguration ) );
			}
		}
	}
	
	@Override
	public @NotNull C getConfiguration ( ) {
		return configuration;
	}
	
	@Override
	public boolean isSpawned ( ) {
		return spawned;
	}
	
	// -- ticking
	
	@Override
	public void tick ( ) {
		if ( !spawned ) {
			return;
		}
		
		// ticking particles
		particleTickers.forEach ( ParticleTicker :: tick );
		// ticking sounds
		soundTickers.forEach ( SoundTicker :: tick );
	}
	
	// -- state
	
	@Override
	public void setState ( @Nullable VehicleState state ) {
		if ( !Objects.equals ( this.state , state ) ) {
			this.state = state;
			
			onStateChanged ( );
		}
	}
	
	protected abstract void onStateChanged ( );
	
	// -- location & rotation
	
	@Override
	public @NotNull Location getLocation ( ) {
		return new Location ( world , x , y , z , rotation , 0.0F );
	}
	
	@NotNull
	@Override
	public World getWorld ( ) {
		return world;
	}
	
	@Override
	public double getX ( ) {
		return x;
	}
	
	@Override
	public double getY ( ) {
		return y;
	}
	
	@Override
	public double getZ ( ) {
		return z;
	}
	
	@Override
	public float getRotation ( ) {
		return rotation;
	}
	
	@Override
	public void setLocationAndRotation ( double x , double y , double z , float rotation ) {
		rotation %= 360.0F;
		
		boolean locationChanged = Double.compare ( x , this.x ) != 0
				|| Double.compare ( y , this.y ) != 0
				|| Double.compare ( z , this.z ) != 0;
		boolean rotationChanged = Float.compare ( this.rotation , rotation ) != 0;
		
		this.x        = x;
		this.y        = y;
		this.z        = z;
		this.rotation = rotation;
		
		if ( locationChanged && rotationChanged ) {
			onLocationAndRotationChanged ( );
		} else if ( locationChanged ) {
			onLocationChanged ( );
		} else {
			onRotationChanged ( );
		}
		
		// relocating particle tickers
		if ( locationChanged || rotationChanged ) {
			relocateParticleTickers ( );
		}
	}
	
	@Override
	public void setLocation ( double x , double y , double z ) {
		if ( Double.compare ( x , this.x ) != 0
				|| Double.compare ( y , this.y ) != 0
				|| Double.compare ( z , this.z ) != 0 ) {
			this.x = x;
			this.y = y;
			this.z = z;
			
			onLocationChanged ( );
			
			// relocating particle tickers
			relocateParticleTickers ( );
		}
	}
	
	@Override
	public void setRotation ( float rotation ) {
		rotation %= 360.0F;
		
		if ( Float.compare ( this.rotation , rotation ) != 0 ) {
			this.rotation = rotation;
			
			onRotationChanged ( );
			
			// relocating particle tickers
			relocateParticleTickers ( );
		}
	}
	
	protected abstract void onLocationAndRotationChanged ( );
	
	protected abstract void onLocationChanged ( );
	
	protected abstract void onRotationChanged ( );
	
	protected void relocateParticleTickers ( ) {
		particleTickers.forEach ( ParticleTicker :: updateLocation );
	}
}