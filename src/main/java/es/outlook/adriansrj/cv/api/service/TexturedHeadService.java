package es.outlook.adriansrj.cv.api.service;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author AdrianSR / 19/12/2023 / 9:53 a. m.
 */
public interface TexturedHeadService extends Service {
	
	@Nullable String getTexture ( @NotNull Block block );
	
	void applyTexture ( @NotNull ItemStack headItem , @NotNull String textureUrl );
}