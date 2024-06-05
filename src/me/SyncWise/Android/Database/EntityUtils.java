/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Database;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;

/**
 * Utilities class for generated GreenDao Entities, holding helper methods for additional functionality.
 * 
 * @author Elias
 *
 */
public class EntityUtils {
	
	/**
	 * Constant double holding the default group size used when querying by group.
	 */
	public static final double DEFAULT_GROUP_SIZE = 500;
	
	/**
	 * Clones the provided object of the GreenDao entity type.<br>
	 * If the object cannot be cloned for any reason, {@code NULL} is returned.
	 * 
	 * @param entity	The entity type (class) of the object to clone. 
	 * @param object	The object to clone.
	 * @param propertiesToIgnore	Array holding the properties to be ignored during the cloning process.
	 * @return	A clone of the object, or {@code NULL} if the object cannot be cloned.
	 */
	public static < T > T clone ( final Class < T > entity , final Object object , final Property ... propertiesToIgnore ) {
		// Determine if the arguments are valid
		if ( entity == null || object == null )
			// Invalid arguments
			return null;
		
		// Check if the provided object is an instance of the indicated class
		if ( ! entity.isInstance ( object ) )
			// The 'object' is not an instance of 'entity'
			return null;
		
		try {

			// Otherwise, the 'object' is an instance of 'entity'
			// Cast a new instance of 'entity' used as the clone
			T clone = entity.newInstance ();
			// Declare and initialize a hash map used to store the returned values of each method
			HashMap < String , Object > values = new HashMap < String , Object > ();
			// Retrieve all the methods of the 'entity'
			Method [] methods = entity.getDeclaredMethods ();
			
			// Iterate over all methods
			for ( Method method : methods ) {
				
				// Check if the current method is a getter
				if ( ! method.getName ().startsWith ( "get" ) )
					// Not a getter, skip method
					continue;
				
				// Otherwise, the method is a getter
				// Determine if the current getter should be ignored
				boolean ignore = false;
				// Iterate over ignored property names
				for ( Property property : propertiesToIgnore )
					// Determine if the current property getter should be ignored
					if ( method.getName ().equalsIgnoreCase ( "get" + property.name ) ) {
						// Flag this method to be ignored
						ignore = true;
						break;
					} // End if
				// Check if the current property getter should be ignored
				if ( ignore )
					// Skip method
					continue;
				
				// Invoke the method over the object to clone and store its returned value
				values.put ( method.getName ().substring ( 3 ) , method.invoke ( object ) );
			} // End for each
			
			// Iterate over all methods
			for ( Method method : methods ) {
				// Check if the current method is a setter
				if ( ! method.getName ().startsWith ( "set" ) )
					// Not a setter, skip method
					continue;
				// Otherwise, the method is a setter
				// Determine if the current setter has a value to use
				if ( values.get ( method.getName ().substring ( 3 ) ) != null )
					// Invoke the method over the cloned object and use the stored value as argument
					method.invoke ( clone , values.get ( method.getName ().substring ( 3 ) ) );
			} // End for each
			
			// Return the created clone
			return clone;
			
		} catch ( Exception exception ) {
			// Object cannot be cloned
			return null;
		} // End of try-catch block
	}
	
	/**
	 * Compares the provided GreenDao entity objects.<br>
	 * If the objects cannot be compared for any reason, they are treated as different.
	 * 
	 * @param object1	The first GreenDao entity object to compare.
	 * @param object2	The second GreenDao entity object to compare.
	 * @param propertiesToIgnore	Array holding the properties to be ignored during the comparison process.
	 * @return	Boolean indicating whether the provided objects are alike ({@code true}) or different ({@code false}).
	 */
	public static boolean compare ( final Object object1 , final Object object2 , final Property ... propertiesToIgnore ) {
		try {
			
			// Determine if both objects are instances of the same class
			if ( ! object1.getClass ().equals ( object2.getClass () ) )
				// Objects cannot be compared
				return false;
			// Otherwise both objects can be compared
			// Retrieve all the methods of their Class
			Method [] methods = object1.getClass ().getDeclaredMethods ();
			
			// Iterate over all methods
			for ( Method method : methods ) {
				
				// Check if the current method is a getter
				if ( ! method.getName ().startsWith ( "get" ) )
					// Not a getter, skip method
					continue;
				
				// Otherwise, the method is a getter
				// Determine if the current getter should be ignored
				boolean ignore = false;
				// Iterate over ignored property names
				for ( Property property : propertiesToIgnore )
					// Determine if the current property getter should be ignored
					if ( method.getName ().equalsIgnoreCase ( "get" + property.name ) ) {
						// Flag this method to be ignored
						ignore = true;
						break;
					} // End if
				// Check if the current property getter should be ignored
				if ( ignore )
					// Skip method
					continue;
				
				// Invoke the method over both objects
				Object value1 = method.invoke ( object1 );
				Object value2 = method.invoke ( object2 );
				
				// Determine if both values are nulls
				if ( value1 == null && value2 == null ) {
					// The values are alike, do nothing
				} // End if
				// Determine if exactly one of the values is null
				else if ( value1 == null || value2 == null )
					// The 2 objects have at least one difference and hence are different
					return false;
				// Otherwise, compare the returned values
				else if ( ! value1.equals ( value2 ) )
					// The 2 objects have at least one difference and hence are different
					return false;
				
			} // End for each
			// Otherwise, the 2 objects have no differences and are alike
			return true;
		} catch ( Exception exception ) {
			// Objects cannot be compared
			return false;
		} // End of try-catch block
	}

	/**
	 * Retrieves a list of entities by executing a query for every group of conditions.<br>
	 * This resort is used due to SQLite limitations : Cannot execute query using a list with more than 1000 elements in the IN statement.
	 * 
	 * @param conditions	List of conditions (of primitive data types) used in the query.
	 * @param dao	Base class for all DAOs: Implements entity operations.
	 * @param in	Meta data describing a property mapped to a database column; used to create WhereCondition object used by the query builder.
	 * @return	The list of entities returned by the query.
	 */
	public static < T > List < T > queryByGroup ( final List < ? > conditions , final AbstractDao < T , ? > dao , final Property in ) {
		try {
			// Define the group size
			final double groupSize = DEFAULT_GROUP_SIZE;
			// Declare and initialize a list of objects to host the result
			List < T > objects = new ArrayList < T > ();
			// Declare and initialize a list of objects to host groups of conditions
			List < Object > group = new ArrayList < Object > ();
			// Compute the number of iterations to perform
			int iterations = (int) Math.ceil ( conditions.size () / groupSize );
			// Perform the specified number of iterations, and execute the query in each iterations for a group
			for ( int i = 0 ; i < iterations ; i ++ ) {
				// Clear any previous group
				group.clear ();
				// Compute the condition group limit
				int condition = (int) ( ( ( i + 1 ) * groupSize ) > conditions.size () ? conditions.size () : ( i + 1 ) * groupSize ) ;
				// Retrieve the new group list
				for ( int j = (int) ( i * groupSize ) ; j < condition ; j ++ )
					group.add ( conditions.get ( j ) );
				// Execute the query using the conditions group above, and add its result to the list
				objects.addAll ( dao.queryBuilder ().where ( in.in ( group ) ).list () );
			} // End for loop
			// Return the result list
			return objects;
		} catch ( Exception exception ) {
			// An error occurred
			return null;
		} // End of try-catch block
	}
	
}