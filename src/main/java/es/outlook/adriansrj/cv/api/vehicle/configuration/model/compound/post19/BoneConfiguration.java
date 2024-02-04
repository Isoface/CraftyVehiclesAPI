package es.outlook.adriansrj.cv.api.vehicle.configuration.model.compound.post19;

import es.outlook.adriansrj.cv.api.interfaces.ConfigurationSectionWritable;
import es.outlook.adriansrj.cv.api.interfaces.IDeyed;
import es.outlook.adriansrj.cv.api.interfaces.Identifiable;
import es.outlook.adriansrj.cv.api.util.ConfigurationUtil;
import es.outlook.adriansrj.cv.api.util.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * @author AdrianSR / 14/1/2024 / 4:06 p. m.
 */
@Getter
@AllArgsConstructor
@Builder
public class BoneConfiguration implements Identifiable, IDeyed, ConfigurationSectionWritable {
	
	public static BoneConfiguration load ( @NotNull ConfigurationSection section )
			throws InvalidConfigurationException {
		
		Vector3D pivot = ConfigurationUtil.loadLibraryObject (
				Vector3D.class , section , Constants.Key.PIVOT );
		Vector3D rotation = ConfigurationUtil.loadLibraryObject (
				Vector3D.class , section , Constants.Key.ROTATION );
		
		if ( pivot == null ) {
			throw new InvalidConfigurationException ( "pivot must be set" );
		}
		
		return new BoneConfiguration (
				Identifiable.loadIdentifierOrGenerate ( section ) ,
				IDeyed.loadId ( section ) ,
				pivot , rotation
		);
	}
	
	private final @NotNull  UUID     identifier;
	private final @NotNull  String   id;
	private final @NotNull  Vector3D pivot;
	private final @Nullable Vector3D rotation;
	
	@Override
	public void write ( @NotNull ConfigurationSection section ) {
		Identifiable.writeIdentifier ( this , section );
		IDeyed.writeId ( this , section );
		
		ConfigurationUtil.writeLibraryObject (
				Vector3D.class , pivot ,
				section.createSection ( Constants.Key.PIVOT )
		);
		
		if ( rotation != null ) {
			ConfigurationUtil.writeLibraryObject (
					Vector3D.class , rotation ,
					section.createSection ( Constants.Key.ROTATION )
			);
		}
	}
}