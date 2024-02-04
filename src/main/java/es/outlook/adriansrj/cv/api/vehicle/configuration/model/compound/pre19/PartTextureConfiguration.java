package es.outlook.adriansrj.cv.api.vehicle.configuration.model.compound.pre19;

import es.outlook.adriansrj.cv.api.interfaces.ConfigurationSectionWritable;
import es.outlook.adriansrj.cv.api.util.ConfigurationUtil;
import es.outlook.adriansrj.cv.api.util.Constants;
import es.outlook.adriansrj.cv.api.util.inventory.ItemStackUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

/**
 * @author AdrianSR / 30/11/2023 / 11:02 a. m.
 */
@Getter
@AllArgsConstructor
@Builder
public class PartTextureConfiguration implements ConfigurationSectionWritable {
	
	private final @NotNull  Material material;
	private final @Nullable Integer  customModelData;
	private final @Nullable Vector3D rotation;
	
	public static PartTextureConfiguration load ( @NotNull ConfigurationSection section )
			throws InvalidConfigurationException {
		Material material = ConfigurationUtil.loadEnum (
				Material.class , section , Constants.Key.MATERIAL );
		if ( material == null ) {
			throw new InvalidConfigurationException (
					"invalid material: " + section.getString ( Constants.Key.MATERIAL ) );
		}
		
		Vector3D rotation = ConfigurationUtil.loadLibraryObject (
				Vector3D.class , section , Constants.Key.ROTATION );
		
		// custom model data
		Object customModelDataRaw = section.get ( Constants.Key.CUSTOM_MODEL_DATA );
		Integer customModelData = customModelDataRaw instanceof Number
				? ( ( Number ) customModelDataRaw ).intValue ( ) : null;
		
		return new PartTextureConfiguration ( material , customModelData , rotation );
	}
	
	public @NotNull ItemStack getItemStack ( ) {
		return ItemStackUtil.buildCustomItem ( material , customModelData );
	}
	
	@Override
	public void write ( @NotNull ConfigurationSection section ) {
		section.set ( Constants.Key.MATERIAL , material.name ( ) );
		
		if ( customModelData != null ) {
			section.set ( Constants.Key.CUSTOM_MODEL_DATA , customModelData );
		}
		
		if ( rotation != null ) {
			ConfigurationUtil.writeLibraryObject (
					Vector3D.class , rotation ,
					section.createSection ( Constants.Key.ROTATION )
			);
		}
	}
}
