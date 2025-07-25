package io.inoa.rest;

/** Test for {@link ThingsApi}. */
@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
public interface ThingsApiTestSpec { 

	// createThing

	void createThing201() throws java.lang.Exception;

	void createThing400() throws java.lang.Exception;

	void createThing401() throws java.lang.Exception;

	void createThing409() throws java.lang.Exception;

	// deleteThing

	void deleteThing204() throws java.lang.Exception;

	void deleteThing401() throws java.lang.Exception;

	void deleteThing404() throws java.lang.Exception;

	// findThing

	void findThing200() throws java.lang.Exception;

	void findThing401() throws java.lang.Exception;

	void findThing404() throws java.lang.Exception;

	// findThingsByGatewayId

	void findThingsByGatewayId200() throws java.lang.Exception;

	void findThingsByGatewayId401() throws java.lang.Exception;

	void findThingsByGatewayId404() throws java.lang.Exception;

	// syncThingsToGateway

	void syncThingsToGateway200() throws java.lang.Exception;

	void syncThingsToGateway401() throws java.lang.Exception;

	void syncThingsToGateway404() throws java.lang.Exception;

	// updateThing

	void updateThing200() throws java.lang.Exception;

	void updateThing400() throws java.lang.Exception;

	void updateThing401() throws java.lang.Exception;

	void updateThing404() throws java.lang.Exception;
}
