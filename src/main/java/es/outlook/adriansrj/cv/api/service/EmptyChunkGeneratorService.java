package es.outlook.adriansrj.cv.api.service;

import org.bukkit.generator.ChunkGenerator;

/**
 * @author AdrianSR / 27/11/2023 / 1:28 a. m.
 */
public interface EmptyChunkGeneratorService extends Service {
	
	ChunkGenerator getNewEmptyChunkGenerator ( );
}