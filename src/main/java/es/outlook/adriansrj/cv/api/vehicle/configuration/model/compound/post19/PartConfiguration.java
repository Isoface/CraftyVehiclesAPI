package es.outlook.adriansrj.cv.api.vehicle.configuration.model.compound.post19;

import es.outlook.adriansrj.cv.api.interfaces.ConfigurationSectionWritable;
import es.outlook.adriansrj.cv.api.interfaces.IDeyed;
import es.outlook.adriansrj.cv.api.interfaces.Identifiable;
import es.outlook.adriansrj.cv.api.util.ConfigurationUtil;
import es.outlook.adriansrj.cv.api.util.Constants;
import es.outlook.adriansrj.cv.api.util.inventory.ItemStackUtil;
import es.outlook.adriansrj.cv.api.vehicle.configuration.data.BannerStyle;
import es.outlook.adriansrj.cv.api.vehicle.configuration.data.DataParser;
import es.outlook.adriansrj.cv.api.vehicle.configuration.data.DataParsers;
import es.outlook.adriansrj.cv.api.vehicle.configuration.data.HeadTexture;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * @author AdrianSR / 23/11/2023 / 12:55 p. m.
 */
@Getter
@AllArgsConstructor
@Builder
public class PartConfiguration implements Identifiable, IDeyed, ConfigurationSectionWritable {
	
	public static PartConfiguration load ( @NotNull ConfigurationSection section )
			throws InvalidConfigurationException {
		Material material = ConfigurationUtil.loadEnum (
				Material.class , section , Constants.Key.MATERIAL );
		
		if ( material == null ) {
			throw new InvalidConfigurationException (
					"invalid material: " + section.getString ( Constants.Key.MATERIAL ) );
		}
		
		// custom model data
		Object customModelDataRaw = section.get ( Constants.Key.CUSTOM_MODEL_DATA );
		Integer customModelData = customModelDataRaw instanceof Number
				? ( ( Number ) customModelDataRaw ).intValue ( ) : null;
		
		// parsing data
		Object               data        = null;
		ConfigurationSection dataSection = section.getConfigurationSection ( Constants.Key.DATA );
		
		if ( dataSection != null ) {
			DataParser parser = DataParsers.matchParser ( dataSection );
			
			if ( parser != null ) {
				data = parser.parse ( dataSection );
			}
		}
		
		Vector3D offset = ConfigurationUtil.loadLibraryObject (
				Vector3D.class , section , Constants.Key.OFFSET );
		Vector3D rotation = ConfigurationUtil.loadLibraryObject (
				Vector3D.class , section , Constants.Key.ROTATION );
		Vector3D scale = ConfigurationUtil.loadLibraryObject (
				Vector3D.class , section , Constants.Key.SCALE );
		
		return new PartConfiguration (
				Identifiable.loadIdentifierOrGenerate ( section ) ,
				IDeyed.loadId ( section ) ,
				material , customModelData , data ,
				offset , rotation , scale
		);
	}
	
	private final @NotNull  UUID     identifier;
	private final @NotNull  String   id;
	private final @NotNull  Material material;
	private final @Nullable Integer  customModelData;
	private final @Nullable Object   data;
	private final @Nullable Vector3D offset;
	private final @Nullable Vector3D rotation;
	private final @Nullable Vector3D scale;
	
	public @NotNull ItemStack getItemStack ( ) {
		ItemStack item = ItemStackUtil.buildCustomItem ( material , customModelData );
		
		if ( ItemStackUtil.isHead ( material ) && data instanceof HeadTexture ) {
			( ( HeadTexture ) data ).applyTexture ( item );
		} else if ( ItemStackUtil.isBanner ( material ) && data instanceof BannerStyle ) {
			( ( BannerStyle ) data ).applyStyle ( item );
		}
		
		return item;
	}
	
	@Override
	public void write ( @NotNull ConfigurationSection section ) {
		Identifiable.writeIdentifier ( this , section );
		IDeyed.writeId ( this , section );
		
		section.set ( Constants.Key.MATERIAL , material.name ( ) );
		
		if ( customModelData != null ) {
			section.set ( Constants.Key.CUSTOM_MODEL_DATA , customModelData );
		}
		
		if ( data != null ) {
			DataParser parser = DataParsers.getParser ( data.getClass ( ) );
			
			if ( parser != null ) {
				parser.write ( data , section.createSection ( Constants.Key.DATA ) );
			}
		}
		
		if ( offset != null ) {
			ConfigurationUtil.writeLibraryObject (
					Vector3D.class , offset ,
					section.createSection ( Constants.Key.OFFSET )
			);
		}
		
		if ( rotation != null ) {
			ConfigurationUtil.writeLibraryObject (
					Vector3D.class , rotation ,
					section.createSection ( Constants.Key.ROTATION )
			);
		}
		
		if ( scale != null ) {
			ConfigurationUtil.writeLibraryObject (
					Vector3D.class , scale ,
					section.createSection ( Constants.Key.SCALE )
			);
		}
	}
}