var JavaPackages = new JavaImporter(
 Packages.java.awt.Color,
 Packages.sage.scene.Group,
 Packages.sage.scene.shape.Line,
 Packages.graphicslib3D.Point3D);
with (JavaPackages)
{
var rootNode = new Group();
var displayAxis = true;
var origin = new Point3D(0,0,0);
var xEnd = new Point3D(20,0,0);
var yEnd = new Point3D(0,20,0);
var zEnd = new Point3D(0,0,20);
var xAxis = new Line(origin, xEnd, Color.red, 3);
var yAxis = new Line(origin, yEnd, Color.green, 3);
var zAxis = new Line(origin, zEnd, Color.blue, 3);

if(displayAxis == true){	
	rootNode.addChild(zAxis); 
	rootNode.addChild(yAxis);
	rootNode.addChild(xAxis);
}

if(displayAxis == false){
	rootNode.removeChild(zAxis);
	rootNode.removeChild(yAxis);
	rootNode.removeChild(xAxis);
}
}
