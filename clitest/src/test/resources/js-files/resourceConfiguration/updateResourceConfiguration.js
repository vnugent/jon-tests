/**
 * @author ahovsepy@redhat.com (Armine Hovsepyan)
 * 
 */
//println(args);
var configProperty=prop
//var propType=propType
var configPropValue = propValue
var resourceId=resourceId

resourceId = java.lang.Integer.parseInt(resourceId);

var common = new _common();
var myResource = resources.find({id:resourceId})
var resource = myResource[0];
if(configProperty== "rhq.agent.security-token"){
	configPropValue = configPropValue+"=";
}


var chnageConfiguration = resource.getConfiguration();
println("CONFIG PROP BEFORE CHANGE IS = "+chnageConfiguration[configProperty]);
println("INDEX OF , in STRING  "+(String.valueOf(chnageConfiguration[configProperty]).toString().indexOf("$")) )
chnageConfiguration[configProperty]=configPropValue;
var result = resource.updateConfiguration(chnageConfiguration);
println("***************** resource.name *********************** "+resource.name + "\n\r");
println("***************** configProperty *********************** "+configProperty + "\n\r");
assertTrue(( typeof result == "undefined" || result )  ,"Configuration change failed!!! " + "config property is "+ configProperty + "rsource id is " +resourceId);
//get the changed configuration
var newConfiguration = resource.getConfiguration();

assertTrue(
		((newConfiguration[configProperty]).toString()).equals((chnageConfiguration[configProperty]).toString()),
				"Configuration change didn't work correctly. Expected value of '"
				+ configProperty + "' is '"
				+ chnageConfiguration[configProperty]
				+ "', but actual value is '" + newConfiguration[configProperty]
				+ "'");
