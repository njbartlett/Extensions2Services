Extensions-2-Services: Integrating Eclipse Extensions with OSGi Services
========================================================================

Extensions2Services ("e2s") is a very small and lightweight framework for integrating Eclipse Extensions with OSGi Services. These two models are difficult to use together for the following reasons:

* No references to OSGi APIs (e.g. `BundleContext`) from extension objects.
* Lifecycle mismatch -- services come and go but extensions cannot easily be disabled if the services they depend on become unavailable.
* Extensions are factories, whereas services are shared singleton-like objects. Coercing services directly into use as extension objects can result in unexpected behaviour.
* No explicit release. There is no API for the client of an extension to indicate that it has finished using an extension object, therefore there is no opportunity for an extension object to unregister its interest in a service.

Example
-------

The approach used by e2s is to declare an "injected factory" using a special extension point. That factory may then be referenced by extensions into any other arbitrary extension point. The following example shows the use of e2s to declare an Eclipse view that uses the OSGi `LogReaderService`:
    
	<!-- Factory Declaration -->
	<extension
	      point="eu.wwuk.eclipse.extsvcs.core.injectedFactories">
	   <factory
	         id="logReaderView"
	         class="org.example.view.LogReaderView">
	      <reference
	            interface="org.osgi.service.log.LogReaderService">
	      </reference>
	   </factory>
	</extension>

	<!-- Standard View Declaration -->
	<extension
	      point="org.eclipse.ui.views">
	   <view
	         id="org.example.views.logView"
	         class="eu.wwuk.eclipse.extsvcs.core.InjectionFactory:logReaderView"
	         name="Log Reader"
	         icon="icons/log.gif">
	   </view>
	</extension>

Notice that the view extension declaration is exactly the same as a normal view declaration, except for the content of the `class` attribute.

Further Information
-------------------

For extensive documentation, see the [included manual](http://github.com/njbartlett/Extensions2Services/raw/master/manual/manual.pdf).

WARNING
-------

This code is currently experimental and contains very little error handling or diagnostic capabilities. These will be added in due course, but for the time being *caveat emptor*. 

Licence
-------

All code and examples are licensed under the Eclipse Public Licence version 1.0.
