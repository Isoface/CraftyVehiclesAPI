package es.outlook.adriansrj.cv.api.util.reflection;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author AdrianSR / 17/11/2023 / 1:12 a. m.
 */
public final class ClassReflection {
	
	/**
	 * @author AdrianSR / 28/11/2023 / 11:11 p. m.
	 */
	private enum EnumPrimitives {
		
		INTEGER ( int.class , Integer.class ),
		LONG ( long.class , Long.class ),
		DOUBLE ( double.class , Double.class ),
		FLOAT ( float.class , Float.class ),
		BYTE ( byte.class , Byte.class ),
		SHORT ( short.class , Short.class ),
		BOOLEAN ( boolean.class , Boolean.class );
		
		private final Class < ? > primitive;
		private final Class < ? > wrapper;
		
		private static EnumPrimitives match ( Class < ? > clazz ) {
			for ( EnumPrimitives primitive : EnumPrimitives.values ( ) ) {
				if ( primitive.primitive == clazz || primitive.wrapper == clazz ) {
					return primitive;
				}
			}
			
			return null;
		}
		
		EnumPrimitives ( Class < ? > primitive , Class < ? > wrapper ) {
			this.primitive = primitive;
			this.wrapper   = wrapper;
		}
	}
	
	public static boolean isPrimitiveType ( Class < ? > type ) {
		return EnumPrimitives.match ( type ) != null;
	}
	
	public static Class < ? > getPrimitiveType ( Class < ? > wrapperType ) {
		return Objects.requireNonNull ( EnumPrimitives.match (
				wrapperType ) , "not a primitive" ).primitive;
	}
	
	public static Class < ? > getPrimitiveWrapperType ( Class < ? > primitive ) {
		return Objects.requireNonNull ( EnumPrimitives.match (
				primitive ) , "not a primitive" ).wrapper;
	}
	
	/**
	 * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
	 * @param packageName The base package
	 * @return The classes
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static Class < ? >[] getClasses ( String packageName )
			throws ClassNotFoundException, IOException {
		ClassLoader classLoader = Thread.currentThread ( ).getContextClassLoader ( );
		
		assert classLoader != null;
		String              path      = packageName.replace ( '.' , '/' );
		Enumeration < URL > resources = classLoader.getResources ( path );
		List < File >       dirs      = new ArrayList < File > ( );
		
		while ( resources.hasMoreElements ( ) ) {
			URL resource = resources.nextElement ( );
			dirs.add ( new File ( resource.getFile ( ) ) );
		}
		
		ArrayList < Class < ? > > classes = new ArrayList < Class < ? > > ( );
		
		for ( File directory : dirs ) {
			classes.addAll ( findClasses ( directory , packageName ) );
		}
		
		return classes.toArray ( new Class[ classes.size ( ) ] );
	}
	
	/**
	 * Recursive method used to find all classes in a given directory and subdirs.
	 * @param directory   The base directory
	 * @param packageName The package name for classes found inside the base directory
	 * @return The classes
	 * @throws ClassNotFoundException
	 */
	public static List < Class < ? > > findClasses ( File directory , String packageName )
			throws ClassNotFoundException {
		List < Class < ? > > classes = new ArrayList < Class < ? > > ( );
		
		if ( !directory.exists ( ) ) {
			return classes;
		}
		
		File[] files = directory.listFiles ( );
		
		for ( File file : files ) {
			if ( file.isDirectory ( ) ) {
				assert !file.getName ( ).contains ( "." );
				
				classes.addAll ( findClasses ( file , packageName + "." + file.getName ( ) ) );
			} else if ( file.getName ( ).endsWith ( ".class" ) ) {
				try {
					classes.add ( Class.forName ( packageName + '.' + file.getName ( )
							.substring ( 0 , file.getName ( ).length ( ) - 6 ) ) );
				} catch ( ClassNotFoundException ex ) {
					ex.printStackTrace ( );
				}
			}
		}
		
		return classes;
	}
	
	/**
	 * Scans the names of all the classes within a package contained by the provided
	 * <strong>{@code .jar}</strong>.
	 * <p>
	 * @param jarFile     the file that represents the .jar
	 * @param packageName the name of the desired package that contains the classes to get, or null to get all the
	 *                    classes contained by the .jar
	 * @return a set with the name of the classes.
	 */
	public static Set < String > getClassNames ( File jarFile , @Nullable String packageName ) {
		Set < String > names = new HashSet <> ( );
		
		try {
			JarFile file = new JarFile ( jarFile );
			
			for ( Enumeration < JarEntry > entry = file.entries ( ) ; entry.hasMoreElements ( ) ; ) {
				JarEntry jarEntry = entry.nextElement ( );
				String   name     = jarEntry.getName ( ).replace ( "/" , "." );
				
				if ( ( packageName == null || packageName.trim ( ).isEmpty ( ) || name.startsWith (
						packageName.trim ( ) ) ) && name.endsWith ( ".class" ) ) {
					names.add ( name.substring ( 0 , name.lastIndexOf ( ".class" ) ) );
				}
			}
			
			file.close ( );
		} catch ( Exception e ) {
			e.printStackTrace ( );
		}
		
		return names;
	}
	
	/**
	 * Scans all the classes within a package contained by the provided
	 * <strong>{@code .jar}</strong>.
	 * <p>
	 * @param jarFile     the file that represents the .jar
	 * @param packageName the name of the desired package that contains the classes to get, or null to get all the
	 *                    classes contained by the .jar
	 * @return a set with the scanned classes.
	 */
	public static Set < Class < ? > > getClasses ( File jarFile , String packageName ) {
		Set < Class < ? > > classes = new HashSet < Class < ? > > ( );
		
		getClassNames ( jarFile , packageName ).forEach ( class_name -> {
			try {
				classes.add ( Class.forName ( class_name ) );
			} catch ( ClassNotFoundException e ) {
				e.printStackTrace ( );
			}
		} );
		return classes;
	}
}