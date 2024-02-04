package es.outlook.adriansrj.cv.api.util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author AdrianSR / 2/2/2024 / 4:16 p. m.
 */
public abstract class LangFormatter {
	
	public static LangFormatter single ( @NotNull String context ) {
		return new Single ( context );
	}
	
	public static LangFormatter multi ( @NotNull String context ) {
		return new Multi ( context );
	}
	
	/**
	 * @author AdrianSR / 2/2/2024 / 4:44 p. m.
	 */
	public static class Single extends LangFormatter {
		
		private final @NotNull Map < Character, String > arguments = new HashMap <> ( );
		
		public Single ( @NotNull String context ) {
			super ( context );
		}
		
		@Override
		public LangFormatter arg ( char character , @NotNull String value ) {
			if ( character == ARGUMENT_CHAR ) {
				throw new IllegalArgumentException ( "unsupported character" );
			}
			
			arguments.put ( Character.toLowerCase ( character ) , value );
			return this;
		}
		
		@Override
		public @NotNull String format ( ) {
			StringBuilder builder = new StringBuilder ( );
			char[]        chars   = context.toCharArray ( );
			
			for ( int i = 0 ; i < chars.length ; i++ ) {
				if ( chars[ i ] != ARGUMENT_CHAR || i + 1 >= chars.length ) {
					builder.append ( chars[ i ] );
					continue;
				}
				
				char   character = Character.toLowerCase ( chars[ i + 1 ] );
				String argument  = arguments.get ( character );
				
				if ( argument != null ) {
					builder.append ( argument );
				}
				
				i++;
			}
			
			return builder.toString ( );
		}
	}
	
	/**
	 * @author AdrianSR / 2/2/2024 / 4:44 p. m.
	 */
	public static class Multi extends LangFormatter {
		
		private final @NotNull Map < Character, List < String > > arguments = new HashMap <> ( );
		
		public Multi ( @NotNull String context ) {
			super ( context );
		}
		
		@Override
		public LangFormatter arg ( char character , @NotNull String value ) {
			if ( character == ARGUMENT_CHAR ) {
				throw new IllegalArgumentException ( "unsupported character" );
			}
			
			arguments.computeIfAbsent (
					Character.toLowerCase ( character ) ,
					k -> new ArrayList <> ( ) ).add ( value );
			return this;
		}
		
		public @NotNull String format ( ) {
			StringBuilder builder = new StringBuilder ( );
			
			char[]                             chars = context.toCharArray ( );
			Map < Character, List < String > > map   = new HashMap <> ( this.arguments );
			
			for ( int i = 0 ; i < chars.length ; i++ ) {
				if ( chars[ i ] != ARGUMENT_CHAR || i + 1 >= chars.length ) {
					builder.append ( chars[ i ] );
					continue;
				}
				
				char            character = Character.toLowerCase ( chars[ i + 1 ] );
				List < String > arguments = map.get ( character );
				
				if ( arguments != null && arguments.size ( ) > 0 ) {
					builder.append ( arguments.remove ( 0 ) );
				}
				
				i++;
			}
			
			return builder.toString ( );
		}
	}
	
	private static final char ARGUMENT_CHAR = '%';
	
	protected final @NotNull String context;
	
	public LangFormatter ( @NotNull String context ) {
		this.context = context;
	}
	
	public abstract LangFormatter arg ( char character , @NotNull String value );
	
	public abstract @NotNull String format ( );
	
	@Override
	public String toString ( ) {
		return format ( );
	}
}