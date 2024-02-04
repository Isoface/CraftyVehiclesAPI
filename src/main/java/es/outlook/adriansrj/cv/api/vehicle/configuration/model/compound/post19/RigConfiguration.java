package es.outlook.adriansrj.cv.api.vehicle.configuration.model.compound.post19;

import es.outlook.adriansrj.cv.api.interfaces.ConfigurationSectionWritable;
import es.outlook.adriansrj.cv.api.interfaces.Identifiable;
import es.outlook.adriansrj.cv.api.util.ConfigurationUtil;
import es.outlook.adriansrj.cv.api.util.Constants;
import lombok.ToString;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author AdrianSR / 14/1/2024 / 6:27 p. m.
 */
public class RigConfiguration implements ConfigurationSectionWritable {
	
	public static RigConfiguration.Builder builder ( ) {
		return new RigConfiguration.Builder ( );
	}
	
	public static @Nullable RigConfiguration load ( @NotNull ConfigurationSection section ,
			@NotNull Post19CompoundModelConfiguration model ) throws InvalidConfigurationException {
		Set < ConfigurationSection > sections = ConfigurationUtil
				.getConfigurationSections ( section , false );
		
		Map < UUID, Element < ? > > elements = new HashMap <> ( );
		
		for ( ConfigurationSection elementSection : sections ) {
			Element < ? > element = Element.of ( elementSection , model );
			
			if ( element != null && !elements.containsKey ( element.identifier ) ) {
				elements.put ( element.identifier , element );
			}
		}
		
		// parents
		for ( ConfigurationSection elementSection : sections ) {
			Element < ? > element = elements.get ( Identifiable.loadIdentifier ( elementSection ) );
			
			if ( element == null ) {
				continue;
			}
			
			try {
				UUID parentIdentifier = UUID.fromString (
						elementSection.getString ( Constants.Key.PARENT , "" ) );
				
				Element < ? > parentRaw = elements.get ( parentIdentifier );
				
				if ( parentRaw instanceof BoneElement ) {
					element.parent = ( BoneElement ) parentRaw;
				}
			} catch ( IllegalArgumentException ignored ) {
				// ignored
			}
		}
		
		// bones children
		for ( ConfigurationSection elementSection : sections ) {
			Element < ? > elementRaw = elements.get ( Identifiable.loadIdentifier ( elementSection ) );
			BoneElement   bone       = elementRaw instanceof BoneElement ? ( BoneElement ) elementRaw : null;
			
			if ( bone == null ) {
				continue;
			}
			
			for ( String entry : elementSection.getStringList ( Constants.Key.CHILDREN ) ) {
				try {
					UUID          childIdentifier = UUID.fromString ( entry );
					Element < ? > child           = elements.get ( childIdentifier );
					
					if ( child != null ) {
						bone.children.add ( child );
					}
				} catch ( IllegalArgumentException ignored ) {
					// ignored
				}
			}
		}
		
		return elements.size ( ) > 0 ? new RigConfiguration ( elements.values ( ) ) : null;
	}
	
	/**
	 * @author AdrianSR / 15/1/2024 / 6:00 p. m.
	 */
	@ToString
	static abstract class Element < E > implements ConfigurationSectionWritable {
		
		static Element < ? > of ( @NotNull ConfigurationSection section ,
				@NotNull Post19CompoundModelConfiguration model ) {
			UUID identifier = Identifiable.loadIdentifier ( section );
			
			if ( identifier == null ) {
				return null;
			}
			
			// it's a part
			PartConfiguration partValue = model.getPartByIdentifier ( identifier );
			
			if ( partValue != null ) {
				return new PartElement ( partValue );
			}
			
			// it's a bone
			BoneConfiguration boneValue = model.getBoneByIdentifier ( identifier );
			
			if ( boneValue != null ) {
				return new BoneElement ( boneValue );
			}
			
			return null;
		}
		
		final @NotNull UUID identifier;
		final @NotNull E    value;
		
		// to be set
		BoneElement parent;
		
		private Element ( @NotNull UUID identifier , @NotNull E value ) {
			this.identifier = identifier;
			this.value      = value;
		}

//		boolean isIncomplete ( ) {
//			return value == null;
//		}
		
		@Override
		public void write ( @NotNull ConfigurationSection section ) {
			Identifiable.writeIdentifier ( identifier , section );
			
			if ( parent != null ) {
				section.set ( Constants.Key.PARENT , parent.identifier.toString ( ) );
			}
		}
	}
	
	/**
	 * @author AdrianSR / 15/1/2024 / 6:02 p. m.
	 */
	static final class PartElement extends Element < PartConfiguration > {
		
		PartElement ( @NotNull PartConfiguration part ) {
			super ( part.getIdentifier ( ) , part );
		}
	}
	
