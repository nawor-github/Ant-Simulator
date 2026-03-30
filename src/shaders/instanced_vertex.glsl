#version 410

in vec4 a_position; // The vertex position from our java class
in vec3 a_worldPos; // The world coordinates at which to place the point
in vec3 a_scale; //The scale to draw at

out float v_xCoord; //xCoordinate of point

uniform mat4 u_mvpMatrix;

void main() {
	// Passing the data into the fragment shader
	mat4 translation = mat4(1,0,0,0, 0,1,0,0, 0,0,1,0, a_worldPos.x,a_worldPos.y,0,1); 
	mat4 scale = mat4(a_scale.x,0,0,0,  0,a_scale.y,0,0, 0,0,1,0, 0,0,0,1);
	mat4 modelMatrix = u_mvpMatrix * translation * scale;
	gl_Position = modelMatrix * a_position;
	
	v_xCoord = a_position.x;
	
}
