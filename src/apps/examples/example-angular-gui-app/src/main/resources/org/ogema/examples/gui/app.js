
angular.module('resources',[])
  .factory('Resources', function() {
    /*
    	The variable 'resources' is used to store information on the OGEMA resources in
    	the system. This information is obtained from the app servlet through the sendGET() 
    	method in the controller below. The format of the variable 'resources' in this example is 
    	{
    		resource_location1 : {
    			column_property1 : value1,
    			column_property2 : value2,
    			...
    		},	
    		resource_location2 : {
    			...
    		},
    		...
    	}
    	where 'resource_locationX' represents an OGEMA resource location, and 'column_propertyY' is any of the column headers
    	shown on the app page.   
    */
    var resources = {};
    var columns = [];
    return {
      getResources: function() {
        return resources;
      },
      getLocations: function() {
     	return Object.keys(resources);
      },
      getColumns: function() {
      	return columns;
      },
      setResources: function(newResources) {
        resources = newResources;
        columns = [];
        Object.keys(newResources).forEach(function(item) {
			Object.keys(newResources[item]).forEach(function(col) {
				if (columns.indexOf(col) < 0 ) columns.push(col);
			});      
        });
      }
    };
   })
   .controller('ResourcesCtrl', function($scope,$http,Resources) {
 
 //*********** variables declaration *****************
 
    var path = "/apps/ogema/AngularGuiApp";  	
    path = path.toLowerCase();
   
 //*********** definition of functions *****************
 
    function getActiveStatus(res) {
   		var resources = Resources.getResources();
		if (!resources.hasOwnProperty(res) || !resources[res].hasOwnProperty("Status")) return false;
		return resources[res].Status.toLowerCase() === "active";
    };
   
    $scope.getStatusBtnMsg = function(resource) {
    	return getActiveStatus(resource) ? "deactivate" : "activate";
    };
 
	$scope.getResources = function() {
		return Resources.getLocations();
	};
	
	$scope.getColumns = function() {
		return Resources.getColumns();
	};
	
	$scope.getColumnValue = function(res,col) {
		var resources = Resources.getResources();
		if (!resources.hasOwnProperty(res)) return "";
		var obj = resources[res];
		if (!obj.hasOwnProperty(col)) return "";
		return obj[col];
	};
	
	// send HTTP GET
	$scope.update = function() {
		$http.get(path).then(function(response) {
			Resources.setResources(response.data);
			console.log("New set of resources",Resources.getResources());	    		
		});
	};
	
	// send HTTP POST		
	$scope.toggleStatus = function(resource) {
		var msg = {'location' : resource, 'status' : !getActiveStatus(resource)};
		$http.post(path, msg).then(function(response) {
			// update view after toggling active status of a resource
			$scope.update();
		});
	};
    	
 //*************** init on startup ******************
  	
    $scope.update();
 });