	/**
	 * @author AdrianSR / 15/1/2024 / 6:02 p. m.
	 */
	static final class BoneElement extends Element < BoneConfiguration > {
		
		final Set < Element < ? > > children = new HashSet <> ( );
		
		BoneElement ( @NotNull BoneConfiguration bone ) {
			super ( bone.getIdentifier ( ) , bone );
		}
		
		@Override
		public void write ( @NotNull ConfigurationSection section ) {
			super.write ( section );
			
			if ( children.size ( ) > 0 ) {
				section.set ( Constants.Key.CHILDREN , children.stream ( )
						.map ( child -> child.identifier.toString ( ) )
						.collect ( Collectors.toList ( ) )
				);
			}
		}
	}
	
	/**
	 * @author AdrianSR / 14/1/2024 / 7:00 p. m.
	 */
	public static class Builder {
		
		private final List < Element < ? > > wrappers = new ArrayList <> ( );
		
		public RigConfiguration build ( ) throws InvalidConfigurationException {
			return new RigConfiguration ( wrappers );
		}
		
		public void root ( @NotNull BoneConfiguration bone ) {
			getWrapper ( bone ); // calling getWrapper() registers it
		}
		
		public void bind ( @NotNull PartConfiguration child , @NotNull BoneConfiguration parent ) {
			PartElement childWrapper  = getWrapper ( child );
			BoneElement parentWrapper = getWrapper ( parent );
			
			if ( childWrapper.parent != null ) {
				throw new IllegalStateException ( "provided part is already bound to another bone" );
			}
			
			childWrapper.parent = parentWrapper;
			parentWrapper.children.add ( childWrapper );
		}
		
		public void bind ( @NotNull BoneConfiguration child , @NotNull BoneConfiguration parent ) {
			BoneElement childWrapper  = getWrapper ( child );
			BoneElement parentWrapper = getWrapper ( parent );
			
			if ( childWrapper.parent != null ) {
				throw new IllegalStateException ( "provided bone is already bound to another bone" );
			}
			
			childWrapper.parent = parentWrapper;
			parentWrapper.children.add ( childWrapper );
		}
		
		private PartElement getWrapper ( PartConfiguration part ) {
			Element < ? > wrapperRaw = getWrapperRaw ( part.getIdentifier ( ) );
			
			if ( wrapperRaw instanceof PartElement ) {
				return ( PartElement ) wrapperRaw;
			} else {
				PartElement wrapper = new PartElement ( part );
				wrappers.add ( wrapper );
				
				return wrapper;
			}
		}
		
		private BoneElement getWrapper ( BoneConfiguration bone ) {
			Element < ? > wrapperRaw = getWrapperRaw ( bone.getIdentifier ( ) );
			
			if ( wrapperRaw instanceof BoneElement ) {
				return ( BoneElement ) wrapperRaw;
			} else {
				BoneElement wrapper = new BoneElement ( bone );
				wrappers.add ( wrapper );
				
				return wrapper;
			}
		}
		
		private Element < ? > getWrapperRaw ( UUID identifier ) {
			for ( Element < ? > wrapper : wrappers ) {
				if ( Objects.equals ( wrapper.identifier , identifier ) ) {
					return wrapper;
				}
			}
			
			return null; // not found
		}
	}
	
	// [identifier <-> wrapper]
	private final Map < UUID, Element < ? > > elementMap = new HashMap <> ( );
	
	RigConfiguration ( @NotNull Collection < Element < ? > > elements ) throws InvalidConfigurationException {
		if ( elements.size ( ) == 0 ) {
			throw new InvalidConfigurationException ( "malformed rig" );
		}
		
		// malformed check
		int rootCount = 0;
		
		for ( Element < ? > raw : elements ) {
//			if ( raw.isIncomplete ( ) ) {
//				throw new InvalidConfigurationException ( "malformed rig" );
//			}
			
			if ( raw instanceof PartElement && raw.parent == null ) {
				throw new InvalidConfigurationException ( "malformed rig" );
			} else if ( raw instanceof BoneElement && raw.parent == null ) {
				rootCount++;
			}
		}
		
		if ( rootCount == 0 ) {
			throw new InvalidConfigurationException ( "malformed rig" );
		}
		
		// mapping elements by identifier
		for ( Element < ? > element : elements ) {
			elementMap.put ( element.identifier , element );
		}
	}
	
	public boolean contains ( @NotNull UUID elementIdentifier ) {
		return elementMap.containsKey ( elementIdentifier );
	}
	
	public boolean contains ( @NotNull PartConfiguration part ) {
		return contains ( part.getIdentifier ( ) );
	}
	
	public boolean contains ( @NotNull BoneConfiguration bone ) {
		return contains ( bone.getIdentifier ( ) );
	}
	
