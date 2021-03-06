/**
 * enables responseTime metric for given RHQ deployment
 *
 * @author lzoubek@redhat.com (Libor Zoubek)
 * Sep 03, 2013
 * 
 * requires rhqapi.js metrics/common.js
 */

// bind input params
var platformName = platform;
var war = deployment;

findRHQDeployment(platformName, war).child({name:"web"}).getMetric("Response Time").set(true,1);
