//Restarted script log at 09/12/17 17:45:37
getBody(1).deselect();
getBody(3).deselect();
getBody(5).deselect();
getBody(6).deselect();
getBody(7).deselect();
getBody(8).deselect();
addBody(2, '{"awake":true,"type":"dynamic"}');
getBody(2).addFixture(2, '{"density":1,"shapes":[{"radius":0,"type":"polygon"}],"friction":0.2,"vertices":{"x":[-0.5,0.5,0.5,-0.5],"y":[-0.5,-0.5,0.5,0.5]}}');
getBody(2).setPosition(0.0198837,0.0795721);
getBody(2).select();
{
	fixture _rube_redoFixture = getFixture(2);
	_rube_redoFixture.setVertex(0,-0.0171718,-0.0171718);
	_rube_redoFixture.setVertex(1,0.0171718,-0.0171718);
	_rube_redoFixture.setVertex(2,0.0171718,0.0171718);
	_rube_redoFixture.setVertex(3,-0.0171718,0.0171718);
}
getBody(2).setPosition(0.126479,0.559251);
getBody(2).deselect();
getFixture(2).delete();
getBody(2).delete();