	public @NotNull List < BoneConfiguration > getAncestors ( @NotNull BoneConfiguration bone ) {
		List < BoneConfiguration > ancestors = new ArrayList <> ( );
		
		BoneElement wrapper = getWrapper ( bone );
		BoneElement cursor  = wrapper.parent;
		
		while ( cursor != null ) {
			ancestors.add ( cursor.value );
			
			cursor = cursor.parent;
		}
		
		Collections.reverse ( ancestors );
		return ancestors;
	}
	
	public @NotNull List < BoneConfiguration > getHierarchyUp ( @NotNull BoneConfiguration bone ) {
		List < BoneConfiguration > ancestors = getAncestors ( bone );
		ancestors.add ( bone );
		
		return ancestors;
	}
	
	public @NotNull List < BoneConfiguration > getHierarchyDown ( @NotNull BoneConfiguration bone ) {
		List < BoneConfiguration > children = getChildrenBones ( bone , true );
		children.add ( 0 , bone );
		
		return children;
	}
	
	public @NotNull List < BoneConfiguration > getChildrenBones ( @NotNull BoneConfiguration bone , boolean deep ) {
		List < BoneConfiguration > children = new ArrayList <> ( );
		BoneElement                wrapper  = getWrapper ( bone );
		
		for ( BoneElement raw : getChildrenBones ( wrapper , deep ) ) {
			children.add ( raw.value );
		}
		
		return children;
	}
	
	public @NotNull List < PartConfiguration > getChildrenParts ( @NotNull BoneConfiguration bone , boolean deep ) {
		List < PartConfiguration > children = new ArrayList <> ( );
		BoneElement                wrapper  = getWrapper ( bone );
		
		for ( PartElement raw : getChildrenParts ( wrapper , deep ) ) {
			children.add ( raw.value );
		}
		
		return children;
	}
	
	public @Nullable BoneConfiguration getParent ( @NotNull BoneConfiguration child ) {
		BoneElement wrapper       = getWrapper ( child );
		BoneElement parentWrapper = wrapper.parent;
		
		return parentWrapper != null ? parentWrapper.value : null;
	}
	
	public @Nullable BoneConfiguration getParent ( @NotNull PartConfiguration child ) {
		return getWrapper ( child ).parent.value;
	}
	
	@Override
	public void write ( @NotNull ConfigurationSection root ) {
		for ( Element < ? > element : elementMap.values ( ) ) {
			element.write ( root.createSection ( element.identifier.toString ( ) ) );
		}
	}
	
	// -- util
	
	private List < BoneElement > getChildrenBones ( BoneElement bone , boolean deep ) {
		List < BoneElement > children = new ArrayList <> ( );
		
		for ( Element < ? > raw : bone.children ) {
			if ( raw instanceof BoneElement ) {
				children.add ( ( BoneElement ) raw );
				
				if ( deep ) {
					children.addAll ( getChildrenBones ( ( BoneElement ) raw , true ) );
				}
			}
		}
		
		return children;
	}
	
	private List < PartElement > getChildrenParts ( BoneElement bone , boolean deep ) {
		List < PartElement > children = new ArrayList <> ( );
		
		for ( Element < ? > raw : bone.children ) {
			if ( raw instanceof PartElement ) {
				children.add ( ( PartElement ) raw );
			} else if ( raw instanceof BoneElement && deep ) {
				children.addAll ( getChildrenParts ( ( BoneElement ) raw , true ) );
			}
		}
		
		return children;
	}
	
	private @NotNull PartElement getWrapper ( @NotNull PartConfiguration part ) {
		PartElement wrapper = ( PartElement ) elementMap.get ( part.getIdentifier ( ) );
		
		if ( wrapper != null ) {
			return wrapper;
		} else {
			throw new IllegalArgumentException (
					"part with identifier '" + part.getIdentifier ( ) + "' was not found in rig" );
		}
	}
	
	private @NotNull BoneElement getWrapper ( @NotNull BoneConfiguration bone ) {
		BoneElement wrapper = ( BoneElement ) elementMap.get ( bone.getIdentifier ( ) );
		
		if ( wrapper != null ) {
			return wrapper;
		} else {
			throw new IllegalArgumentException (
					"bone with identifier '" + bone.getIdentifier ( ) + "' was not found in rig" );
		}
	}
	
	// public BoneConfiguration[] getAncestors ( BoneConfiguration bone )
	// public BoneConfiguration[] getChildrenBones ( BoneConfiguration bone )
	// public PartConfiguration[] getChildrenParts ( BoneConfiguration bone )
	// public @Nullable BoneConfiguration getParent ( BoneConfiguration child )
	// public @NotNull BoneConfiguration getParent ( PartConfiguration child )
}