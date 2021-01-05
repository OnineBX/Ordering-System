/**========================================
 * File:	INeedInitializable.java
 * Package:	com.hithing.sys
 * Create:	by leo
 * Date:	2012-7-6:下午4:31:42
 **======================================*/
package com.hithing.sys;

/**
 * <p>INeedInitializable</p>
 * 	
 * <p>需要初始化接口</p>
 *
 * @author leo
 * 
 */
public interface INeedInitializable {
	void doPostInitialized(Object param);
}
